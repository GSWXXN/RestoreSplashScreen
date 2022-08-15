package com.gswxxn.restoresplashscreen.hook

import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.RoundDegree
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.Utils
import com.gswxxn.restoresplashscreen.utils.Utils.getField
import com.gswxxn.restoresplashscreen.utils.Utils.isMIUI
import com.gswxxn.restoresplashscreen.utils.Utils.printLog
import com.gswxxn.restoresplashscreen.utils.Utils.setField
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.ActivityInfoClass
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringType
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class SystemUIHooker : YukiBaseHooker() {
    private val iconPackManager by lazy {
        IconPackManager(
            appContext,
            prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)
        )
    }

    private fun isExcept(pkgName: String): Boolean {
        val list = prefs.get(DataConst.CUSTOM_SCOPE_LIST)
        val isExceptionMode = prefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
        return prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
                && ((isExceptionMode && (pkgName in list)) || (!isExceptionMode && pkgName !in list))
    }

    override fun onHook() {

        /**
         * 自定义作用域
         *
         * 此处在 makeSplashScreenContentView() 中调用
         *
         * 原理：干预 makeSplashScreenContentView() 中的 if 判断
         * - 核心功能， 若此处无法正常运行，则模块大部分功能将失效
         */
        findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer").hook {

            injectMember {
                method {
                    name = "getBGColorFromCache"
                    paramCount(2)
                }
                beforeHook {
                    if (!prefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN)) {
                        val pkgName = args(0).cast<ActivityInfo>()?.packageName!!
                        val isExcept = isExcept(pkgName)
                        if (!isExcept)
                            instance.getField("mTmpAttrs").any()!!.setField("mIconBgColor", 1)
                        printLog(
                            "****** ${pkgName}:",
                            "1. getBGColorFromCache(): ${
                                if (isExcept) "Except this app" else "Not Except, Set mIconBgColor 1"
                            }"
                        )
                    }
                }
            }
        }

        /**
         * 此处实现功能：
         * - 强制开启启动遮罩
         * - 忽略应用主动设置的图标
         * - 移除品牌图标
         * - 自适应背景颜色
         * - 设置微信背景色为黑色
         */
        findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder")
            .hook {

                /**
                 * 此处实现功能：
                 * - 忽略应用主动设置的图标
                 * - 移除品牌图标
                 */
                injectMember {
                    method {
                        name = "build"
                        emptyParam()
                    }
                    beforeHook {
                        val pkgName =
                            instance.getField("mActivityInfo").cast<ActivityInfo>()?.packageName!!
                        val isDefaultStyle = prefs.get(DataConst.ENABLE_DEFAULT_STYLE)
                                && pkgName in prefs.get(DataConst.DEFAULT_STYLE_LIST)
                        val isRemoveBrandingImage = prefs.get(DataConst.REMOVE_BRANDING_IMAGE)
                                && pkgName in prefs.get(DataConst.REMOVE_BRANDING_IMAGE_LIST)
                        val isRemoveBGColor = prefs.get(DataConst.REMOVE_BG_COLOR)
                        val isReplaceToEmptySplashScreen =
                            prefs.get(DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN)
                        val isExcept = isExcept(pkgName)
                        val forceEnableSplashScreen =
                            prefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN)
                        val context = instance.getField("mContext").cast<Context>()
                        val mSplashscreenContentDrawer = instance.getField("this\$0").any()!!
                        val mTmpAttrs = mSplashscreenContentDrawer
                            .getField("mTmpAttrs").any()!!

                        /**
                         * 强制开启启动遮罩
                         *
                         * 直接干预 build() 中的 if 判断
                         */
                        if (forceEnableSplashScreen) {
                            if (!isExcept) {
                                instance.setField("mSuggestType", 1)
                                instance.setField("mOverlayDrawable", null)
                            }
                            printLog(
                                "****** ${pkgName}(forceEnableSplashScreen):",
                                "1. build(): ${
                                    if (isExcept) "Except this app"
                                    else "set mSuggestType to 1; set mOverlayDrawable to null"
                                }"
                            )
                        }

                        // 打印日志
                        printLog(
                            "info: build(): mSuggestType is ${
                                instance.getField("mSuggestType").int()
                            }"
                        )

                        // 重置因实现自定义作用域而影响到的 mTmpAttrs
                        mSplashscreenContentDrawer.current {
                            method {
                                name = "getWindowAttrs"
                                paramCount(2)
                            }.call(context, mTmpAttrs)
                        }
                        printLog("2. build(): call getWindowAttrs() to reset mTmpAttrs")

                        // 将作用域外的应用替换为空白启动遮罩
                        if (isReplaceToEmptySplashScreen && isExcept && mTmpAttrs.getField("mSplashScreenIcon")
                                .any() == null
                        ) {
                            instance.setField("mSuggestType", 3)
                            instance.setField(
                                "mOverlayDrawable",
                                context!!.getDrawable(mTmpAttrs.getField("mWindowBgResId").int())
                            )
                        }
                        printLog("2.1. build(): ${if (isReplaceToEmptySplashScreen && isExcept) "set mSuggestType to 3;" else "Not"} replace to empty splash screen")

                        /**
                         * 忽略应用主动设置的图标
                         *
                         * 干预 build() 中的 if 判断
                         */
                        if (isDefaultStyle) {
                            mTmpAttrs.setField("mSplashScreenIcon", null)
                        }
                        printLog(
                            "3. build(): ${if (isDefaultStyle) "" else "Not"} ignore set icon"
                        )

                        /**
                         * 移除品牌图标
                         *
                         * 干预 fillViewWithIcon() 中的 if 判断
                         */
                        if (isRemoveBrandingImage) {
                            mTmpAttrs.setField("mBrandingImage", null)
                        }
                        printLog(
                            "4. build(): ${if (isRemoveBrandingImage) "" else "Not"} remove Branding Image"
                        )

                        /**
                         * 移除背景颜色
                         */
                        if (isRemoveBGColor) {
                            instance.setField("mThemeColor", Color.parseColor("#F5F5F5"))
                        }
                        printLog(
                            "5. build(): ${if (isRemoveBGColor) "" else "Not"} remove BG Color"
                        )
                    }
                }

                /**
                 * 此处实现功能：
                 * - 自适应背景颜色
                 * - 设置微信背景色为深色
                 *
                 * 在系统执行 createIconDrawable() 时，会将 Splash Screen 图标传入到此函数的第一个参数，
                 * 在此处就可以通过 Palette API 自动从图标中选取颜色或手动指定颜色，并将背景颜色设置到 mThemeColor
                 * 成员变量中，以备后续调用
                 */
                injectMember {
                    method {
                        name = "createIconDrawable"
                        when (Build.VERSION.SDK_INT) {
                            33 -> param(DrawableClass, BooleanType, BooleanType)
                            else -> param(DrawableClass, BooleanType)
                        }
                    }
                    beforeHook {
                        if (prefs.get(DataConst.REMOVE_BG_COLOR)) return@beforeHook

                        val enableChangeBgColor = prefs.get(DataConst.ENABLE_CHANG_BG_COLOR)
                        val ignoreDarkMode = prefs.get(DataConst.IGNORE_DARK_MODE)
                        val colorMode = prefs.get(DataConst.BG_COLOR_MODE)
                        val isDarkMode = appContext.resources
                            .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                        val pkgName =
                            instance.getField("mActivityInfo").cast<ActivityInfo>()?.packageName!!
                        val isInExceptList =
                            pkgName in prefs.get(DataConst.BG_EXCEPT_LIST) || isExcept(pkgName)

                        when {
                            // 设置微信背景色为深色
                            pkgName == "com.tencent.mm" && prefs.get(DataConst.INDEPENDENT_COLOR_WECHAT) -> {
                                instance.setField("mThemeColor", Color.parseColor("#010C15"))
                                printLog("10. createIconDrawable(): set WeChat background color")
                            }

                            // 自适应背景色
                            enableChangeBgColor && !isInExceptList && (!isDarkMode || ignoreDarkMode) -> {
                                val drawable = args(0).cast<Drawable>()
                                val color = Utils.getBgColor(
                                    Utils.drawable2Bitmap(drawable!!, 100)!!,
                                    when (colorMode) {
                                        1 -> false
                                        2 -> !isDarkMode
                                        else -> true
                                    }
                                )
                                instance.setField("mThemeColor", color)
                                printLog("10. createIconDrawable(): set adaptive background color")
                            }

                            else -> printLog("10. createIconDrawable(): Not set background color")
                        }

                    }
                }

            }

        /**
         * 图标处理
         *
         * 此处实现功能：
         * - 替换获取图标方式
         * - 使用图标包
         * - 绘制图标圆角
         *
         * 此处在 com.android.wm.shell.startingsurface.SplashscreenContentDrawer
         *   .$StartingWindowViewBuilder.build() 中被调用
         */
        findClass("com.android.launcher3.icons.IconProvider").hook {
            injectMember {
                method {
                    name = "getIcon"
                    param(ActivityInfoClass, IntType)
                }
                afterHook {
                    val enableReplaceIcon = prefs.get(DataConst.ENABLE_REPLACE_ICON)
                    val shrinkIconType = prefs.get(DataConst.SHRINK_ICON)
                    val iconPackPackageName = prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)
                    val isDrawIconRoundCorner = prefs.get(DataConst.ENABLE_DRAW_ROUND_CORNER)
                    val pkgName = args(0).cast<ActivityInfo>()?.packageName!!
                    val pkgActivity = args(0).cast<ActivityInfo>()?.targetActivity
                    val iconSize = args(1).cast<Int>()!!

                    if (isExcept(pkgName)) return@afterHook

                    /**
                     * 替换获取图标方式
                     *
                     * 使用 Context.packageManager.getApplicationIcon() 的方式获取图标
                     */
                    var drawable = if (enableReplaceIcon) {
                        when {
                            pkgName == "com.android.contacts" && pkgActivity == "com.android.contacts.activities.PeopleActivity" ->
                                appContext.packageManager.getActivityIcon(
                                    ComponentName(
                                        "com.android.contacts",
                                        "com.android.contacts.activities.TwelveKeyDialer"
                                    )
                                )
                            pkgName == "com.android.settings" && pkgActivity == "com.android.settings.BackgroundApplicationsManager" ->
                                appContext.packageManager.getApplicationIcon("com.android.settings")
                            else -> pkgName.let { appContext.packageManager.getApplicationIcon(it) }
                        }
                    } else {
                        result<Drawable>()
                    }
                    printLog("6. getIcon(): ${if (enableReplaceIcon) "" else "Not"} replace way of getting icon")

                    // 使用图标包
                    if (iconPackPackageName != "None") {
                        when {
                            pkgName == "com.android.contacts" && pkgActivity == "com.android.contacts.activities.PeopleActivity" ->
                                iconPackManager.getIconByComponentName("ComponentInfo{com.android.contacts/com.android.contacts.activities.TwelveKeyDialer}")
                            else -> iconPackManager.getIconByPackageName(pkgName)
                        }?.let { drawable = it }
                    }
                    printLog("7. getIcon(): ${if (iconPackPackageName != "None") "" else "Not"} use Icon Pack")


                    /**
                     * 绘制图标圆角
                     * 缩小图标
                     */
                    Utils.roundBitmapByShader(
                        drawable?.let { Utils.drawable2Bitmap(it, iconSize) },
                        if (isDrawIconRoundCorner) RoundDegree.RoundCorner else RoundDegree.NotDrawRoundCorner,
                        when (shrinkIconType) {
                            0 -> 0               // 不缩小图标
                            1 -> iconSize / 4    // 仅缩小分辨率较低的图标
                            else -> 5000         // 缩小全部图标
                        }
                    )?.let { drawable = BitmapDrawable(appContext.resources, it) }
                    printLog("8. getIcon(): ${if (isDrawIconRoundCorner) "" else "Not"} draw round corner; shrink icon type is $shrinkIconType")

                    result = drawable
                }
            }
        }

        /**
         * 设置图标不缩小
         *
         * 此处在 com.android.wm.shell.startingsurface.SplashscreenContentDrawer
         *   .$StartingWindowViewBuilder.build() 中被调用
         *
         * createScaledBitmapWithoutShadow() 的第二个参数在 Android 源代码中被称为 shrinkNonAdaptiveIcons
         * 若将此参数设置为 true 时，在 MIUI 中的非自适应图标将会错位显示
         * 若将此参数设置为 false 时，非自适应图标不会缩小而且显示较模糊，我们可以在后续方法中将图标缩小绘制
         *
         * *** 为适配 Android 13 此块作废，使用底部新 Hook 方法
         */
