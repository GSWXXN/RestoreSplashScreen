package com.gswxxn.restoresplashscreen.ui.page

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.MainActivity
import dev.lackluster.hyperx.compose.activity.HyperXActivity
import dev.lackluster.hyperx.compose.activity.SafeSP
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.base.IconSize
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.TextPreference
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.min

/**
 * 关于页面
 */
@Composable
fun AboutPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val referencesList = listOf(
        Reference("MIUINativeNotifyIcon", "fankes","AGPL-3.0","https://github.com/fankes/MIUINativeNotifyIcon"),
        Reference("Hide-My-Applist", "Dr-TSNG", "AGPL-3.0", "https://github.com/Dr-TSNG/Hide-My-Applist"),
        Reference("YukiHookAPI", "fankes", "Apache-2.0", "https://github.com/fankes/YukiHookAPI"),
        Reference("MiuiHomeR", "YuKongA", "GPL-3.0", "https://github.com/qqlittleice/MiuiHome_R"),
        Reference("BlockMIUI", "577fkj", "LGPL-2.1", "https://github.com/Block-Network/blockmiui"),
        Reference("HyperCompose", "HowieHChen", "Apache-2.0", "https://github.com/HowieHChen/hyperx-compose"),
        Reference("Miuix", "miuix-kotlin-multiplatform", "Apache-2.0", "https://github.com/miuix-kotlin-multiplatform/miuix"),
    )

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.about),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            var count = 0
            var lastClickTime: Long = 0
            AdaptiveHeaderCard(
                colorCardContent = {
                    Box(
                        modifier = Modifier.clickable {
                            val now = System.currentTimeMillis()
                            if (now - lastClickTime < 500)
                                count++
                            else
                                count = 1
                            lastClickTime = now
                            if (count != 5) return@clickable
                            count = 0
                            if (!SafeSP.getBoolean(DataConst.ENABLE_DEV_SETTINGS.key)) {
                                MainActivity.devMode.value = true
                                SafeSP.putAny(DataConst.ENABLE_DEV_SETTINGS.key, true)
                                HyperXActivity.context.let {
                                    Toast.makeText(
                                        it,
                                        it.getString(R.string.enable_dev_settings),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                HyperXActivity.context.let {
                                    Toast.makeText(
                                        it,
                                        it.getString(R.string.enable_dev_settings),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    ) {
                        val offset: Offset
                        val blurRadius: Float
                        with(LocalDensity.current) {
                            offset = Offset(0f, 3.dp.toPx())
                            blurRadius = 6.dp.toPx()
                        }
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.08f)),
                            alignment = Alignment.CenterStart,
                            contentScale = ContentScale.Fit,
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = stringResource(R.string.app_name),
                                color = Color.White.copy(alpha = 0.7f),
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.1f),
                                        offset = offset,
                                        blurRadius = blurRadius
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.heightIn(min = 12.dp))
                            Text(
                                text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                                fontSize = MiuixTheme.textStyles.body2.fontSize,
                                color = Color.White.copy(alpha = 0.7f),
                            )
                        }
                    }
                },
                infoCardContent = {
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.mipmap.img_developer,
                            iconSize = IconSize.App,
                            cornerRadius = 40.dp
                        ),
                        title = stringResource(R.string.developer_milu),
                        summary = stringResource(R.string.developer)
                    ) {
                        HyperXActivity.context.let {
                            Toast.makeText(it, it.getString(R.string.follow_me), Toast.LENGTH_SHORT).show()
                            try {
                                it.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("coolmarket://u/1189245")))
                            } catch (e: Exception) {
                                it.openUrl("https://www.coolapk.com/u/1189245")
                            }
                        }
                    }
                    TextPreference(
                        icon = ImageIcon(
                            iconRes = R.drawable.img_github,
                            iconSize = IconSize.App
                        ),
                        title = "GitHub",
                        summary = stringResource(R.string.open_source_repo)
                    ) {
                        HyperXActivity.context.openUrl("https://github.com/GSWXXN/RestoreSplashScreen")
                    }
                    SuperArrow(
                        title = "",
                        leftAction = {
                            Image(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .height(40.dp),
                                painter = painterResource(R.drawable.img_iconfont),
                                contentDescription = null
                            )
                        },
                        insideMargin = PaddingValues(16.dp),
                        onClick = {
                            HyperXActivity.context.openUrl("https://www.iconfont.cn")
                        }
                    )
                }
            )
        }
        item {
            PreferenceGroup(
                title = stringResource(R.string.open_source_license),
                last = true
            ) {
                for (project in referencesList) {
                    TextPreference(
                        title = "${project.author}/${project.name}",
                        summary = project.license
                    ) {
                        HyperXActivity.context.let {
                            Toast.makeText(it, it.getString(R.string.thanks_to, project.author), Toast.LENGTH_SHORT).show()
                            it.openUrl(project.link)
                        }
                    }
                }
            }
        }
    }
}

fun Context.openUrl(url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        this.startActivity(intent)
    } catch (_: Exception) { }
}

@Composable
private fun AdaptiveHeaderCard(
    colorCardContent: @Composable () -> Unit,
    infoCardContent: @Composable () -> Unit
) {
    Layout(
        content = {
            Card(
                color = Color("#21A2EE".toColorInt())
            ) {
                colorCardContent()
            }
            Card {
                infoCardContent()
            }
        }
    ) { measurables, constraints ->
        if (measurables.size != 2) {
            layout(0, 0) { }
        }
        if (constraints.maxWidth >= 768.dp.roundToPx()) {
            val cardWidthPx = (constraints.maxWidth - 48.dp.roundToPx()) / 2
            val infoCard = measurables[1].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx
            ))
            val colorCard = measurables[0].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx,
                minHeight = infoCard.height, maxHeight = infoCard.height
            ))
            val layoutHeight = infoCard.height + 18.dp.roundToPx()
            layout(constraints.maxWidth, layoutHeight) {
                colorCard.place(12.dp.roundToPx(), 12.dp.roundToPx())
                infoCard.place(constraints.maxWidth - cardWidthPx - 12.dp.roundToPx(), 12.dp.roundToPx())
            }
        } else {
            val cardWidthPx = constraints.maxWidth - 24.dp.roundToPx()
            val infoCard = measurables[1].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx
            ))
            val colorCardHeight = min(infoCard.height, cardWidthPx / 2)
            val colorCard = measurables[0].measure(constraints.copy(
                minWidth = cardWidthPx, maxWidth = cardWidthPx,
                minHeight = colorCardHeight, maxHeight = colorCardHeight
            ))
            val layoutHeight = colorCard.height + infoCard.height + 30.dp.roundToPx()
            layout(constraints.maxWidth, layoutHeight) {
                colorCard.place(12.dp.roundToPx(), 12.dp.roundToPx())
                infoCard.place(12.dp.roundToPx(), colorCard.height + 24.dp.roundToPx())
            }
        }
    }
}

data class Reference(
    val name: String,
    val author:String,
    val license: String,
    val link: String
)