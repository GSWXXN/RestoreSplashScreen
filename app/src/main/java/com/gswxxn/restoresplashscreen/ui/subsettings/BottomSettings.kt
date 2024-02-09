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
import com.highcapable.yukihookapi.hook.factory.prefs

/**
 * 底部 界面
 */
object BottomSettings : ISubSettings {
    override val titleID = R.string.bottom_settings
    override val demoImageID = R.drawable.demo_branding

    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit =  {
        fun getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
            when (flags) {
                0 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            }
        }

        // 移除底部图片
        val removeBrandingImageBinding = getDataBinding(context.prefs().get(DataConst.REMOVE_BRANDING_IMAGE))
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.remove_branding_image,
                tipsId = R.string.remove_branding_image_tips
            ),
            SwitchView(DataConst.REMOVE_BRANDING_IMAGE, dataBindingSend = removeBrandingImageBinding.bindingSend) {
                if (it) context.toast(context.getString(R.string.custom_scope_message))
            }
        )

        // 配置移除列表
        TextSummaryArrow(TextSummaryV(textId = R.string.remove_branding_image_list, onClickListener = {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BRANDING_IMAGE)
            })
        }), dataBindingRecv = removeBrandingImageBinding.getRecv(0))

    }
}