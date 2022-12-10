package com.gswxxn.restoresplashscreen.ui.configapps

import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object CustomScope : IConfigApps {
    override val titleID: Int
        get() = R.string.custom_scope_title

    override val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.CUSTOM_SCOPE_LIST
}