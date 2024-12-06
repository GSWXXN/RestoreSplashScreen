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
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference

/**
 * 底部 - 移除底部图片 - 配置移除列表
 */
@Composable
fun RemoveBrandingPage(navController: NavController, adjustPadding: PaddingValues) {
    var exceptionMode by remember { mutableStateOf(SafeSP.getBoolean(DataConst.IS_REMOVE_BRANDING_IMAGE_EXCEPTION_MODE.key)) }
    val exceptionSummary = stringResource(
        R.string.exception_mode_message,
        if (exceptionMode)
            stringResource(R.string.not_chosen)
        else
            stringResource(R.string.chosen)
    )
    AppListPage(
        navController,
        adjustPadding,
        stringResource(R.string.background_image_title),
        DataConst.REMOVE_BRANDING_IMAGE_LIST.key
    ) {
        item {
            PreferenceGroup {
                SwitchPreference(
                    title = stringResource(R.string.exception_mode),
                    summary = exceptionSummary,
                    key = DataConst.IS_REMOVE_BRANDING_IMAGE_EXCEPTION_MODE.key
                ) {
                    exceptionMode = it
                }
            }
        }
    }
}