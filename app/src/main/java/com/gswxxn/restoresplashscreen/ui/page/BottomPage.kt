package com.gswxxn.restoresplashscreen.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.Pages
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.ui.component.HeaderCard
import com.highcapable.yukihookapi.hook.factory.prefs
import dev.lackluster.hyperx.compose.base.BasePage
import dev.lackluster.hyperx.compose.base.BasePageDefaults
import dev.lackluster.hyperx.compose.navigation.navigateTo
import dev.lackluster.hyperx.compose.preference.PreferenceGroup
import dev.lackluster.hyperx.compose.preference.SwitchPreference
import dev.lackluster.hyperx.compose.preference.TextPreference

/**
 * 底部 界面
 */
@Composable
fun BottomPage(navController: NavController, adjustPadding: PaddingValues, mode: BasePageDefaults.Mode) {
    val prefs = LocalContext.current.prefs()
    var removeBrandingImage by remember { mutableStateOf(prefs.get(DataConst.REMOVE_BRANDING_IMAGE)) }
    val context = LocalContext.current

    BasePage(
        navController,
        adjustPadding,
        stringResource(R.string.bottom_settings),
        MainActivity.blurEnabled,
        MainActivity.blurTintAlphaLight,
        MainActivity.blurTintAlphaDark,
        mode
    ) {
        item {
            HeaderCard(
                imageResID = R.drawable.demo_branding,
                title = "BRANDING\nIMAGE",
                maxLines = 2
            )
        }
        item {
            PreferenceGroup(
                last = true
            ) {
                // 移除底部图片
                SwitchPreference(
                    title = stringResource(R.string.remove_branding_image),
                    summary = stringResource(R.string.remove_branding_image_tips),
                    key = DataConst.REMOVE_BRANDING_IMAGE.key
                ) { newValue ->
                    removeBrandingImage = newValue
                    if (newValue) {
                        context.let {
                            Toast.makeText(it, it.getString(R.string.custom_scope_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                AnimatedVisibility(
                    removeBrandingImage
                ) {
                    // 配置移除列表
                    TextPreference(
                        title = stringResource(R.string.remove_branding_image_list)
                    ) {
                        navController.navigateTo(Pages.CONFIG_REMOVE_BRANDING)
                    }
                }
            }
        }
    }
}