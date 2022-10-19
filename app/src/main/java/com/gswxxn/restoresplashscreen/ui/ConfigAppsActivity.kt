package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.PopupMenu
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivityConfigAppsBinding
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.utils.Utils.sendToHost
import com.gswxxn.restoresplashscreen.utils.Utils.toast
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

class ConfigAppsActivity : BaseActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityConfigAppsBinding

    override fun onCreate() {
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
                else -> DataConst.UNDEFINED_LIST
            }).toMutableSet()
        // AppInfoHelper 实例
        val appInfo = AppInfoHelper(this, checkedList)
        // 在列表中的条目
        var appInfoFilter =  listOf<AppInfoHelper.MyAppInfo>()

        var onRefreshList: (() -> Unit)? = null

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
                    showView(false, binding.appListTitle, binding.configDescription, binding.configTitleFilter)
                    // 弹出软键盘
                    imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_FORCED)
                }else {
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(this.windowToken, 0)
                }
            }
        }

        // 列表
        binding.configListView.apply {
            adapter = object : BaseAdapter() {
                override fun getCount() = appInfoFilter.size

                override fun getItem(position: Int) = appInfoFilter[position]

                override fun getItemId(position: Int) = position.toLong()

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
                    getItem(position).also {
                        // 设置图标
                        holder.adpAppIcon.setImageDrawable(it.icon)
                        // 设置应用名
                        holder.adpAppName.text = it.appName
                        // 设置包名
                        holder.adpAppPkgName.text = it.packageName
                        // 设置复选框
                        holder.adpAppCheckBox.apply {
                            setOnCheckedChangeListener { _, isChecked ->
                                appInfo.setChecked(it, isChecked)
                                if (isChecked) {
                                    checkedList.add(it.packageName)

                                } else {
                                    checkedList.remove(it.packageName)
                                }
                            }
                            isChecked = it.packageName in checkedList
                        }
                        // 设置LinearLayout
                        holder.adapterLayout.setOnClickListener {
                            holder.adpAppCheckBox.isChecked = !holder.adpAppCheckBox.isChecked
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
                    else -> DataConst.UNDEFINED_LIST
                }.also { sendToHost(it) }, checkedList)
            toast(getString(R.string.save_successful))
            finish()
        }

        // 菜单栏事件
        binding.moreOptions.setOnClickListener { it ->
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.select_system_apps -> {
                            appInfo.getAppInfoList().forEach{
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
                            appInfo.getAppInfoList().forEach{
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

    override fun onBackPressed() {
        if (binding.searchEditText.isFocused){
            binding.searchEditText.apply {
                clearFocus()
                visibility = View.GONE
                text = null
            }
            showView(true, binding.appListTitle, binding.configDescription, binding.configTitleFilter)
        }else {
            cancel()
            super.onBackPressed()
        }
    }
}