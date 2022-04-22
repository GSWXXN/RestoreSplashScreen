package com.gswxxn.restoresplashscreen.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gswxxn.restoresplashscreen.R
import com.gyf.immersionbar.ktx.immersionBar



abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        immersionBar {
            statusBarColor(R.color.colorThemeBackground)
            autoDarkModeEnable(true)
            navigationBarColor(R.color.colorThemeBackground)
            fitsSystemWindows(true)
        }
        onCreate()
    }
    abstract fun onCreate()
}