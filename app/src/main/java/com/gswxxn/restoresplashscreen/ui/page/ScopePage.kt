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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 作用域 界面
 */
@Composable
fun ScopePage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val prefs = LocalContext.current.prefs()

    var customScope by remember { mutableStateOf(prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)) }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.custom_scope_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_scope,
                title = "SCOPE"
            )
        }
        item {
            PreferenceGroup(
                last = true
            ) {
                val context = LocalContext.current
                // 自定义模块作用域
                SwitchPreference(
                    title = stringResource(R.string.custom_scope),
                    key = DataConst.ENABLE_CUSTOM_SCOPE.key
                ) { newValue ->
                    customScope = newValue
                    if (newValue) {
                        context.let {
                            Toast.makeText(it, it.getString(R.string.custom_scope_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AnimatedVisibility(
                    customScope
                ) {
                    Column {
                        // 将作用域外的应用替换位空白启动遮罩
                        SwitchPreference(
                            title = stringResource(R.string.replace_to_empty_splash_screen),
                            summary = stringResource(R.string.replace_to_empty_splash_screen_tips),
                            key = DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN.key
                        )
                        // 配置应用列表
                        TextPreference(
                            title = stringResource(R.string.exception_mode_list)
                        ) {
                            navController.navigateTo(Pages.CONFIG_CUSTOM_SCOPE)
                        }
                    }
                }
            }
        }
    }
}