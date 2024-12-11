package com.gswxxn.restoresplashscreen.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.YukiHelper
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.DropDownEntry
import dev.lackluster.hyperx.compose.preference.DropDownPreference
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 图标 界面
 */
@Composable
fun IconPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var shrinkIcon by remember { mutableIntStateOf(SafeSP.getInt(DataConst.SHRINK_ICON.key)) }
    var selectedIconPack by remember { mutableIntStateOf(0) }
    var ignoreAppIcon by remember { mutableStateOf(SafeSP.getBoolean(DataConst.ENABLE_DEFAULT_STYLE.key)) }
    var hideSplashIcon by remember { mutableStateOf(SafeSP.getBoolean(DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON.key)) }

    val shrinkIconItems = listOf(
        DropDownEntry(stringResource(R.string.not_shrink_icon)),
        DropDownEntry(stringResource(R.string.shrink_low_resolution_icon)),
        DropDownEntry(stringResource(R.string.shrink_all_icon))
    )
    val availableIconPackItems = remember { mutableStateListOf(
        DropDownEntry(title = "None", summary = "None")
    ) }

    LaunchedEffect(Unit) {
        val availableIconPacks = IconPackManager(HyperXActivity.context).getAvailableIconPacks()
        val tempIconPackItems = mutableListOf<DropDownEntry>()
        for (iconPack in availableIconPacks) {
            if (iconPack.key == "None") continue
            tempIconPackItems.add(
                DropDownEntry(
                    title = iconPack.value,
                    summary = iconPack.key
                )
            )
        }
        availableIconPackItems.addAll(tempIconPackItems)
        val selectedPkgName = SafeSP.getString(DataConst.ICON_PACK_PACKAGE_NAME.key, DataConst.ICON_PACK_PACKAGE_NAME.value)
        availableIconPackItems.indexOfFirst {
            it.summary == selectedPkgName
        }.let {
            if (it != -1) {
                selectedIconPack = it
            } else {
                selectedIconPack = 0
                SafeSP.putAny(DataConst.ICON_PACK_PACKAGE_NAME.key, "None")
                HyperXActivity.context.let { context ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.icon_pack_is_removed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.icon_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_icon,
                title = "ICON"
            )
        }
        item {
            PreferenceGroup {
                // 绘制图标圆角
                SwitchPreference(
                    title = stringResource(R.string.draw_round_corner),
                    key = DataConst.ENABLE_DRAW_ROUND_CORNER.key
                )
                // 缩小图标
                DropDownPreference(
                    title = stringResource(R.string.shrink_icon),
                    entries = shrinkIconItems,
                    key = DataConst.SHRINK_ICON.key
                ) {
                    shrinkIcon = it
                }
                AnimatedVisibility(
                    shrinkIcon != 0
                ) {
                    // 为缩小的图标添加模糊背景
                    SwitchPreference(
                        title = stringResource(R.string.add_icon_blur_bg),
                        key = DataConst.ENABLE_ADD_ICON_BLUR_BG.key
                    )
                }
                // 替换图标获取方式
                SwitchPreference(
                    title = stringResource(R.string.replace_icon),
                    summary = stringResource(R.string.replace_icon_tips),
                    key = DataConst.ENABLE_REPLACE_ICON.key
                )
                if (YukiHelper.atLeastMIUI14) {
                    // 使用 MIUI 大图标
                    SwitchPreference(
                        title = stringResource(R.string.use_miui_large_icon),
                        key = DataConst.ENABLE_USE_MIUI_LARGE_ICON.key
                    )
                }
                // 使用图标包
                DropDownPreference(
                    title = stringResource(R.string.use_icon_pack),
                    entries = availableIconPackItems,
                    defValue = selectedIconPack
                ) {
                    SafeSP.putAny(DataConst.ICON_PACK_PACKAGE_NAME.key, availableIconPackItems[it].summary ?: "None")
                    selectedIconPack = it
                }
            }
        }
        item {
            PreferenceGroup {
                // 忽略应用主动设置的图标
                SwitchPreference(
                    title = stringResource(R.string.default_style),
                    summary = stringResource(R.string.default_style_tips),
                    key = DataConst.ENABLE_DEFAULT_STYLE.key
                ) { newValue ->
                    ignoreAppIcon = newValue
                    if (newValue) {
                        HyperXActivity.context.let {
                            Toast.makeText(it, it.getString(R.string.custom_scope_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AnimatedVisibility(
                    ignoreAppIcon
                ) {
                    // 配置应用列表
                    TextPreference(
                        title = stringResource(R.string.default_style_list)
                    ) {
                        navController.navigateTo(Pages.CONFIG_IGNORE_APP_ICON)
                    }
                }
            }
        }
        item {
            PreferenceGroup(
                last = true
            ) {
                // 不显示图标
                SwitchPreference(
                    title = stringResource(R.string.hide_splash_screen_icon),
                    key = DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON.key
                ) { newValue ->
                    hideSplashIcon = newValue
                    if (newValue) {
                        HyperXActivity.context.let {
                            Toast.makeText(it, it.getString(R.string.custom_scope_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AnimatedVisibility(
                    hideSplashIcon
                ) {
                    // 配置应用列表
                    TextPreference(
                        title = stringResource(R.string.default_style_list)
                    ) {
                        navController.navigateTo(Pages.CONFIG_HIDE_SPLASH_ICON)
                    }
                }
            }
        }
    }
}