package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.gswxxn.restoresplashscreen.Data.ConstValue
import com.gswxxn.restoresplashscreen.Data.ConstValue.CUSTOM_SCOPE
import com.gswxxn.restoresplashscreen.Data.ConstValue.DEFAULT_STYLE
import com.gswxxn.restoresplashscreen.Data.ConstValue.EXTRA_MESSAGE
import com.gswxxn.restoresplashscreen.Data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivityConfigAppsBinding
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import org.jetbrains.anko.toast


class ConfigAppsActivity : BaseActivity() {
    private lateinit var binding: ActivityConfigAppsBinding
    private lateinit var appInfo : AppInfoHelper
    private var appInfoFilter = mutableListOf<AppInfoHelper.MyAppInfo>()
    private var onChanged: (() -> Unit)? = null
    private lateinit var isCheckedList : MutableSet<String>
//    private var isCheckedList = mutableSetOf<String>()

    override fun onCreate() {
        binding = ActivityConfigAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val message = intent.getIntExtra(EXTRA_MESSAGE, 0)

        isCheckedList = modulePrefs.get(
            when (message) {
                CUSTOM_SCOPE -> DataConst.CUSTOM_SCOPE_LIST
                DEFAULT_STYLE -> DataConst.DEFAULT_STYLE_LIST
                ConstValue.BACKGROUND_EXCEPT -> DataConst.BG_EXCEPT_LIST
                else -> DataConst.UNDEFINED_LIST
            }).toMutableSet()
        appInfo = AppInfoHelper(isCheckedList)

        binding.configListLoadingView.visibility = View.VISIBLE
        binding.configListView.visibility = View.GONE

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }

        // 标题名称
        binding.appListTitle.text = when (message) {
            CUSTOM_SCOPE -> "作用域列表"
            DEFAULT_STYLE -> "默认风格列表"
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
                    onChanged?.invoke()
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
                            isChecked = it.packageName in isCheckedList
                            setOnCheckedChangeListener { _, isChecked ->
                                appInfo.setChecked(it, isChecked)
                                if (isChecked) {
                                    isCheckedList.add(it.packageName)

                                } else {
                                    isCheckedList.remove(it.packageName)
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
            }.apply{ onChanged = { notifyDataSetChanged() } }

        }

        // 保存按钮点击事件
        binding.configSaveButton.setOnClickListener {
            modulePrefs.put(
                when (message) {
                    CUSTOM_SCOPE -> DataConst.CUSTOM_SCOPE_LIST
                    DEFAULT_STYLE -> DataConst.DEFAULT_STYLE_LIST
                    ConstValue.BACKGROUND_EXCEPT -> DataConst.BG_EXCEPT_LIST
                    else -> DataConst.UNDEFINED_LIST
                }, isCheckedList)
            toast("保存成功，请重启系统界面")
            finish()
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            appInfoFilter = appInfo.getAppInfoList()
            onChanged?.invoke()
            binding.configListLoadingView.visibility = View.GONE
            binding.configListView.visibility = View.VISIBLE
        }
    }
}





