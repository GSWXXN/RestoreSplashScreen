package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.CommonUtils.isAtLeastT
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getField
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.method

/**
 * Android 系统相关 Hook
 */
object AndroidHooker : YukiBaseHooker() {
    override fun onHook() {

        val activityRecordClass = "com.android.server.wm.ActivityRecord".toClass()

        /**
         * 强制显示遮罩
         *
         * 类原始位置在 services.jar 中
         *
         * 此处在 evaluateStartingWindowTheme() 中被调用，最终将参数传递给 showStartingWindow()
         */
        activityRecordClass.method {
            name = "validateStartingWindowTheme"
            paramCount(3)
        }.hook {
            before {
                val pkgName = args(1).string()
                val isLaunchedFromSystemSurface = instance.current().method {
                    name = "launchedFromSystemSurface"
                    emptyParam()
                }.boolean()
                val isForceShowSS = prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN)
                        && pkgName in prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST)
                        && (!prefs.get(DataConst.REDUCE_SPLASH_SCREEN) || isLaunchedFromSystemSurface)

                if (isForceShowSS) resultTrue()
                printLog("[Android] validateStartingWindowTheme():" +
                        "${if (isForceShowSS) "" else "Not"} force show $pkgName splash screen, " +
                        "isLaunchedFromSystemSurface: $isLaunchedFromSystemSurface")
            }
        }

        // 彻底关闭 Splash Screen
        activityRecordClass.method {
            name = "showStartingWindow"
            paramCount(if (isAtLeastT) 7 else  5)
        }.hook {
            before {
                val currentPkgName = instance.getField<String>("packageName")

                val isDisableSS = prefs.get(DataConst.DISABLE_SPLASH_SCREEN)
                printLog("[Android] addStartingWindow():${if (isDisableSS) "" else "Not"} disable $currentPkgName splash screen")
                if (isDisableSS) {
                    resultNull()
                }
            }
        }

        // 热启动时生成启动遮罩
        activityRecordClass.method {
            name = "getStartingWindowType"
            paramCount(if (isAtLeastT) 7 else  6)
        }.hook {
            before {
                val isHotStartCompatible = prefs.get(DataConst.ENABLE_HOT_START_COMPATIBLE) && args(1).boolean()
                if (isHotStartCompatible) result = 2
                printLog("[Android] getStartingWindowType():${if (isHotStartCompatible) "" else "Not"} set result to 2")
            }
        }
    }
}