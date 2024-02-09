package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.hook.NewSystemUIHooker.toClass
import com.gswxxn.restoresplashscreen.hook.base.BaseHookHandler
import com.gswxxn.restoresplashscreen.hook.base.HookManager
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder.Result.Instance
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * YukiHookAPI 工具类 */
object YukiHelper {
    /**
     * 读取 MapPrefs
     *
     * @param p [PrefsData] 实例
     * @return [MutableMap]
     */
    fun YukiBaseHooker.getMapPrefs(p: PrefsData<MutableSet<String>>) = prefs.get(p).toMap()
    /**
     * 读取 MapPrefs
     *
     * @param p [PrefsData] 实例
     * @return [MutableMap]
     */
    fun BaseHookHandler.getMapPrefs(p: PrefsData<MutableSet<String>>) = prefs.get(p).toMap()

    /**
     * 根据名称获取实例 的 Field 实例处理类
     *
     * 需要获取 Field 的实例
     * @param fieldName Field 名称
     * @return [Instance]
     */
    @JvmName("getFieldAny")
    fun Any.getField(fieldName : String) = current().field { name = fieldName }.any()

    /**
     * 根据名称获取实例 的 Field 实例处理类, 并转换为指定类型
     *
     * 需要获取 Field 的实例
     * @param fieldName Field 名称
     * @return [Instance]
     */
    fun <T> Any.getField(fieldName : String) = current().field { name = fieldName }.cast<T>()

    /**
     * 根据名称设置实例 的 Field 实例内容
     *
     * 需要设置 Field 的实例
     * @param fieldName Field 名称
     * @param value 设置的实例内容
     */
    fun Any.setField(fieldName : String, value : Any?) = current().field { name = fieldName }.set(value)

    /**
     * 通过 DataChannel 发送消息，检查宿主与模块版本是否一致
     * @param packageName 宿主包名
     * @param result 结果回调
     */
    fun Context.checkingHostVersion(packageName: String, result: (Boolean) -> Unit) {
        this.dataChannel(packageName).wait<String>("${packageName.replace('.', '_')}_version_result") { result(it == YukiHookAPI.Status.compiledTimestamp.toString()) }
        this.dataChannel(packageName).put("${packageName.replace('.', '_')}_version_get")
    }

    /**
     * 通过 DataChannel 发送消息，获取 Hook 信息
     * @param packageName 宿主包名
     * @param result 结果回调
     */
    fun Context.getHookInfo(packageName: String, result: (Map<String, HookManager>) -> Unit) {
        this.dataChannel(packageName).wait<Map<String, ByteArray>>("${packageName.replace('.', '_')}_hook_info_result") {
            val hookInfo = mutableMapOf<String, HookManager>()
            it.forEach { (string, byteArray) ->
                val objectInputStream = ObjectInputStream(ByteArrayInputStream(byteArray))
                hookInfo += string to objectInputStream.readObject() as HookManager
            }
            result(hookInfo)
        }
        this.dataChannel(packageName).put("${packageName.replace('.', '_')}_hook_info_get")
    }

    /**
     * 宿主注册接收 DataChannel 通知
     */
    fun YukiBaseHooker.register() {
        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_version_get") {
            dataChannel.put(key = "${packageName.replace('.', '_')}_version_result", value = YukiHookAPI.Status.compiledTimestamp.toString())
        }
    }

    /**
     * 宿主注册接收获取HookInfo 的 DataChannel 通知
     */
    fun YukiBaseHooker.registerHookInfo(membersObject: Any) {
        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_hook_info_get") {
            val hookInfo: Map<String, ByteArray> = membersObject.javaClass.declaredFields
                .filter { field -> field.apply { isAccessible = true }.get(null) is HookManager }
                .associateBy({ field -> field.name }, { field ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
                    objectOutputStream.writeObject(field.get(null))
                    objectOutputStream.flush()
                    byteArrayOutputStream.toByteArray()
                })
            dataChannel.put(
                key = "${packageName.replace('.', '_')}_hook_info_result",
                value = hookInfo
            )
        }
    }

    /**
     * 打印日志
     */
    fun YukiBaseHooker.printLog(vararg msg: String) = printLog(prefs, *msg)

    /**
     * 打印日志
     */
    fun BaseHookHandler.printLog(vararg msg: String) = printLog(prefs, *msg)

    /**
     * 打印日志
     */
    fun printLog(prefs: YukiHookPrefsBridge, vararg msg: String) {
        if (!prefs.get(DataConst.ENABLE_LOG)) return
        if (System.currentTimeMillis() - prefs.get(DataConst.ENABLE_LOG_TIMESTAMP) > 86400000) return
        msg.forEach { YLog.info(it) }
    }
    /**
    * 当前设备是否是 MIUI 定制 Android 系统
    * @return [Boolean] 是否符合条件
    */
    val isMIUI by lazy { "android.miui.R".hasClass() }

    /**
     * 检测 MIUI 版本是否至少为 14
     *
     * @return [Boolean] 是否符合条件
     */
    val atLeastMIUI14 by lazy {
        isMIUI && try {
            "android.os.SystemProperties".toClass().method {
                name = "get"
                param(StringClass)
            }.get().invoke<String>("ro.miui.ui.version.code") ?: ""
        } catch (e: Throwable) {
            ""
        }.toInt() >= 14
    }

    /**
     * 当前设备是否是 ColorOS 定制 Android 系统
     * @return [Boolean] 是否符合条件
     */
    val isColorOS by lazy { "oppo.R".hasClass() || "com.color.os.ColorBuild".hasClass() || "oplus.R".hasClass() }

    /**
     * 加载 HookHandler
     */
    fun YukiBaseHooker.loadHookHandler(vararg hookHandler: BaseHookHandler) {
        hookHandler.forEach {
            it.baseHooker = this
            it.onHook()
        }
    }

    /**
     * 获取 开发者选项 Prefs 值
     */
    inline fun <reified T> BaseHookHandler.getDevPrefs(prefsData: PrefsData<T>): T {
        if (prefs.get(DataConst.ENABLE_DEV_SETTINGS)) {
           return prefs.get(prefsData)
        }
        return prefsData.value
    }
}