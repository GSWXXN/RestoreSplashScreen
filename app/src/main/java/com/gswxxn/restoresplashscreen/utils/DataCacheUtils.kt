package com.gswxxn.restoresplashscreen.utils

import android.graphics.drawable.Drawable

object DataCacheUtils {
    var iconData = HashMap<String, Drawable>()
    var colorData = HashMap<String, Int?>()

    fun clear() {
        iconData.clear()
        colorData.clear()
    }
}
