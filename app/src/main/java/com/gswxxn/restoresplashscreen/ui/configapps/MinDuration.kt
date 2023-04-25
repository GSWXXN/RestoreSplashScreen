package com.gswxxn.restoresplashscreen.ui.configapps

import android.annotation.SuppressLint
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.fkj233.ui.activity.view.MIUIEditText
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object MinDuration : IConfigApps {
    override val titleID: Int = R.string.min_duration_title
    override val subSettingHint: Int
        get() = R.string.min_duration_sub_setting_hint
    override val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.MIN_DURATION_LIST
    override val configMapPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.MIN_DURATION_CONFIG_MAP
    override val submitMap: Boolean
        get() = true

    override fun moreOptions(context: ConfigAppsActivity): ImageView.() -> Unit = {
        visibility = View.GONE
    }

    override fun blockMIUIView(context: ConfigAppsActivity): BlockMIUIItemData.() -> Unit = {
        TextSummaryArrow(TextSummaryV(textId = R.string.set_default_min_duration) {
            MIUIDialog(context) {
                setTitle(R.string.set_default_min_duration)
                setMessage(R.string.set_min_duration_unit)
                setEditText(context.prefs().get(DataConst.MIN_DURATION).toString(), "")
                current().method {
                    emptyParam()
                    returnType = MIUIEditText::class.java
                }.invoke<MIUIEditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
                setRButton(R.string.button_okay) {
                    context.prefs().edit {
                        if (getEditText().isNotBlank())
                            put(DataConst.MIN_DURATION, getEditText().toInt())
                        else
                            put(DataConst.MIN_DURATION, 0)
                    }
                    dismiss()
                }
                setLButton(R.string.button_cancel) { dismiss() }
            }.show()
        })
        Line()
        TitleText(textId = R.string.min_duration_separate_configuration)
    }

    override fun adpTextView(
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): TextView.() -> Unit = {
        holder.adpAppTextView.text =
            if (item.config == null) context.getString(R.string.not_set_min_duration)
            else "${item.config} ms"
    }

    @SuppressLint("SetTextI18n")
    override fun adpLinearLayout(
        context: ConfigAppsActivity,
        holder: AdapterConfigBinding,
        item: AppInfoHelper.MyAppInfo
    ): (View) -> Unit = {
        holder.adpAppCheckBox.isChecked = true
        MIUIDialog(context) {
            setTitle(R.string.set_min_duration)
            setMessage(R.string.set_min_duration_unit)
            setEditText(item.config ?: "", "")
            current().method {
                emptyParam()
                returnType = MIUIEditText::class.java
            }.invoke<MIUIEditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
            setRButton(R.string.button_okay) {
                if (getEditText().isEmpty() || getEditText() == "0") {
                    context.appInfo.setConfig(item, null)
                    holder.adpAppTextView.text = context.getString(R.string.not_set_min_duration)
                    context.configMap.remove(item.packageName)
                } else {
                    context.appInfo.setConfig(item, getEditText())
                    holder.adpAppTextView.text = "${getEditText()} ms"
                    context.configMap[item.packageName] = getEditText()
                }
                dismiss()
            }
            setLButton(R.string.button_cancel) { dismiss() }
        }.show()
    }
}