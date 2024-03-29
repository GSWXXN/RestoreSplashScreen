package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.Intent
import android.view.View
import android.widget.Switch
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
import com.highcapable.yukihookapi.hook.factory.prefs

/**
 * 显示设置 界面
 */
object DisplaySettings : ISubSettings {
    override val titleID = R.string.display_settings
    override val demoImageID = R.drawable.demo_display

    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        fun getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> if (data as Boolean) (view as Switch).isChecked = true
                2 -> if (!(data as Boolean)) (view as Switch).isChecked = false
               }
        }

        // 遮罩最小持续时间
        TextSummaryArrow(TextSummaryV(textId = R.string.min_duration, tipsId = R.string.min_duration_tips) {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.MIN_DURATION)
            })
        })

        Line()

        // 强制显示遮罩
        val forceShowSplashScreenBinding = getDataBinding(context.prefs().get(DataConst.FORCE_SHOW_SPLASH_SCREEN))
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.force_show_splash_screen,
                tipsId = R.string.force_show_splash_screen_tips
            ),
            SwitchView(DataConst.FORCE_SHOW_SPLASH_SCREEN, dataBindingSend = forceShowSplashScreenBinding.bindingSend) {
                if (it) context.toast(context.getString(R.string.custom_scope_message))
            }
        )

        // 配置应用列表
        TextSummaryArrow(TextSummaryV(textId = R.string.force_show_splash_screen_list, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.FORCE_SHOW_SPLASH_SCREEN)
            })
        }), dataBindingRecv = forceShowSplashScreenBinding.getRecv(0))

        // 减少不必要的启动遮罩
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.reduce_splash_screen,
                tipsId = R.string.reduce_splash_screen_tips
            ),
            SwitchView(DataConst.REDUCE_SPLASH_SCREEN),
            dataBindingRecv = forceShowSplashScreenBinding.getRecv(0)
        )

        Line()

        // 强制开启启动遮罩
        val hotStartBinding = getDataBinding(context.prefs().get(DataConst.ENABLE_HOT_START_COMPATIBLE))
        val forceEnableSplashScreenBinding = getDataBinding(context.prefs().get(DataConst.FORCE_ENABLE_SPLASH_SCREEN))
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.force_enable_splash_screen,
                tipsId = R.string.force_enable_splash_screen_tips
            ),
            SwitchView(
                DataConst.FORCE_ENABLE_SPLASH_SCREEN,
                dataBindingRecv = hotStartBinding.getRecv(1),
                dataBindingSend = forceEnableSplashScreenBinding.bindingSend
            )
        )

        // 将启动遮罩适用于热启动
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.hot_start_compatible,
                tipsId = R.string.hot_start_compatible_tips
            ),
            SwitchView(
                DataConst.ENABLE_HOT_START_COMPATIBLE,
                dataBindingRecv = forceEnableSplashScreenBinding.getRecv(2),
                dataBindingSend = hotStartBinding.bindingSend
            )
        )

        // 彻底关闭 Splash Screen
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.disable_splash_screen, tipsId = R.string.disable_splash_screen_tips), SwitchView(
                DataConst.DISABLE_SPLASH_SCREEN)
        )

    }
}