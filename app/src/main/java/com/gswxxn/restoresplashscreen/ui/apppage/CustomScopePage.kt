package com.gswxxn.restoresplashscreen.ui.apppage

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
import com.gswxxn.restoresplashscreen.ui.component.AppListPage
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference

/**
 * 作用域 - 自定义模块作用域 - 配置应用列表
 */
@Composable
fun CustomScopePage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    var exceptionMode by remember { mutableStateOf(SafeSP.getBoolean(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE.key)) }
    val exceptionSummary = stringResource(
        R.string.custom_scope_exception_mode_message,
        if (exceptionMode)
            stringResource(R.string.will_not)
        else
            stringResource(R.string.will_only)
    )
    AppListPage(
        navController,
        adjustPadding,
        stringResource(R.string.custom_scope_title),
        DataConst.CUSTOM_SCOPE_LIST.key,
        mode
    ) {
        item {
            PreferenceGroup {
                SwitchPreference(
                    title = stringResource(R.string.exception_mode),
                    summary = exceptionSummary,
                    key = DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE.key
                ) {
                    exceptionMode = it
                }
            }
        }
    }
}