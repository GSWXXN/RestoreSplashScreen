package com.gswxxn.restoresplashscreen.ui.subsettings

import android.widget.LinearLayout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.ui.`interface`.ISubSettings
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.YukiHelper.getHookInfo
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData

/**
 * Hook 信息 界面
 */
object HookInfo: ISubSettings {
    override val titleID = R.string.hook_info
    override val demoImageID = null

    /**
     * 创建一个新的 BlockMIUIItemData 实例
     */
    override fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit = {
        context.getHookInfo("com.android.systemui") { hookInfo ->
            redrawView(context, binding.settingItems, hookInfo)
        }
    }

    /**
     * 重新绘制视图，使用给定的上下文和钩子管理器的映射。
     * 从线性布局中移除所有现有视图，并为映射中的每个条目添加新的 TextSummary 视图，
     * 按照 Hook 是否可能异常以及键进行排序
     *
     * @param context 用于创建视图的上下文。
     * @param linearLayout 要重新绘制的线性布局。
     * @param map 钩子管理器的映射。
     */
    private fun redrawView(context: SubSettings, linearLayout: LinearLayout, map: Map<String, HookManager.HookInfo>) {
        linearLayout.removeAllViews()
        linearLayout.addBlockMIUIView(context) {
            map.entries.sortedWith(compareBy({ !it.value.isAbnormal }, {it.key})).forEach { (key, hookInfo) ->
                TextSummary(
                    text = key,
                    colorInt = if (hookInfo.isAbnormal) Color.Red.toArgb() else null,
                    tips = "createCondition: ${hookInfo.createCondition}\n" +
                            "isMemberFound: ${hookInfo.isMemberFound}\n" +
                            "hasBeforeHooks: ${hookInfo.hasBeforeHooks}\n" +
                            "isBeforeHookExecuted: ${hookInfo.isBeforeHookExecuted}\n" +
                            "hasAfterHooks: ${hookInfo.hasAfterHooks}\n" +
                            "isAfterHookExecuted: ${hookInfo.isAfterHookExecuted}\n" +
                            "hasReplaceHook: ${hookInfo.hasReplaceHook}\n" +
                            "isReplaceHookExecuted: ${hookInfo.isReplaceHookExecuted}"
                )
            }
        }
    }
}