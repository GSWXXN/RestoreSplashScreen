package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.apppage.BackgroundExceptPage
import com.gswxxn.restoresplashscreen.ui.apppage.BgIndividualPage
import com.gswxxn.restoresplashscreen.ui.page.AboutPage
import com.gswxxn.restoresplashscreen.ui.page.BackgroundPage
import com.gswxxn.restoresplashscreen.ui.page.BasicPage
import com.gswxxn.restoresplashscreen.ui.page.BottomPage
import com.gswxxn.restoresplashscreen.ui.apppage.CustomScopePage
import com.gswxxn.restoresplashscreen.ui.apppage.ForceSplashPage
import com.gswxxn.restoresplashscreen.ui.apppage.HideIconPage
import com.gswxxn.restoresplashscreen.ui.apppage.IgnoreAppIconPage
import com.gswxxn.restoresplashscreen.ui.apppage.MinDurationPage
import com.gswxxn.restoresplashscreen.ui.apppage.RemoveBrandingPage
import com.gswxxn.restoresplashscreen.ui.component.ColorPickerPage
import com.gswxxn.restoresplashscreen.ui.component.ColorPickerPageArgs
import com.gswxxn.restoresplashscreen.ui.page.DevPage
import com.gswxxn.restoresplashscreen.ui.page.DisplayPage
import com.gswxxn.restoresplashscreen.ui.page.IconPage
import com.gswxxn.restoresplashscreen.ui.page.MainPage
import com.gswxxn.restoresplashscreen.ui.page.ScopePage
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isXiaomiPad
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.HyperXApp
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : ComponentActivity() {
    companion object {
        val moduleActive: MutableState<Boolean> = mutableStateOf(false)
        val devMode: MutableState<Boolean> = mutableStateOf(false)
        val blurEnabled: MutableState<Boolean> = mutableStateOf(true)
        val blurTintAlphaLight: MutableFloatState = mutableFloatStateOf(0.6f)
        val blurTintAlphaDark: MutableFloatState = mutableFloatStateOf(0.5f)
        val splitEnabled: MutableState<Boolean> = mutableStateOf(true)

        val systemUIRestartNeeded = mutableStateOf(true)
        val androidRestartNeeded = mutableStateOf<Boolean?>(null)
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            SafeSP.setSP(
                getSharedPreferences("${packageName ?: "unknown"}_preferences", MODE_WORLD_READABLE)
            )
        } catch (exception: SecurityException) {
            devMode.value = false
            blurEnabled.value = true
            blurTintAlphaLight.floatValue = 0.6f
            blurTintAlphaDark.floatValue = 0.5f
            splitEnabled.value = isXiaomiPad
        }

        devMode.value = prefs().get(DataConst.ENABLE_DEV_SETTINGS)
        devMode.value = prefs().get(DataConst.ENABLE_DEV_SETTINGS)
        blurEnabled.value = prefs().get(DataConst.BLUR)
        blurTintAlphaLight.floatValue = prefs().get(DataConst.HAZE_TINT_ALPHA_LIGHT) / 100f
        blurTintAlphaDark.floatValue = prefs().get(DataConst.HAZE_TINT_ALPHA_DARK) / 100f
        splitEnabled.value = prefs().get(DataConst.SPLIT_VIEW)

        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        setContent {
            AppContent()
        }
    }

    override fun onResume() {
        super.onResume()
        moduleActive.value = YukiHookAPI.Status.isXposedModuleActive
        dataChannel("com.android.systemui").checkingVersionEquals {
            systemUIRestartNeeded.value = !it
        }
        dataChannel("android").checkingVersionEquals {
            androidRestartNeeded.value = !it
        }
    }

    @Composable
    fun AppContent() {
        HyperXApp(
            autoSplitView = splitEnabled,
            mainPageContent = { navController, adjustPadding, mode ->
                MainPage(navController, adjustPadding, mode)
            },
            emptyPageContent = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(256.dp),
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.secondary)
                    )
                }
            },
            otherPageBuilder = { navController, adjustPadding, mode ->
                composable(Pages.ABOUT) { AboutPage(navController, adjustPadding, mode) }
                composable(Pages.BASIC_SETTINGS) { BasicPage(navController, adjustPadding, mode) }
                composable(Pages.SCOPE_SETTINGS) { ScopePage(navController, adjustPadding, mode) }
                composable(Pages.ICON_SETTINGS) { IconPage(navController, adjustPadding, mode) }
                composable(Pages.BOTTOM_SETTINGS) { BottomPage(navController, adjustPadding, mode) }
                composable(Pages.BACKGROUND_SETTINGS) { BackgroundPage(navController, adjustPadding, mode) }
                composable(Pages.DISPLAY_SETTINGS) { DisplayPage(navController, adjustPadding, mode) }
                composable(Pages.DEVELOPER_SETTINGS) { DevPage(navController, adjustPadding, mode) }

                composable(Pages.CONFIG_CUSTOM_SCOPE) { CustomScopePage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_IGNORE_APP_ICON) { IgnoreAppIconPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_HIDE_SPLASH_ICON) { HideIconPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_REMOVE_BRANDING) { RemoveBrandingPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_BACKGROUND_EXCEPT) { BackgroundExceptPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_BACKGROUND_INDIVIDUALLY) { BgIndividualPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_MIN_DURATION) { MinDurationPage(navController, adjustPadding, mode) }
                composable(Pages.CONFIG_FORCE_SHOW_SPLASH) { ForceSplashPage(navController, adjustPadding, mode) }

                composable(
                    "${Pages.CONFIG_COLOR_PICKER}?" +
                            "${ColorPickerPageArgs.PACKAGE_NAME}={${ColorPickerPageArgs.PACKAGE_NAME}}"
                ) {
                    val pkgName = it.arguments?.getString(ColorPickerPageArgs.PACKAGE_NAME) ?: ""
                    /* Todo: 传入 是否为 OVERALL_BG 设置, 不应为现在的依据包名为 “” 来判断*/
                    val keyLight = it.arguments?.getString(ColorPickerPageArgs.KEY_LIGHT) ?: DataConst.OVERALL_BG_COLOR.key
                    val keyDark = it.arguments?.getString(ColorPickerPageArgs.KEY_DARK) ?: DataConst.OVERALL_BG_COLOR_NIGHT.key
                    ColorPickerPage(navController, adjustPadding, pkgName, mode)
                }
            }
        )
    }
}