//        findClass("com.android.launcher3.icons.BaseIconFactory").hook {
//            injectMember {
//                method {
//                    name = "createScaledBitmapWithoutShadow"
//                    param(DrawableClass, BooleanType)
//                }
//                beforeHook {
//                    args(1).set(false)
//                    printLog("9. BaseIconFactory(): set shrinkNonAdaptiveIcons false")
//                }
//            }
//        }

        /**
         * 忽略深色模式
         *
         * 类原始位置在 framework.jar 中
         *
         * 此处在 com.android.wm.shell.startingsurface.SplashscreenContentDrawer
         *   .$StartingWindowViewBuilder.fillViewWithIcon() 中被调用
         */
        if (prefs.get(DataConst.IGNORE_DARK_MODE) && isMIUI)
            findClass("android.window.SplashScreenView\$Builder").hook {
                injectMember {
                    method {
                        name = "isStaringWindowUnderNightMode"
                        emptyParam()
                    }
                    beforeHook {
                        resultFalse()
                        printLog(
                            "11. isStaringWindowUnderNightMode(): ignore dark mode"
                        )
                    }
                }
            }

        /**
         * 移除截图背景
         *
         * 类原始位置在 miui-framework.jar 中
         *
         * 此处在 com.android.wm.shell.startingsurface.SplashscreenContentDrawer
         *   .$StartingWindowViewBuilder.fillViewWithIcon() 中被调用
         *
         * 原理为干预 fillViewWithIcon() 中的 if 判断，使其将启动器判断为不是 MIUI 桌面
         */
        if (prefs.get(DataConst.REMOVE_BG_DRAWABLE))
            findClass("android.app.TaskSnapshotHelperImpl").hook {
                injectMember {
                    method {
                        name = "isMiuiHome"
                        param(StringType)
                    }
                    beforeHook {
                        resultFalse()
                        printLog(
                            "12. isMiuiHome(): set isMiuiHome() false"
                        )
                    }
                }
            }
    }

    /**
     * 设置图标不缩小
     *
     * 兼容 Android 13
     */
    fun onXPEvent(lpparam: XC_LoadPackage.LoadPackageParam) {
        val hookClass = XposedHelpers.findClass(
            "com.android.launcher3.icons.BaseIconFactory",
            lpparam.classLoader
        )

        fun findAndHookFirstMethod(
            methodName: String,
            callback: XC_MethodHook
        ): XC_MethodHook.Unhook? {
            for (method in hookClass.declaredMethods) {
                if (method.name == methodName) return XposedBridge.hookMethod(method, callback)
            }
            loggerE(msg = "Cannot find method [$methodName] in class [com.android.launcher3.icons.BaseIconFactory]")
            return null
        }

        findAndHookFirstMethod("createScaledBitmapWithoutShadow", object : XC_MethodHook() {
            var hook: Unhook? = null
            override fun beforeHookedMethod(param: MethodHookParam?) {

                hook = findAndHookFirstMethod(
                    "normalizeAndWrapToAdaptiveIcon",
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            param!!.args[1] = false
                            printLog("9. BaseIconFactory(): set shrinkNonAdaptiveIcons false")
                        }
                    })

            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                hook?.unhook()
            }
        })
    }

}