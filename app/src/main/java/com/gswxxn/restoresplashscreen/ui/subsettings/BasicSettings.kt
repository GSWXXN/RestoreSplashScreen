package com.gswxxn.restoresplashscreen.ui.subsettings

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import cn.fkj233.ui.activity.view.TextSummaryV
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.ConstValue.CREATE_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.data.ConstValue.OPEN_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.ConfigAppsActivity
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.BackupUtils
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData
import com.gswxxn.restoresplashscreen.view.SwitchView

object BasicSettings : ISubSettings {
    override val titleID: Int = R.string.basic_settings
    override val demoImageID: Int = R.drawable.demo_basic

    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        // 启用日志
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.enable_log), SwitchView(DataConst.ENABLE_LOG))

        // 隐藏桌面图标
        TextSummaryWithSwitch(TextSummaryV(textId = R.string.hide_icon), SwitchView(DataConst.ENABLE_HIDE_ICON) {
            context.packageManager.setComponentEnabledSetting(
                ComponentName(context, "${BuildConfig.APPLICATION_ID}.Home"),
                if (it) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        })

        // 遮罩最小持续时间
        TextSummaryArrow(TextSummaryV(textId = R.string.min_duration, tipsId = R.string.min_duration_tips) {
            context.startActivity(Intent(context, ConfigAppsActivity::class.java).apply {
                putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.MIN_DURATION)
            })
        })

        Line()
        TitleText(textId = R.string.backup_restore_title)

        // 备份设置项
        TextSummaryArrow(TextSummaryV(textId = R.string.backup) {
            BackupUtils.saveFile(context)
        })

        // 恢复设置项
        TextSummaryArrow(TextSummaryV(textId = R.string.restore) {
            BackupUtils.openFile(context)
        })
    }

    override fun onActivityResult(context: SubSettings, requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CREATE_DOCUMENT_CODE -> BackupUtils.handleCreateDocument(context, data?.data)
            OPEN_DOCUMENT_CODE -> BackupUtils.handleReadDocument(context, data?.data)
        }
    }
}