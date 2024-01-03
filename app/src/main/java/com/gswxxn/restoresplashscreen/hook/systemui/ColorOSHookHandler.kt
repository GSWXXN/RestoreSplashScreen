package com.gswxxn.restoresplashscreen.hook.systemui

import android.graphics.drawable.Drawable
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

        // 处理 Drawable 图标
        NewSystemUIHooker.Members.getIconExt_OplusShellStartingWindowManager?.addAfterHook {
            printLog("getIconExt_OplusShellStartingWindowManager(): current method is getIconExt")
            result = IconHookHandler.processIconDrawable(result as Drawable)
        }

        // 禁止读取 WindowAttrs 缓存
        NewSystemUIHooker.Members.getWindowAttrsIfPresent_OplusShellStartingWindowManager?.addBeforeHook {
            printLog("getWindowAttrsIfPresent_OplusShellStartingWindowManager(): return false")
            resultFalse()
        }
    }
}