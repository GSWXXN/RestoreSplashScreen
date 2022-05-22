package com.gswxxn.restoresplashscreen.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.gswxxn.restoresplashscreen.R

abstract class BaseActivity : Activity() {

    open fun showView(isShow: Boolean = true, vararg views: View?) {
        for (element in views) {
            element?.visibility = if (isShow) View.VISIBLE else View.GONE
        }
    }

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