package com.gswxxn.restoresplashscreen.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 模板类定义模块与宿主需要使用的键值数据
 *
 *  [YukiHookAPI](https://fankes.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/prefs/data/PrefsData.html)
 */
object DataConst {
    val ENABLE_NEW_SYSTEM_UI_HOOKER = PrefsData("enable_new_system_ui_hooker", true)
    val ENABLE_LOG = PrefsData("enable_log", false)
    val ENABLE_LOG_TIMESTAMP = PrefsData("enable_log_timestamp", 0L)
    val ENABLE_CUSTOM_SCOPE = PrefsData("enable_custom_scope", false)
    val IS_CUSTOM_SCOPE_EXCEPTION_MODE = PrefsData("is_custom_scope_exception_mode", true)
    val IS_REMOVE_BRANDING_IMAGE_EXCEPTION_MODE = PrefsData("is_remove_branding_image_exception_mode", false)
    val IS_DEFAULT_STYLE_LIST_EXCEPTION_MODE = PrefsData("is_default_style_list_exception_mode", false)
    val IS_HIDE_SPLASH_SCREEN_ICON_EXCEPTION_MODE = PrefsData("is_hide_splash_screen_icon_exception_mode", false)
    val REPLACE_TO_EMPTY_SPLASH_SCREEN = PrefsData("replace_to_empty_splash_screen", false)
    val ENABLE_DEFAULT_STYLE = PrefsData("enable_default_style", false) // 忽略应用主动设置的图标
    val ENABLE_HIDE_SPLASH_SCREEN_ICON = PrefsData("enable_hide_splash_screen_icon", false)
    val ENABLE_HIDE_ICON = PrefsData("enable_hide_icon", false)
    val ENABLE_REPLACE_ICON = PrefsData("enable_replace_icon", false)
    val ENABLE_USE_MIUI_LARGE_ICON = PrefsData("enable_use_miui_large_icon", false)
    val ENABLE_ADD_ICON_BLUR_BG = PrefsData("enable_add_blur_bg", false)
    val ICON_PACK_PACKAGE_NAME = PrefsData("icon_pack_package_name", "None")
    val OVERALL_BG_COLOR = PrefsData("overall_bg_color", "#FFFFFF")
    val OVERALL_BG_COLOR_NIGHT = PrefsData("overall_bg_color_night", "#000000")
    val IGNORE_DARK_MODE = PrefsData("ignore_dark_mode", false)
    val REMOVE_BG_DRAWABLE = PrefsData("remove_bg_drawable", false)
    val SKIP_APP_WITH_BG_COLOR = PrefsData("skip_app_with_bg_color", true)
    val REMOVE_BRANDING_IMAGE = PrefsData("remove_branding_image", false)
    val REDUCE_SPLASH_SCREEN = PrefsData("reduce_splash_screen", true)
    val FORCE_ENABLE_SPLASH_SCREEN = PrefsData("force_enable_splash_screen", false)
    val FORCE_SHOW_SPLASH_SCREEN = PrefsData("force_show_splash_screen", false)
    val DISABLE_SPLASH_SCREEN = PrefsData("disable_splash_screen", false)
    val ENABLE_HOT_START_COMPATIBLE = PrefsData("enable_hot_start_compatible", false)
    val ENABLE_DRAW_ROUND_CORNER = PrefsData("draw_round_corner", false)
    val SHRINK_ICON = PrefsData("shrink_icon", 0)
    val BG_COLOR_MODE = PrefsData("color_mode", 0)
    val CHANG_BG_COLOR_TYPE = PrefsData("change_bg_color_type", 0)
    val MIN_DURATION = PrefsData("min_duration", 0)

    val UNDEFINED_LIST = PrefsData("undefined_list", mutableSetOf<String>())
    val CUSTOM_SCOPE_LIST = PrefsData("custom_scope_list", mutableSetOf<String>())
    val DEFAULT_STYLE_LIST = PrefsData("default_style_list", mutableSetOf<String>()) // 忽略应用主动设置的图标 应用列表
    val HIDE_SPLASH_SCREEN_ICON_LIST = PrefsData("hide_splash_screen_icon_list", mutableSetOf<String>())
    val BG_EXCEPT_LIST =PrefsData("bg_except_list", mutableSetOf<String>()) //自适应背景颜色排除列表
    val REMOVE_BRANDING_IMAGE_LIST = PrefsData("remove_branding_image_list", mutableSetOf<String>())
    val FORCE_SHOW_SPLASH_SCREEN_LIST = PrefsData("force_show_splash_screen_list", mutableSetOf<String>())
    val MIN_DURATION_LIST = PrefsData("min_duration_list", mutableSetOf<String>())
    val MIN_DURATION_CONFIG_MAP = PrefsData("min_duration_config_map", mutableSetOf<String>())
    val INDIVIDUAL_BG_COLOR_APP_MAP = PrefsData("individual_bg_color_app_map", mutableSetOf<String>())
    val INDIVIDUAL_BG_COLOR_APP_MAP_DARK = PrefsData("individual_bg_color_app_map_dark", mutableSetOf<String>())

    // 开发者设置
    val ENABLE_DEV_SETTINGS = PrefsData("enable_dev_settings", false)
    val DEV_ICON_ROUND_CORNER_RATE = PrefsData("dev_icon_round_corner", 25)
    val HAZE_TINT_ALPHA_LIGHT = PrefsData("module_blur_tint_light", 60)
    val HAZE_TINT_ALPHA_DARK = PrefsData("module_blur_tint_dark", 50)

    // 模块应用设置
    val BLUR = PrefsData("module_blur", true)
    val SPLIT_VIEW = PrefsData("module_split", false)
}