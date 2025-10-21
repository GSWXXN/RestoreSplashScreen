package com.gswxxn.restoresplashscreen.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker.hook
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ApplicationInfoClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.JavaClass

/**
 * 用于从 MIUI 桌面检索大图标的辅助类
 */
@SuppressLint("DiscouragedApi")
class MIUIIconsHelper(private val context: Context, private val classLoader: ClassLoader) {
    private val miuiHomeContext = context.createPackageContext(
        "com.miui.home",
        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
    )
    private val largeIconsHelperClazz =
        if (YukiHelper.atLeastMIUI14)
            "com.miui.maml.util.LargeIconsHelper".toClass(miuiHomeContext.classLoader)
        else null
    private val dependencyClazz by lazy {
        "com.miui.systemui.MiuiDependency".toClassOrNull(classLoader)
            ?: "com.android.systemui.Dependency".toClassOrNull(classLoader)
    }
    private val mDependencyGet by lazy {
        dependencyClazz?.method {
            name = "get"
            paramCount(1)
            param(JavaClass)
        }?.ignored()?.give()
    }
    private val interfacesImplManagerClazz by lazy {
        "com.miui.systemui.interfacesmanager.InterfacesImplManager".toClassOrNull(classLoader)
    }
    private val mImplManagerGet by lazy {
        interfacesImplManagerClazz?.method {
            name = "getImpl"
            paramCount(1)
            param(JavaClass)
        }?.give()
    }
    private val appIconsManagerClazz by lazy {
        "com.miui.systemui.graphics.AppIconsManager".toClass(classLoader)
    }
    private val loadAppIcon by lazy {
        appIconsManagerClazz.getDeclaredMethod("loadAppIcon",
            StringClass, Int::class.java, ApplicationInfoClass, PackageManager::class.java
        )
    }
    private val appIconsManager by lazy {
        mDependencyGet?.invoke(null, appIconsManagerClazz)
            ?: mImplManagerGet?.invoke(null, appIconsManagerClazz)
    }
    private val drawableUtilsClazz by lazy {
        "com.miui.utils.DrawableUtils".toClass(classLoader)
    }
    private val getFancyChildOrSelf by lazy {
        drawableUtilsClazz.method {
            name = "getFancyChildOrSelf"
            paramCount(2)
            param(Drawable::class.java, BooleanType)
        }.ignored().give()
    }

    /** 当前是否启用 MIUI 完美图标 */
    val isSupportMIUIModeIcon by lazy {
        Settings.System.getInt(context.contentResolver, "key_miui_mod_icon_enable", 0) == 1 || getFancyChildOrSelf != null
    }

    init {
        // 防止获取到 System UI 的 Resources
        "miuix.pickerwidget.date.CalendarFormatSymbols".toClass(miuiHomeContext.classLoader).method {
            name = "getWeekDays"
        }.hook {
            replaceAny {
                val resources = miuiHomeContext.resources
                val id = resources.getIdentifier("week_days", "array", "com.miui.home")
                resources.getStringArray(id)
            }
        }

        // 为获取完美图标时设置一个缓存时间, 避免获取费时图标(如天气)时, 经常显示不出数据的问题 原调用为固定值 0.
        "com.miui.maml.util.AppIconsHelper".toClass(miuiHomeContext.classLoader).method {
            name = "getFancyIconDrawable"
        }.hook {
            before {
                val packageName = args(args.indexOfFirst { it is String }).string()
                val cacheTimeIndex = args.indexOfFirst { it is Long }
                args(cacheTimeIndex).set(getCacheTime(packageName))
            }
        }

        // 只获取本地天气数据, 不获取网络数据; 参考 https://zhuti.designer.xiaomi.com/docs/blog/weatherApi.html
        "com.miui.maml.data.ContentProviderBinder".toClass(miuiHomeContext.classLoader).method {
            name = "getUriText"
        }.hook {
            after {
                if (result == "content://weather/actualWeatherData/1")
                    result = "content://weather/actualWeatherData/2"
            }
        }

        // 由于大图标的变更通知不到系统界面, 所以只能每次都重新读取配置
        if (YukiHelper.atLeastMIUI14) {
            "com.miui.maml.util.LargeIconsHelper".toClass( miuiHomeContext.classLoader).method {
                name = "hasLargeIcon"
            }.hook {
                before {
                    largeIconsHelperClazz?.field { name = "sManagerList" }?.get()?.set(null)
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
        YLog.error(msg = "Failed to get hasLargeIcon for package $packageName", e = e)
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
        YLog.error(msg = "Failed to get hasLargeIcon for package $packageName", e = e)
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
        YLog.error(msg = "Failed to get large icon drawable for package $packageName", e = e)
        null
    }

    /**
     * 从指定的应用程序包中获取完美的图标 Drawable
     *
     * @param packageName 要获取图标的应用程序包的包名。
     * @param userId 应用程序的用户 ID。
     * @param applicationInfo 应用程序的 ApplicationInfo 对象。
     * @param packageManager 用于获取应用程序信息的 PackageManager 实例。
     * @return 如果成功获取到完美图标，则返回一个 BitmapDrawable；如果发生错误，则返回 null。
     */
    fun getFancyIconDrawable(packageName: String, userId: Int, applicationInfo: ApplicationInfo?, packageManager: PackageManager?) = try {
        loadAppIcon.invoke(appIconsManager,
            packageName,
            userId,
            applicationInfo,
            packageManager
        )
    } catch (e: Throwable) {
        YLog.error(msg = "Failed to get FancyIconDrawable with package: $packageName", e = e)
        null
    } as Drawable?

    /**
     * 返回给定包名的缓存时间。
     *
     * @param packageName 要获取缓存时间的包名。
     * @return 缓存时间(毫秒)。
     */
    private fun getCacheTime(packageName: String) = when (packageName) {
        "com.miui.weather2" ->  3600000L
        "com.android.deskclock" -> 0L
        else -> 86400000L
    }
}