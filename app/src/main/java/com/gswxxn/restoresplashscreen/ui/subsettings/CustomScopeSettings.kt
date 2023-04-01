package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.Intent
import android.view.View
import cn.fkj233.ui.activity.view.TextSummaryV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView
import com.highcapable.yukihookapi.hook.factory.modulePrefs

object CustomScopeSettings : ISubSettings {
    override val titleID: Int = R.string.custom_scope_settings
    override val demoImageID: Int = R.drawable.demo_scope

    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        fun getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }

        // 自定义模块作用域
        val customScopeBinding = getDataBinding(context.modulePrefs.get(DataConst.ENABLE_CUSTOM_SCOPE))
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.custom_scope), SwitchView(DataConst.ENABLE_CUSTOM_SCOPE, dataBindingSend = customScopeBinding.bindingSend) {
            if (it) context.toast(context.getString(R.string.custom_scope_message))
        })

        // 将作用域外的应用替换位空白启动遮罩
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.replace_to_empty_splash_screen, tipsId = R.string.replace_to_empty_splash_screen_tips), SwitchView(
                DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN), dataBindingRecv = customScopeBinding.binding.getRecv(0))

        // 配置应用列表
        TextSummaryArrow(TextSummaryV(textId = R.string.exception_mode_list) {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.CUSTOM_SCOPE)
            })
        }, dataBindingRecv = customScopeBinding.binding.getRecv(0))
    }
}