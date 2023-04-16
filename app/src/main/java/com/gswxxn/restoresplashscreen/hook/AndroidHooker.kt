package com.gswxxn.restoresplashscreen.hook

import android.os.Build
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getField
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.gswxxn.restoresplashscreen.utils.YukiHelper.register
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.log.loggerE

object AndroidHooker : YukiBaseHooker() {
    override fun onHook() {

        register()

        findClass("com.android.server.wm.ActivityRecord").hook {
            /**
             * 强制显示遮罩
             *
             * 类原始位置在 services.jar 中
             *
             * 此处在 evaluateStartingWindowTheme() 中被调用，最终将参数传递给 showStartingWindow()
             */
            injectMember {
                method {
                    name = "validateStartingWindowTheme"
                    paramCount(3)
                }
                beforeHook {
                    val pkgName = args(1).string()
                    val isLaunchedFromSystemSurface = instance.current().method {
                        name = "isLaunchedFromSystemSurface"
                        emptyParam()
                    }.boolean()
                    val isForceShowSS = prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN) && pkgName in prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST) && isLaunchedFromSystemSurface

                    if (isForceShowSS) resultTrue()
                    printLog("[Android] validateStartingWindowTheme():${if (isForceShowSS) "" else "Not"} force show $pkgName splash screen, isLaunchedFromSystemSurface: $isLaunchedFromSystemSurface")
                   }
            }

            injectMember {
                method {
                    name = "showStartingWindow"
                    paramCount(when (Build.VERSION.SDK_INT) {
                        33 -> 7
                        else -> 5
                    })
                }
                beforeHook {
                    val currentPkgName = instance.getField<String>("packageName")

                    // 彻底关闭 Splash Screen
                    val isDisableSS = prefs.get(DataConst.DISABLE_SPLASH_SCREEN)
                    printLog("[Android] addStartingWindow():${if (isDisableSS) "" else "Not"} disable $currentPkgName splash screen")
                    if (isDisableSS) {
                        resultNull()
                        return@beforeHook
                    }
                }
            }

            // 热启动时生成启动遮罩
            injectMember {
                method {
                    name = "getStartingWindowType"
                    paramCount(when (Build.VERSION.SDK_INT) {
                        33 -> 7
                        else -> 6
                    })
                }
                beforeHook {
                    val isHotStartCompatible = prefs.get(DataConst.ENABLE_HOT_START_COMPATIBLE) && args(1).boolean()
                    if (isHotStartCompatible) result = 2
                    printLog("[Android] getStartingWindowType():${if (isHotStartCompatible) "" else "Not"} set result to 2")
                }
            }
        }.onHookClassNotFoundFailure {
            loggerE(msg = "[Android] Class Not Found: com.android.server.wm.ActivityRecord")
        }
    }
}