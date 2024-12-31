package com.gswxxn.restoresplashscreen.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.ColorPickerPageArgs
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.gswxxn.restoresplashscreen.ui.page.data.ChangeBGColorTypes
import com.gswxxn.restoresplashscreen.ui.page.data.BGColorModes
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 背景 界面
 */
@Composable
fun BackgroundPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController = navController,
        adjustPadding = adjustPadding,
        title = stringResource(R.string.background_settings),
        blurEnabled = MainActivity.blurEnabled,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        mode = mode
    ) {
        item {
            HeaderCard(imageResID = R.drawable.demo_background, title = "BACKGROUND")

            SettingItems(navController)
        }
    }
}

/**
 * 分组设置
 */
@Composable
private fun SettingItems(navController: NavController) {
    val prefs = LocalContext.current.prefs()
    val ignoreDarkMode = remember { mutableStateOf(prefs.get(DataConst.IGNORE_DARK_MODE)) }

    PreferenceGroup(last = !isMIUI) {
        GeneralSettingItems(navController = navController, ignoreDarkMode = ignoreDarkMode)
    }

    if(isMIUI) {
        PreferenceGroup(last = true) {
            MIUISettingsGroup(ignoreDarkMode = ignoreDarkMode)
        }
    }
}

/**
 * 替换背景颜色的通用设置
 */
@Composable
private fun GeneralSettingItems(
    navController: NavController,
    ignoreDarkMode: MutableState<Boolean>
) {
    val prefs = LocalContext.current.prefs()

    val colorMode = remember { mutableIntStateOf(prefs.get(DataConst.BG_COLOR_MODE)) }
    val changeBGColorType = remember { mutableIntStateOf(prefs.get(DataConst.CHANG_BG_COLOR_TYPE)) }

    val shouldShowColorMode = changeBGColorType.intValue == ChangeBGColorTypes.FromIcon.ordinal ||
            changeBGColorType.intValue == ChangeBGColorTypes.FromMonet.ordinal

    // 替换背景颜色
    DropDownPreference(
        title = stringResource(R.string.change_bg_color),
        entries = ChangeBGColorTypes.entries.map { DropDownEntry(stringResource(it.stringID)) },
        key = DataConst.CHANG_BG_COLOR_TYPE.key,
        onSelectedIndexChange = { changeBGColorType.intValue = it }
    )

    AnimatedVisibility(shouldShowColorMode) {
        // 颜色模式
        DropDownPreference(
            title = stringResource(R.string.color_mode),
            summary = if (isMIUI) stringResource(R.string.color_mode_tips) else null,
            entries = BGColorModes.entries.map { DropDownEntry(stringResource(it.stringID)) },
            key = DataConst.BG_COLOR_MODE.key
        ) {
            colorMode.intValue = it

            if (isMIUI && colorMode.intValue == BGColorModes.FollowSystem.ordinal) {
                prefs.edit { put(DataConst.IGNORE_DARK_MODE, true) }
                ignoreDarkMode.value = true
            }
        }
    }
    AnimatedVisibility(changeBGColorType.intValue == ChangeBGColorTypes.FromCustom.ordinal) {
        // 自定义背景颜色
        TextPreference(
            title = stringResource(R.string.set_custom_bg_color),
            summary = stringResource(R.string.set_custom_bg_color_tips)
        ) {
            navController.navigateTo(
                "${Pages.CONFIG_COLOR_PICKER}?" +
                        "${ColorPickerPageArgs.PACKAGE_NAME}=${""}"
            )
        }
    }
    AnimatedVisibility(changeBGColorType.intValue != ChangeBGColorTypes.NotChangeBGColor.ordinal) {
        Column {
            // 跳过已主动设置背景颜色的应用
            SwitchPreference(
                title = stringResource(R.string.skip_app_with_bg_color),
                key = DataConst.SKIP_APP_WITH_BG_COLOR.key,
                defValue = DataConst.SKIP_APP_WITH_BG_COLOR.value
            )
            // 配置应用列表
            TextPreference(title = stringResource(R.string.change_bg_color_list)) {
                navController.navigateTo(Pages.CONFIG_BACKGROUND_EXCEPT)
            }
        }
    }
    // 单独配置应用背景颜色
    TextPreference(title = stringResource(R.string.configure_bg_colors_individually)) {
        navController.navigateTo(Pages.CONFIG_BACKGROUND_INDIVIDUALLY)
    }
}

/**
 * 仅在小米设备上生效的设置项
 */
@Composable
private fun MIUISettingsGroup(ignoreDarkMode: MutableState<Boolean>) {
    // 忽略深色模式
    SwitchPreference(
        title = stringResource(R.string.ignore_dark_mode),
        summary = stringResource(R.string.ignore_dark_mode_tips),
        key = DataConst.IGNORE_DARK_MODE.key,
        checked = ignoreDarkMode
    )

    if ("android.app.TaskSnapshotHelperImpl".hasClass()) {
        // 移除截图背景
        SwitchPreference(
            title = stringResource(R.string.remove_bg_drawable),
            summary = stringResource(R.string.remove_bg_drawable_tips),
            key = DataConst.REMOVE_BG_DRAWABLE.key
        )
    }
}
