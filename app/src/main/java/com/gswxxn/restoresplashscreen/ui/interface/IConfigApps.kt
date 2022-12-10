package com.gswxxn.restoresplashscreen.ui.`interface`

import android.content.Intent
import android.view.View
import android.widget.*
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

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

    fun blockMIUIView (context: ConfigAppsActivity): BlockMIUIItemData.() -> Unit = {
        context.findViewById<LinearLayout>(R.id.overall_settings).visibility = View.GONE
    }

    fun adpTextView (
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): TextView.() -> Unit = { }

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

    fun adpLinearLayout (
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): ((View) -> Unit) = {
        holder.adpAppCheckBox.isChecked = !holder.adpAppCheckBox.isChecked
    }

    fun onActivityResult(context: ConfigAppsActivity, requestCode: Int, resultCode: Int, data: Intent?) { }
}