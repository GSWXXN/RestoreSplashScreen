package com.gswxxn.restoresplashscreen.ui

import android.content.Context
import android.content.Intent
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.databinding.ActivityMainSettingsBinding
import com.gswxxn.restoresplashscreen.view.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.Utils.shrinkIcon
import com.gswxxn.restoresplashscreen.utils.Utils.toast
import com.gswxxn.restoresplashscreen.view.NewMIUIDialog
import com.highcapable.yukihookapi.YukiHookAPI
import com.topjohnwu.superuser.Shell


class MainSettingsActivity : BaseActivity() {

    companion object {
        lateinit var appContext: Context
    }

    private lateinit var binding: ActivityMainSettingsBinding

    override fun onCreate() {
        appContext = this
        binding = ActivityMainSettingsBinding.inflate(layoutInflater).apply { setContentView(root) }

        binding.mainTextVersion.text = getString(R.string.module_version, BuildConfig.VERSION_NAME)

        // 重启UI
        binding.titleRestartIcon.setOnClickListener {
            NewMIUIDialog(this) {
                setTitle(R.string.restart_title)
                setMessage(R.string.restart_message)
                Button(getString(R.string.reboot)) {
                    Shell.cmd("reboot").exec()
                }
                Button(getString(R.string.restart_system_ui)) {
                    Shell.cmd(
                        "pkill -f com.android.systemui",
                        "pkill -f com.gswxxn.restoresplashscreen"
                    ).exec()
                }
                Button(getString(R.string.cancel), cancelStyle = true) {
                    dismiss()
                }
            }.show()
        }

        // 关于页面
        binding.titleAboutPage.setOnClickListener {
            val intent = Intent(this, AboutPageActivity::class.java)
            startActivity(intent)
        }

        binding.settingsEntry.addBlockMIUIView(this) {
            Author(shrinkIcon(R.drawable.ic_setting), getString(R.string.basic_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BASIC_SETTINGS)
                })
            })

            Line(true)

            Author(shrinkIcon(R.drawable.ic_app), getString(R.string.custom_scope_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.CUSTOM_SCOPE_SETTINGS)
                })
            })

            Author(shrinkIcon(R.drawable.ic_picture), getString(R.string.icon_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.ICON_SETTINGS)
                })
            })

            Author(shrinkIcon(R.drawable.ic_bottom), getString(R.string.bottom_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BOTTOM_SETTINGS)
                })
            })

            Author(shrinkIcon(R.drawable.ic_color), getString(R.string.background_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BACKGROUND_SETTINGS)
                })
            })

            Author(shrinkIcon(R.drawable.ic_lab), getString(R.string.lab_settings), null, 0f, {
                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.LAB_SETTINGS)
                })
            })

            Line(true)

            Author(shrinkIcon(R.drawable.ic_warn), getString(R.string.faq), null, 0f, {
                toast("Unavailable for now")
//                startActivity(Intent(this@MainSettingsActivity, SubSettings::class.java).apply {
//                    putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.FAQ)
//                })
            })
        }
    }

    private fun refreshState() {
        binding.mainStatus.setBackgroundResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.bg_green_round
                else -> R.drawable.bg_dark_round
            }
        )
        binding.mainImgStatus.setImageResource(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.drawable.ic_success
                else -> R.drawable.ic_warn
            }
        )
        binding.mainTextStatus.text =
            when {
                YukiHookAPI.Status.isXposedModuleActive -> getString(R.string.module_is_active)
                else -> getString(R.string.module_is_not_active)
            }
        showView(YukiHookAPI.Status.isXposedModuleActive, binding.mainTextApiWay)
        binding.mainTextApiWay.text =
            getString(R.string.xposed_framework_version,
                YukiHookAPI.Status.executorName,
                YukiHookAPI.Status.executorVersion)

        window.statusBarColor = getColor(
            when {
                YukiHookAPI.Status.isXposedModuleActive -> R.color.green
                else -> R.color.gray
            }
        )
    }

    override fun onResume() {
        super.onResume()
        refreshState()
    }
}