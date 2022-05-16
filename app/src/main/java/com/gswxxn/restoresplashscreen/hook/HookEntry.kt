package com.gswxxn.restoresplashscreen.hook


import com.gswxxn.restoresplashscreen.data.DataConst
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugTag = "RestoreSplashScreen"
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.android.systemui") {
            when {
                !prefs.get(DataConst.ENABLE_MODULE) -> loggerW(msg = "Aborted Hook -> Hook Closed")
                else -> loadHooker(SystemUIHooker())
            }
        }
    }
}