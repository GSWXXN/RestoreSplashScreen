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
 * 背景 - 替换背景颜色 - 排除列表
 */
@Composable
fun BackgroundExceptPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    AppListPage(
        navController,
        adjustPadding,
        stringResource(R.string.background_except_title),
        DataConst.BG_EXCEPT_LIST.key,
        mode
    )
}