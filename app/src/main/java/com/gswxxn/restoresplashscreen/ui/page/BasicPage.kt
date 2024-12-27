package com.gswxxn.restoresplashscreen.ui.page

import android.content.ComponentName
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.gswxxn.restoresplashscreen.utils.BackupUtils
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference
import java.time.LocalDateTime

/**
 * 基础设置 界面
 */
@Composable
fun BasicPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val prefs = LocalContext.current.prefs()

    var enableLog by remember { mutableStateOf(prefs.get(DataConst.ENABLE_LOG)) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (enableLog && (System.currentTimeMillis() - prefs.get(DataConst.ENABLE_LOG_TIMESTAMP)) > 86400000) {
            prefs.edit { put(DataConst.ENABLE_LOG, false) }
            enableLog = false
        }
    }

    val backupUri = remember { mutableStateOf<Uri?>(null) }
    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) {
        backupUri.value = it
    }
    backupUri.value?.let {
        BackupUtils.handleCreateDocument(context, it)
    }
    val restoreUri = remember { mutableStateOf<Uri?>(null) }
    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) {
        restoreUri.value = it
    }
    restoreUri.value?.let { uri ->
        BackupUtils.handleReadDocument(context, uri)
    }

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.basic_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_basic,
                title = "BASIC"
            )
        }
        item {
            PreferenceGroup {
                // 启用日志
                SwitchPreference(
                    title = stringResource(R.string.enable_log),
                    summary = stringResource(R.string.enable_log_tips),
                    key = DataConst.ENABLE_LOG.key,
                    defValue = enableLog
                ) {
                    enableLog = it
                    if (it) {
                        prefs.edit { put(DataConst.ENABLE_LOG_TIMESTAMP, System.currentTimeMillis()) }
                    }
                }
                // 隐藏桌面图标
                SwitchPreference(
                    title = stringResource(R.string.hide_icon),
                    key = DataConst.ENABLE_HIDE_ICON.key
                ) {
                    context.let { context ->
                        context.packageManager.setComponentEnabledSetting(
                            ComponentName(context, "${BuildConfig.APPLICATION_ID}.Home"),
                            if (it)
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            else
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }
                }
                // 模糊效果
                SwitchPreference(
                    title = stringResource(R.string.blur),
                    key = DataConst.BLUR.key,
                    defValue = MainActivity.blurEnabled.value
                ) {
                    MainActivity.blurEnabled.value = it
                }
                // 自适应布局
                SwitchPreference(
                    title = stringResource(R.string.split_view),
                    summary = stringResource(R.string.split_view_tips),
                    key = DataConst.SPLIT_VIEW.key,
                    defValue = MainActivity.splitEnabled.value
                ) {
                    MainActivity.splitEnabled.value = it
                }
            }
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.backup_restore_title),
                last = true
            ) {
                // 备份设置项
                TextPreference(
                    title = stringResource(R.string.backup)
                ) {
                    backupLauncher.launch("RestoreSplashScreen_${LocalDateTime.now()}.json")
                }
                // 恢复设置项
                TextPreference(
                    title = stringResource(R.string.restore)
                ) {
                    restoreLauncher.launch(arrayOf("application/json"))
                }
            }
        }
    }
}