package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.BuildConfig
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/** Hook 入口类 */
@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { tag = "RestoreSplashScreen" }
        isDebug = BuildConfig.DEBUG
    }

    override fun onHook() = encase {
        loadApp("com.android.systemui", SystemUIHooker)
        loadSystem(AndroidHooker)
    }
}