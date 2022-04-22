package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.content.Intent
import android.view.View
import com.gswxxn.restoresplashscreen.Data.DataConst
import org.jetbrains.anko.alert
import com.gswxxn.restoresplashscreen.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.topjohnwu.superuser.Shell

const val EXTRA_MESSAGE = "com.gswxxn.MainActivity.MESSAGE"
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appContext = applicationContext

        // 重启UI
        binding.titleRestartIcon.setOnClickListener {
            alert( "你真的要重启系统界面吗？", "重启SystemUI") {
                positiveButton("确定") { Shell.su("pkill -f com.android.systemui").exec() }
                negativeButton("取消") {}
            }.show()
        }

        // 启用模块
        binding.enableModel.apply {
            isChecked = modulePrefs.get(DataConst.ENABLE_MODULE)
            setOnCheckedChangeListener { _, isChecked ->
                modulePrefs.put(DataConst.ENABLE_MODULE, isChecked)
            }
        }

        // 启用日志
        binding.enableLog.apply {
            isChecked = modulePrefs.get(DataConst.ENABLE_LOG)
            setOnCheckedChangeListener { _, isChecked ->
                modulePrefs.put(DataConst.ENABLE_LOG, isChecked)
            }
        }

        // 自定义模块作用域
        binding.customScope.apply {
            setOnCheckedChangeListener { _, isChecked ->
                binding.customScopeLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
                modulePrefs.put(DataConst.ENABLE_CUSTOM_SCOPE, isChecked)
            }
            isChecked = modulePrefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
        }

        // 排除模式
        binding.customScopeExceptMode.apply {
            setOnCheckedChangeListener { _, isChecked ->
                binding.exceptModeStatusText.text = if (isChecked) "不会" else "仅会"
                modulePrefs.put(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE, isChecked)
            }
            isChecked = modulePrefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
        }

        // 作用域应用列表
        binding.customScopeList.setOnClickListener {
            val intent = Intent(this, ConfigAppsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, 1)
            }
            startActivity(intent)
        }

        // 使用系统默认风格
        binding.defaultStyle.apply {
            isChecked = modulePrefs.get(DataConst.ENABLE_DEFAULT_STYLE)
            setOnCheckedChangeListener { _, isChecked ->
                binding.defaultStyleList.visibility = if (isChecked) View.VISIBLE else View.GONE
                modulePrefs.put(DataConst.ENABLE_DEFAULT_STYLE, isChecked)
            }
        }

        // 使用系统默认风格应用列表
        binding.defaultStyleList.setOnClickListener {
            val intent = Intent(this, ConfigAppsActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, 2)
            }
            startActivity(intent)
        }

        // 自定义Splash Screen View
        binding.customView.apply {
            isChecked = modulePrefs.get(DataConst.ENABLE_CUSTOM_VIEW)
            setOnCheckedChangeListener { _, isChecked ->
                binding.customViewConfig.visibility = if (isChecked) View.VISIBLE else View.GONE
                modulePrefs.put(DataConst.ENABLE_CUSTOM_VIEW, isChecked)
            }
        }

        // 配置自定义Splash Screen View按钮
        binding.customViewConfig.setOnClickListener {

        }

        // 未开发功能
        binding.styleLayout.visibility = View.GONE
        binding.moreLayout.visibility = View.GONE

    }


}