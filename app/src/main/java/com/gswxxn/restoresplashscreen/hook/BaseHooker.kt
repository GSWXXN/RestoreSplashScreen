package com.gswxxn.restoresplashscreen.hook

import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.HostPrefsUtil
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.loggerI

abstract class BaseHooker : YukiBaseHooker()  {
    protected val pref by lazy { HostPrefsUtil(this) }

    protected fun printLog(vararg msg: String) {
        if (pref.get(DataConst.ENABLE_LOG)) msg.forEach { loggerI(msg = it) }
    }
}