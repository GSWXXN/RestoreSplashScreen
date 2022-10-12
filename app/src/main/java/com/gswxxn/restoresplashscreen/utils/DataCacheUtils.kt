package com.gswxxn.restoresplashscreen.utils

import android.graphics.drawable.Drawable

object DataCacheUtils {
    var iconData = HashMap<String, Drawable>()
    var colorData = HashMap<String, Int?>()
    private var isDarkModeCache = false

    fun clear() {
        iconData.clear()
        colorData.clear()
    }

    fun checkDarkModeChanged(currentDarkMode: Boolean) {
        if (currentDarkMode != isDarkModeCache) {
            colorData.clear()
            isDarkModeCache = currentDarkMode
        }
    }
}