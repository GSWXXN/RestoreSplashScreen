package com.gswxxn.restoresplashscreen.ui.page.data

import androidx.annotation.StringRes
import com.gswxxn.restoresplashscreen.R

/**
 * 缩小图标 下拉框内容
 */
enum class ShrinkIconType(@StringRes val stringID: Int) {
    NotShrinkIcon(R.string.not_shrink_icon),
    ShrinkLowResolutionIcon(R.string.shrink_low_resolution_icon),
    ShrinkAllIcon(R.string.shrink_all_icon)
}