package com.gswxxn.restoresplashscreen.hook

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.YukiHookLogger
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
    override fun onInit() {
        YukiHookAPI.Configs.isDebug = false
        YukiHookLogger.Configs.tag = "RestoreSplashScreen"
    }

    override fun onHook() = encase {
        loadApp("com.android.systemui", SystemUIHooker())
        loadSystem(AndroidHooker())
    }

    override fun onXposedEvent() {
        YukiXposedEvent.events {
            onHandleLoadPackage {
                SystemUIHooker().onXPEvent(it)
            }
        }
    }
}