package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.data.DataConst
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/** Hook 入口类 */
@InjectYukiHookWithXposed(isUsingResourcesHook = false)
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { tag = "RestoreSplashScreen" }
        isDebug = false
    }

    override fun onHook() = encase {
        if (prefs.get(DataConst.ENABLE_NEW_SYSTEM_UI_HOOKER))
            loadApp("com.android.systemui", NewSystemUIHooker)
        else
            loadApp("com.android.systemui", SystemUIHooker)
        loadSystem(AndroidHooker)
    }
}