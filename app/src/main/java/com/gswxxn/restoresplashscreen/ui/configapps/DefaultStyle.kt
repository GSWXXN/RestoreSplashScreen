package com.gswxxn.restoresplashscreen.ui.configapps

import android.widget.TextView
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 图标 - 忽略应用主动谁知的图标 - 配置应用列表
 */
object DefaultStyle : IConfigApps {
    override val titleID: Int
        get() = R.string.default_style_title

    override val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.DEFAULT_STYLE_LIST

    override fun blockMIUIView(context: ConfigAppsActivity): BlockMIUIItemData.() -> Unit = {
        fun getDataBinding(pref: Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> (view as TextView).text = context.getString(R.string.exception_mode_message, context.getString(if (data as Boolean) R.string.not_chosen else R.string.chosen))
            }
        }

        // 排除模式
        val exceptionModePrefs = DataConst.IS_DEFAULT_STYLE_LIST_EXCEPTION_MODE
        val exceptionModeBinding = getDataBinding(context.prefs().get(exceptionModePrefs))
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.exception_mode), SwitchView(exceptionModePrefs, dataBindingSend = exceptionModeBinding.bindingSend))

        CustomView(
            TextV(
                textSize = 13F,
                colorId = R.color.colorTextRed,
                dataBindingRecv = exceptionModeBinding.getRecv(0)
            ).create(context, null).apply {
                setPadding(0, 0, 0, 0)
            },
        )
    }
}