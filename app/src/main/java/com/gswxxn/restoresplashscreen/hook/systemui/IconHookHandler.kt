package com.gswxxn.restoresplashscreen.hook.systemui

import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.RoundDegree
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker.toClass
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentActivity
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentPackageName
import com.gswxxn.restoresplashscreen.utils.GraphicUtils
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.LargeIconsHelper
import com.gswxxn.restoresplashscreen.utils.YukiHelper.atLeastMIUI14
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field

/**
 * 此对象用于处理图标 Hook
 */
object IconHookHandler: BaseHookHandler() {
    var currentIconDominantColor: Int? = null
    private var currentActualBitmapIconSize = 0
    private var currentIsNeedShrinkIcon = false
    private var currentUseMIUILagerIcon = false
    private val iconPackManager by lazy { IconPackManager(appContext!!, prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)) }
    private val largeIcons by lazy { LargeIconsHelper(appContext!!) }

    /**
     * 重置当前应用的属性
     */
    fun resetCache() {
        currentIconDominantColor = null
        currentActualBitmapIconSize = 0
        currentIsNeedShrinkIcon = false
        currentUseMIUILagerIcon = false
    }

    /** 开始 Hook */
    override fun onHook() {
        NewSystemUIHooker.Members.getWindowAttrs?.addAfterHook {

            //忽略应用主动设置的图标
            val isDefaultStyle = prefs.get(DataConst.ENABLE_DEFAULT_STYLE) &&
                    if (prefs.get(DataConst.IS_DEFAULT_STYLE_LIST_EXCEPTION_MODE))
                        currentPackageName !in prefs.get(DataConst.DEFAULT_STYLE_LIST)
                    else
                        currentPackageName in prefs.get(DataConst.DEFAULT_STYLE_LIST)
            if (isDefaultStyle) {
                args[1]!!.current().field { name = "mSplashScreenIcon" }.set(null)
            }
            printLog("getWindowAttrs(): ${if (isDefaultStyle) "" else "Not"} ignore set icon")
        }

        // 处理 Drawable 图标
        NewSystemUIHooker.Members.getIcon_IconProvider?.addAfterHook {
            val currentIconSize = getIconSize(result as Drawable)
            result = processIconDrawable(result as Drawable, currentIconSize)
        }

        // 执行缩小图标
        NewSystemUIHooker.Members.createIconDrawable?.addBeforeHook {
            if (currentUseMIUILagerIcon) {
                val mFinalIconSize = instance.current().field { name = "mFinalIconSize" }
                mFinalIconSize.set((mFinalIconSize.int() * 1.35).toInt())
                printLog("createIconDrawable(): execute enlarge icon")
            } else if (currentIsNeedShrinkIcon) {
                val mFinalIconSize = instance.current().field { name = "mFinalIconSize" }
                mFinalIconSize.set((mFinalIconSize.int() / 1.5).toInt())
                printLog("createIconDrawable(): execute shrink icon")
            }
        }

        // 不使用自带的图标缩放, 防止在 MIUI 上出现图标白边及图标错位
        NewSystemUIHooker.Members.normalizeAndWrapToAdaptiveIcon?.addBeforeHook {
            val scale = instance.current()
                .method { name = "getNormalizer"; superClass() }.call()!!.current()
                .method { name = "getScale"; paramCount(4); superClass() }
                .invoke<Float>(args.first { it is Drawable }, args.first { it is RectF }, null, null)!!

            args(args.indexOfFirst { it is FloatArray }).cast<FloatArray>()!![0] = scale
            printLog("normalizeAndWrapToAdaptiveIcon(): avoid shrink icon by system ui")
            result = args.first { it is Drawable } as Drawable
        }
    }

    /**
     * 处理图标 Drawable 的方法
     *
     * 实现功能:
     * - 替换获取图标方式
     * - 不显示 Splash Screen 图标
     * - 使用图标包
     * - 绘制图标圆角
     *
     * @param drawable     要处理的 Drawable 对象
     * @param iconSize     图标的大小
     * @return 处理后的 Drawable 对象
     */
    private fun processIconDrawable(drawable: Drawable, iconSize: Int): Drawable {
        val enableReplaceIcon = prefs.get(DataConst.ENABLE_REPLACE_ICON)
        val shrinkIconType = prefs.get(DataConst.SHRINK_ICON)
        val colorMode = prefs.get(DataConst.BG_COLOR_MODE)
        val iconPackPackageName = prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)
        val isDrawIconRoundCorner = prefs.get(DataConst.ENABLE_DRAW_ROUND_CORNER)
        val isDarkMode = (appContext!!.resources
            .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

        val isHideSplashScreenIcon = prefs.get(DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON) &&
                if (prefs.get(DataConst.IS_HIDE_SPLASH_SCREEN_ICON_EXCEPTION_MODE))
                    currentPackageName !in prefs.get(DataConst.HIDE_SPLASH_SCREEN_ICON_LIST)
                else
                    currentPackageName in prefs.get(DataConst.HIDE_SPLASH_SCREEN_ICON_LIST)
        var iconDrawable: Drawable = drawable

        // 不显示 Splash Screen 图标
        if (isHideSplashScreenIcon) {
            printLog("getIcon(): draw TRANSPARENT icon", "")
            return ColorDrawable(Color.TRANSPARENT)
        }

        if (atLeastMIUI14) {
            // MIUI 大图标
            largeIcons.getOriginLargeIconDrawable(currentPackageName, "2x2")?.let {
                iconDrawable = it
                currentUseMIUILagerIcon = true
                printLog("getIcon(): use MIUI Large Icon")
            }
        }

        if (!currentUseMIUILagerIcon) {
            if (iconPackPackageName != "None") {
                // 使用图标包
                iconDrawable = when {
                    currentPackageName == "com.android.contacts" && currentActivity == "com.android.contacts.activities.PeopleActivity" ->
                        iconPackManager.getIconByComponentName("ComponentInfo{com.android.contacts/com.android.contacts.activities.TwelveKeyDialer}")

                    else -> iconPackManager.getIconByPackageName(currentPackageName)
                } ?: iconDrawable
                printLog("getIcon(): use Icon Pack")
            } else if (enableReplaceIcon || currentPackageName == "com.android.settings") {
                /**
                 * 替换获取图标方式
                 *
                 * 使用 Context.packageManager.getApplicationIcon() 的方式获取图标
                 */
                iconDrawable = when {
                    currentPackageName == "com.android.contacts" && currentActivity == "com.android.contacts.activities.PeopleActivity" ->
                        appContext!!.packageManager.getActivityIcon(
                            ComponentName("com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer")
                        )

                    currentPackageName == "com.android.settings" && currentActivity == "com.android.settings.BackgroundApplicationsManager" ->
                        appContext!!.packageManager.getApplicationIcon("com.android.settings")

                    isMIUI && largeIcons.isSupportMIUIModeIcon && currentPackageName != "com.android.fileexplorer" -> { // 在 MIUI 上优先获取完美图标
                        largeIcons.getFancyIconDrawable(currentPackageName) ?:
                        appContext!!.packageManager.getApplicationIcon(currentPackageName)
                    }

                    else -> appContext!!.packageManager.getApplicationIcon(currentPackageName)
                }
                printLog("getIcon(): replace way of getting icon")
            }
        }

        val bitmap = GraphicUtils.drawable2Bitmap(iconDrawable, if (currentUseMIUILagerIcon) iconSize * 2 else iconSize)

        // 判断是否需要缩小图标
        when (shrinkIconType) {
            0 -> currentIsNeedShrinkIcon = false                                         // 不缩小图标
            1 -> currentIsNeedShrinkIcon =                                               // 仅缩小分辨率较低的图标
                if (iconDrawable !is AdaptiveIconDrawable) iconDrawable.intrinsicWidth < iconSize / 1.5 else false
            2 -> currentIsNeedShrinkIcon = true                                          // 缩小全部图标
        }
        printLog("getIcon(): currentIsNeedShrinkIcon: $currentIsNeedShrinkIcon")

        // 绘制图标圆角
        if (currentUseMIUILagerIcon) {
            iconDrawable = BitmapDrawable(appResources, GraphicUtils.roundBitmapByShader(bitmap, RoundDegree.MIUIWidget))
            printLog("getIcon(): draw MIUI large icon round corner")
        } else if (isDrawIconRoundCorner) {
            iconDrawable = BitmapDrawable(appResources, GraphicUtils.roundBitmapByShader(bitmap, RoundDegree.RoundCorner))
            printLog("getIcon(): draw icon round corner")
        }

        // 获取图标颜色
        currentIconDominantColor = GraphicUtils.getBgColor(bitmap,
            when (colorMode) {
                1 -> false
                2 -> !isDarkMode
                else -> true
            }
        )
        return iconDrawable
    }

    /**
     * 获取 SplashScreen 图标的大小。
     *
     * @param drawable 要获取大小的 Drawable 对象
     * @return 图标的大小
     */
    private fun getIconSize(drawable: Drawable): Int {
        val mIconSize = appResources!!.getDimensionPixelSize(
            "com.android.internal.R\$dimen".toClass().field { name = "starting_surface_icon_size" }.get().int()
        )

        return if (drawable is AdaptiveIconDrawable) (mIconSize * 1.2 + 0.5).toInt()
        else mIconSize
    }
}