package com.gswxxn.restoresplashscreen.ui.apppage

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.component.AppListPage
import dev.lackluster.hyperx.compose.base.BasePageDefaults

/**
 * 实验功能 - 强制显示遮罩 - 配置应用列表
 */
@Composable
fun ForceSplashPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    AppListPage(
        navController,
        adjustPadding,
        stringResource(R.string.force_show_splash_screen_title),
        DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST.key,
        mode
    )
}