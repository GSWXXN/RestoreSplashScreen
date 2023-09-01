package com.gswxxn.restoresplashscreen.hook.systemui

import android.content.Context
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.StartingWindowInfo
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.currentPackageName
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.exceptCurrentApp
import com.gswxxn.restoresplashscreen.hook.systemui.GenerateHookHandler.isHooking
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.gswxxn.restoresplashscreen.utils.YukiHelper.printLog
import com.highcapable.yukihookapi.hook.factory.current

/**
 * 此对象用于处理作用域 Hook
 */
object ScopeHookHandler: BaseHookHandler() {

    /** 开始 Hook */
    override fun onHook() {
        /**
         * 设置后续 Hooks 的默认执行条件, 只有 [currentPackageName] 在作用域内,
         * 并且当前 [isHooking] 才执行后续 Hooks
         */
        HookManager.defaultExecCondition = { isHooking && !exceptCurrentApp }

        // 将作用域外的应用替换为空白启动遮罩
        NewSystemUIHooker.Members.makeSplashScreenContentView?.addBeforeHook({ true }) {
            val isReplaceToEmptySplashScreen = prefs.get(DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN)

            if (isReplaceToEmptySplashScreen && exceptCurrentApp) {
                args(args.indexOfFirst { it is Int }).set(StartingWindowInfo.STARTING_WINDOW_TYPE_LEGACY_SPLASH_SCREEN)
            }
            printLog("makeSplashScreenContentView(): ${if (isReplaceToEmptySplashScreen && exceptCurrentApp) "set mSuggestType to 4;" else "Not"} replace to empty splash screen")
        }

        /**
         * 绕过部分厂商为非主动适配 splash screen 的应用进行额外操作
         *
         * 原理为将 mIconBgColor 设置为一个固定值, 骗过厂商的额外判断, 后续再恢复成默认值
         *
         * 此操作在原生系统为非必要操作, 尤其在某些类原生系统执行此 Hook 会造成额外错误,
         * 所以这里手动指定为只在 MIUI 系统上执行该 Hook, 后续如有返回其他厂商系统需要类似操作, 再手动添加
         */
        if (isMIUI){
            NewSystemUIHooker.Members.getBGColorFromCache?.addAfterHook {
                instance.current().field { name = "mTmpAttrs" }.any()!!.current().field { name = "mIconBgColor" }.set(1)
                printLog("getBGColorFromCache(): Set mIconBgColor to 1")
            }

            // 重置因实现自定义作用域而影响到的 mTmpAttrs
            NewSystemUIHooker.Members.startingWindowViewBuilderConstructor?.addAfterHook {
                val mSplashscreenContentDrawer = instance.current().field { name = "this\$0" }.any()!!
                val mTmpAttrs = mSplashscreenContentDrawer.current().field { name = "mTmpAttrs" }.any()!!
                val context = args.first { it is Context }

                mSplashscreenContentDrawer.current().method {
                    name = "getWindowAttrs"
                    paramCount(2)
                }.call(context, mTmpAttrs)
            }
        }
    }
}