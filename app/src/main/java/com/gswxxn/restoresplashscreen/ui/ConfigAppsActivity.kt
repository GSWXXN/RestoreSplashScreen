package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.PopupMenu
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivityConfigAppsBinding
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.Utils.sendToHost
import com.gswxxn.restoresplashscreen.utils.Utils.toMap
import com.gswxxn.restoresplashscreen.utils.Utils.toSet
import com.gswxxn.restoresplashscreen.utils.Utils.toast
import com.gswxxn.restoresplashscreen.utils.Utils.toastL
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

class ConfigAppsActivity : BaseActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityConfigAppsBinding
    private lateinit var appInfo: AppInfoHelper
    private lateinit var configMap: MutableMap<String, String>
    private var onRefreshList: (() -> Unit)? = null

    override fun onCreate() {
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        window.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )

        binding = ActivityConfigAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val message = intent.getIntExtra(ConstValue.EXTRA_MESSAGE, 0)

        // 已勾选的应用包名 Set
        val checkedList : MutableSet<String> = modulePrefs.get(
            when (message) {
                ConstValue.CUSTOM_SCOPE -> DataConst.CUSTOM_SCOPE_LIST
                ConstValue.DEFAULT_STYLE -> DataConst.DEFAULT_STYLE_LIST
                ConstValue.BACKGROUND_EXCEPT -> DataConst.BG_EXCEPT_LIST
                ConstValue.BRANDING_IMAGE -> DataConst.REMOVE_BRANDING_IMAGE_LIST
                ConstValue.FORCE_SHOW_SPLASH_SCREEN -> DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST
                ConstValue.MIN_DURATION -> DataConst.MIN_DURATION_LIST
                else -> DataConst.UNDEFINED_LIST
            }).toMutableSet()
        // 应用配置信息
        configMap = modulePrefs.get(
            when (message) {
                ConstValue.MIN_DURATION -> DataConst.MIN_DURATION_CONFIG_MAP
                ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> if (isDarkMode) DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK else DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
                else -> DataConst.UNDEFINED_LIST
            }).toMap()
        // AppInfoHelper 实例
        appInfo = AppInfoHelper(this, checkedList, configMap)
        // 在列表中的条目
        var appInfoFilter =  listOf<AppInfoHelper.MyAppInfo>()

        fun searchEvent() {
            val content = binding.searchEditText.text.toString()
            appInfoFilter = if (content.isBlank()) {
                appInfo.getAppInfoList()
            } else {
                appInfo.getAppInfoList().filter { it.appName.contains(content) or it.packageName.contains(content) }
            }
            onRefreshList?.invoke()
        }

        launch {
            appInfoFilter = withContext(Dispatchers.Default) { appInfo.getAppInfoList() }

            showView(false, binding.configListLoadingView)
            showView(true, binding.configListView)

            // 搜索栏内容改变事件
            binding.searchEditText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchEvent()
                }
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            })
            searchEvent()
        }

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }

        // 标题名称
        binding.appListTitle.text = when (message) {
            ConstValue.CUSTOM_SCOPE -> getString(R.string.custom_scope_title)
            ConstValue.DEFAULT_STYLE -> getString(R.string.default_style_title)
            ConstValue.BACKGROUND_EXCEPT -> getString(R.string.background_except_title)
            ConstValue.BRANDING_IMAGE -> getString(R.string.background_image_title)
            ConstValue.FORCE_SHOW_SPLASH_SCREEN -> getString(R.string.force_show_splash_screen_title)
            ConstValue.MIN_DURATION -> {
                binding.subSettingHint.text = getString(R.string.min_duration_sub_setting_hint)
                getString(R.string.min_duration_title)
            }
            ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> {
                binding.subSettingHint.text = getString(R.string.custom_bg_color_sub_setting_hint)
                getString(R.string.configure_bg_colors_individually)
            }
            else -> "Unavailable"
        }

        // 搜索按钮点击事件
        binding.configTitleFilter.setOnClickListener {
            binding.searchEditText.apply {
                visibility = View.VISIBLE
                requestFocus()
            }
        }

        // 搜索栏事件监听
        binding.searchEditText.apply {
            // 焦点事件
            setOnFocusChangeListener { v, hasFocus ->
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (hasFocus) {
                    showView(false, binding.appListTitle, binding.configDescription, binding.configTitleFilter, binding.overallSettings)
                    // 弹出软键盘
                    imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_FORCED)
                }else {
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(this.windowToken, 0)
                }
            }
        }

        // 总体设置
        binding.overallSettings.addBlockMIUIView(this@ConfigAppsActivity) {
            when (message) {
                ConstValue.MIN_DURATION -> {
                    TextSummaryArrow(TextSummaryV(textId = R.string.set_default_min_duration) {
                        MIUIDialog(this@ConfigAppsActivity) {
                            setTitle(R.string.set_default_min_duration)
                            setMessage(R.string.set_min_duration_unit)
                            setEditText(modulePrefs.get(DataConst.MIN_DURATION).toString(), "")
                            this.javaClass.method {
                                emptyParam()
                                returnType = EditText::class.java
                            }.get(this).invoke<EditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
                            setRButton(R.string.button_okay) {
                                if (getEditText().isNotBlank())
                                    modulePrefs.put(DataConst.MIN_DURATION, getEditText().toInt())
                                else
                                    modulePrefs.put(DataConst.MIN_DURATION, 0)
                                sendToHost(DataConst.MIN_DURATION)
                                dismiss()
                            }
                            setLButton(R.string.button_cancel) { dismiss() }
                        }.show()
                    })
                    Line()
                    TitleText(textId = R.string.min_duration_separate_configuration)
                }
            }
        }

        // 列表
        binding.configListView.apply {
            adapter = object : BaseAdapter() {
                override fun getCount() = appInfoFilter.size

                override fun getItem(position: Int) = appInfoFilter[position]

                override fun getItemId(position: Int) = position.toLong()

                @SuppressLint("SetTextI18n")
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    var cView = convertView
                    val holder: AdapterConfigBinding
                    if (convertView == null) {
                        holder = AdapterConfigBinding.inflate(LayoutInflater.from(context))
                        cView =holder.root
                        cView.tag = holder
                    }else {
                        holder = cView?.tag as AdapterConfigBinding
                    }
                    getItem(position).also { item ->
                        // 设置图标
                        holder.adpAppIcon.setImageDrawable(item.icon)
                        // 设置应用名
                        holder.adpAppName.text = item.appName
                        // 设置包名
                        holder.adpAppPkgName.text = item.packageName
                        // 设置 TextView
                        when (message) {
                            ConstValue.MIN_DURATION -> holder.adpAppTextView.text = if (item.config == null) getString(R.string.not_set_min_duration) else "${item.config} ms"
                            ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> holder.adpAppTextView.apply {
                                if (item.config == null) {
                                    text = getString(R.string.default_value)
                                    background = null
                                } else {
                                    text = ""
                                    width = dp2px(this@ConfigAppsActivity, 30f)
                                    background = GradientDrawable().apply { setColor(
                                        Color.parseColor(item.config))
                                        setStroke(2, getColor(R.color.brandColor))
                                        cornerRadius = dp2px(this@ConfigAppsActivity, 15f).toFloat() }
                                }
                            }
                        }

                        // 设置复选框
                        holder.adpAppCheckBox.apply {
                            if (message == ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG) { visibility = View.GONE }
                            else {
                                setOnCheckedChangeListener { _, isChecked ->
                                    appInfo.setChecked(item, isChecked)
                                    if (isChecked) {
                                        checkedList.add(item.packageName)
                                    } else {
                                        checkedList.remove(item.packageName)
                                    }
                                }
                                isChecked = item.packageName in checkedList
                            }
                        }
                        // 设置 LinearLayout 单击事件
                        holder.adapterLayout.setOnClickListener {
                            when (message) {
                                ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> {
                                    startActivityForResult(Intent(this@ConfigAppsActivity, ColorSelectActivity::class.java).apply {
                                        putExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME, item.packageName)
                                        putExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, appInfo.getIndex(item))
                                        putExtra(ConstValue.EXTRA_MESSAGE_CURRENT_COLOR, item.config)
                                    }, 1)
                                }

                                ConstValue.MIN_DURATION -> {
                                    holder.adpAppCheckBox.isChecked = true
                                    MIUIDialog(this@ConfigAppsActivity) {
                                        setTitle(R.string.set_min_duration)
                                        setMessage(R.string.set_min_duration_unit)
                                        setEditText(item.config ?: "", "")
                                        this@MIUIDialog.javaClass.method {
                                            emptyParam()
                                            returnType = EditText::class.java
                                        }.get(this@MIUIDialog).invoke<EditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
                                        setRButton(R.string.button_okay) {
                                            if (getEditText().isEmpty() || getEditText() == "0") {
                                                appInfo.setConfig(item, null)
                                                holder.adpAppTextView.text = getString(R.string.not_set_min_duration)
                                                configMap.remove(item.packageName)
                                            } else {
                                                appInfo.setConfig(item, getEditText())
                                                holder.adpAppTextView.text = "${getEditText()} ms"
                                                configMap[item.packageName] = getEditText()
                                            }
                                            dismiss()
                                        }
                                        setLButton(R.string.button_cancel) { dismiss() }
                                    }.show()
                                }

                                else -> { holder.adpAppCheckBox.isChecked = !holder.adpAppCheckBox.isChecked }
                            }
                        }
                    }
                    return cView
                }
            }.apply{ onRefreshList = { notifyDataSetChanged() } }
        }

        // 保存按钮点击事件
        binding.configSaveButton.setOnClickListener {
            modulePrefs.put(
                when (message) {
                    ConstValue.CUSTOM_SCOPE -> DataConst.CUSTOM_SCOPE_LIST
                    ConstValue.DEFAULT_STYLE -> DataConst.DEFAULT_STYLE_LIST
                    ConstValue.BACKGROUND_EXCEPT -> DataConst.BG_EXCEPT_LIST
                    ConstValue.BRANDING_IMAGE -> DataConst.REMOVE_BRANDING_IMAGE_LIST
                    ConstValue.FORCE_SHOW_SPLASH_SCREEN -> DataConst.FORCE_SHOW_SPLASH_SCREEN_LIST
                    ConstValue.MIN_DURATION -> DataConst.MIN_DURATION_LIST
                    else -> DataConst.UNDEFINED_LIST
                }.also { sendToHost(it) }, checkedList)

            if (configMap.isNotEmpty())
                modulePrefs.put(
                    when (message) {
                        ConstValue.MIN_DURATION -> DataConst.MIN_DURATION_CONFIG_MAP
                        ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> if (isDarkMode) DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK else DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
                        else -> DataConst.UNDEFINED_LIST
                    }.also { sendToHost(it) }, configMap.toSet())
            toast(getString(R.string.save_successful))
            finish()
        }

        // 菜单栏事件
        binding.moreOptions.apply{
            if (message == ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG) { visibility = View.GONE; return@apply }
            setOnClickListener { it ->
                PopupMenu(this@ConfigAppsActivity, it).apply {
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.select_system_apps -> {
                                appInfo.getAppInfoList().forEach {
                                    if (it.isSystemApp) {
                                        appInfo.setChecked(it, true)
                                        checkedList.add(it.packageName)
                                    }
                                }
                                appInfoFilter = appInfo.getAppInfoList()
                                onRefreshList?.invoke()
                                true
                            }
                            R.id.clear_slection -> {
                                appInfo.getAppInfoList().forEach {
                                    appInfo.setChecked(it, false)
                                    checkedList.remove(it.packageName)
                                }
                                appInfoFilter = appInfo.getAppInfoList()
                                onRefreshList?.invoke()
                                true
                            }
                            else -> false
                        }
                    }
                    inflate(R.menu.more_options_menu)
                    show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.searchEditText.isFocused){
            binding.searchEditText.apply {
                clearFocus()
                visibility = View.GONE
                text = null
            }
            showView(true, binding.appListTitle, binding.configDescription, binding.configTitleFilter, binding.overallSettings)
        }else {
            cancel()
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val color = data?.getStringExtra(ConstValue.EXTRA_MESSAGE_SELECTED_COLOR)
        val pkgName = data?.getStringExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME)
        val index = data?.getIntExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, -1) ?: -1
        if (requestCode != 1 || color == null || index == -1 || pkgName == null) return
        try {
            when (resultCode) {
                ConstValue.SELECTED_COLOR -> {
                    appInfo.setConfig(index, color)
                    onRefreshList?.invoke()
                    configMap[pkgName] = color
                }
                ConstValue.DEFAULT_COLOR -> {
                    appInfo.setConfig(index, null)
                    onRefreshList?.invoke()
                    configMap.remove(pkgName)
                }
                ConstValue.UNDO_MODIFY -> {}
            }
        } catch (_: RuntimeException) {
            toastL(getString(R.string.mode_conflict))
        }

    }
}