package com.gswxxn.restoresplashscreen.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import dev.lackluster.hyperx.compose.base.DrawableResIcon
import dev.lackluster.hyperx.compose.base.ImageIcon
import top.yukonga.miuix.kmp.extra.SuperSwitch

/**
 * 开关可组合函数, 使用 YukiHookAPI 管理 SharedPreferences, 并在模块未激活时提示用户
 */
@Composable
fun SwitchPreference(
    icon: ImageIcon? = null,
    title: String,
    summary: String? = null,
    prefsData: PrefsData<Boolean>? = null,
    enabled: Boolean = true,
    checked: MutableState<Boolean>? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    val context = LocalContext.current
    val prefs = context.prefs()
    val _checked = checked
        ?: prefsData?.let { remember { mutableStateOf(prefs.get(it)) } }
        ?: remember { mutableStateOf(false) }

    SuperSwitch(
        title = title,
        summary = summary,
        leftAction = { icon?.let { DrawableResIcon(it) } },
        checked = _checked.value,
        onCheckedChange = { newValue ->
            if (!YukiHookAPI.Status.isXposedModuleActive) {
                context.toast(R.string.make_sure_active)
            } else {
                prefsData?.let { prefs.edit { put(it, newValue) } }
                _checked.value = newValue
                onCheckedChange?.invoke(newValue)
            }
        },
        insideMargin = PaddingValues((icon?.getHorizontalPadding() ?: 16.dp), 16.dp, 16.dp, 16.dp),
        enabled = enabled
    )
}