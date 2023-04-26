package com.gswxxn.restoresplashscreen.ui.configapps

import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.ui.`interface`.IConfigApps
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

// 背景 - 替换背景颜色 - 排除列表
object BackgroundExcept :IConfigApps {
    override val titleID: Int
        get() = R.string.background_except_title
    override val checkedListPrefs: PrefsData<MutableSet<String>>
        get() = DataConst.BG_EXCEPT_LIST
}