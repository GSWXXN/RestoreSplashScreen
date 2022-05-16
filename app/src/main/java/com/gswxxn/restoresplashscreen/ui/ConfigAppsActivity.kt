package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivityConfigAppsBinding
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.utils.Utils.toast
import com.highcapable.yukihookapi.hook.factory.modulePrefs


class ConfigAppsActivity : BaseActivity() {
    private lateinit var binding: ActivityConfigAppsBinding

    // AppInfoHelper 实例
    private lateinit var appInfo : AppInfoHelper
    // 在列表中的条目
    private var appInfoFilter = mutableListOf<AppInfoHelper.MyAppInfo>()
    // 已勾选的应用包名 Set
    private lateinit var checkedList : MutableSet<String>

    private var onRefreshList: (() -> Unit)? = null

    override fun onCreate() {
        window.insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )

        binding = ActivityConfigAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val message = intent.getIntExtra(ConstValue.EXTRA_MESSAGE, 0)

        checkedList = modulePrefs.get(
            when (message) {
                ConstValue.CUSTOM_SCOPE -> DataConst.CUSTOM_SCOPE_LIST
                ConstValue.DEFAULT_STYLE -> DataConst.DEFAULT_STYLE_LIST
                ConstValue.BACKGROUND_EXCEPT -> DataConst.BG_EXCEPT_LIST
                else -> DataConst.UNDEFINED_LIST
            }).toMutableSet()
        appInfo = AppInfoHelper(checkedList)

        binding.configListLoadingView.visibility = View.VISIBLE
        binding.configListView.visibility = View.GONE

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }

        // 标题名称
        binding.appListTitle.text = when (message) {
            ConstValue.CUSTOM_SCOPE -> "作用域列表"
            ConstValue.DEFAULT_STYLE -> "忽略图标列表"
            ConstValue.BACKGROUND_EXCEPT -> "排除列表"
            else -> "标题"
        }

        // 搜索栏事件监听
        binding.searchEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val content = binding.searchEditText.text.toString()
                    if (content.isBlank()) {
                        appInfoFilter = appInfo.getAppInfoList()
                    } else {
                        appInfoFilter.clear()
                        appInfo.getAppInfoList().forEach {
                            if ((content in it.appName) or (content in it.packageName)) appInfoFilter.add(it)
                        }
                    }
                    onRefreshList?.invoke()
                }
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            })

            // 焦点事件
            setOnFocusChangeListener { v, hasFocus ->
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (hasFocus) {
                    binding.appListTitle.visibility = View.GONE
                    binding.configDescription.visibility = View.GONE
                    binding.configTitleFilter.visibility = View.GONE
                    // 弹出软键盘
                    imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_FORCED)
                }else {
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(this.windowToken, 0)
                }
            }
        }

        // 搜索按钮点击事件
        binding.configTitleFilter.setOnClickListener {
            binding.searchEditText.apply {
                visibility = View.VISIBLE
                requestFocus()
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
                            setOnCheckedChangeListener(null)
                            isChecked = it.packageName in checkedList
                            setOnCheckedChangeListener { _, isChecked ->
                                appInfo.setChecked(it, isChecked)
                                if (isChecked) {
                                    checkedList.add(it.packageName)

                                } else {
                                    checkedList.remove(it.packageName)
                                }
                            }
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
                    else -> DataConst.UNDEFINED_LIST
                }, checkedList)
            toast("保存成功")
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
                            onRefreshList?.invoke()
                            true
                        }
                        R.id.clear_slection -> {
                            appInfo.getAppInfoList().forEach{
                                appInfo.setChecked(it, false)
                                checkedList.remove(it.packageName)
                            }
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
            binding.appListTitle.visibility = View.VISIBLE
            binding.configDescription.visibility = View.VISIBLE
            binding.configTitleFilter.visibility = View.VISIBLE
        }else {
            super.onBackPressed()
        }
    }

    // 获取到焦点后向 appInfoFilter 存完整数据，并刷新列表，防止卡在上一个 Activity
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            appInfoFilter = appInfo.getAppInfoList()
            onRefreshList?.invoke()

            binding.configListLoadingView.visibility = View.GONE
            binding.configListView.visibility = View.VISIBLE
        }
    }
}





