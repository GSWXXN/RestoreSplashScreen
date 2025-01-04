package com.gswxxn.restoresplashscreen.ui.page

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.gswxxn.restoresplashscreen.ui.component.SwitchPreference
import com.gswxxn.restoresplashscreen.ui.component.TextPreference
import com.gswxxn.restoresplashscreen.utils.BackupUtils
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import java.time.LocalDateTime

/**
 * 基础设置 界面
 */
@Composable
fun BasicPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    BasePage(
        navController = navController,
        adjustPadding = adjustPadding,
        title = stringResource(R.string.basic_settings),
        blurEnabled = MainActivity.blurEnabled,
        blurTintAlphaLight = MainActivity.blurTintAlphaLight,
        blurTintAlphaDark = MainActivity.blurTintAlphaDark,
        mode = mode
    ) {
        item {
            HeaderCard(imageResID = R.drawable.demo_basic, title = "BASIC")

            SettingItems()
        }
    }
}

/**
 * 分组设置
 */
@Composable
private fun SettingItems() {
    PreferenceGroup {
        ModuleAppSettings()
    }
    PreferenceGroup(title = stringResource(R.string.backup_restore_title), last = true) {
        BackupAndRestore()
    }
}

/**
 * 模块 App 相关设置项
 */
@Composable
private fun ModuleAppSettings() {
    val context = LocalContext.current
    val prefs = context.prefs()

    val enableLog = remember { mutableStateOf(prefs.get(DataConst.ENABLE_LOG)) }
    LaunchedEffect(Unit) {
        if (enableLog.value && (System.currentTimeMillis() - prefs.get(DataConst.ENABLE_LOG_TIMESTAMP)) > 86400000) {
            prefs.edit { put(DataConst.ENABLE_LOG, false) }
            enableLog.value = false
        }
    }

    // 启用日志
    SwitchPreference(
        title = stringResource(R.string.enable_log),
        summary = stringResource(R.string.enable_log_tips),
        prefsData = DataConst.ENABLE_LOG,
        checked = enableLog
    ) {
        if (it) {
            prefs.edit { put(DataConst.ENABLE_LOG_TIMESTAMP, System.currentTimeMillis()) }
        }
    }
    // 隐藏桌面图标
    SwitchPreference(
        title = stringResource(R.string.hide_icon),
        prefsData = DataConst.ENABLE_HIDE_ICON
    ) {
        val newState = if (it) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, "${BuildConfig.APPLICATION_ID}.Home"),
            newState,
            PackageManager.DONT_KILL_APP
        )
    }
    // 模糊效果
    SwitchPreference(
        title = stringResource(R.string.blur),
        prefsData = DataConst.MODULE_APP_BLUR,
        onCheckedChange = { MainActivity.blurEnabled.value = it }
    )
    // 自适应布局
    SwitchPreference(
        title = stringResource(R.string.split_view),
        summary = stringResource(R.string.split_view_tips),
        prefsData = DataConst.SPLIT_VIEW,
        onCheckedChange = { MainActivity.splitEnabled.value = it }
    )
}

/**
 * 备份与恢复设置
 */
@Composable
private fun BackupAndRestore() {
    val context = LocalContext.current

//    val backupUri = remember { mutableStateOf<Uri?>(null) }
//    val restoreUri = remember { mutableStateOf<Uri?>(null) }
//    backupUri.value?.let { BackupUtils.handleCreateDocument(context, it) }
//    restoreUri.value?.let { BackupUtils.handleReadDocument(context, it) }
//    val backupLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.CreateDocument("application/json"),
//        onResult = { backupUri.value = it }
//    )
//    val restoreLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.OpenDocument(),
//        onResult = { restoreUri.value = it }
//    )

    // todo: 待测试
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { BackupUtils.handleCreateDocument(context, it) }
    )
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { BackupUtils.handleReadDocument(context, it) }
    )

    // 备份设置项
    TextPreference(title = stringResource(R.string.backup)) {
        backupLauncher.launch("RestoreSplashScreen_${LocalDateTime.now()}.json")
    }
    // 恢复设置项
    TextPreference(title = stringResource(R.string.restore)) {
        restoreLauncher.launch(arrayOf("application/json"))
    }
}