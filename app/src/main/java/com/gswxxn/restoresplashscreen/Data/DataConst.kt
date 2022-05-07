package com.gswxxn.restoresplashscreen.Data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {
    val ENABLE_MODULE = PrefsData("enable_module", true)
    val ENABLE_LOG = PrefsData("enable_log", false)
    val ENABLE_CUSTOM_SCOPE = PrefsData("enable_custom_scope", false)
    val IS_CUSTOM_SCOPE_EXCEPTION_MODE = PrefsData("is_custom_scope_exception_mode", true)
    val ENABLE_DEFAULT_STYLE = PrefsData("enable_default_style", false)
    val ENABLE_HIDE_ICON = PrefsData("enable_hide_icon", false)
    val ENABLE_REPLACE_ICON = PrefsData("enable_replace_icon", false)
    val ICON_PACK_PACKAGE_NAME = PrefsData("icon_pack_package_name", "None")
    val IS_CIRCLE_ICON = PrefsData("is_circle_icon", false) //未使用
    val ENABLE_CHANG_BG_COLOR = PrefsData("enable_change_bg_color", false)
    val ENABLE_SHRINK_ICON = PrefsData("enable_shrink_icon", true)
    val INDEPENDENT_COLOR_WECHAT = PrefsData("independent_color_wechat", false)
    val IGNORE_DARK_MODE = PrefsData("ignore_dark_mode", false)

    val UNDEFINED_LIST = PrefsData("undefined_list", mutableSetOf<String>())
    val CUSTOM_SCOPE_LIST = PrefsData("custom_scope_list", mutableSetOf<String>())
    val DEFAULT_STYLE_LIST = PrefsData("default_style_list", mutableSetOf<String>())
    val BG_EXCEPT_LIST =PrefsData("bg_except_list", mutableSetOf<String>())


}