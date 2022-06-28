package com.gswxxn.restoresplashscreen.hook

import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugTag = "RestoreSplashScreen"
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.android.systemui", SystemUIHooker())
        loadSystem(AndroidHooker())
    }
}