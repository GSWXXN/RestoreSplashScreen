package com.gswxxn.restoresplashscreen.ui

import android.widget.Toast
import cn.fkj233.ui.activity.view.TextSummaryV
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivityDevSettingsBinding
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView

/** 开发者选项 */
class DevSettings: BaseActivity<ActivityDevSettingsBinding>() {

    private val itemData: BlockMIUIItemData.() -> Unit = {
        TextSummaryWithSwitch(
            TextSummaryV(
                textId = R.string.dev_settings
            ),
            SwitchView(DataConst.ENABLE_DEV_SETTINGS) {
                if (it) return@SwitchView

                Toast.makeText(
                   this@DevSettings,
                   R.string.disabled_dev_settings,
                   Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
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

    /** onCreate 事件 */
    override fun onCreate() {
        window.statusBarColor = getColor(R.color.colorThemeBackground)

        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.appListTitle.text = getString(R.string.dev_settings)
        binding.settingItems.addBlockMIUIView(this, itemData)
    }
}