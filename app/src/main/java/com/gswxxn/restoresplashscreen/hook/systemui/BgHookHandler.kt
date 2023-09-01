package com.gswxxn.restoresplashscreen.hook.systemui

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.toArgb
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentPackageName
import com.gswxxn.restoresplashscreen.utils.GraphicUtils
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getMapPrefs
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.gswxxn.restoresplashscreen.wrapper.SplashScreenViewBuilderWrapper
import com.highcapable.yukihookapi.hook.factory.current

/**
 * 此对象用于处理 背景 Hook
 */
object BgHookHandler: BaseHookHandler() {
    private var mTmpAttrsInstance: Any? = null

    /**
     * 重置当前应用的属性
     */
    fun resetCache() {
        mTmpAttrsInstance = null
    }

    /** 开始 Hook */
    override fun onHook() {
        NewSystemUIHooker.Members.getBGColorFromCache?.addAfterHook {
            mTmpAttrsInstance = instance.current().field { name = "mTmpAttrs" }.any()
        }
        NewSystemUIHooker.Members.build_SplashScreenViewBuilder?.addBeforeHook {
            val isRemoveBGColor = prefs.get(DataConst.REMOVE_BG_COLOR)
            val builder = SplashScreenViewBuilderWrapper.getInstance(instance)

            // 设置背景颜色
            getColor()?.let { builder.setBackgroundColor(it) }

            /**
             * 移除背景颜色
             */
            if (isRemoveBGColor) builder.setBackgroundColor(Color.parseColor("#F5F5F5"))
            printLog("SplashScreenViewBuilder(): ${if (isRemoveBGColor) "" else "Not"} remove BG Color")
        }
    }

    /**
     * 此处实现功能：
     * - 替换背景颜色
     * - 单独配置应用背景颜色
     */
    private fun getColor(): Int? {
        val isDarkMode = (appContext!!.resources
            .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        val bgColorMode = prefs.get(DataConst.BG_COLOR_MODE)
        val bgColorType = prefs.get(DataConst.CHANG_BG_COLOR_TYPE)
        val isInBGExceptList = currentPackageName in prefs.get(DataConst.BG_EXCEPT_LIST)
        val ignoreDarkMode = prefs.get(DataConst.IGNORE_DARK_MODE) || !isMIUI
        val individualBgColorAppMap = getMapPrefs(
            if (!isDarkMode) DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
            else DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK)
        val skipAppWithBgColor = bgColorType != 0 &&
                currentPackageName !in individualBgColorAppMap.keys &&
                prefs.get(DataConst.SKIP_APP_WITH_BG_COLOR) &&
                mTmpAttrsInstance!!.current().field { name = "mWindowBgColor" }.int() != 0


        if (prefs.get(DataConst.REMOVE_BG_COLOR)) {
            printLog("SplashScreenViewBuilder(): skip set bg color cuz REMOVE_BG_COLOR is on")
            return null
        }
        if (skipAppWithBgColor) {
            printLog("SplashScreenViewBuilder(): skip set bg color cuz app has been set bg color")
            return null
        }

        return if (currentPackageName in individualBgColorAppMap.keys) {
            printLog("SplashScreenViewBuilder(): set individual background color, ${individualBgColorAppMap[currentPackageName]}")
            Color.parseColor(individualBgColorAppMap[currentPackageName])
        } else if (!isInBGExceptList && (!isDarkMode || ignoreDarkMode))
            when (bgColorType) {

                // 从图标取色
                1 -> {
                    printLog("SplashScreenViewBuilder(): get adaptive background color")
                    IconHookHandler.currentIconDominantColor ?:
                    mTmpAttrsInstance!!.current().field { name = "mSplashScreenIcon" }.cast<Drawable>()?.let { drawable ->
                        val bitmap = GraphicUtils.drawable2Bitmap(drawable, 100)
                        val colorMode = prefs.get(DataConst.BG_COLOR_MODE)
                        GraphicUtils.getBgColor(
                            bitmap,
                            when (colorMode) {
                                1 -> false
                                2 -> !isDarkMode
                                else -> true
                            }
                        )
                    }
                }

                // 从壁纸取色
                2 -> {
                    printLog("SplashScreenViewBuilder(): get monet background color")
                    when (bgColorMode) {
                        0 -> dynamicLightColorScheme(appContext!!).primaryContainer.toArgb()
                        1 -> dynamicDarkColorScheme(appContext!!).primaryContainer.toArgb()
                        else -> if (!isDarkMode)
                            dynamicLightColorScheme(appContext!!).primaryContainer.toArgb()
                        else
                            dynamicDarkColorScheme(appContext!!).primaryContainer.toArgb()
                    }
                }

                // 自定义颜色
                3 -> {
                    printLog("SplashScreenViewBuilder(): set overall background color")
                    Color.parseColor(prefs.get(if (isDarkMode) DataConst.OVERALL_BG_COLOR_NIGHT else DataConst.OVERALL_BG_COLOR))
                }

                else -> {
                    printLog("SplashScreenViewBuilder(): not replace background color"); null
                }
            } else {
                printLog("SplashScreenViewBuilder(): skip set bg color cuz app in except list"); null
            }
    }
}