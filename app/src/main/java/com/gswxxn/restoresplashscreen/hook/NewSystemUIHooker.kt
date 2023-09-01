package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.hook.systemui.*
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isColorOS
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.gswxxn.restoresplashscreen.utils.YukiHelper.loadHookHandler
import com.gswxxn.restoresplashscreen.utils.YukiHelper.register
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.type.android.ActivityInfoClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass

object NewSystemUIHooker: YukiBaseHooker() {
    object Members {
        val makeSplashScreenContentView = HookManager.create {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass()
                .method { name = "makeSplashScreenContentView" }.give()!!
        }
        val getWindowAttrs = HookManager.create {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass()
                .method { name = "getWindowAttrs"
                    paramCount(2)
                }.give()!!
        }
        val getBGColorFromCache = HookManager.create {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass().method {
                name = "getBGColorFromCache"
                paramCount(2)
            }.give()!!
        }
        val startingWindowViewBuilderConstructor = HookManager.create {
            ("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder".toClassOrNull()
                ?: "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$SplashViewBuilder".toClass()) // ColorOS 13.1 (Android 14)
                .constructor { paramCount(2..3) }.give()!!
        }
        val createIconDrawable = HookManager.create {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder".toClass()
                .method { name = "createIconDrawable" }.give()!!
        }
        val getIcon_IconProvider = HookManager.create {
            "com.android.launcher3.icons.IconProvider".toClass().method {
                name = "getIcon"
                paramCount(2)
                param { IntType in it && ActivityInfoClass in it }
            }.give()!!
        }
        val normalizeAndWrapToAdaptiveIcon = HookManager.create {
            "com.android.launcher3.icons.BaseIconFactory".toClass().method {
                name = "normalizeAndWrapToAdaptiveIcon"
            }.give()!!
        }
        val build_SplashScreenViewBuilder = HookManager.create {
            "android.window.SplashScreenView\$Builder".toClass().method {
                name = "build"
            }.give()!!
        }
        val removeStartingWindow = HookManager.create {
            "com.android.wm.shell.ShellTaskOrganizer".toClass().method {
                name = "removeStartingWindow"
            }.give()!!
        }

        // MIUI
        val isMiuiHome_TaskSnapshotHelperImpl = HookManager.create(isMIUI) {
            try {
                "android.app.TaskSnapshotHelperImpl".toClass().method {
                    name = "isMiuiHome"
                    param(StringClass)
                }.give()!!
            } catch (e: NoClassDefFoundError) {
                loggerW(msg = "Class android.app.TaskSnapshotHelperImpl not found, be relax, this is not a requirement")
                null
            }
        }
        val updateForceDarkSplashScreen_ForceDarkHelperStubImpl = HookManager.create(isMIUI) {
            "android.window.SplashScreenView\$Builder".toClass().method {
                name = "isStaringWindowUnderNightMode"
                emptyParam()
            }.ignored().give() ?:
            "android.view.ForceDarkHelperStubImpl".toClass().method {
                name = "updateForceDarkSplashScreen"
            }.give()!!
        }

        // ColorOS
        val setContentViewBackground_OplusShellStartingWindowManager = HookManager.create(isColorOS) {
            "com.android.wm.shell.startingsurface.OplusShellStartingWindowManager".toClass().method {
                name = "setContentViewBackground"
            }.give()!!
        }
    }

    /** 开始 Hook */
    override fun onHook() {
        // 注册 DataChannel
        register()

        loadHookHandler(
            GenerateHookHandler,
            ScopeHookHandler,
            IconHookHandler,
            BottomHookHandler,
            BgHookHandler,
            MIUIHookHandler,
            ColorOSHookHandler
        )

        // 执行 Hook
        Members.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val hookManager = field.get(null)

            if (hookManager is HookManager) try {
                hookManager.startHook()
            } catch (e: Throwable) {
                loggerE(e = e)
            }
        }
    }

}