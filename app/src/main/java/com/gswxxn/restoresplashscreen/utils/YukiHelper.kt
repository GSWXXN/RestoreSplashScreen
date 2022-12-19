package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder.Result.Instance
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object YukiHelper {
    /** 缓存的 [Map]<[String], [String]> 键值数据 */
    private var stringMapData = HashMap<String, Map<String, String>>()
    fun YukiBaseHooker.getMapPrefs(p: PrefsData<MutableSet<String>>) =
        stringMapData.getOrPut(p.key) { prefs.get(p).toMap() }

    /**
     * 根据名称获取实例 的 Field 实例处理类
     *
     * 需要获取 Field 的实例
     * @param fieldName Field 名称
     * @return [Instance]
     */
    @JvmName("getFieldAny")
    fun Any.getField(fieldName : String) = current().field { name = fieldName }.any()
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
     * 给宿主发送通讯，通知配置变化
     */
    fun Context.sendToHost() =
        setOf("android", "com.android.systemui")
            .forEach { dataChannel(it).put("${it.replace('.', '_')}_config_change") }

    /**
     * 宿主注册接收 DataChannel 通知
     */
    fun YukiBaseHooker.register() {
        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_version_get") {
            dataChannel.put(key = "${packageName.replace('.', '_')}_version_result", value = YukiHookAPI.Status.compiledTimestamp.toString())
        }

        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_config_change") {
            prefs.clearCache()
            stringMapData.clear()
        }
    }

    /**
     * 打印日志
     */
    fun YukiBaseHooker.printLog(vararg msg: String) {
        if (prefs.get(DataConst.ENABLE_LOG)) msg.forEach { loggerI(msg = it) }
    }
}