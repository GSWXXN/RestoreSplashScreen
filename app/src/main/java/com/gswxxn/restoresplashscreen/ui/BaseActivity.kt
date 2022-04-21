package com.gswxxn.restoresplashscreen.ui

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import androidx.appcompat.app.AppCompatActivity
import com.gswxxn.restoresplashscreen.R
import com.gyf.immersionbar.ktx.immersionBar
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        javaClass.genericSuperclass.also { type ->
            if (type is ParameterizedType) {
                binding = (type.actualTypeArguments[0] as Class<*>).method {
                    name = "inflate"
                    param(LayoutInflaterClass)
                }.get().invoke<VB>(layoutInflater) ?: error("binding failed")
                setContentView(binding.root)
            } else error("binding but got wrong type")
        }
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