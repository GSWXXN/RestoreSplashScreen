package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.Utils.printLog
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

class AndroidHooker : YukiBaseHooker() {

    override fun onHook() {

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
                    val isForceShowSS = prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN) && pkgName in prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST)

                    if (isForceShowSS && !prefs.get(DataConst.DISABLE_SPLASH_SCREEN)) resultTrue()
                    printLog("!!! validateStartingWindowTheme():${if (isForceShowSS) "" else "Not"} force show $pkgName splash screen")
                   }
            }

            // 彻底关闭 Splash Screen
            injectMember {
                method {
                    name = "addStartingWindow"
                    paramCount(15)
                }
                beforeHook {
                    val isDisableSS = prefs.get(DataConst.DISABLE_SPLASH_SCREEN)
                    if (isDisableSS) resultFalse()
                    printLog("!!! addStartingWindow():${if (isDisableSS) "" else "Not"} disable ${args(0).string()} splash screen")
                }
            }
        }
    }
}