package com.gswxxn.restoresplashscreen.hook

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.Utils
import com.gswxxn.restoresplashscreen.utils.Utils.getField
import com.gswxxn.restoresplashscreen.utils.Utils.setField
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.type.android.ActivityInfoClass
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringType

class SystemUIHooker : YukiBaseHooker() {
    private val enableLog = prefs.get(DataConst.ENABLE_LOG)

    private fun printLog(vararg msg: String) {
        if (enableLog) msg.forEach { loggerI(msg = it) }
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
                    val pkgName = args(0).cast<ActivityInfo>()?.packageName
//                    val mIconBgColor = instance.getField("mTmpAttrs").any()!!
//                        .getField("mIconBgColor").int()
                    val list = prefs.get(DataConst.CUSTOM_SCOPE_LIST)
                    val isExceptionMode = prefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
                    val enableCustomScope = prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
                    val isException = enableCustomScope &&
                            ((isExceptionMode && (pkgName in list))
                                    || (!isExceptionMode && pkgName !in list))

                    if (!isException)
                        instance.getField("mTmpAttrs").any()!!.setField("mIconBgColor", 1)

                    printLog(
                        "****** ${pkgName}:",
                        "1. getBGColorFromCache(): ${
                            if (isException) "Except this app" else "Not Except, Set mIconBgColor 1"
                        }"
                    )
                }
            }
        }

        /**
         * 此处实现功能：
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
                        val pkgName = instance.getField("mActivityInfo").cast<ActivityInfo>()?.packageName
                        val isDefaultStyle = prefs.get(DataConst.ENABLE_DEFAULT_STYLE)
                                && pkgName in prefs.get(DataConst.DEFAULT_STYLE_LIST)
                        val isRemoveBrandingImage = prefs.get(DataConst.REMOVE_BRANDING_IMAGE)
                                && pkgName in prefs.get(DataConst.REMOVE_BRANDING_IMAGE_LIST)
                        val mSplashscreenContentDrawer = instance.getField("this\$0").any()!!
                        val mTmpAttrs = mSplashscreenContentDrawer
                            .getField("mTmpAttrs").any()!!

                        // 打印日志
                        printLog("info: build(): mSuggestType is ${instance.getField("mSuggestType").int()}")

                        // 重置因实现自定义作用域而影响到的 mTmpAttrs
                        mSplashscreenContentDrawer.current {
                            method {
                                name = "getWindowAttrs"
                                paramCount(2)
                            }.call(instance.getField("mContext").cast<Context>(), mTmpAttrs)
                        }
                        printLog("2. build(): call getWindowAttrs() to reset mTmpAttrs")

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
                        param(DrawableClass, BooleanType)
                    }
                    beforeHook {
                        val enableChangeBgColor = prefs.get(DataConst.ENABLE_CHANG_BG_COLOR)
                        val ignoreDarkMode = prefs.get(DataConst.IGNORE_DARK_MODE)
                        val isDarkMode = appContext.resources
                            .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                        val pkgName = instance.getField("mActivityInfo").cast<ActivityInfo>()?.packageName
                        val isInExceptList = pkgName in prefs.get(DataConst.BG_EXCEPT_LIST)

                        when {
                            // 设置微信背景色为深色
                            pkgName == "com.tencent.mm" && prefs.get(DataConst.INDEPENDENT_COLOR_WECHAT) -> {
                                instance.setField("mThemeColor", Color.parseColor("#010C15"))
                                printLog("9. createIconDrawable(): set WeChat background color")
                            }

                            // 自适应背景色
                            enableChangeBgColor && !isInExceptList && (!isDarkMode || ignoreDarkMode) -> {
                                val drawable = args(0).cast<Drawable>()
                                val color = Utils.getBgColor(Utils.drawable2Bitmap(drawable!!, 100)!!)
                                instance.setField("mThemeColor", color)
                                printLog("9. createIconDrawable(): set adaptive background color")
                            }

                            else -> printLog("9. createIconDrawable(): Not set background color")
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
                    val enableShrinkIcon = prefs.get(DataConst.ENABLE_SHRINK_ICON)
                    val iconPackPackageName = prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)
                    val pkgName = args(0).cast<ActivityInfo>()?.packageName

                    /**
                     * 替换获取图标方式
                     *
                     * 使用 Context.packageManager.getApplicationIcon() 的方式获取图标
                     */
                    var drawable = if (enableReplaceIcon) {
                        pkgName?.let { appContext.packageManager.getApplicationIcon(it) }!!
                    } else {
                        result<Drawable>()
                    }
                    printLog("5. getIcon(): ${if (enableReplaceIcon) "" else "Not"} replace Icon")

                    // 使用图标包
                    if (iconPackPackageName != "None")
                        IconPackManager(appContext, iconPackPackageName).getIconForPackage(pkgName)
                            ?.let { drawable = it }
                    printLog("6. getIcon(): ${if (iconPackPackageName != "None") "" else "Not"} use Icon Pack")


                    /**
                     * 绘制图标圆角
                     *
                     * - 默认开启，不提供手动设置
                     */
                    result = BitmapDrawable(
                        appContext.resources,
                        Utils.roundBitmapByShader(
                            drawable?.let { Utils.drawable2Bitmap(it, args(1).cast<Int>()!!) },
                            false,
                            enableShrinkIcon
                        )
                    )
                    printLog("7. getIcon(): draw round corner")
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
         */
        findClass("com.android.launcher3.icons.BaseIconFactory").hook {
            injectMember {
                method {
                    name = "createScaledBitmapWithoutShadow"
                    param(DrawableClass, BooleanType)
                }
                beforeHook {
                    args(1).set(false)
                    printLog("8. BaseIconFactory(): set shrinkNonAdaptiveIcons false")
                }
            }
        }

        /**
         * 忽略深色模式
         *
         * 类原始位置在 framework.jar 中
         *
         * 此处在 com.android.wm.shell.startingsurface.SplashscreenContentDrawer
         *   .$StartingWindowViewBuilder.fillViewWithIcon() 中被调用
         */
        findClass("android.window.SplashScreenView\$Builder").hook {
            injectMember {
                method {
                    name = "isStaringWindowUnderNightMode"
                    emptyParam()
                }
                beforeHook {
                    val isIgnoreDarkMode = prefs.get(DataConst.IGNORE_DARK_MODE)
                    if (isIgnoreDarkMode) resultFalse()
                    printLog("10. isStaringWindowUnderNightMode(): " +
                            "${if (isIgnoreDarkMode) "" else "Not"} ignore dark mode")
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
        findClass("android.app.TaskSnapshotHelperImpl").hook {
            injectMember {
                method {
                    name = "isMiuiHome"
                    param(StringType)
                }
                beforeHook {
                    val isRemoveBGDrawable = prefs.get(DataConst.REMOVE_BG_DRAWABLE)
                    if (isRemoveBGDrawable) resultFalse()
                    printLog("11. isMiuiHome(): " +
                            "${if (isRemoveBGDrawable) "" else "Not"} set isMiuiHome() false")
                }
            }
        }
    }
}