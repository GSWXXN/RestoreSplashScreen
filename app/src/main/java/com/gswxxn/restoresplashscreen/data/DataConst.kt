package com.gswxxn.restoresplashscreen.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {
    val ENABLE_LOG = PrefsData("enable_log", false)
    val ENABLE_DATA_CACHE = PrefsData("enable_data_cache", false)
    val ENABLE_CUSTOM_SCOPE = PrefsData("enable_custom_scope", false)
    val IS_CUSTOM_SCOPE_EXCEPTION_MODE = PrefsData("is_custom_scope_exception_mode", true)
    val REPLACE_TO_EMPTY_SPLASH_SCREEN = PrefsData("replace_to_empty_splash_screen", false)
    val ENABLE_DEFAULT_STYLE = PrefsData("enable_default_style", false) // 忽略应用主动设置的图标
    val ENABLE_HIDE_ICON = PrefsData("enable_hide_icon", false)
    val ENABLE_REPLACE_ICON = PrefsData("enable_replace_icon", false)
    val ICON_PACK_PACKAGE_NAME = PrefsData("icon_pack_package_name", "None")
    val INDEPENDENT_COLOR_WECHAT = PrefsData("independent_color_wechat", false)
    val IGNORE_DARK_MODE = PrefsData("ignore_dark_mode", false)
    val REMOVE_BG_DRAWABLE = PrefsData("remove_bg_drawable", false)
    val REMOVE_BG_COLOR = PrefsData("remove_bg_color", false)
    val REMOVE_BRANDING_IMAGE = PrefsData("remove_branding_image", false)
    val FORCE_ENABLE_SPLASH_SCREEN = PrefsData("force_enable_splash_screen", false)
    val FORCE_SHOW_SPLASH_SCREEN = PrefsData("force_show_splash_screen", false)
    val DISABLE_SPLASH_SCREEN = PrefsData("disable_splash_screen", false)
    val ENABLE_HOT_START_COMPATIBLE = PrefsData("enable_hot_start_compatible", false)
    val ENABLE_DRAW_ROUND_CORNER = PrefsData("draw_round_corner", true)
    val SHRINK_ICON = PrefsData("shrink_icon", 0)
    val BG_COLOR_MODE = PrefsData("color_mode", 0)
    val CHANG_BG_COLOR_TYPE = PrefsData("change_bg_color_type", 0)
    val MIN_DURATION = PrefsData("min_duration", 0)

    val UNDEFINED_LIST = PrefsData("undefined_list", mutableSetOf<String>())
    val CUSTOM_SCOPE_LIST = PrefsData("custom_scope_list", mutableSetOf<String>())
    val DEFAULT_STYLE_LIST = PrefsData("default_style_list", mutableSetOf<String>()) // 忽略应用主动设置的图标 应用列表
    val BG_EXCEPT_LIST =PrefsData("bg_except_list", mutableSetOf<String>()) //自适应背景颜色排除列表
    val REMOVE_BRANDING_IMAGE_LIST = PrefsData("remove_branding_image_list", mutableSetOf<String>())
    val FORCE_SHOW_SPLASH_SCREEN_LIST = PrefsData("force_show_splash_screen_list", mutableSetOf<String>())
}