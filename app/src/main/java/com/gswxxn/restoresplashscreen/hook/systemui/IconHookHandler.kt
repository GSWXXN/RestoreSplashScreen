package com.gswxxn.restoresplashscreen.hook.systemui

import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Outline
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentActivity
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentPackageName
import com.gswxxn.restoresplashscreen.utils.GraphicUtils
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.MIUIIconsHelper
import com.gswxxn.restoresplashscreen.utils.YukiHelper.atLeastMIUI14
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getDevPrefs
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isColorOS
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.toClass

/**
 * 此对象用于处理图标 Hook
 */
object IconHookHandler : BaseHookHandler() {
    var currentIconDominantColor: Int? = null
    private var currentIsNeedShrinkIcon = false

    /**
     * currentUseBigMIUILagerIcon 有三种状态:
     *
     * null: 当前没有使用 MIUI 大图标
     *
     * false: 当前使用 1x2 或 2x1 或 2x2 的图标
     *
     * true: 当前使用 1x1 的图标
     *
     */
    private var currentUseBigMIUILagerIcon: Boolean? = null
    private var currentIconDrawable: Drawable? = null
    private val iconPackManager by lazy { IconPackManager(appContext!!, prefs.get(DataConst.ICON_PACK_PACKAGE_NAME)) }
    private val miuiIcons by lazy { MIUIIconsHelper(appContext!!) }

    /**
     * 重置当前应用的属性
     */
    fun resetCache() {
        currentIconDominantColor = null
        currentIsNeedShrinkIcon = false
        currentUseBigMIUILagerIcon = null
        currentIconDrawable = null
    }

    /** 开始 Hook */
    override fun onHook() {
        NewSystemUIHooker.Members.getWindowAttrs.addAfterHook {

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
        NewSystemUIHooker.Members.getIcon_IconProvider.addAfterHook {
            result = processIconDrawable(result as Drawable)
        }

        // 执行缩小图标
        NewSystemUIHooker.Members.createIconDrawable.addBeforeHook {
            if (currentUseBigMIUILagerIcon == true) {
                val mFinalIconSize = instance.current().field { name = "mFinalIconSize" }
                mFinalIconSize.set((mFinalIconSize.int() * 1.35).toInt())
                printLog("createIconDrawable(): execute enlarge icon")
            } else if (currentIsNeedShrinkIcon) {
                val mFinalIconSize = instance.current().field { name = "mFinalIconSize" }
                mFinalIconSize.set((mFinalIconSize.int() / 1.5).toInt())
                printLog("createIconDrawable(): execute shrink icon")
            }
        }

        // 创建模糊背景 View
        NewSystemUIHooker.Members.build_SplashScreenViewBuilder.addAfterHook {
            if (prefs.get(DataConst.SHRINK_ICON) == 0 || !prefs.get(DataConst.ENABLE_ADD_ICON_BLUR_BG)) {
                printLog("build_SplashScreenViewBuilder(): not enable add icon blur bg")
                return@addAfterHook
            } else if (!currentIsNeedShrinkIcon || currentUseBigMIUILagerIcon == true) {
                printLog("build_SplashScreenViewBuilder(): not need add icon blur bg")
                return@addAfterHook
            }

            val splashScreenView = result<FrameLayout>()!!
            val iconView = splashScreenView.current().field { name = "mIconView" }.cast<ImageView>()
                ?: return@addAfterHook

            val iconSize = (appResources!!.getDimensionPixelSize(
                "com.android.internal.R\$dimen".toClass().field { name = "starting_surface_icon_size" }.get().int()
            ) / 1.5).toInt()
            val bgIconSize = iconSize * 4

            val blurBgDrawable = GraphicUtils.createShadowedIcon(
                appContext,
                currentIconDrawable,
                iconSize,
                iconSize * 4,
                iconSize * getDevPrefs(DataConst.DEV_ICON_ROUND_CORNER_RATE) / 100f
            ) ?: return@addAfterHook

            val iconBlurBGView = ImageView(appContext).apply {
                setImageDrawable(blurBgDrawable)
                setRenderEffect(RenderEffect.createBlurEffect(bgIconSize.toFloat() / 10, bgIconSize.toFloat() / 10, Shader.TileMode.DECAL))
                z = -1f
            }

            val layoutParams = FrameLayout.LayoutParams(bgIconSize, bgIconSize)
                .apply { gravity = Gravity.CENTER }

            splashScreenView.addView(iconBlurBGView, layoutParams)
            iconView.alpha = 0.9f
            printLog("build_SplashScreenViewBuilder(): add icon blur bg")
        }

        // 绘制圆角
        NewSystemUIHooker.Members.build_SplashScreenViewBuilder.addAfterHook {
            val splashScreenView = result<FrameLayout>()!!
            val iconView = splashScreenView.current().field { name = "mIconView" }.cast<ImageView>()
                ?: return@addAfterHook
            val iconSize = instance.current().field { name = "mIconSize" }.int()
            val iconDrawable = instance.current().field { name = "mIconDrawable" }.cast<Drawable>()
                ?: return@addAfterHook
            val isNeedDrawRoundCorner = prefs.get(DataConst.ENABLE_DRAW_ROUND_CORNER) && // 用户配置
                    "android.window.SplashScreenView\$IconAnimateListener".toClass() !in iconDrawable.javaClass.interfaces && // 不为动态图标绘制圆角
                    iconSize != 0 && // 如果没有图标 则不绘制圆角
                    currentUseBigMIUILagerIcon == null // 如果当前使用 MIUI 大图标, 则不绘制圆角

            if (!isNeedDrawRoundCorner) {
                return@addAfterHook
            }

            // 为 view 添加轮廓
            iconView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val border = dp2px(appContext!!, 1.5f)
                    outline.setRoundRect(
                        border, border, view.width - border, view.height - border,
                        iconSize.toFloat() * getDevPrefs(DataConst.DEV_ICON_ROUND_CORNER_RATE) / 100
                    )
                }
            }
            iconView.clipToOutline = true // 启用轮廓剪裁
            printLog("build_SplashScreenViewBuilder(): draw icon round corner")
        }

