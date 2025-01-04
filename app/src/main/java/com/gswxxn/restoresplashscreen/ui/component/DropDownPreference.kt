package com.gswxxn.restoresplashscreen.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import top.yukonga.miuix.kmp.extra.SpinnerEntry
import top.yukonga.miuix.kmp.extra.SpinnerMode
import top.yukonga.miuix.kmp.extra.SuperSpinner


/**
 * 下拉框可组合函数, 使用 YukiHookAPI 管理 SharedPreferences, 并在模块未激活时提示用户
 */
@Composable
fun DropDownPreference(
    title: String,
    summary: String? = null,
    entries: List<SpinnerEntry>,
    prefsData: PrefsData<Int>? = null,
    selectedIndex: MutableIntState? = null,
    showValue: Boolean = true,
    onSelectedIndexChange: ((Int) -> Unit)? = null,
) {
    val context = LocalContext.current
    val prefs = context.prefs()

    val currentSelectedIndex = selectedIndex
        ?: prefsData?.let { remember { mutableIntStateOf(prefs.get(it).coerceIn(0, entries.size - 1)) } }
        ?: remember { mutableIntStateOf(0) }

    SuperSpinner(
        title = title,
        summary = summary,
        items = entries,
        selectedIndex = currentSelectedIndex.intValue,
        mode = SpinnerMode.Normal,
        showValue = showValue,
    ) { newValue ->
        if (!YukiHookAPI.Status.isXposedModuleActive) {
            context.toast(com.gswxxn.restoresplashscreen.R.string.make_sure_active)
            return@SuperSpinner
        }
        prefsData?.let { prefs.edit { put(it, newValue) } }
        currentSelectedIndex.intValue = newValue
        onSelectedIndexChange?.invoke(newValue)
    }
}
