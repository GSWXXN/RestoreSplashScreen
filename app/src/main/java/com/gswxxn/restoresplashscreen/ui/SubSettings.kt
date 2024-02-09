package com.gswxxn.restoresplashscreen.ui

import android.content.Intent
import android.view.View
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.BackgroundSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.BasicSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.BottomSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.CustomScopeSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.DevSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.HookInfo
import com.gswxxn.restoresplashscreen.ui.subsettings.IconSettings
import com.gswxxn.restoresplashscreen.ui.subsettings.LabSettings
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView

/** 子界面 */
class SubSettings : BaseActivity<ActivitySubSettingsBinding>() {
    private lateinit var instance: ISubSettings

    override fun onCreate() {
        val message = intent.getIntExtra(ConstValue.EXTRA_MESSAGE, 0)

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }

        when (message) {
            // 基础设置
            ConstValue.BASIC_SETTINGS -> BasicSettings
            // 作用域
            ConstValue.CUSTOM_SCOPE_SETTINGS -> CustomScopeSettings
            // 图标
            ConstValue.ICON_SETTINGS -> IconSettings
            // 底部
            ConstValue.BOTTOM_SETTINGS -> BottomSettings
            // 背景
            ConstValue.BACKGROUND_SETTINGS -> BackgroundSettings
            // 实验功能
            ConstValue.LAB_SETTINGS -> LabSettings
            // Hook 信息
            ConstValue.HOOK_INFO -> HookInfo
            // 开发者选项
            ConstValue.DEV_SETTINGS -> DevSettings

            else -> null
        }?.apply {
            instance = this
            binding.appListTitle.text = getString(titleID)
            demoImageID?.let { binding.demoImage.setImageDrawable(getDrawable(it)) }
                ?: run {
                    binding.demoImageLayout.visibility = View.GONE
                    binding.mainStatus.background = null
                    window.statusBarColor = getColor(R.color.colorThemeBackground)
                }
            binding.settingItems.addBlockMIUIView(this@SubSettings, itemData = create(this@SubSettings, binding))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        instance.onActivityResult(this, requestCode, resultCode, data)
}
