package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Switch
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.YukiHelper.sendToHost
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView
import com.highcapable.yukihookapi.hook.factory.modulePrefs

object BackgroundSettings : ISubSettings {
    override val titleID = R.string.background_settings
    override val demoImageID = R.drawable.demo_background

    override fun create(context: Context, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        fun getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> if ((data as String) == context.getString(R.string.follow_system)) (view as Switch).isChecked = true
                1 -> view.visibility = if ((data as String) == context.getString(R.string.not_change_bg_color)) View.GONE else View.VISIBLE
            }
        }
        // 替换背景颜色
        val changeColorTypeItems = mapOf(
            0 to context.getString(R.string.not_change_bg_color),
            1 to context.getString(R.string.from_icon),
            2 to context.getString(R.string.from_monet))
        val changeBGColorTypeBinding = getDataBinding(changeColorTypeItems[context.modulePrefs.get(DataConst.CHANG_BG_COLOR_TYPE)]!!)
        TextWithSpinner(
            TextV(textId = R.string.change_bg_color), SpinnerV(changeColorTypeItems[context.modulePrefs.get(
                DataConst.CHANG_BG_COLOR_TYPE)]!!, 180F, dataBindingSend = changeBGColorTypeBinding.bindingSend){
            for (item in changeColorTypeItems) {
                add(item.value) {
                    context.modulePrefs.put(DataConst.CHANG_BG_COLOR_TYPE, item.key)
                    context.sendToHost(DataConst.CHANG_BG_COLOR_TYPE)
                }
            }
        })

        // 单独配置应用背景颜色
        TextSummaryArrow(TextSummaryV(textId = R.string.configure_bg_colors_individually, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BACKGROUND_INDIVIDUALLY_CONFIG)
            })
        }), dataBindingRecv = changeBGColorTypeBinding.getRecv(1))

        // 颜色模式
        val colorModeItems = mapOf(
            0 to context.getString(R.string.light_color),
            1 to context.getString(R.string.dark_color),
            2 to context.getString(R.string.follow_system))
        val colorModeBinding = getDataBinding(colorModeItems[context.modulePrefs.get(DataConst.BG_COLOR_MODE)]!!)
        TextSummaryWithSpinner(
            TextSummaryV(textId = R.string.color_mode, tipsId = R.string.color_mode_tips), SpinnerV(colorModeItems[context.modulePrefs.get(
                DataConst.BG_COLOR_MODE)]!!, dataBindingSend = colorModeBinding.bindingSend) {
            for (item in colorModeItems) {
                add(item.value) {
                    context.modulePrefs.put(DataConst.BG_COLOR_MODE, item.key)
                    context.sendToHost(DataConst.BG_COLOR_MODE)
                }
            }
        }, dataBindingRecv = changeBGColorTypeBinding.getRecv(1))

        // 配置应用列表
        TextSummaryArrow(TextSummaryV(textId = R.string.change_bg_color_list, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BACKGROUND_EXCEPT)
            })
        }), dataBindingRecv = changeBGColorTypeBinding.getRecv(1))

        Line()

        // 忽略深色模式
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.ignore_dark_mode, tipsId = R.string.ignore_dark_mode_tips), SwitchView(
                DataConst.IGNORE_DARK_MODE, dataBindingRecv = colorModeBinding.getRecv(0))
        )

        // 移除截图背景
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.remove_bg_drawable, tipsId = R.string.remove_bg_drawable_tips), SwitchView(
                DataConst.REMOVE_BG_DRAWABLE)
        )

        // 移除背景颜色
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.remove_bg_color, tipsId = R.string.remove_bg_color_tips), SwitchView(
                DataConst.REMOVE_BG_COLOR)
        )

    }
}