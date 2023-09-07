package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.provider.Settings
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker.hook
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.getCenterDrawable
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass

/**
 * 用于从 MIUI 桌面检索大图标的辅助类
 */
class LargeIconsHelper(private val context: Context) {
    private val miuiHomeContext = context.createPackageContext(
        "com.miui.home",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
    )
    private val largeIconsHelperClazz = "com.miui.maml.util.LargeIconsHelper".toClass(miuiHomeContext.classLoader)
    private val appIconsHelper = "com.miui.maml.util.AppIconsHelper".toClass(miuiHomeContext.classLoader)
    /** 当前是否启用 MIUI 完美图标 */
    val isSupportMIUIModeIcon by lazy {
        Settings.System.getInt(context.contentResolver, "key_miui_mod_icon_enable", 0) == 1
    }

    init {
        // 防止获取到 System UI 的 Resources
        NewSystemUIHooker.findClass("miuix.pickerwidget.date.CalendarFormatSymbols", miuiHomeContext.classLoader).hook {
            injectMember {
                method { name = "getWeekDays" }
                replaceAny {
                    val resources = miuiHomeContext.resources
                    val id = resources.getIdentifier("week_days", "array", "com.miui.home")
                    resources.getStringArray(id)
                }
            }
        }
    }

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

    /**
     * 从指定的应用程序包中获取完美的图标 Drawable
     *
     * @param packageName 要获取图标的应用程序包的包名。
     * @return 如果成功获取到精美的图标，则返回一个 BitmapDrawable；如果发生错误，则返回 null。
     */
    fun getFancyIconDrawable(packageName: String) = try {
        val drawable = appIconsHelper.method {
            name = "getIconDrawable"
            param(ContextClass, StringClass, StringClass, LongType)
        }.get().call(
            context,
            packageName,
            context.packageManager.getLaunchIntentForPackage(packageName)!!.component!!.className,
            3600000L,
        )

        if (drawable is AdaptiveIconDrawable && drawable.javaClass.name == "com.miui.maml.MamlAdaptiveIconDrawable"){
            val layer0QuietDrawable = drawable.background.current().method { name = "getQuietDrawable" }.invoke<Drawable>()!!
            val layerFancyDrawables = drawable.current().method { name = "getLayerFancyDrawables" }.invoke<ArrayList<Drawable>>()!!

            layerFancyDrawables.add(0, layer0QuietDrawable)

            getCenterDrawable(
                LayerDrawable(layerFancyDrawables.toTypedArray()),
                0.60f,
                context.resources
            )
        } else if (drawable?.javaClass?.name == "com.miui.maml.FancyDrawable") {
            drawable
        } else null
    } catch (e: Throwable) {
        loggerE(e = e)
        null
    } as Drawable?
}