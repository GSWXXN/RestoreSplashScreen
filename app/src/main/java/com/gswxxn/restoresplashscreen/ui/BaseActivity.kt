package com.gswxxn.restoresplashscreen.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
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

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
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