package com.gswxxn.restoresplashscreen.hook.systemui

import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog

/**
 * 此对象用于处理针对 ColorOS 的 Hook
 */
object ColorOSHookHandler: BaseHookHandler() {

    /** 开始 Hook */
    override fun onHook() {
        NewSystemUIHooker.Members.setContentViewBackground_OplusShellStartingWindowManager?.addBeforeHook {
            printLog("ColorOS: setContentViewBackground(): intercept!!")
            result = null
        }
    }
}