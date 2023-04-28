package com.gswxxn.restoresplashscreen.ui.`interface`

import android.content.Intent
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 配置应用界面接口
 *
 * @property titleID 标题文本 ID, 之后通过 getString(titleID) 获取标题文本
 * @property subSettingHint 子设置界面提示文本 ID, 之后通过 getString(subSettingHint) 获取子设置界面提示文本
 * @property submitSet 是否需要提交保存 [Set] 集合设置
 * @property submitMap 是否需要提交保存 [Map] 集合设置
 * @property checkedListPrefs 保存 [Set] 集合设置的 [PrefsData]
 * @property configMapPrefs 保存 [Map] 集合设置的 [PrefsData]
 */
interface IConfigApps {

    val titleID: Int
    val subSettingHint: Int
        get() = R.string.save_hint
    val submitSet: Boolean
        get() = true
    val submitMap: Boolean
        get() = false
    val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.UNDEFINED_LIST
    val configMapPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.UNDEFINED_LIST

    /**
     * 配置界面右上角的菜单栏事件
     *
     * @param context 当前配置界面的实例
     * @return 返回一个 lambda 表达式, 该表达式会在 ImageView 中被调用
     */
    fun moreOptions (context: ConfigAppsActivity): ImageView.() -> Unit = {
        setOnClickListener { it ->
            PopupMenu(context, it).apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.select_system_apps -> {
                            context.appInfo.getAppInfoList().forEach {
                                if (it.isSystemApp) {
                                    context.appInfo.setChecked(it, true)
                                    context.checkedList.add(it.packageName)
                                }
                            }
                            context.appInfoFilter = context.appInfo.getAppInfoList()
                            context.onRefreshList?.invoke()
                            true
                        }
                        R.id.clear_slection -> {
                            context.appInfo.getAppInfoList().forEach {
                                context.appInfo.setChecked(it, false)
                                context.checkedList.remove(it.packageName)
                            }
                            context.appInfoFilter = context.appInfo.getAppInfoList()
                            context.onRefreshList?.invoke()
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

    /**
     * 在提示文本和列表之间添加一个自定义的 BlockMIUIView, 用于添加一些自定义的设置
     * @see BlockMIUIItemData
     *
     * @param context 当前配置界面的实例
     */
    fun blockMIUIView (context: ConfigAppsActivity): BlockMIUIItemData.() -> Unit = {
        context.findViewById<LinearLayout>(R.id.overall_settings).visibility = View.GONE
    }

    /**
     * 配置每个列表项目的布局
     *
     * @param context 当前配置界面的实例
     * @param holder 当前列表项的 [AdapterConfigBinding] 实例
     * @param item 当前列表项的 [AppInfoHelper.MyAppInfo] 实例
     * @return 返回一个 lambda 表达式, 该表达式会在 TextView 中被调用
     */
    fun adpTextView (
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): TextView.() -> Unit = { }

    /**
     * 配置每个列表项目的复选框
     *
     * @param context 当前配置界面的实例
     * @param holder 当前列表项的 [AdapterConfigBinding] 实例
     * @param item 当前列表项的 [AppInfoHelper.MyAppInfo] 实例
     * @return 返回一个 lambda 表达式, 该表达式会在 CheckBox 中被调用
     */
    fun adpCheckBox (
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): CheckBox.() -> Unit = {
        setOnCheckedChangeListener { _, isChecked ->
            context.appInfo.setChecked(item, isChecked)
            if (isChecked) {
                context.checkedList.add(item.packageName)
            } else {
                context.checkedList.remove(item.packageName)
            }
        }
        isChecked = item.packageName in context.checkedList
    }

    /**
     * 配置每个列表项目的线性布局
     *
     * @param context 当前配置界面的实例
     * @param holder 当前列表项的 [AdapterConfigBinding] 实例
     * @param item 当前列表项的 [AppInfoHelper.MyAppInfo] 实例
     * @return 返回一个 lambda 表达式, 该表达式会在 LinearLayout 中被调用
     */
    fun adpLinearLayout (
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): ((View) -> Unit) = {
        holder.adpAppCheckBox.isChecked = !holder.adpAppCheckBox.isChecked
    }

    /**
     * 重写以处理 onActivityResult
     *
     * @param context 当前子设置界面的实例
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回的 Intent
     *
     * @see SubSettings.onActivityResult
     */
    fun onActivityResult(context: ConfigAppsActivity, requestCode: Int, resultCode: Int, data: Intent?) { }
}