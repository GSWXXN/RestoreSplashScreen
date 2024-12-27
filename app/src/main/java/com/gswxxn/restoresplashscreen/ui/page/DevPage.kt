package com.gswxxn.restoresplashscreen.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getHookInfo
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

/**
 * 开发者选项
 */
@Composable
fun DevPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val hookInfos = remember { mutableStateListOf<HookInfo>() }

    LaunchedEffect(Unit) {
        hookInfos.clear()
        HyperXActivity.context.getHookInfo("com.android.systemui") { hookInfo ->
            hookInfo.entries.sortedWith(
                compareBy({ !it.value.isAbnormal }, {it.key})
            ).forEach { (key, hookManager) ->
                hookInfos.add(
                    HookInfo(
                        title = key,
                        msg = "createCondition: ${hookManager.createCondition}\n" +
                                "isMemberFound: ${hookManager.isMemberFound}\n" +
                                "hasBeforeHooks: ${hookManager.hasBeforeHooks}\n" +
                                "isBeforeHookExecuted: ${hookManager.isBeforeHookExecuted}\n" +
                                "hasAfterHooks: ${hookManager.hasAfterHooks}\n" +
                                "isAfterHookExecuted: ${hookManager.isAfterHookExecuted}\n" +
                                "hasReplaceHook: ${hookManager.hasReplaceHook}\n" +
                                "isReplaceHookExecuted: ${hookManager.isReplaceHookExecuted}",
                        error = hookManager.isAbnormal
                    )
                )
            }
        }
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.dev_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            PreferenceGroup(
                first = true
            ) {
                SwitchPreference(
                    title = stringResource(R.string.dev_settings),
                    key = DataConst.ENABLE_DEV_SETTINGS.key,
                    defValue = MainActivity.devMode.value
                ) {
                    MainActivity.devMode.value = it
                }
                EditTextPreference(
                    title = stringResource(R.string.dev_blur_tint_alpha_light),
                    key = DataConst.HAZE_TINT_ALPHA_LIGHT.key,
                    defValue = (MainActivity.blurTintAlphaLight.floatValue * 100).roundToInt().coerceIn(0..100),
                    dataType = EditTextDataType.INT,
                    dialogMessage = stringResource(R.string.dev_blur_tint_alpha_tips),
                    isValueValid = {
                        (it as? Int) in (0..100)
                    }
                ) { _, newValue ->
                    (newValue as? Int)?.let {
                        MainActivity.blurTintAlphaLight.floatValue = it / 100f
                    }
                }
                EditTextPreference(
                    title = stringResource(R.string.dev_blur_tint_alpha_dark),
                    key = DataConst.HAZE_TINT_ALPHA_DARK.key,
                    defValue = (MainActivity.blurTintAlphaDark.floatValue * 100).roundToInt().coerceIn(0..100),
                    dataType = EditTextDataType.INT,
                    dialogMessage = stringResource(R.string.dev_blur_tint_alpha_tips),
                    isValueValid = {
                        (it as? Int) in (0..100)
                    }
                ) { _, newValue ->
                    (newValue as? Int)?.let {
                        MainActivity.blurTintAlphaDark.floatValue = it / 100f
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.icon_settings)
            ) {
                SeekBarPreference(
                    title = stringResource(R.string.dev_icon_round_corner_rate),
                    key =  DataConst.DEV_ICON_ROUND_CORNER_RATE.key,
                    defValue = DataConst.DEV_ICON_ROUND_CORNER_RATE.value,
                    min = 0,
                    max = 50,
                    format = "%d%% / 50%%"
                )
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.hook_info),
                last = true
            ) {
                if (hookInfos.size == 0) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dev_hook_info_empty),
                            fontSize = MiuixTheme.textStyles.headline1.fontSize,
                            fontWeight = FontWeight.Medium,
                            color = MiuixTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    hookInfos.forEach { hookInfo ->
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = hookInfo.title,
                                fontSize = MiuixTheme.textStyles.headline1.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = if (hookInfo.error) Color.Red else MiuixTheme.colorScheme.onSurface
                            )
                            Text(
                                text = hookInfo.msg,
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class HookInfo(
    val title: String,
    val msg: String,
    val error: Boolean
)