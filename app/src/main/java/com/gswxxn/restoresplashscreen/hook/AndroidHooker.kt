package com.gswxxn.restoresplashscreen.hook

import android.os.Build
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.Utils.register
import com.highcapable.yukihookapi.hook.log.loggerE

class AndroidHooker : BaseHooker() {
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
                    val isForceShowSS = pref.get(DataConst.FORCE_SHOW_SPLASH_SCREEN) && pkgName in pref.get(DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST)

                    if (isForceShowSS) resultTrue()
                    printLog("[Android] validateStartingWindowTheme():${if (isForceShowSS) "" else "Not"} force show $pkgName splash screen")
                   }
            }

            // 彻底关闭 Splash Screen
            injectMember {
                method {
                    name = "addStartingWindow"
                    paramCount(when (Build.VERSION.SDK_INT) {
                        33 -> 10
                        else -> 15
                    })
                }
                beforeHook {
                    val isDisableSS = pref.get(DataConst.DISABLE_SPLASH_SCREEN)
                    if (isDisableSS) resultFalse()
                    printLog("[Android] addStartingWindow():${if (isDisableSS) "" else "Not"} disable ${args(0).string()} splash screen")
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
                    val isHotStartCompatible = pref.get(DataConst.ENABLE_HOT_START_COMPATIBLE)
                    if (isHotStartCompatible) result = 2
                    printLog("[Android] getStartingWindowType():${if (isHotStartCompatible) "" else "Not"} set result to 2")
                }
            }
        }.onHookClassNotFoundFailure {
            loggerE(msg = "[Android] Class Not Found: com.android.server.wm.ActivityRecord")
        }
    }
}