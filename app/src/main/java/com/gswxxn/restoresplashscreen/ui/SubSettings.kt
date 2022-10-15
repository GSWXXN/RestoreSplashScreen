package com.gswxxn.restoresplashscreen.ui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import cn.fkj233.ui.activity.view.SpinnerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.BuildConfig
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.Utils.sendToHost
import com.gswxxn.restoresplashscreen.utils.Utils.toast
import com.gswxxn.restoresplashscreen.view.InitView
import com.gswxxn.restoresplashscreen.view.SwitchView
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SubSettings : BaseActivity() {
    private lateinit var binding: ActivitySubSettingsBinding

    override fun onCreate() {
        binding = ActivitySubSettingsBinding.inflate(layoutInflater).apply { setContentView(root) }
        val message = intent.getIntExtra(ConstValue.EXTRA_MESSAGE, 0)

        window.apply {
            statusBarColor = getColor(R.color.colorDemoBackground)
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }

        // 标题名称
        binding.appListTitle.text = when (message) {
            ConstValue.BASIC_SETTINGS -> getString(R.string.basic_settings)
            ConstValue.CUSTOM_SCOPE_SETTINGS -> getString(R.string.custom_scope_settings)
            ConstValue.ICON_SETTINGS -> getString(R.string.icon_settings)
            ConstValue.BOTTOM_SETTINGS -> getString(R.string.bottom_settings)
            ConstValue.BACKGROUND_SETTINGS -> getString(R.string.background_settings)
            ConstValue.LAB_SETTINGS -> getString(R.string.lab_settings)
            else -> "Unavailable"
        }

        // 示例图片
        binding.demoImage.setImageDrawable(when (message) {
            ConstValue.BASIC_SETTINGS -> getDrawable(R.drawable.demo_basic)
            ConstValue.CUSTOM_SCOPE_SETTINGS -> getDrawable(R.drawable.demo_scope)
            ConstValue.ICON_SETTINGS -> getDrawable(R.drawable.demo_icon)
            ConstValue.BOTTOM_SETTINGS -> getDrawable(R.drawable.demo_branding)
            ConstValue.BACKGROUND_SETTINGS -> getDrawable(R.drawable.demo_background)
            ConstValue.LAB_SETTINGS -> getDrawable(R.drawable.demo_lab)
            else -> null
        })

        // 设置项
        binding.settingItems.addBlockMIUIView(this) {
            when (message) {
                // 基础设置
                ConstValue.BASIC_SETTINGS -> {
                    // 启用日志
                    TextWithSwitch(TextV(textId = R.string.enable_log), SwitchView(DataConst.ENABLE_LOG))

                    // 隐藏桌面图标
                    TextWithSwitch(TextV(textId = R.string.hide_icon), SwitchView(DataConst.ENABLE_HIDE_ICON) {
                        packageManager.setComponentEnabledSetting(
                            ComponentName(this@SubSettings, "${BuildConfig.APPLICATION_ID}.Home"),
                            if (it) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    })

                    // 遮罩最小持续时间
                    TextSummaryArrow(TextSummaryV(textId = R.string.min_duration, tipsId = R.string.min_duration_tips) {
                        MIUIDialog(this@SubSettings) {
                            setTitle(R.string.set_min_duration)
                            setMessage(R.string.set_min_duration_unit)
                            setEditText(modulePrefs.get(DataConst.MIN_DURATION).toString(), "")
                            this.javaClass.method {
                                returnType = EditText::class.java
                            }.get(this).invoke<EditText>()?.keyListener = DigitsKeyListener.getInstance("1234567890")
                            setRButton(R.string.button_okay) {
                                if (getEditText().isNotBlank()) {
                                    modulePrefs.put(DataConst.MIN_DURATION, getEditText().toInt())
                                    sendToHost(DataConst.MIN_DURATION)
                                }
                                dismiss()
                            }
                            setLButton(R.string.button_cancel) { dismiss() }
                        }.show()
                    })

                    // 启用缓存
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.enable_data_cache, tipsId = R.string.enable_data_cache_tips), SwitchView(DataConst.ENABLE_DATA_CACHE))
                }

                // 作用域
                ConstValue.CUSTOM_SCOPE_SETTINGS -> {
                    // 自定义模块作用域
                    val customScopeBinding = getDataBinding(modulePrefs.get(DataConst.ENABLE_CUSTOM_SCOPE))
                    TextWithSwitch(TextV(textId = R.string.custom_scope), SwitchView(DataConst.ENABLE_CUSTOM_SCOPE, dataBindingSend = customScopeBinding.bindingSend) {
                        if (it) toast(getString(R.string.custom_scope_message))
                    })

                    // 排除模式
                    val exceptionModeBinding = getDataBinding(modulePrefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE))
                    TextWithSwitch(TextV(textId = R.string.exception_mode), SwitchView(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE, dataBindingSend = exceptionModeBinding.bindingSend), dataBindingRecv = customScopeBinding.getRecv(2))

                    // 将作用域外的应用替换位空白启动遮罩
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.replace_to_empty_splash_screen, tipsId = R.string.replace_to_empty_splash_screen_tips), SwitchView(DataConst.REPLACE_TO_EMPTY_SPLASH_SCREEN), dataBindingRecv = customScopeBinding.binding.getRecv(2))

                    // 配置应用列表
                    TextSummaryArrow(TextSummaryV(textId = R.string.exception_mode_list) {
                        startActivity(Intent(this@SubSettings, ConfigAppsActivity::class.java).apply {
                            putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.CUSTOM_SCOPE)
                        })
                    }, dataBindingRecv = customScopeBinding.binding.getRecv(2))

                    CustomView(
                        TextV(
                            textSize = 15F,
                            colorId = R.color.colorTextRed,
                            dataBindingRecv = exceptionModeBinding.getRecv(6)
                        ).create(this@SubSettings, null),
                        dataBindingRecv = customScopeBinding.binding.getRecv(2)
                    )
                }

                // 图标
                ConstValue.ICON_SETTINGS -> {
                    // 绘制图标圆角
                    TextWithSwitch(TextV(textId = R.string.draw_round_corner), SwitchView(DataConst.ENABLE_DRAW_ROUND_CORNER))

                    // 缩小图标
                    val shrinkIconItems = mapOf(0 to getString(R.string.not_shrink_icon), 1 to getString(R.string.shrink_low_resolution_icon), 2 to getString(R.string.shrink_all_icon))
                    TextWithSpinner(TextV(textId = R.string.shrink_icon), SpinnerV(shrinkIconItems[modulePrefs.get(DataConst.SHRINK_ICON)]!!, 180F) {
                        for (item in shrinkIconItems) {
                            add(item.value) {
                                modulePrefs.put(DataConst.SHRINK_ICON, item.key)
                                sendToHost(DataConst.SHRINK_ICON)
                            }
                        }
                    })

                    // 替换图标获取方式
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.replace_icon, tipsId = R.string.replace_icon_tips), SwitchView(DataConst.ENABLE_REPLACE_ICON))

                    // 使用图标包
                    val availableIconPacks = IconPackManager(this@SubSettings).getAvailableIconPacks()
                    TextWithSpinner(TextV(textId = R.string.use_icon_pack), SpinnerV(availableIconPacks[modulePrefs.get(DataConst.ICON_PACK_PACKAGE_NAME)]?:getString(R.string.icon_pack_is_removed)) {
                        for (item in availableIconPacks) {
                            add(item.value) {
                                modulePrefs.put(DataConst.ICON_PACK_PACKAGE_NAME, item.key)
                                sendToHost(DataConst.ICON_PACK_PACKAGE_NAME)
                            }
                        }
                    })

                    Line()

                    // 忽略应用主动设置的图标
                    val defaultStyleBinding = getDataBinding(modulePrefs.get(DataConst.ENABLE_DEFAULT_STYLE))
                    TextSummaryWithSwitch(
                        TextSummaryV(
                            textId = R.string.default_style,
                            tipsId = R.string.default_style_tips
                        ),
                        SwitchView(DataConst.ENABLE_DEFAULT_STYLE, dataBindingSend = defaultStyleBinding.bindingSend) {
                            if (it) {
                                toast(getString(R.string.custom_scope_message))
                                MainScope().launch {
                                    delay(100)
                                    binding.nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
                                }
                            }
                        }
                    )

                    // 配置应用列表
                    TextSummaryArrow(TextSummaryV(textId = R.string.default_style_list, onClickListener = {
                        startActivity(Intent(this@SubSettings, ConfigAppsActivity::class.java).apply {
                            putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.DEFAULT_STYLE)
                        })
                    }), dataBindingRecv = defaultStyleBinding.getRecv(2))
                }

                // 底部
                ConstValue.BOTTOM_SETTINGS -> {
                    // 移除底部图片
                    val removeBrandingImageBinding = getDataBinding(modulePrefs.get(DataConst.REMOVE_BRANDING_IMAGE))
                    TextSummaryWithSwitch(
                        TextSummaryV(
                            textId = R.string.remove_branding_image,
                            tipsId = R.string.remove_branding_image_tips
                        ),
                        SwitchView(DataConst.REMOVE_BRANDING_IMAGE, dataBindingSend = removeBrandingImageBinding.bindingSend) {
                            if (it) toast(getString(R.string.custom_scope_message))
                        }
                    )

                    // 配置移除列表
                    TextSummaryArrow(TextSummaryV(textId = R.string.remove_branding_image_list, onClickListener = {
                        startActivity(Intent(this@SubSettings, ConfigAppsActivity::class.java).apply {
                            putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BRANDING_IMAGE)
                        })
                    }), dataBindingRecv = removeBrandingImageBinding.getRecv(2))
                }

                // 背景
                ConstValue.BACKGROUND_SETTINGS -> {
                    // 设置微信启动背景为深色
                    TextWithSwitch(TextV(textId = R.string.independent_color_wechat), SwitchView(DataConst.INDEPENDENT_COLOR_WECHAT))

                    Line()

                    // 替换背景颜色
                    val changeColorTypeItems = mapOf(0 to getString(R.string.not_change_bg_color), 1 to getString(R.string.from_icon), 2 to getString(R.string.from_monet))
                    val changeBGColorTypeBinding = getDataBinding(changeColorTypeItems[modulePrefs.get(DataConst.CHANG_BG_COLOR_TYPE)]!!)
                    TextWithSpinner(TextV(textId = R.string.change_bg_color), SpinnerV(changeColorTypeItems[modulePrefs.get(DataConst.CHANG_BG_COLOR_TYPE)]!!, 180F, dataBindingSend = changeBGColorTypeBinding.bindingSend){
                        for (item in changeColorTypeItems) {
                            add(item.value) {
                                modulePrefs.put(DataConst.CHANG_BG_COLOR_TYPE, item.key)
                                sendToHost(DataConst.CHANG_BG_COLOR_TYPE)
                            }
                        }
                    })

                    // 颜色模式
                    val colorModeItems = mapOf(0 to getString(R.string.light_color), 1 to getString(R.string.dark_color), 2 to getString(R.string.follow_system))
                    val colorModeBinding = getDataBinding(colorModeItems[modulePrefs.get(DataConst.BG_COLOR_MODE)]!!)
                    TextSummaryWithSpinner(TextSummaryV(textId = R.string.color_mode, tipsId = R.string.color_mode_tips), SpinnerV(colorModeItems[modulePrefs.get(DataConst.BG_COLOR_MODE)]!!, dataBindingSend = colorModeBinding.bindingSend) {
                        for (item in colorModeItems) {
                            add(item.value) {
                                modulePrefs.put(DataConst.BG_COLOR_MODE, item.key)
                                sendToHost(DataConst.BG_COLOR_MODE)
                            }
                        }
                    }, dataBindingRecv = changeBGColorTypeBinding.getRecv(8))

                    // 配置应用列表
                    TextSummaryArrow(TextSummaryV(textId = R.string.change_bg_color_list, onClickListener = {
                        startActivity(Intent(this@SubSettings, ConfigAppsActivity::class.java).apply {
                            putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.BACKGROUND_EXCEPT)
                        })
                    }), dataBindingRecv = changeBGColorTypeBinding.getRecv(8))

                    Line()

                    // 忽略深色模式
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.ignore_dark_mode, tipsId = R.string.ignore_dark_mode_tips), SwitchView(DataConst.IGNORE_DARK_MODE, dataBindingRecv = colorModeBinding.getRecv(7)))

                    // 移除截图背景
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.remove_bg_drawable, tipsId = R.string.remove_bg_drawable_tips), SwitchView(DataConst.REMOVE_BG_DRAWABLE))

                    // 移除背景颜色
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.remove_bg_color, tipsId = R.string.remove_bg_color_tips), SwitchView(DataConst.REMOVE_BG_COLOR))
                }

                // 实验功能
                ConstValue.LAB_SETTINGS -> {
                    // 强制显示遮罩
                    val forceShowSplashScreenBinding = getDataBinding(modulePrefs.get(DataConst.FORCE_SHOW_SPLASH_SCREEN))
                    TextSummaryWithSwitch(
                        TextSummaryV(
                            textId = R.string.force_show_splash_screen,
                            tipsId = R.string.force_show_splash_screen_tips
                        ),
                        SwitchView(DataConst.FORCE_SHOW_SPLASH_SCREEN, dataBindingSend = forceShowSplashScreenBinding.bindingSend) {
                            if (it) toast(getString(R.string.custom_scope_message))
                        }
                    )

                    // 配置应用列表
                    TextSummaryArrow(TextSummaryV(textId = R.string.force_show_splash_screen_list, onClickListener = {
                        startActivity(Intent(this@SubSettings, ConfigAppsActivity::class.java).apply {
                            putExtra(ConstValue.EXTRA_MESSAGE, ConstValue.FORCE_SHOW_SPLASH_SCREEN)
                        })
                    }), dataBindingRecv = forceShowSplashScreenBinding.getRecv(2))

                    Line()

                    // 强制开启启动遮罩
                    val hotStartBinding = getDataBinding(modulePrefs.get(DataConst.ENABLE_HOT_START_COMPATIBLE))
                    val forceEnableSplashScreenBinding = getDataBinding(modulePrefs.get(DataConst.FORCE_ENABLE_SPLASH_SCREEN))
                    TextSummaryWithSwitch(
                        TextSummaryV(
                            textId = R.string.force_enable_splash_screen,
                            tipsId = R.string.force_enable_splash_screen_tips
                        ),
                        SwitchView(
                            DataConst.FORCE_ENABLE_SPLASH_SCREEN,
                            dataBindingRecv = hotStartBinding.getRecv(3),
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
                            dataBindingRecv = forceEnableSplashScreenBinding.getRecv(4),
                            dataBindingSend = hotStartBinding.bindingSend
                        )
                    )

                    // 彻底关闭 Splash Screen
                    TextSummaryWithSwitch(TextSummaryV(textId = R.string.disable_splash_screen, tipsId = R.string.disable_splash_screen_tips), SwitchView(DataConst.DISABLE_SPLASH_SCREEN))
                }
            }
        }
    }

    private fun InitView.ItemData.getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
        when (flags) {
            1 -> (view as Switch).isEnabled = data as Boolean
            2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            3 -> if (data as Boolean) (view as Switch).isChecked = true
            4 -> if (!(data as Boolean)) (view as Switch).isChecked = false
            6 -> (view as TextView).text = getString(R.string.exception_mode_message, getString(if (data as Boolean) R.string.will_not else R.string.will_only))
            7 -> if ((data as String) == getString(R.string.follow_system)) (view as Switch).isChecked = true
            8 -> view.visibility = if ((data as String) == getString(R.string.not_change_bg_color)) View.GONE else View.VISIBLE
        }
    }
}
