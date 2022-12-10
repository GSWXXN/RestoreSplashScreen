package com.gswxxn.restoresplashscreen.ui.configapps

import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object BrandingImage : IConfigApps {
    override val titleID: Int
        get() = R.string.background_image_title

    override val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.REMOVE_BRANDING_IMAGE_LIST
}