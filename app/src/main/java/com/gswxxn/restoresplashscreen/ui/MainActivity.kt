package com.gswxxn.restoresplashscreen.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.Data.DataConst
import com.gswxxn.restoresplashscreen.R
import org.jetbrains.anko.alert
import com.gswxxn.restoresplashscreen.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.hook.factory.isXposedModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus
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

        binding.apply {

            mainTextVersion.text = "模块版本：${BuildConfig.VERSION_NAME}"

            // 重启UI
            titleRestartIcon.setOnClickListener {
                alert("你真的要重启系统界面吗？", "重启SystemUI") {
                    positiveButton("确定") {
                        Shell.su("pkill -f com.android.systemui").exec()
                        Shell.su("pkill -f com.gswxxn.restoresplashscreen").exec()
                    }
                    negativeButton("取消") {}
                }.show()
            }

            titleAboutPage.setOnClickListener {
                val intent = Intent(this@MainActivity, AboutPageActivity::class.java)
                startActivity(intent)
            }

            // 启用模块
            enableModel.apply {
                isChecked = modulePrefs.get(DataConst.ENABLE_MODULE)
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_MODULE, isChecked)
                }
            }

            // 启用日志
            enableLog.apply {
                isChecked = modulePrefs.get(DataConst.ENABLE_LOG)
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_LOG, isChecked)
                }
            }

            // 自定义模块作用域
            customScope.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    binding.customScopeLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
                    modulePrefs.put(DataConst.ENABLE_CUSTOM_SCOPE, isChecked)
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
            }

            hideIconInLauncherSwitch.apply{
                setOnCheckedChangeListener { btn, b ->
                    if (btn.isPressed.not()) return@setOnCheckedChangeListener
                    modulePrefs.put(DataConst.ENABLE_HIDE_ICON, b)
                    packageManager.setComponentEnabledSetting(
                        ComponentName(this@MainActivity, "${BuildConfig.APPLICATION_ID}.Home"),
                        if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_HIDE_ICON)
            }

            // 排除模式
            customScopeExceptMode.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    binding.exceptModeStatusText.text = if (isChecked) "不会" else "仅会"
                    modulePrefs.put(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE, isChecked)
                }
                isChecked = modulePrefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
            }

            // 作用域应用列表
            customScopeList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, 1)
                startActivity(intent)
            }

            // 使用系统默认风格
            defaultStyle.apply {
                isChecked = modulePrefs.get(DataConst.ENABLE_DEFAULT_STYLE)
                setOnCheckedChangeListener { _, isChecked ->
                    binding.defaultStyleList.visibility = if (isChecked) View.VISIBLE else View.GONE
                    modulePrefs.put(DataConst.ENABLE_DEFAULT_STYLE, isChecked)
                }
            }

            // 使用系统默认风格应用列表
            defaultStyleList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, 2)
                startActivity(intent)
            }

            // 自定义Splash Screen View
            customView.apply {
                isChecked = modulePrefs.get(DataConst.ENABLE_CUSTOM_VIEW)
                setOnCheckedChangeListener { _, isChecked ->
                    binding.customViewConfig.visibility = if (isChecked) View.VISIBLE else View.GONE
                    modulePrefs.put(DataConst.ENABLE_CUSTOM_VIEW, isChecked)
                }
            }

            // 配置自定义Splash Screen View按钮
            customViewConfig.setOnClickListener {

            }

            // 未开发功能
            styleLayout.visibility = View.GONE
            moreLayout.visibility = View.GONE
        }
    }

    private fun refreshState(){
        binding.apply {
            mainLinStatus.setBackgroundResource(
                when {
                    isXposedModuleActive && modulePrefs.get(DataConst.ENABLE_MODULE).not() -> R.drawable.bg_yellow_round
                    isXposedModuleActive -> R.drawable.bg_green_round
                    else -> R.drawable.bg_dark_round
                }
            )
            mainImgStatus.setImageResource(
                when {
                    isXposedModuleActive -> R.drawable.ic_success
                    else -> R.drawable.ic_warn
                }
            )
            mainTextStatus.text =
                when {
                    isXposedModuleActive && modulePrefs.get(DataConst.ENABLE_MODULE).not() -> "模块已停用"
                    isXposedModuleActive -> "模块已激活"
                    else -> "模块未激活"
                }
            mainTextApiWay.visibility = if (isXposedModuleActive) View.VISIBLE else View.GONE
            mainTextApiWay.text =
                "Activated by ${YukiHookModuleStatus.executorName} API ${YukiHookModuleStatus.executorVersion}"
        }
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }
}