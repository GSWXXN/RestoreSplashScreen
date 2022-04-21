package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.content.Intent
import android.view.View
import com.gswxxn.restoresplashscreen.databinding.ActivityMainBinding

const val EXTRA_MESSAGE = "com.gswxxn.MainActivity.MESSAGE"
class MainActivity : BaseActivity<ActivityMainBinding>() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        appContext = applicationContext

        // 启用模块
        binding.enableModel.setOnCheckedChangeListener { _, isChecked ->
        }

        // 启用日志
        binding.enableLog.setOnCheckedChangeListener { _, isChecked ->
        }

        // 自定义模块作用应用
        binding.customScope.setOnCheckedChangeListener { _, isChecked ->
            binding.customScopeLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 排除模式
        binding.customScopeExceptMode.setOnCheckedChangeListener { _, isChecked ->
            binding.exceptModeStatusText.text = if (isChecked) "不会" else "仅会"
        }

        // 作用域应用列表
        binding.customScopeList.setOnClickListener {
            val intent = Intent(this, ConfigAppsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, 1)
            }
            startActivity(intent)
        }

        // 使用系统默认风格
        binding.defaultStyle.setOnCheckedChangeListener { _, isChecked ->
            binding.defaultStyleList.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 使用系统默认风格应用列表
        binding.defaultStyleList.setOnClickListener {
            val intent = Intent(this, ConfigAppsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, 2)
            }
            startActivity(intent)
        }

        // 自定义Splash Screen View
        binding.customView.setOnCheckedChangeListener { _, isChecked ->
            binding.customViewConfig.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 配置Splash Screen View按钮
        binding.customViewConfig.setOnClickListener {

        }

        binding.customScope.isChecked = false

        binding.styleLayout.visibility = View.GONE
        binding.moreLayout.visibility = View.GONE

    }

}