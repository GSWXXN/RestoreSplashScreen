package com.gswxxn.restoresplashscreen.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 显示设置 界面
 */
@Composable
fun DisplayPage(navController: NavController, adjustPadding: PaddingValues) {
    var forceShowSplash by remember { mutableStateOf(SafeSP.getBoolean(DataConst.FORCE_SHOW_SPLASH_SCREEN.key)) }
    var forceEnableSplash by remember { mutableStateOf(SafeSP.getBoolean(DataConst.FORCE_ENABLE_SPLASH_SCREEN.key)) }
    val hotStartCompatible = remember { mutableStateOf(SafeSP.getBoolean(DataConst.ENABLE_HOT_START_COMPATIBLE.key)) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.display_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_display,
                title = "DISPLAY"
            )
        }
        item {
            // 遮罩最小持续时间
            PreferenceGroup {
                TextPreference(
                    title = stringResource(R.string.min_duration),
                    summary = stringResource(R.string.min_duration_tips)
                ) {
                    navController.navigateTo(Pages.CONFIG_MIN_DURATION)
                }
            }
        }
        item {
            PreferenceGroup {
                // 强制显示遮罩
                SwitchPreference(
                    title = stringResource(R.string.force_show_splash_screen),
                    summary = stringResource(R.string.force_show_splash_screen_tips),
                    key = DataConst.FORCE_SHOW_SPLASH_SCREEN.key
                ) { newValue ->
                    forceShowSplash = newValue
                    if (newValue) {
                        HyperXActivity.context.let {
                            Toast.makeText(it, it.getString(R.string.custom_scope_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AnimatedVisibility(
                    forceShowSplash
                ) {
                    Column {
                        // 配置应用列表
                        TextPreference(
                            title = stringResource(R.string.force_show_splash_screen_list)
                        ) {
                            navController.navigateTo(Pages.CONFIG_FORCE_SHOW_SPLASH)
                        }
                        // 减少不必要的启动遮罩
                        SwitchPreference(
                            title = stringResource(R.string.reduce_splash_screen),
                            summary = stringResource(R.string.reduce_splash_screen_tips),
                            key = DataConst.REDUCE_SPLASH_SCREEN.key
                        )
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                last = true
            ) {
                // 强制开启启动遮罩
                SwitchPreference(
                    title = stringResource(R.string.force_enable_splash_screen),
                    summary = stringResource(R.string.force_enable_splash_screen_tips),
                    key = DataConst.FORCE_ENABLE_SPLASH_SCREEN.key
                ) {
                    forceEnableSplash = it
                    if (!it) {
                        hotStartCompatible.value = false
                        SafeSP.putAny(DataConst.ENABLE_HOT_START_COMPATIBLE.key, false)
                    }
                }
                // 将启动遮罩适用于热启动
                SwitchPreference(
                    title = stringResource(R.string.hot_start_compatible),
                    summary = stringResource(R.string.hot_start_compatible_tips),
                    key = DataConst.ENABLE_HOT_START_COMPATIBLE.key,
                    checked = hotStartCompatible,
                    enabled = forceEnableSplash
                )
                // 彻底关闭 Splash Screen
                SwitchPreference(
                    title = stringResource(R.string.disable_splash_screen),
                    summary = stringResource(R.string.disable_splash_screen_tips),
                    key = DataConst.DISABLE_SPLASH_SCREEN.key
                )
            }
        }
    }
}