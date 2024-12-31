package com.gswxxn.restoresplashscreen.ui.page.data

import androidx.annotation.StringRes
import com.gswxxn.restoresplashscreen.R

/**
 * 替换背景颜色 下拉框内容
 */
enum class ChangeBGColorTypes(@StringRes val stringID: Int) {
    NotChangeBGColor(R.string.not_change_bg_color),
    FromIcon(R.string.from_icon),
    FromMonet(R.string.from_monet),
    FromCustom(R.string.from_custom)
}

/**
 * 背景颜色模式 下拉框内容, 包括浅色/深色/跟随系统
 */
enum class BGColorModes(@StringRes val stringID: Int) {
    LightColor(R.string.light_color),
    DarkColor(R.string.dark_color),
    FollowSystem(R.string.follow_system)
}