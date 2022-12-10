package com.gswxxn.restoresplashscreen.ui.configapps

import android.annotation.SuppressLint
import android.text.method.DigitsKeyListener
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.AdapterConfigBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.gswxxn.restoresplashscreen.utils.AppInfoHelper
import com.gswxxn.restoresplashscreen.utils.YukiHelper.sendToHost
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.modulePrefs
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
                setEditText(context.modulePrefs.get(DataConst.MIN_DURATION).toString(), "")
                this.javaClass.method {
                    emptyParam()
                    returnType = EditText::class.java
                }.get(this).invoke<EditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
                setRButton(R.string.button_okay) {
                    if (getEditText().isNotBlank())
                        context.modulePrefs.put(DataConst.MIN_DURATION, getEditText().toInt())
                    else
                        context.modulePrefs.put(DataConst.MIN_DURATION, 0)
                    context.sendToHost(DataConst.MIN_DURATION)
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
            this@MIUIDialog.javaClass.method {
                emptyParam()
                returnType = EditText::class.java
            }.get(this@MIUIDialog).invoke<EditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
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