package com.gswxxn.restoresplashscreen.hook

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.YukiHookLogger
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() {
        YukiHookLogger.Configs.tag = "RestoreSplashScreen"
        YukiHookAPI.Configs.isDebug = false
        YukiHookAPI.Configs.isEnableModulePrefsCache = false
    }

    override fun onHook() = YukiHookAPI.encase {
        loadApp("com.android.systemui", SystemUIHooker())
        loadSystem(AndroidHooker())
    }
}