package com.gswxxn.restoresplashscreen.ui.page

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.MainActivity.Companion.androidRestartNeeded
import com.gswxxn.restoresplashscreen.ui.MainActivity.Companion.moduleActive
import com.gswxxn.restoresplashscreen.ui.MainActivity.Companion.systemUIRestartNeeded
import com.gswxxn.restoresplashscreen.utils.CommonUtils.execShell
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.YukiHookAPI.Status.Executor
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.navigation.navigateWithPopup
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ImmersionMore
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup

@Composable
fun MainPage(navController: NavController, adjustPadding: PaddingValues) {
    val isTopPopupExpanded = remember { mutableStateOf(false) }
    val showTopPopup = remember { mutableStateOf(false) }
    val dialogRestartVisibility = remember { mutableStateOf(false) }

    val cardBackground = Color(HyperXActivity.context.getColor(
        when {
            moduleActive.value && (androidRestartNeeded.value == true || systemUIRestartNeeded.value) ->
                R.color.yellow
            moduleActive.value ->
                R.color.green
            else ->
                R.color.gray
        }
    ))
    val stateIconRes = if (moduleActive.value && androidRestartNeeded.value != true && !systemUIRestartNeeded.value)
        R.drawable.ic_success
    else
        R.drawable.ic_warn
    val stateText = when {
        moduleActive.value && androidRestartNeeded.value == true ->
            stringResource(R.string.module_is_updated, stringResource(R.string.phone))
        moduleActive.value && systemUIRestartNeeded.value->
            stringResource(R.string.module_is_updated, stringResource(R.string.system_ui))
        moduleActive.value ->
            stringResource(R.string.module_is_active)
        else ->
            stringResource(R.string.module_is_not_active)
    }

    val contextMenuItems = listOf(
        stringResource(R.string.restart),
        stringResource(R.string.about)
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.app_name),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        navigationIcon = {},
        actions = {
            if (isTopPopupExpanded.value) {
                ListPopup(
                    show = showTopPopup,
                    popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                    alignment = PopupPositionProvider.Align.TopRight,
                    onDismissRequest = {
                        isTopPopupExpanded.value = false
                    }
                ) {
                    ListPopupColumn {
                        contextMenuItems.forEachIndexed { index, string ->
                            DropdownImpl(
                                text = string,
                                optionSize = contextMenuItems.size,
                                isSelected = false,
                                onSelectedIndexChange = {
                                    when(it) {
                                        0 -> {
                                            dialogRestartVisibility.value = true
                                        }
                                        1 -> {
                                            navController.navigateWithPopup(Pages.ABOUT)
                                        }
                                    }
                                    dismissPopup(showTopPopup)
                                    isTopPopupExpanded.value = false
                                },
                                index = index
                            )
                        }
                    }
                }
                showTopPopup.value = true
            }
            IconButton(
                modifier = Modifier.padding(end = 21.dp).size(40.dp),
                onClick = {
                    isTopPopupExpanded.value = true
                }
            ) {
                Icon(
                    imageVector = MiuixIcons.ImmersionMore,
                    contentDescription = "Menu"
                )
            }
        }
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp, top = 12.dp),
                color = cardBackground
            ) {
                Row(
                    modifier = Modifier.clickable {
                        try {
                            execShell("am broadcast -a android.telephony.action.SECRET_CODE -d android_secret_code://5776733 android")
                        } catch (_: Exception) {
                            HyperXActivity.context.let {
                                it.toast(it.getString(R.string.no_root))
                            }
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.padding(16.dp).size(28.dp),
                        painter = painterResource(stateIconRes),
                        colorFilter = ColorFilter.tint(Color.White),
                        contentDescription = null
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 16.dp),
                            text = stateText,
                            fontSize = MiuixTheme.textStyles.title3.fontSize,
                            color = Color.White
                        )
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = stringResource(R.string.module_version, BuildConfig.VERSION_NAME),
                            fontSize = MiuixTheme.textStyles.body1.fontSize,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                        AnimatedVisibility(
                            !moduleActive.value
                        ) {
                            Spacer(Modifier.height(8.dp))
                        }
                        AnimatedVisibility(
                            moduleActive.value
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 16.dp),
                                text = stringResource(
                                    R.string.xposed_framework_version,
                                    Executor.name,
                                    Executor.apiLevel
                                ),
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = Color.White.copy(alpha = 0.6f),
                            )
                        }
                    }
                }
            }
        }
        item {
            PreferenceGroup {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_setting),
                    title = stringResource(R.string.basic_settings)
                ) {
                    navController.navigateWithPopup(Pages.BASIC_SETTINGS)
                }
            }
        }
        item {
            PreferenceGroup {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_app),
                    title = stringResource(R.string.custom_scope_settings)
                ) {
                    navController.navigateWithPopup(Pages.SCOPE_SETTINGS)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_picture),
                    title = stringResource(R.string.icon_settings)
                ) {
                    navController.navigateWithPopup(Pages.ICON_SETTINGS)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_bottom),
                    title = stringResource(R.string.bottom_settings)
                ) {
                    navController.navigateWithPopup(Pages.BOTTOM_SETTINGS)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_color),
                    title = stringResource(R.string.background_settings)
                ) {
                    navController.navigateWithPopup(Pages.BACKGROUND_SETTINGS)
                }
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_monitor),
                    title = stringResource(R.string.display_settings)
                ) {
                    navController.navigateWithPopup(Pages.DISPLAY_SETTINGS)
                }
                AnimatedVisibility(
                    visible = MainActivity.devMode.value
                ) {
                    TextPreference(
                        icon = ImageIcon(iconRes = R.drawable.ic_lab),
                        title = stringResource(R.string.dev_settings)
                    ) {
                        navController.navigateWithPopup(Pages.DEVELOPER_SETTINGS)
                    }
                }
            }
        }
        item {
            PreferenceGroup {
                TextPreference(
                    icon = ImageIcon(iconRes = R.drawable.ic_help),
                    title = stringResource(R.string.faq)
                ) {
                    HyperXActivity.context.let {
                        it.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(it.getString(R.string.faq_url)))
                        )
                    }
                }
            }
        }
        item {
            Text(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                text = stringResource(R.string.main_activity_hint),
                fontSize = MiuixTheme.textStyles.subtitle.fontSize,
                color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }

    SuperDialog(
        title = stringResource(R.string.restart_title),
        summary = stringResource(R.string.restart_message),
        show = dialogRestartVisibility,
        onDismissRequest = {
            dismissDialog(dialogRestartVisibility)
        }
    ) {
        Column {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.reboot),
                onClick = {
                    execShell("reboot")
                    Thread.sleep(300)
                    HyperXActivity.context.let {
                        it.toast(it.getString(R.string.no_root))
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.restart_system_ui),
                onClick = {
                    execShell("pkill -f com.android.systemui && pkill -f com.gswxxn.restoresplashscreen")
                    Thread.sleep(300)
                    HyperXActivity.context.let {
                        it.toast(it.getString(R.string.no_root))
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                text = stringResource(R.string.button_cancel),
                onClick = {
                    dismissDialog(dialogRestartVisibility)
                }
            )
        }
    }
}