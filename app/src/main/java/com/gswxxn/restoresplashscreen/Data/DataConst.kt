package com.gswxxn.restoresplashscreen.Data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {
    val ENABLE_MODULE = PrefsData("enable_module", true)
    val ENABLE_LOG = PrefsData("enable_log", false)
    val ENABLE_CUSTOM_SCOPE = PrefsData("enable_custom_scope", false)
    val IS_CUSTOM_SCOPE_EXCEPTION_MODE = PrefsData("is_custom_scope_exception_mode", true)
    val ENABLE_DEFAULT_STYLE = PrefsData("enable_default_style", false)
    val ENABLE_CUSTOM_VIEW = PrefsData("enable_custom_view", false)
    val ENABLE_HIDE_ICON = PrefsData("enable_hide_icon", false)

    val CUSTOM_SCOPE_LIST = PrefsData("custom_scope_list", mutableSetOf<String>())

}