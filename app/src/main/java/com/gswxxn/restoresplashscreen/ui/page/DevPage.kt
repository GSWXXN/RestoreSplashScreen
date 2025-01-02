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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.SwitchPreference
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getHookInfo
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.EditTextDataType
import dev.lackluster.hyperx.compose.preference.EditTextPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SeekBarPreference
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

/**
 * 开发者选项
 */
@Composable
fun DevPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController = navController,
        adjustPadding = adjustPadding,
        title = stringResource(R.string.dev_settings),
        blurEnabled = MainActivity.blurEnabled,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        mode = mode
    ) {
        item {
            SettingItems()
        }
    }
}

/**
 * 分组设置
 */
@Composable
private fun SettingItems() {
    PreferenceGroup(first = true) { GeneralSettingItems() }
    PreferenceGroup(title = stringResource(R.string.icon_settings)) { IconSettingItems() }
    PreferenceGroup(title = stringResource(R.string.hook_info), last = true) { HookInfo() }
}

/**
 * 通用设置
 */
@Composable
private fun GeneralSettingItems() {
    SwitchPreference(
        title = stringResource(R.string.dev_settings),
        prefsData = DataConst.ENABLE_DEV_SETTINGS,
        onCheckedChange = { MainActivity.devMode.value = it }
    )
    EditTextPreference(
        title = stringResource(R.string.dev_blur_tint_alpha_light),
        key = DataConst.HAZE_TINT_ALPHA_LIGHT.key,
        defValue = (MainActivity.blurTintAlphaLight.floatValue * 100).roundToInt().coerceIn(0..100),
        dataType = EditTextDataType.INT,
        dialogMessage = stringResource(R.string.dev_blur_tint_alpha_tips),
        isValueValid = { (it as? Int) in (0..100) }
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
        isValueValid = { (it as? Int) in (0..100) }
    ) { _, newValue ->
        (newValue as? Int)?.let {
            MainActivity.blurTintAlphaDark.floatValue = it / 100f
        }
    }
}

/**
 * 图标设置
 */
@Composable
private fun IconSettingItems() {
    SeekBarPreference(
        title = stringResource(R.string.dev_icon_round_corner_rate),
        key =  DataConst.DEV_ICON_ROUND_CORNER_RATE.key,
        defValue = DataConst.DEV_ICON_ROUND_CORNER_RATE.value,
        min = 0,
        max = 50,
        format = "%d%% / 50%%"
    )
}

/**
 * 显示 Hook 信息
 */
@Composable
private fun HookInfo() {
    val context = LocalContext.current
    val hookInfos = remember { mutableStateListOf<Map.Entry<String, HookManager.HookInfo>>() }

    LaunchedEffect(Unit) {
        hookInfos.clear()
        context.getHookInfo("com.android.systemui") { newHookInfos ->
            hookInfos.addAll(newHookInfos.entries.sortedWith(compareBy({ !it.value.isAbnormal }, {it.key})))
        }
    }

    if (hookInfos.isEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.dev_hook_info_empty),
                fontSize = MiuixTheme.textStyles.headline1.fontSize,
                fontWeight = FontWeight.Medium,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    } else hookInfos.forEach { (key, hookInfo) ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = key,
                fontSize = MiuixTheme.textStyles.headline1.fontSize,
                fontWeight = FontWeight.Medium,
                color = if (hookInfo.isAbnormal) Color.Red else MiuixTheme.colorScheme.onSurface
            )
            Text(
                text = "createCondition: ${hookInfo.createCondition}\n" +
                        "isMemberFound: ${hookInfo.isMemberFound}\n" +
                        "hasBeforeHooks: ${hookInfo.hasBeforeHooks}\n" +
                        "isBeforeHookExecuted: ${hookInfo.isBeforeHookExecuted}\n" +
                        "hasAfterHooks: ${hookInfo.hasAfterHooks}\n" +
                        "isAfterHookExecuted: ${hookInfo.isAfterHookExecuted}\n" +
                        "hasReplaceHook: ${hookInfo.hasReplaceHook}\n" +
                        "isReplaceHookExecuted: ${hookInfo.isReplaceHookExecuted}",
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}
