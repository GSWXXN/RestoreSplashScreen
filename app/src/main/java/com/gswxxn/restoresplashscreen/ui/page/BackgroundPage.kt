package com.gswxxn.restoresplashscreen.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.ColorPickerPageArgs
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.gswxxn.restoresplashscreen.utils.YukiHelper.isMIUI
import com.highcapable.yukihookapi.hook.factory.hasClass
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
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
fun BackgroundPage(navController: NavController, adjustPadding: PaddingValues) {
    var changeColorType by remember { mutableIntStateOf(SafeSP.getInt(DataConst.CHANG_BG_COLOR_TYPE.key)) }
    var colorMode by remember { mutableIntStateOf(SafeSP.getInt(DataConst.BG_COLOR_MODE.key)) }
    val ignoreDarkMode = remember { mutableStateOf(SafeSP.getBoolean(DataConst.IGNORE_DARK_MODE.key)) }
    var ignoreDarkModeEnabled by remember { mutableStateOf(true) }

    val changeColorTypeItems = listOf(
        DropDownEntry(stringResource(R.string.not_change_bg_color)),
        DropDownEntry(stringResource(R.string.from_icon)),
        DropDownEntry(stringResource(R.string.from_monet)),
        DropDownEntry(stringResource(R.string.from_custom))
    )
    val colorModeItems = listOf(
        DropDownEntry(stringResource(R.string.light_color)),
        DropDownEntry(stringResource(R.string.dark_color)),
        DropDownEntry(stringResource(R.string.follow_system))
    )

    if (isMIUI && colorMode == 2 && (changeColorType == 1 || changeColorType == 2)) {
        SafeSP.putAny(DataConst.IGNORE_DARK_MODE.key, true)
        ignoreDarkMode.value = true
        ignoreDarkModeEnabled = false
    } else {
        ignoreDarkModeEnabled = true
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.background_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_basic,
                title = "BACKGROUND"
            )
        }
        item {
            PreferenceGroup(
                last = !isMIUI
            ) {
                // 替换背景颜色
                DropDownPreference(
                    title = stringResource(R.string.change_bg_color),
                    entries = changeColorTypeItems,
                    key = DataConst.CHANG_BG_COLOR_TYPE.key
                ) {
                    changeColorType = it
                }
                AnimatedVisibility(
                    changeColorType == 1 || changeColorType == 2
                ) {
                    // 颜色模式
                    DropDownPreference(
                        title = stringResource(R.string.color_mode),
                        summary = if (isMIUI) stringResource(R.string.color_mode_tips) else null,
                        entries = colorModeItems,
                        key = DataConst.BG_COLOR_MODE.key
                    ) {
                        colorMode = it
                    }
                }
                AnimatedVisibility(
                    changeColorType == 3
                ) {
                    // 自定义背景颜色
                    TextPreference(
                        title = stringResource(R.string.set_custom_bg_color),
                        summary = stringResource(R.string.set_custom_bg_color_tips)
                    ) {
                        navController.navigateTo(
                            "${Pages.CONFIG_COLOR_PICKER}?" +
                                    "${ColorPickerPageArgs.PACKAGE_NAME}=${""}," +
                                    "${ColorPickerPageArgs.KEY_LIGHT}=${DataConst.OVERALL_BG_COLOR.key}," +
                                    "${ColorPickerPageArgs.KEY_DARK}=${DataConst.OVERALL_BG_COLOR_NIGHT.key}"
                        )
                    }
                }
                AnimatedVisibility(
                    changeColorType != 0
                ) {
                    Column {
                        // 如果应用主动设置了背景颜色则不替换
                        SwitchPreference(
                            title = stringResource(R.string.skip_app_with_bg_color),
                            key = DataConst.SKIP_APP_WITH_BG_COLOR.key,
                            defValue = DataConst.SKIP_APP_WITH_BG_COLOR.value
                        )
                        // 配置应用列表
                        TextPreference(
                            title = stringResource(R.string.change_bg_color_list)
                        ) {
                            navController.navigateTo(Pages.CONFIG_BACKGROUND_EXCEPT)
                        }
                    }
                }
                // 单独配置应用背景颜色
                TextPreference(
                    title = stringResource(R.string.configure_bg_colors_individually)
                ) {
                    navController.navigateTo(Pages.CONFIG_BACKGROUND_INDIVIDUALLY)
                }
            }
        }
        item {
            if(isMIUI) {
                Column {
                    PreferenceGroup(
                        last = true
                    ) {
                        // 忽略深色模式
                        SwitchPreference(
                            title = stringResource(R.string.ignore_dark_mode),
                            summary = stringResource(R.string.ignore_dark_mode_tips),
                            key = DataConst.IGNORE_DARK_MODE.key,
                            checked = ignoreDarkMode,
                            enabled = ignoreDarkModeEnabled
                        )
                        if("android.app.TaskSnapshotHelperImpl".hasClass()) {
                            // 移除截图背景
                            SwitchPreference(
                                title = stringResource(R.string.remove_bg_drawable),
                                summary = stringResource(R.string.remove_bg_drawable_tips),
                                key = DataConst.REMOVE_BG_DRAWABLE.key
                            )
                        }
                    }
                }
            }
        }
    }
}