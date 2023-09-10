package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.Intent
import android.view.View
import androidx.core.widget.NestedScrollView
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.YukiHelper
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView
import com.highcapable.yukihookapi.hook.factory.prefs
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 图标 界面
 */
object IconSettings : ISubSettings {
    override val titleID: Int = R.string.icon_settings
    override val demoImageID: Int = R.drawable.demo_icon

    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        fun getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
                1 -> view.visibility = if (data as String != context.getString(R.string.not_shrink_icon)) View.VISIBLE else View.GONE
            }
        }

        // 绘制图标圆角
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.draw_round_corner), SwitchView(DataConst.ENABLE_DRAW_ROUND_CORNER))

        // 缩小图标
        val shrinkIconItems = mapOf(
            0 to context.getString(R.string.not_shrink_icon),
            1 to context.getString(R.string.shrink_low_resolution_icon),
            2 to context.getString(R.string.shrink_all_icon)
        )
        val shrinkIconBinding = getDataBinding(shrinkIconItems[context.prefs().get(DataConst.SHRINK_ICON)]!!)
        TextWithSpinner(TextV(textId = R.string.shrink_icon), SpinnerV(shrinkIconItems[context.prefs().get(DataConst.SHRINK_ICON)]!!, 180F, dataBindingSend = shrinkIconBinding.bindingSend) {
            for (item in shrinkIconItems) {
                add(item.value) {
                    context.prefs().edit { put(DataConst.SHRINK_ICON, item.key) }
                }
            }
        })

        // 为缩小的图标添加模糊背景
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.add_icon_blur_bg),
            SwitchView(DataConst.ENABLE_ADD_ICON_BLUR_BG),
            dataBindingRecv = shrinkIconBinding.getRecv(1)
        )

        // 替换图标获取方式
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.replace_icon, tipsId = R.string.replace_icon_tips),
            SwitchView(DataConst.ENABLE_REPLACE_ICON)
        )

        // 使用 MIUI 大图标
        if (YukiHelper.atLeastMIUI14)
            TextSummaryWithSwitch(
                TextSummaryV(textId = R.string.use_miui_large_icon),
                SwitchView(DataConst.ENABLE_USE_MIUI_LARGE_ICON)
            )

        // 使用图标包
        val availableIconPacks = IconPackManager(context).getAvailableIconPacks()
        TextWithSpinner(
            TextV(textId = R.string.use_icon_pack), SpinnerV(availableIconPacks[context.prefs().get(DataConst.ICON_PACK_PACKAGE_NAME)]?:context.getString(
                R.string.icon_pack_is_removed)) {
            for (item in availableIconPacks) {
                add(item.value) {
                    context.prefs().edit { put(DataConst.ICON_PACK_PACKAGE_NAME, item.key) }
                }
            }
        })

        Line()

        // 忽略应用主动设置的图标
        val defaultStyleBinding = getDataBinding(context.prefs().get(DataConst.ENABLE_DEFAULT_STYLE))
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.default_style,
                tipsId = R.string.default_style_tips
            ),
            SwitchView(DataConst.ENABLE_DEFAULT_STYLE, dataBindingSend = defaultStyleBinding.bindingSend) {
                if (it) {
                    context.toast(context.getString(R.string.custom_scope_message))
                }
            }
        )

        // 配置应用列表
        TextSummaryArrow(TextSummaryV(textId = R.string.default_style_list, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.DEFAULT_STYLE)
            })
        }), dataBindingRecv = defaultStyleBinding.getRecv(0))

        Line()

        // 不显示图标
        val hideSplashScreenIconBinding = getDataBinding(context.prefs().get(DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON))
        TextSummaryWithSwitch(
            TextSummaryV(textId = R.string.hide_splash_screen_icon),
            SwitchView(DataConst.ENABLE_HIDE_SPLASH_SCREEN_ICON, dataBindingSend = hideSplashScreenIconBinding.bindingSend) {
                if (it) {
                    context.toast(context.getString(R.string.custom_scope_message))
                    MainScope().launch {
                        delay(100)
                        binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
                    }
                }
            }
        )

        // 配置应用列表
        TextSummaryArrow(TextSummaryV(textId = R.string.default_style_list, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.HIDE_SPLASH_SCREEN_ICON)
            })
        }), dataBindingRecv = hideSplashScreenIconBinding.getRecv(0))
    }
}