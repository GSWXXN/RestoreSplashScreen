package com.gswxxn.restoresplashscreen.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.YukiHookAPI
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.ImageIcon
import top.yukonga.miuix.kmp.extra.SuperArrow

@Composable
fun TextPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    value: String? = null,
    ignoreModuleActiveStatus: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    SuperArrow(
        title = title,
        summary = summary,
        leftAction = { icon?.let { DrawableResIcon(it) } },
        rightText = value,
        insideMargin = PaddingValues((icon?.getHorizontalPadding() ?: 16.dp), 16.dp, 16.dp, 16.dp),
        onClick = {
            if (!YukiHookAPI.Status.isXposedModuleActive && !ignoreModuleActiveStatus) {
                context.toast(R.string.make_sure_active)
                return@SuperArrow
            }
            onClick?.invoke()
        }
    )
}