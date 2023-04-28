package com.gswxxn.restoresplashscreen.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.viewbinding.ViewBinding
import com.gswxxn.restoresplashscreen.R
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass
import java.lang.reflect.ParameterizedType

/**
 * 改自 [MIUINativeNotifyIcon](https://github.com/fankes/MIUINativeNotifyIcon/blob/master/app/src/main/java/com/fankes/miui/notify/ui/activity/base/BaseActivity.kt)
 */
abstract class BaseActivity<VB : ViewBinding> : Activity() {
    lateinit var binding: VB

    /**
     * 批量显示或隐藏 [View]
     *
     * @param isShow [Boolean]
     * @param views [View]
     */
    open fun showView(isShow: Boolean = true, vararg views: View?) {
        for (element in views) {
            element?.visibility = if (isShow) View.VISIBLE else View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        window.apply {
            statusBarColor = getColor(R.color.colorDemoBackground)
            navigationBarColor = getColor(R.color.colorThemeBackground)
            setDecorFitsSystemWindows(true)
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        // 通过反射绑定布局
        javaClass.genericSuperclass.also { type ->
            if (type is ParameterizedType) {
                binding = (type.actualTypeArguments[0] as Class<*>).method {
                    name = "inflate"
                    param(LayoutInflaterClass)
                }.get().invoke<VB>(layoutInflater) ?: error("binding failed")
                setContentView(binding.root)
            } else error("binding but got wrong type")
        }

        /** 装载子类 */
        onCreate()
    }

    /** 回调 [onCreate] 方法 */
    abstract fun onCreate()
}