package com.gswxxn.restoresplashscreen.utils

import android.annotation.SuppressLint
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
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass

/**
 * 用于从 MIUI 桌面检索大图标的辅助类
 */
@SuppressLint("DiscouragedApi")
class LargeIconsHelper(private val context: Context) {
    private val miuiHomeContext = context.createPackageContext(
        "com.miui.home",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
    )
    private val largeIconsHelperClazz =
        if (YukiHelper.atLeastMIUI14)
            "com.miui.maml.util.LargeIconsHelper".toClass(miuiHomeContext.classLoader)
        else null
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

        // 为获取完美图标时设置一个缓存时间, 避免获取费时图标(如天气)时, 经常显示不出数据的问题 原调用为固定值 0.
        NewSystemUIHooker.findClass("com.miui.maml.util.AppIconsHelper", miuiHomeContext.classLoader).hook {
            injectMember {
                method { name = "getFancyIconDrawable" }
                beforeHook { args(args.indexOfFirst { it is Long }).set(3600000L) }
            }
        }

        // 由于大图标的变更通知不到系统界面, 所以只能每次都重新读取配置
        if (YukiHelper.atLeastMIUI14) {
            NewSystemUIHooker.findClass("com.miui.maml.util.LargeIconsHelper", miuiHomeContext.classLoader).hook {
                injectMember {
                    method { name = "hasLargeIcon" }
                    beforeHook { largeIconsHelperClazz?.field { name = "sManagerList" }?.get()?.set(null) }
                }
            }
        }
    }

    /**
     * 检查给定的程序是否存在大图标
     *
     * @param packageName 要检查的程序包的名称。
     * @return 如果程序包有大图标则返回 `true`，否则返回 `false`。
     */
    fun hasLargeIcon(packageName: String) = try {
        largeIconsHelperClazz?.method {
            name = "hasLargeIcon"
            param(StringClass, StringClass, StringClass, UserHandleClass)
        }?.get()?.boolean (
            packageName,
            null,
            "desktop",
            UserHandleClass.field { name = "CURRENT" }.get().any()
        ) ?: false
    } catch (e: Throwable) {
        loggerE(msg = "Failed to get hasLargeIcon for package $packageName", e = e)
        false
    }

    /**
     * 获取指定程序包的大图标的尺寸。
     *
     * @param packageName 需要获取大图标尺寸的程序包的名称。
     * @return 如果成功获取大图标的尺寸则返回该尺寸，否则在捕获异常后返回null。
     */
    fun getLargeIconSize(packageName: String) = try {
        val iconsConfigs = largeIconsHelperClazz?.method {
            name = "getLargeIconConfigFile"
            param(StringClass, BooleanType)
        }?.get()?.call("desktop", false)?.current()
            ?.method { name = "getIconsConfigs" }
            ?.invoke<HashMap<String, Any>>()
        iconsConfigs?.get(packageName)?.current()?.field { name = "size" }?.string()
    } catch (e: Throwable) {
        loggerE(msg = "Failed to get hasLargeIcon for package $packageName", e = e)
        null
    }

    /**
     * 获取指定包名的大图标
     *
     * @param packageName 应用程序的包名
     * @return 原始大图标可绘制对象，如果未找到则为 null
     */
    fun getLargeIconDrawable(packageName: String) = try {
        largeIconsHelperClazz?.method {
            name = "getLargeIconDrawable"
            param(ContextClass, StringClass, StringClass, StringClass, StringClass, LongType, UserHandleClass)
        }?.get()?.call(
            miuiHomeContext,
            packageName,
            null,
            "desktop",
            null,
            0L,
            UserHandleClass.field { name = "CURRENT" }.get().any()
        )?.current()?.method { name = "getDrawable" }?.invoke<Drawable>()
    } catch (e: Throwable) {
        loggerE(msg = "Failed to get large icon drawable for package $packageName", e = e)
        null
    }

    /**
     * 从指定的应用程序包中获取完美的图标 Drawable
     *
     * @param packageName 要获取图标的应用程序包的包名。
     * @return 如果成功获取到完美图标，则返回一个 BitmapDrawable；如果发生错误，则返回 null。
     */
    fun getFancyIconDrawable(packageName: String) = try {
        val drawable = appIconsHelper.method {
            name = "getIconDrawable"
            param(ContextClass, StringClass, StringClass, LongType)
        }.get().call(
            context,
            packageName,
            null,
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
        loggerE(msg = "Failed to get FancyIconDrawable with package: $packageName", e = e)
        null
    } as Drawable?
}