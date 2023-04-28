package com.gswxxn.restoresplashscreen.ui.`interface`

import android.content.Intent
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData

/**
 * 创建子设置界面的接口, 需要在 [SubSettings.onCreate] 中注册
 *
 * @property titleID 标题文本 ID, 之后通过 getString(titleID) 获取标题文本
 * @property demoImageID 演示图片 ID, 之后通过 getDrawable(demoImageID) 获取字节面上方演示图片
 */
interface ISubSettings {
    val titleID: Int
    val demoImageID: Int

    /**
     * 创建子设置界面
     *
     * @param context 当前子设置界面的实例
     * @param binding 当前子设置界面的 DataBinding
     * @return 返回一个 lambda 表达式, 该表达式会在 BlockMIUIItemData 中被调用
     */
    fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit

    /**
     * 重写以处理 onActivityResult
     *
     * @param context 当前子设置界面的实例
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回的 Intent
     *
     * @see SubSettings.onActivityResult
     */
    fun onActivityResult(context: SubSettings, requestCode: Int, resultCode: Int, data: Intent?) { }
}