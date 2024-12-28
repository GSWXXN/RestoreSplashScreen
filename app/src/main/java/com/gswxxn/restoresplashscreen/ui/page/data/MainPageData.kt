package com.gswxxn.restoresplashscreen.ui.page.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.Pages
import dev.lackluster.hyperx.compose.base.ImageIcon
import dev.lackluster.hyperx.compose.navigation.navigateWithPopup
import dev.lackluster.hyperx.compose.preference.TextPreference

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

/**
 * 根据 [ModulePreferenceRes] 提供的资源来创建模块首页设置项
 *
 * @param modulePreferenceRes 模块偏好配置资源，包含图标资源、标题资源和导航目标
 * @param navController 导航控制器，用于执行页面跳转。如果为 null，则不会触发导航
 * @param onClick 点击事件的回调函数。如果不为 null，则会在点击时执行此回调
 */
@Composable
fun ModuleSettingPreference(
    modulePreferenceRes: ModulePreferenceRes,
    navController: NavController? = null,
    onClick: (() -> Unit)? = null
) {
    TextPreference(
        icon = ImageIcon(iconRes = modulePreferenceRes.iconRes),
        title = stringResource(modulePreferenceRes.stringRes)
    ) {
        onClick?.invoke()
        modulePreferenceRes.navigateTo?.let { navController?.navigateWithPopup(it) }
    }
}

/**
 * 根据提供的条件确定模块现实的激活状态
 *
 * @param moduleActive 一个布尔值，表示模块是否处于激活状态
 * @param androidRestartNeeded 一个可空的布尔值，表示是否需要 Android 重启, 空为未获取到数据
 * @param systemUIRestartNeeded 一个可空的布尔值，表示是否需要系统 UI 重启, 空为未获取到数据
 * @return 返回表示当前模块状态的 [ModuleStatusType]
 */
fun getModuleStatusType(
    moduleActive: Boolean,
    androidRestartNeeded: Boolean?,
    systemUIRestartNeeded: Boolean?
): ModuleStatusType = when {
        moduleActive && androidRestartNeeded == true -> ModuleStatusType.ACTIVE_ANDROID_RESTART
        moduleActive && systemUIRestartNeeded == true -> ModuleStatusType.ACTIVE_SYSTEM_UI_RESTART
        moduleActive -> ModuleStatusType.ACTIVE_NO_NEED_RESTART
        else -> ModuleStatusType.INACTIVE
}
