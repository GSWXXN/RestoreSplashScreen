package com.gswxxn.restoresplashscreen.ui.configapps

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.ConstValue.SELECT_COLOR_CODE
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.ColorSelectActivity
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toastL
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/** 单独配置背景颜色 */
object BGColorIndividualConfig : IConfigApps {
    override val titleID: Int
        get() = R.string.configure_bg_colors_individually
    override val subSettingHint: Int
        get() = R.string.custom_bg_color_sub_setting_hint
    override val configMapPrefs: PrefsData<MutableSet<String>>
        get() = if (ConfigAppsActivity.isDarkMode) DataConst.INDIVIDUAL_BG_COLOR_APP_MAP_DARK else DataConst.INDIVIDUAL_BG_COLOR_APP_MAP
    override val submitSet: Boolean
        get() = false
    override val submitMap: Boolean
        get() = true

    override fun adpTextView(
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): TextView.() -> Unit = {
        if (item.config == null) {
            text = context.getString(R.string.default_value)
            background = null
        } else {
            text = ""
            width = dp2px(context, 30f)
            background = GradientDrawable().apply {
                setColor(Color.parseColor(item.config))
                setStroke(2, context.getColor(R.color.brandColor))
                cornerRadius = dp2px(context, 15f).toFloat()
            }
        }
    }

    override fun adpCheckBox(
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): CheckBox.() -> Unit = { visibility = View.GONE }

    override fun adpLinearLayout(
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): (View) -> Unit = {
        context.startActivityForResult(Intent(context, ColorSelectActivity::class.java).apply {
            putExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME, item.packageName)
            putExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, context.appInfo.getIndex(item))
            putExtra(ConstValue.EXTRA_MESSAGE_CURRENT_COLOR, item.config)
        }, SELECT_COLOR_CODE)
    }

    override fun onActivityResult(context: ConfigAppsActivity, requestCode: Int, resultCode: Int, data: Intent?) {
        val color = data?.getStringExtra(ConstValue.EXTRA_MESSAGE_SELECTED_COLOR)
        val pkgName = data?.getStringExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME)
        val index = data?.getIntExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, -1) ?: -1
        if (requestCode != SELECT_COLOR_CODE || color == null || index == -1 || pkgName == null) return
        try {
            when (resultCode) {
                ConstValue.SELECTED_COLOR -> {
                    context.appInfo.setConfig(index, color)
                    context.onRefreshList?.invoke()
                    context.configMap[pkgName] = color
                }

                ConstValue.DEFAULT_COLOR -> {
                    context.appInfo.setConfig(index, null)
                    context.onRefreshList?.invoke()
                    context.configMap.remove(pkgName)
                }

                ConstValue.UNDO_MODIFY -> {}
            }
        } catch (_: RuntimeException) {
            context.toastL(context.getString(R.string.mode_conflict))
        }
    }
}