package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.Intent
import cn.fkj233.ui.activity.view.TextSummaryV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView

/** 开发者选项 */
object DevSettings: ISubSettings {
    override val titleID = R.string.dev_settings
    override val demoImageID = null

    /** onCreate 事件 */
    override fun create(
        context: SubSettings,
        binding: ActivitySubSettingsBinding
    ): BlockMIUIItemData.() -> Unit = {
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.dev_settings
            ),
            SwitchView(DataConst.ENABLE_DEV_SETTINGS) {
                if (it) return@SwitchView

                context.toast(context.getString(R.string.disabled_dev_settings))
                context.finishAfterTransition()
            }
        )

        TextSummaryArrow(
            TextSummaryV(textId = R.string.hook_info) {
                context.startActivity(Intent(context, SubSettings::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.HOOK_INFO)
                })
            }
        )

        Line()
        TitleText(textId = R.string.icon_settings)

        SeekBarWithStatus(
            titleID = R.string.dev_icon_round_corner_rate,
            pref = DataConst.DEV_ICON_ROUND_CORNER_RATE,
            min = 0,
            max = 50,
            isPercentage = true
        )
    }
}