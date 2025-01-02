package com.gswxxn.restoresplashscreen.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.gswxxn.restoresplashscreen.ui.component.SwitchPreference
import com.gswxxn.restoresplashscreen.ui.component.TextPreference
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.PreferenceGroup

/**
 * 显示设置 界面
 */
@Composable
fun DisplayPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController = navController,
        adjustPadding = adjustPadding,
        title = stringResource(R.string.display_settings),
        blurEnabled = MainActivity.blurEnabled,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        mode = mode
    ) {
        item {
            HeaderCard(imageResID = R.drawable.demo_display, title = "DISPLAY")

            SettingItems(navController)
        }
    }
}

/**
 * 分组设置
 */
@Composable
private fun SettingItems(navController: NavController) {
    PreferenceGroup {
        // 遮罩最小持续时间
        TextPreference(
            title = stringResource(R.string.min_duration),
            summary = stringResource(R.string.min_duration_tips),
            onClick = { navController.navigateTo(Pages.CONFIG_MIN_DURATION) }
        )
    }
    PreferenceGroup {
        ForceShowSplashScreenSettingsGroup(navController)
    }
    PreferenceGroup(last = true) {
        OtherDisplaySettingsGroup()
    }
}

/**
 * 强制显示遮罩
 */
@Composable
private fun ForceShowSplashScreenSettingsGroup(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.prefs()
    var forceShowSplash by remember { mutableStateOf(prefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN)) }
    // 强制显示遮罩
    SwitchPreference(
        title = stringResource(R.string.force_show_splash_screen),
        summary = stringResource(R.string.force_show_splash_screen_tips),
        prefsData = DataConst.FORCE_SHOW_SPLASH_SCREEN
    ) { newValue ->
        forceShowSplash = newValue
        if (newValue) {
            context.toast(R.string.custom_scope_message)
        }
    }
    AnimatedVisibility(forceShowSplash) {
        Column {
            // 配置应用列表
            TextPreference(title = stringResource(R.string.force_show_splash_screen_list)) {
                navController.navigateTo(Pages.CONFIG_FORCE_SHOW_SPLASH)
            }
            // 减少不必要的启动遮罩
            SwitchPreference(
                title = stringResource(R.string.reduce_splash_screen),
                summary = stringResource(R.string.reduce_splash_screen_tips),
                prefsData = DataConst.REDUCE_SPLASH_SCREEN
            )
        }
    }
}

/**
 * 其他显示设置组
 */
@Composable
private fun OtherDisplaySettingsGroup() {
    val prefs = LocalContext.current.prefs()

    var forceEnableSplash by remember { mutableStateOf(prefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN)) }

    // 强制开启启动遮罩
    SwitchPreference(
        title = stringResource(R.string.force_enable_splash_screen),
        summary = stringResource(R.string.force_enable_splash_screen_tips),
        prefsData = DataConst.FORCE_ENABLE_SPLASH_SCREEN,
    ) {
        forceEnableSplash = it
    }
    // 将启动遮罩适用于热启动
    SwitchPreference(
        title = stringResource(R.string.hot_start_compatible),
        summary = stringResource(R.string.hot_start_compatible_tips),
        prefsData = DataConst.ENABLE_HOT_START_COMPATIBLE,
        enabled = forceEnableSplash
    )
    // 彻底关闭 Splash Screen
    SwitchPreference(
        title = stringResource(R.string.disable_splash_screen),
        summary = stringResource(R.string.disable_splash_screen_tips),
        prefsData = DataConst.DISABLE_SPLASH_SCREEN
    )
}
