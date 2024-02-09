package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.hook.systemui.*
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isColorOS
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.gswxxn.restoresplashscreen.utils.YukiHelper.loadHookHandler
import com.gswxxn.restoresplashscreen.utils.YukiHelper.register
import com.gswxxn.restoresplashscreen.utils.YukiHelper.registerHookInfo
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ActivityInfoClass
import com.highcapable.yukihookapi.hook.type.android.DrawableClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass

object NewSystemUIHooker: YukiBaseHooker() {
    object Members {
        val makeSplashScreenContentView = HookManager {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass()
                .method { name = "makeSplashScreenContentView" }.give()!!
        }
        val getWindowAttrs = HookManager {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass()
                .method { name = "getWindowAttrs"
                    paramCount(2)
                }.give()!!
        }
        val getBGColorFromCache = HookManager {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer".toClass().method {
                name = "getBGColorFromCache"
                paramCount(2)
            }.give()!!
        }
        val startingWindowViewBuilderConstructor = HookManager {
            ("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder".toClassOrNull()
                ?: "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$SplashViewBuilder".toClass()) // Android 14
                .constructor { paramCount(2..3) }.give()!!
        }
        val createIconDrawable = HookManager {
            ("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder".toClassOrNull()
                ?: "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$SplashViewBuilder".toClass()) // Android 14
                .method { name = "createIconDrawable" }.give()!!
        }
        val iconColor_constructor = HookManager {
            "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$ColorCache\$IconColor".toClass()
                .constructor().give()!!
        }
        val getIcon_IconProvider = HookManager {
            "com.android.launcher3.icons.IconProvider".toClass().method {
                name = "getIcon"
                paramCount(2)
                param { IntType in it && ActivityInfoClass in it }
            }.give()!!
        }
        val normalizeAndWrapToAdaptiveIcon = HookManager {
            "com.android.launcher3.icons.BaseIconFactory".toClass().method {
                name = "normalizeAndWrapToAdaptiveIcon"
            }.give()!!
        }
        val createIconBitmap_BaseIconFactory = HookManager {
            "com.android.launcher3.icons.BaseIconFactory".toClass().method {
                name = "createIconBitmap"
                param (DrawableClass, FloatType, IntType)
            }.give()!!
        }
        val build_SplashScreenViewBuilder = HookManager {
            "android.window.SplashScreenView\$Builder".toClass().method {
                name = "build"
            }.give()!!
        }
        val removeStartingWindow = HookManager {
            "com.android.wm.shell.ShellTaskOrganizer".toClass().method {
                name = "removeStartingWindow"
            }.give()!!
        }

        // MIUI
        val isMiuiHome_TaskSnapshotHelperImpl = HookManager(
            isMIUI && "android.app.TaskSnapshotHelperImpl".hasClass()
        ) {
            "android.app.TaskSnapshotHelperImpl".toClass().method {
                name = "isMiuiHome"
                param(StringClass)
            }.give()!!
        }
        val updateForceDarkSplashScreen_ForceDarkHelperStubImpl = HookManager(isMIUI) {
            "android.window.SplashScreenView\$Builder".toClass().method {
                name = "isStaringWindowUnderNightMode"
                emptyParam()
            }.ignored().give() ?:
            "android.view.ForceDarkHelperStubImpl".toClass().method {
                name = "updateForceDarkSplashScreen"
            }.give()!!
        }

        // ColorOS
        val setContentViewBackground_OplusShellStartingWindowManager = HookManager(isColorOS) {
            "com.android.wm.shell.startingsurface.OplusShellStartingWindowManager".toClass().method {
                name = "setContentViewBackground"
            }.give()!!
        }
        val getIconExt_OplusShellStartingWindowManager = HookManager(isColorOS) {
            "com.android.wm.shell.startingsurface.OplusShellStartingWindowManager".toClass().method {
                name = "getIconExt"
                paramCount(4..5)
            }.give()!!
        }
        val getWindowAttrsIfPresent_OplusShellStartingWindowManager = HookManager(isColorOS) {
            "com.android.wm.shell.startingsurface.OplusShellStartingWindowManager".toClass().method {
                name = "getWindowAttrsIfPresent"
            }.give()!!
        }
    }

    /** 开始 Hook */
    override fun onHook() {
        // 注册 DataChannel
        register()
        registerHookInfo(Members)

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
                YLog.error(e = e)
            }
        }
    }

}