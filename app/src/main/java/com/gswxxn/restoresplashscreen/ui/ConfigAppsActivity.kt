package com.gswxxn.restoresplashscreen.ui

import android.content.Intent
import android.content.res.Configuration
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.databinding.ActivityConfigAppsBinding
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.configapps.BGColorIndividualConfig
import com.gswxxn.restoresplashscreen.ui.configapps.BackgroundExcept
import com.gswxxn.restoresplashscreen.ui.configapps.BrandingImage
import com.gswxxn.restoresplashscreen.ui.configapps.CustomScope
import com.gswxxn.restoresplashscreen.ui.configapps.DefaultStyle
import com.gswxxn.restoresplashscreen.ui.configapps.ForceShowSplashScreen
import com.gswxxn.restoresplashscreen.ui.configapps.HideSplashScreenIcon
import com.gswxxn.restoresplashscreen.ui.configapps.MinDuration
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.CommonUtils.notEqualsTo
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toSet
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.hook.factory.prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 配置应用列表 Activity
 */
class ConfigAppsActivity : BaseActivity<ActivityConfigAppsBinding>(), CoroutineScope by MainScope() {
    companion object {
        var isDarkMode: Boolean = false
    }

    lateinit var appInfo: AppInfoHelper
    lateinit var configMap: MutableMap<String, String>
    lateinit var checkedList: MutableSet<String>
    lateinit var appInfoFilter: List<AppInfoHelper.MyAppInfo>
    lateinit var instance: IConfigApps
    var onRefreshList: (() -> Unit)? = null

    override fun onCreate() {
        isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        instance = when (intent.getIntExtra(ConstValue.EXTRA_MESSAGE, 0)) {
            ConstValue.CUSTOM_SCOPE -> CustomScope
            ConstValue.DEFAULT_STYLE -> DefaultStyle
            ConstValue.BACKGROUND_EXCEPT -> BackgroundExcept
            ConstValue.BRANDING_IMAGE -> BrandingImage
            ConstValue.FORCE_SHOW_SPLASH_SCREEN -> ForceShowSplashScreen
            ConstValue.MIN_DURATION -> MinDuration
            ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG -> BGColorIndividualConfig
            ConstValue.HIDE_SPLASH_SCREEN_ICON -> HideSplashScreenIcon
            else -> {
                object : IConfigApps {
                    override val titleID: Int get() = R.string.unavailable
                    override val submitSet: Boolean
                        get() = false
                }
            }
        }

        // 已勾选的应用包名 Set
        checkedList = prefs().get(instance.checkedListPrefs).toMutableSet()
        // 应用配置信息
        configMap = prefs().get(instance.configMapPrefs).toMap()
        // AppInfoHelper 实例
        appInfo = AppInfoHelper(this, checkedList, configMap)
        // 在列表中的条目
        appInfoFilter = listOf()

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

        // 返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { finishAfterTransition() }

        // 标题名称
        binding.appListTitle.text = getString(instance.titleID)
        binding.subSettingHint.text = getString(instance.subSettingHint)

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
                val imm = v.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if (hasFocus) {
                    showView(false, binding.appListTitle, binding.configDescription, binding.configTitleFilter, binding.overallSettings)
                    // 弹出软键盘
                    imm.showSoftInput(binding.searchEditText, 0)
                } else {
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(this.windowToken, 0)
                }
            }
        }

        // 总体设置
        binding.overallSettings.addBlockMIUIView(this@ConfigAppsActivity, itemData = instance.blockMIUIView(this@ConfigAppsActivity))

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
                        cView = holder.root
                        cView.tag = holder
                    } else {
                        holder = cView.tag as AdapterConfigBinding
                    }
                    getItem(position).also { item ->
                        // 设置图标
                        holder.adpAppIcon.setImageDrawable(item.icon)
                        // 设置应用名
                        holder.adpAppName.text = item.appName
                        // 设置包名
                        holder.adpAppPkgName.text = item.packageName
                        // 设置 TextView
                        holder.adpAppTextView.apply(instance.adpTextView(this@ConfigAppsActivity, holder, item))
                        // 设置复选框
                        holder.adpAppCheckBox.apply(instance.adpCheckBox(this@ConfigAppsActivity, holder, item))
                        // 设置 LinearLayout 单击事件
                        holder.adapterLayout.setOnClickListener(instance.adpLinearLayout(this@ConfigAppsActivity, holder, item))
                    }
                    return cView
                }
            }.apply { onRefreshList = { notifyDataSetChanged() } }
        }

        // 保存按钮点击事件
        binding.configSaveButton.setOnClickListener {
            prefs().edit {
                if (instance.submitSet)
                    put(instance.checkedListPrefs, checkedList)
                if (instance.submitMap)
                    put(instance.configMapPrefs, configMap.toSet())
            }
            toast(getString(R.string.save_successful))
            finish()
        }

        // 菜单栏事件
        binding.moreOptions.apply(instance.moreOptions(this))
    }

    override fun finishAfterTransition() {
        fun isNeedSavePrompt() = (instance.submitSet && prefs().get(instance.checkedListPrefs) notEqualsTo checkedList) ||
                (instance.submitMap && prefs().get(instance.configMapPrefs) notEqualsTo configMap.toSet())

        if (binding.searchEditText.isFocused) {
            binding.searchEditText.apply {
                clearFocus()
                visibility = View.GONE
                text = null
            }
            showView(true, binding.appListTitle, binding.configDescription, binding.configTitleFilter, binding.overallSettings)
        } else if (isNeedSavePrompt()) {
            MIUIDialog(this) {
                setTitle(getString(R.string.not_saved_title))
                setMessage(getString(R.string.not_saved_hint))
                setRButton(getString(R.string.button_abandonment)) { this@ConfigAppsActivity.cancel(); super.finishAfterTransition() }
                setLButton(getString(R.string.button_reedit)) { dismiss() }
            }.show()
        } else {
            cancel()
            super.finishAfterTransition()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        instance.onActivityResult(this, requestCode, resultCode, data)
}