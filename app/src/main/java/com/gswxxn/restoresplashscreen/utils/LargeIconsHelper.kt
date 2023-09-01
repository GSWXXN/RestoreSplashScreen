package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass

/**
 * 用于从 MIUI 桌面检索大图标的辅助类
 */
class LargeIconsHelper(private val context: Context) {
    private val miuiHomeContext = context.createPackageContext(
        "com.miui.home",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
    )
    private val largeIconsHelperClazz = "com.miui.maml.util.LargeIconsHelper".toClass(miuiHomeContext.classLoader)

    /**
     * 获取指定包名和大小的原始大图标
     *
     * @param packageName 应用程序的包名
     * @param size        图标的大小, 如 "1x2", "2x1", "2x2"
     * @return 原始大图标可绘制对象，如果未找到则为 null
     */
    fun getOriginLargeIconDrawable(packageName: String, size: String ) = try {
        largeIconsHelperClazz.method { name = "getOriginLargeIconDrawable" }.get().call(
            context,
            packageName,
            context.packageManager.getLaunchIntentForPackage(packageName)!!.component!!.className,
            "desktop",
            "",
            size,
            0L,
            UserHandleClass.field { name = "OWNER" }.get().any()
        )!!.current().method { name = "getDrawable" }.invoke<Drawable>()
    } catch (e: Throwable) {
        loggerE(e = e)
        null
    }
}