        // 不使用自带的图标缩放, 防止在 MIUI 上出现图标白边及图标错位
        NewSystemUIHooker.Members.normalizeAndWrapToAdaptiveIcon.addBeforeHook {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val drawable = args.first { it is Drawable } as Drawable
                if (drawable !is AdaptiveIconDrawable) {
                    printLog("normalizeAndWrapToAdaptiveIcon(): avoid shrink icon by system ui")
                    val mWrapperBackgroundColor = this.instance.current().field { name = "mWrapperBackgroundColor" }.int()
                    val scaleDrawable = ScaleDrawable(drawable, Gravity.CENTER, 0.296f, 0.296f) // 0.296f 刚好看不到黑边
                    scaleDrawable.level = 1
                    val adaptiveIconDrawable = AdaptiveIconDrawable(ColorDrawable(mWrapperBackgroundColor), scaleDrawable)
                    result = adaptiveIconDrawable
                }
            } else {
                val scale = instance.current()
                    .method { name = "getNormalizer"; superClass() }.call()!!.current()
                    .method { name = "getScale"; paramCount(4); superClass() }
                    .invoke<Float>(args.first { it is Drawable }, args.first { it is RectF }, null, null)!!
                args(args.indexOfFirst { it is FloatArray }).cast<FloatArray>()!![0] = scale
                printLog("normalizeAndWrapToAdaptiveIcon(): avoid shrink icon by system ui")
                result = args.first { it is Drawable } as Drawable
            }
        }
        NewSystemUIHooker.Members.createIconBitmap_BaseIconFactory.addBeforeHook {
            args(0).cast<Drawable>()?.let { drawable ->
                printLog("createIconBitmap_BaseIconFactory(): avoid shrink icon by system ui")
                result = GraphicUtils.drawable2Bitmap(drawable, getIconSize(drawable))
            }
        }

        // 强制使图标背景被判断为复杂, 以防止安卓抹去简单的图标背景
        NewSystemUIHooker.Members.iconColor_constructor.addAfterHook {
            instance.current().field { name = "mIsBgComplex" }.set(true)
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
     * @param oriDrawable  原始 Drawable 对象
     * @return 处理后的 Drawable 对象
     */
    fun processIconDrawable(oriDrawable: Drawable): Drawable {
        val shrinkIconType = prefs.get(DataConst.SHRINK_ICON)
        val colorMode = prefs.get(DataConst.BG_COLOR_MODE)
        val isDarkMode = (appContext!!.resources
            .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

        val isHideSplashScreenIcon = prefs.get(DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON) &&
                if (prefs.get(DataConst.IS_HIDE_SPLASH_SCREEN_ICON_EXCEPTION_MODE))
                    currentPackageName !in prefs.get(DataConst.HIDE_SPLASH_SCREEN_ICON_LIST)
                else
                    currentPackageName in prefs.get(DataConst.HIDE_SPLASH_SCREEN_ICON_LIST)

        val iconSize = getIconSize(oriDrawable)

        // 不显示 Splash Screen 图标
        if (isHideSplashScreenIcon) {
            printLog("getIcon(): draw TRANSPARENT icon", "")
            return ColorDrawable(Color.TRANSPARENT)
        }

        // 检索图标优先级: 使用 MIUI 大图标 -> 使用图标包 -> 替换获取图标方式 -> 原始图标
        val iconDrawable = getMIUILargeIcon() ?: getIconFromIconPack() ?: replaceWayOfGetIcons() ?: oriDrawable
        val bitmap = GraphicUtils.drawable2Bitmap(iconDrawable, if (currentUseBigMIUILagerIcon == true) iconSize * 2 else iconSize)

        // 判断是否需要缩小图标
        when (shrinkIconType) {
            0 -> currentIsNeedShrinkIcon = false                                         // 不缩小图标
            1 -> currentIsNeedShrinkIcon =                                               // 仅缩小分辨率较低的图标
                if (iconDrawable !is AdaptiveIconDrawable) iconDrawable.intrinsicWidth < iconSize / 1.5 else false

            2 -> currentIsNeedShrinkIcon = true                                          // 缩小全部图标
        }
        printLog("getIcon(): currentIsNeedShrinkIcon: $currentIsNeedShrinkIcon")

        // 获取图标颜色
        currentIconDominantColor = GraphicUtils.getBgColor(
            bitmap,
            when (colorMode) {
                1 -> false
                2 -> !isDarkMode
                else -> true
            }
        )

        currentIconDrawable = iconDrawable
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

    /** 使用 MIUI 大图标 */
    private fun getMIUILargeIcon(): Drawable? {
        if (atLeastMIUI14 && prefs.get(DataConst.ENABLE_USE_MIUI_LARGE_ICON) && miuiIcons.hasLargeIcon(currentPackageName)) {
            printLog("getIcon(): use MIUI Large Icon")
            return miuiIcons.getLargeIconDrawable(currentPackageName)?.let {
                val largeIconSize = miuiIcons.getLargeIconSize(currentPackageName)
                printLog("getIcon(): large icon size: $largeIconSize")
                currentUseBigMIUILagerIcon =
                    if (largeIconSize in arrayOf("1x1", "1x2", "2x1", "2x2")) largeIconSize != "1x1" else null

                // 转换成正方形图标
                if (largeIconSize in arrayOf("1x2", "2x1")) {
                    GraphicUtils.convertToSquareDrawable(it, appResources!!)
                } else {
                    it
                }
            }
        }
        return null
    }

    /** 使用图标包 */
    private fun getIconFromIconPack(): Drawable? {
        if (prefs.get(DataConst.ICON_PACK_PACKAGE_NAME) != "None") {
            printLog("getIcon(): use Icon Pack")
            return when {
                currentPackageName == "com.android.contacts" && currentActivity == "com.android.contacts.activities.PeopleActivity" ->
                    if (isColorOS) iconPackManager.getIconByComponentName("ComponentInfo{com.android.contacts/com.android.contacts.DialtactsActivityAlias}")
                    else iconPackManager.getIconByComponentName("ComponentInfo{com.android.contacts/com.android.contacts.activities.TwelveKeyDialer}")

                else -> iconPackManager.getIconByPackageName(currentPackageName)
            }
        }
        return null
    }

    /**
     * 替换获取图标方式
     *
     * 使用 Context.packageManager.getApplicationIcon() 的方式获取图标
     */
    private fun replaceWayOfGetIcons(): Drawable? {
        if (prefs.get(DataConst.ENABLE_REPLACE_ICON) || currentPackageName == "com.android.settings") {
            printLog("getIcon(): replace way of getting icon")
            return when {
                currentPackageName == "com.android.contacts" && currentActivity == "com.android.contacts.activities.PeopleActivity" ->
                    if (isColorOS) {
                        appContext!!.packageManager.getActivityIcon(
                            ComponentName("com.android.contacts", "com.android.contacts.DialtactsActivityAlias")
                        )
                    } else appContext!!.packageManager.getActivityIcon(
                        ComponentName("com.android.contacts", "com.android.contacts.activities.TwelveKeyDialer")
                    )

                currentPackageName == "com.android.settings" && currentActivity == "com.android.settings.BackgroundApplicationsManager" ->
                    appContext!!.packageManager.getApplicationIcon("com.android.settings")

//                isMIUI && miuiIcons.isSupportMIUIModeIcon && currentPackageName != "com.android.fileexplorer" -> { // 在 MIUI 上优先获取完美图标
//                    miuiIcons.getFancyIconDrawable(currentPackageName) ?:
//                    appContext!!.packageManager.getApplicationIcon(currentPackageName)
//                }

                else -> appContext!!.packageManager.getApplicationIcon(currentPackageName)
            }
        }
        return null
    }
}