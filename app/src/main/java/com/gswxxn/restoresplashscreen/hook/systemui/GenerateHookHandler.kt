package com.gswxxn.restoresplashscreen.hook.systemui

import android.content.pm.ActivityInfo
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.StartingWindowInfo
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getMapPrefs
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.highcapable.yukihookapi.hook.factory.current
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 此对象用于处理 基础设置 和 实验功能 中的 Hook
 */
object GenerateHookHandler: BaseHookHandler() {
    var currentPackageName = ""
    var currentActivity = ""
    var exceptCurrentApp = false
    var isHooking = false

    /**
     * 重置当前应用信息的缓存
     */
    private fun resetCache() {
        currentPackageName = ""
        currentActivity = ""
        isHooking = false
        exceptCurrentApp = false

        IconHookHandler.resetCache()
        BgHookHandler.resetCache()
    }

    /** 开始 Hook */
    override fun onHook() {

        // Hook 起始位置, 获取应用信息
        NewSystemUIHooker.Members.makeSplashScreenContentView.addBeforeHook({ true }) {
            var activityInfo: ActivityInfo?

            if (args[1]!! is ActivityInfo)
                activityInfo = args[1] as ActivityInfo
            else {
                activityInfo = args[1]!!.current().field { name = "targetActivityInfo" }.cast<ActivityInfo>()
                if (activityInfo == null) {
                    activityInfo = args[1]!!.current().field { name = "taskInfo" }.any()!!.current()
                        .field { superClass(); name = "topActivityInfo" }.cast<ActivityInfo>()!!
                }
            }

            isHooking = true
            currentPackageName = activityInfo.packageName
            currentActivity = activityInfo.targetActivity ?: ""
            exceptCurrentApp = isExcept()

            printLog(
                "****** $currentPackageName; $currentActivity:",
                "makeSplashScreenContentView(): ${ if (exceptCurrentApp) "Except" else "Allow"} this app"
            )

            /**
             * 强制开启启动遮罩
             *
             * 直接干预 build() 中的 if 判断
             */
            val forceEnableSplashScreen = prefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN)
            if (forceEnableSplashScreen) {
                if (!exceptCurrentApp) {
                    args(args.indexOfFirst { it is Int }).set(StartingWindowInfo.STARTING_WINDOW_TYPE_SPLASH_SCREEN)
                    printLog("makeSplashScreenContentView(): forceEnableSplashScreen, set mSuggestType to STARTING_WINDOW_TYPE_SPLASH_SCREEN(1)")
                }
            }
        }

        // 遮罩最小持续时间, 也是 Hook 结束位置, 清除缓存的应用信息
        NewSystemUIHooker.Members.removeStartingWindow.addReplaceHook({ true }) {
            if (exceptCurrentApp || !isHooking) callOriginal()
            else when (currentPackageName) {
                "" -> {
                    callOriginal()
                }

                // 单独配置应用最小持续时长
                in prefs.get(DataConst.MIN_DURATION_LIST) -> {
                    val configMap = getMapPrefs(DataConst.MIN_DURATION_CONFIG_MAP)
                    try {
                        val duration = configMap[currentPackageName].toString().toLong()

                        if (duration == 0L) callOriginal()
                        else {
                            printLog("removeStartingWindow(): remove splash screen of $currentPackageName after $duration ms")
                            MainScope().launch {
                                delay(duration)
                                callOriginal()
                            }
                        }

                    } catch (_: NumberFormatException) {
                        printLog("removeStartingWindow(): $currentPackageName: a NumberFormatException is threw, maybe it's MIN_DURATION config is incorrect")
                        callOriginal()
                    }
                }

                // 默认值
                else -> prefs.get(DataConst.MIN_DURATION).let { duration ->
                    if (duration == 0) callOriginal()
                    else {
                        printLog("removeStartingWindow(): remove splash screen of $currentPackageName after $duration ms (default value)")
                        MainScope().launch {
                            delay(duration.toLong())
                            callOriginal()
                        }
                    }
                }
            }

            // 清除缓存的应用信息
            resetCache()
            null
        }
    }

    /**
     * 判断是否应执行Hook操作。
     *
     * @return 是否应执行Hook操作。
     */
    private fun isExcept(): Boolean {
        return if (currentPackageName.isBlank())
            true
        else {
            val list = prefs.get(DataConst.CUSTOM_SCOPE_LIST)
            val isExceptionMode = prefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
            (prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
                    && ((isExceptionMode && (currentPackageName in list))
                    || (!isExceptionMode && currentPackageName !in list)))
        }
    }
}