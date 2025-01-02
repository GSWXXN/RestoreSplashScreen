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
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 作用域 界面
 */
@Composable
fun ScopePage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController = navController,
        adjustPadding = adjustPadding,
        title = stringResource(R.string.custom_scope_settings),
        blurEnabled = MainActivity.blurEnabled,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        mode = mode
    ) {
        item {
            HeaderCard(imageResID = R.drawable.demo_scope, title = "SCOPE")

            PreferenceGroup(last = true) {
                SettingItems(navController)
            }
        }
    }
}

/**
 * 作用阈设置项
 */
@Composable
private fun SettingItems(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.prefs()

    var customScope by remember { mutableStateOf(prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)) }

    // 自定义模块作用域
    SwitchPreference(
        title = stringResource(R.string.custom_scope),
        prefsData = DataConst.ENABLE_CUSTOM_SCOPE
    ) { newValue ->
        customScope = newValue
        if (newValue) {
            context.toast(R.string.custom_scope_message)
        }
    }
    AnimatedVisibility(customScope) {
        Column {
            // 将作用域外的应用替换位空白启动遮罩
            SwitchPreference(
                title = stringResource(R.string.replace_to_empty_splash_screen),
                summary = stringResource(R.string.replace_to_empty_splash_screen_tips),
                prefsData = DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN
            )
            // 配置应用列表
            TextPreference(title = stringResource(R.string.exception_mode_list)) {
                navController.navigateTo(Pages.CONFIG_CUSTOM_SCOPE)
            }
        }
    }
}