package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue.BACKGROUND_EXCEPT
import com.gswxxn.restoresplashscreen.data.ConstValue.BRANDING_IMAGE
import com.gswxxn.restoresplashscreen.data.ConstValue.CUSTOM_SCOPE
import com.gswxxn.restoresplashscreen.data.ConstValue.DEFAULT_STYLE
import com.gswxxn.restoresplashscreen.data.ConstValue.EXTRA_MESSAGE
import com.gswxxn.restoresplashscreen.data.ConstValue.FORCE_SHOW_SPLASH_SCREEN
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivityMainBinding
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.highcapable.yukihookapi.YukiHookAPI.Status.executorName
import com.highcapable.yukihookapi.YukiHookAPI.Status.executorVersion
import com.highcapable.yukihookapi.YukiHookAPI.Status.isXposedModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.topjohnwu.superuser.Shell

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var appContext: Context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appContext = applicationContext

        binding.apply {

            mainTextVersion.text = "模块版本：${BuildConfig.VERSION_NAME}"

            // 重启UI
            titleRestartIcon.setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("重启提示")
                    .setMessage("配置修改后需要重启系统界面才能生效\n\n如果您LSPosed框架中的作用域包含系统框架，则需要重启手机")
                    .setNeutralButton("取消") { _, _ -> }
                    .setNegativeButton("重启手机") { _, _ ->
                        Shell.cmd("reboot").exec()
                    }
                    .setPositiveButton("重启系统界面") { _, _ ->
                        Shell.cmd(
                            "pkill -f com.android.systemui",
                            "pkill -f com.gswxxn.restoresplashscreen"
                        ).exec()
                    }
                    .show()
            }

            // 关于页面
            titleAboutPage.setOnClickListener {
                val intent = Intent(this@MainActivity, AboutPageActivity::class.java)
                startActivity(intent)
            }

            // 启用日志
            enableLog.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_LOG, isChecked)
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_LOG)
            }

            // 隐藏功能描述
            hideDescribe.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    showView(
                        !isChecked,
                        forceShowSplashScreenDescribe,
                        forceEnableDescribe,
                        defaultStyleDescribe,
                        customScopeDescribe,
                        ignoreDarkModeDescribe,
                        removeBgDrawableDescribe,
                        removeBrandingImageDescribe,
                        replaceBgDescribe,
                        replaceIconDescribe,
                        hideDescribeDescribe,
                        disableSplashScreenDescribe,
                        enableHotStartCompatibleDescribe,
                        removeBgColorDescribe,
                        replaceToEmptySplashScreenDescribe
                    )
                    modulePrefs.put(DataConst.ENABLE_HIDE_DESCRIBE, isChecked)
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_HIDE_DESCRIBE)
            }

            // 自定义模块作用域
            customScope.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    showView(isChecked, customScopeLayout)
                    modulePrefs.put(DataConst.ENABLE_CUSTOM_SCOPE, isChecked)
                }
                setOnLongClickListener {
                    showView(true, customScopeDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
            }

            // 隐藏桌面图标
            hideIconInLauncherSwitch.apply {
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
                intent.putExtra(EXTRA_MESSAGE, CUSTOM_SCOPE)
                startActivity(intent)
            }

            // 将作用域外的应用替换为空白启动遮罩
            replaceToEmptySplashScreen.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN, isChecked)
                }
                setOnLongClickListener {
                    showView(true, replaceToEmptySplashScreenDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN)
            }

            // 绘制图标圆角
            drawIconRoundCorner.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_DRAW_ROUND_CORNER, isChecked)
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_DRAW_ROUND_CORNER)
            }

            // 缩小图标
            shrinkIcon.apply {
                val item = DataConst.SHRINK_ICON_ITEMS.values.toList()

                adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, item)
                    .apply { setDropDownViewResource(R.layout.spinner_dropdown) }

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) { modulePrefs.put(DataConst.SHRINK_ICON, position) }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                setSelection(modulePrefs.get(DataConst.SHRINK_ICON))
            }

            // 替换获取图标方式
            replaceIcon.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_REPLACE_ICON, isChecked)
                }
                setOnLongClickListener {
                    showView(true, replaceIconDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_REPLACE_ICON)
            }

            // 使用图标包
            iconPackList.apply {
                val availableIconPacks = IconPackManager(this@MainActivity).getAvailableIconPacks()
                val item = availableIconPacks.keys.toList()

                adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, item)
                    .apply { setDropDownViewResource(R.layout.spinner_dropdown) }

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        modulePrefs.put(
                            DataConst.ICON_PACK_PACKAGE_NAME,
                            availableIconPacks.values.elementAt(position)
                        )
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                availableIconPacks.forEach {
                    if (it.value == modulePrefs.get(DataConst.ICON_PACK_PACKAGE_NAME)) {
                        setSelection(item.indexOf(it.key))
                    }
                }
            }

            // 忽略应用主动设置的图标
            defaultStyle.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    showView(isChecked, defaultStyleList)
                    modulePrefs.put(DataConst.ENABLE_DEFAULT_STYLE, isChecked)
                }
                setOnLongClickListener {
                    showView(true, defaultStyleDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_DEFAULT_STYLE)
            }

            // 忽略应用主动设置的图标 应用列表
            defaultStyleList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, DEFAULT_STYLE)
                startActivity(intent)
            }

            // 移除底部图片
            removeBrandingImage.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.REMOVE_BRANDING_IMAGE, isChecked)
                    showView(isChecked, removeBrandingImageList)
                }
                setOnLongClickListener {
                    showView(true, removeBrandingImageDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.REMOVE_BRANDING_IMAGE)
            }

            // 移除底部图片 配置列表
            removeBrandingImageList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, BRANDING_IMAGE)
                startActivity(intent)
            }

            // 设置微信背景为黑色
            independentColorWechat.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.INDEPENDENT_COLOR_WECHAT, isChecked)
                    if (isChecked) removeBgColor.isChecked = false
                }
                isChecked = modulePrefs.get(DataConst.INDEPENDENT_COLOR_WECHAT) &&
                        !modulePrefs.get(DataConst.REMOVE_BG_COLOR)
            }

            // 自适应背景颜色
            replaceBg.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_CHANG_BG_COLOR, isChecked)
                    showView(isChecked, bgExceptList)
                    if (isChecked) removeBgColor.isChecked = false
                }
                setOnLongClickListener {
                    showView(true, replaceBgDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_CHANG_BG_COLOR) &&
                        !modulePrefs.get(DataConst.REMOVE_BG_COLOR)
            }

            // 自适应背景颜色排除列表
            bgExceptList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, BACKGROUND_EXCEPT)
                startActivity(intent)
            }

            // 忽略深色模式
            ignoreDarkMode.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.IGNORE_DARK_MODE, isChecked)
                }
                setOnLongClickListener {
                    showView(true, ignoreDarkModeDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.IGNORE_DARK_MODE)
            }

            // 移除背景图片
            removeBgDrawable.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.REMOVE_BG_DRAWABLE, isChecked)
                }
                setOnLongClickListener {
                    showView(true, removeBgDrawableDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.REMOVE_BG_DRAWABLE)&&
                        !modulePrefs.get(DataConst.INDEPENDENT_COLOR_WECHAT) &&
                        !modulePrefs.get(DataConst.ENABLE_CHANG_BG_COLOR)
            }

            // 移除背景颜色
            removeBgColor.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.REMOVE_BG_COLOR, isChecked)
                    if (isChecked) {
                        independentColorWechat.isChecked = false
                        replaceBg.isChecked = false
                    }
                }
                setOnLongClickListener {
                    showView(true, removeBgColorDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.REMOVE_BG_COLOR)
            }

            // 强制显示启动遮罩
            forceShowSplashScreen.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.FORCE_SHOW_SPLASH_SCREEN, isChecked)
                    showView(isChecked, forceShowSplashScreenList)
                }
                setOnLongClickListener {
                    showView(true, forceShowSplashScreenDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN)
            }

            // 强制显示启动遮罩 应用列表
            forceShowSplashScreenList.setOnClickListener {
                val intent = Intent(this@MainActivity, ConfigAppsActivity::class.java)
                intent.putExtra(EXTRA_MESSAGE, FORCE_SHOW_SPLASH_SCREEN)
                startActivity(intent)
            }

            // 强制显示启动遮罩 描述
            forceShowSplashScreenDescribe.text = Html.fromHtml(
                "如果有部分应用没有显示Splash Screen, 请尝试开启此选项并将其加入到应用列表。<br>" +
                "<b>注意</b>：开启此选项需要在Xposed管理器的作用域中勾选<font color = \"#B22222\">系统框架</font>，并重启手机。",
                Html.FROM_HTML_MODE_LEGACY
            )

            // 强制开启启动遮罩
            forceEnableSplashScreen.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.FORCE_ENABLE_SPLASH_SCREEN, isChecked)
                    if (!isChecked) enableHotStartCompatible.isChecked = false
                }
                setOnLongClickListener {
                    showView(true, forceEnableDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN)
            }

            // 强制开启启动遮罩描述
            forceEnableDescribe.text = Html.fromHtml(
                "如果模块显示成功激活但是不起作用，请先尝试 卸载模块 -> 重启系统界面 -> 安装模块 -> 在Xposed管理器中激活 -> 重启系统界面；如果问题依然存在，请尝试开启此选项。<br>" +
                "<i>如果模块可以正常运行，请<font color = \"#B22222\">不要</font>开启此选项。</i><br>" +
                "开启此选项可能导致的问题：<br>" +
                "1. 可能会在不需要显示Splash Screen的场景显示启动遮罩。<br>" +
                "2. 如果应用主动设置了Splash Screen的背景图片，可能将无法显示。",
                Html.FROM_HTML_MODE_LEGACY)

            // 将启动遮罩适用于热启动
            enableHotStartCompatible.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.ENABLE_HOT_START_COMPATIBLE, isChecked)
                    if (isChecked) forceEnableSplashScreen.isChecked = true
                }
                setOnLongClickListener {
                    showView(true, enableHotStartCompatibleDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.ENABLE_HOT_START_COMPATIBLE)
            }

            // 将启动遮罩适用于热启动 描述
            enableHotStartCompatibleDescribe.text = Html.fromHtml(
                "<b>注意</b>：开启此选项需要在Xposed管理器的作用域中勾选<font color = \"#B22222\">系统框架</font>，并重启手机；" +
                        "此选项需要与 \"强制开启启动遮罩\" 同时开启",
                Html.FROM_HTML_MODE_LEGACY)

            // 彻底关闭 Splash Screen
            disableSplashScreen.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    modulePrefs.put(DataConst.DISABLE_SPLASH_SCREEN, isChecked)
                }
                setOnLongClickListener {
                    showView(true, disableSplashScreenDescribe)
                    true
                }
                isChecked = modulePrefs.get(DataConst.DISABLE_SPLASH_SCREEN)
            }

            // 彻底关闭 Splash Screen 描述
            disableSplashScreenDescribe.text = Html.fromHtml(
                "开启此选项需要在Xposed作用域中勾选<font color = \"#B22222\">系统框架</font>并重启手机; " +
                        "由于开启了此选项，模块其他设置将成为摆设，所以没有必要在作用域中勾选系统界面。",
                Html.FROM_HTML_MODE_LEGACY)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun refreshState() {
        binding.apply {
            mainLinStatus.setBackgroundResource(
                when {
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
                    isXposedModuleActive -> "模块已激活"
                    else -> "模块未激活"
                }
            showView(isXposedModuleActive, mainTextApiWay)
            mainTextApiWay.text =
                "Activated by $executorName API $executorVersion"
        }

        window.statusBarColor = getColor(
            when {
                isXposedModuleActive -> R.color.green
                else -> R.color.gray
            }
        )
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }
}