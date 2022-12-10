package com.gswxxn.restoresplashscreen.ui.`interface`

import android.content.Intent
import com.gswxxn.restoresplashscreen.databinding.ActivitySubSettingsBinding
import com.gswxxn.restoresplashscreen.ui.SubSettings
import com.gswxxn.restoresplashscreen.view.BlockMIUIItemData

interface ISubSettings {
    val titleID: Int
    val demoImageID: Int
    fun create(context: SubSettings, binding: ActivitySubSettingsBinding): BlockMIUIItemData.() -> Unit

    fun onActivityResult(context: SubSettings, requestCode: Int, resultCode: Int, data: Intent?) { }
}