package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.data.DataConst
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerI

class AndroidHooker : YukiBaseHooker() {

    override fun onHook() {
        /**
         * 强制显示遮罩
         *
         * 类原始位置在 services.jar 中
         *
         * 此处在 evaluateStartingWindowTheme() 中被调用，最终将参数传递给 showStartingWindow()
         */
        findClass("com.android.server.wm.ActivityRecord").hook {
            injectMember {
                method {
                    name = "validateStartingWindowTheme"
                    paramCount(3)
                }
                beforeHook {
                    val pkgName = args(1).string()
                    val isForceShowSS = prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN) && pkgName in prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST)
                    if (isForceShowSS) resultTrue()
                    if (prefs.get(DataConst.ENABLE_LOG))
                        loggerI(msg = "!!! validateStartingWindowTheme():${if (isForceShowSS) " " else "Not "}force show $pkgName splash screen")
                }
            }
        }
    }
}