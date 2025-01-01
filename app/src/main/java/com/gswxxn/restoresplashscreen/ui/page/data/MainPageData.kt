package com.gswxxn.restoresplashscreen.ui.page.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.Pages

/**
 * 模块首页状态显示类型的枚举类
 *
 * @param cardBackground 状态关联的背景颜色。
 * @param stateIconRes 状态关联的图标的 drawable 资源 ID
 * @param stateTextRes 状态关联的文本的 string 资源 ID
 */
enum class ModuleStatusType(
    @ColorRes val cardBackground: Int,
    @DrawableRes val stateIconRes: Int,
    @StringRes val stateTextRes: Int
) {
    ACTIVE_NO_NEED_RESTART(R.color.green, R.drawable.ic_success, R.string.module_is_active),
    ACTIVE_ANDROID_RESTART(R.color.yellow, R.drawable.ic_warn, R.string.module_is_updated_restart_phone_needed),
    ACTIVE_SYSTEM_UI_RESTART(R.color.yellow, R.drawable.ic_warn, R.string.module_is_updated_restart_phone_needed),
    INACTIVE(R.color.gray, R.drawable.ic_warn, R.string.module_is_not_active)
}

/**
 * 模块主页设置项资源的枚举类
 *
 * @property iconRes 图标资源的 drawable ID，用于在 UI 中显示设置项的图标
 * @property stringRes 标题资源的 string ID，用于在 UI 中显示设置项的名称
 * @property navigateTo 导航目标的路由字符串，表示点击此设置项后跳转的页面。如果为 null，则不执行导航操作。
 */
enum class ModulePreferenceRes(
    @DrawableRes val iconRes: Int,
    @StringRes val stringRes: Int,
    // todo: navigateTo 不应为 String, 后续封装为枚举类型
    val navigateTo: String? = null,
) {
    BasicSettings(R.drawable.ic_setting, R.string.basic_settings, Pages.BASIC_SETTINGS),
    CustomScopeSettings(R.drawable.ic_app, R.string.custom_scope_settings, Pages.SCOPE_SETTINGS),
    IconSettings(R.drawable.ic_picture, R.string.icon_settings, Pages.ICON_SETTINGS),
    BottomSettings(R.drawable.ic_bottom, R.string.bottom_settings, Pages.BOTTOM_SETTINGS),
    BackgroundSettings(R.drawable.ic_color, R.string.background_settings, Pages.BACKGROUND_SETTINGS),
    DisplaySettings(R.drawable.ic_monitor, R.string.display_settings, Pages.DISPLAY_SETTINGS),
    DevSettings(R.drawable.ic_lab, R.string.dev_settings, Pages.DEVELOPER_SETTINGS),
    FAQ(R.drawable.ic_help, R.string.faq)
}
