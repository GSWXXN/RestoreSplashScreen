package com.gswxxn.restoresplashscreen.ui

import android.app.Activity
import android.os.Bundle
import com.gswxxn.restoresplashscreen.R

abstract class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        window.apply {
            statusBarColor = getColor(R.color.colorThemeBackground)
            navigationBarColor = getColor(R.color.colorThemeBackground)
            setDecorFitsSystemWindows(true)
        }

        onCreate()
    }
    abstract fun onCreate()
}