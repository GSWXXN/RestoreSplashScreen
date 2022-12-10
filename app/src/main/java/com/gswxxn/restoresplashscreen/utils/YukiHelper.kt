package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder.Result.Instance
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object YukiHelper {
    /**
     * 根据名称获取实例 的 Field 实例处理类
     *
     * 需要获取 Field 的实例
     * @param fieldName Field 名称
     * @return [Instance]
     */
    fun Any.getField(fieldName : String) = this.javaClass.field { name = fieldName }.get(this)

    /**
     * 根据名称设置实例 的 Field 实例内容
     *
     * 需要设置 Field 的实例
     * @param fieldName Field 名称
     * @param value 设置的实例内容
     */
    fun Any.setField(fieldName : String, value : Any?) =
        this.javaClass.field { name = fieldName }.get(this).set(value)

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
     * @param prefsData 键值对存储实例
     */
    inline fun <reified T> Context.sendToHost(prefsData: PrefsData<T>) {
        val host = if (prefsData.key in setOf(
                "force_show_splash_screen_list",
                "force_show_splash_screen",
                "disable_splash_screen",
                "enable_hot_start_compatible")
        )
            "android"
        else "com.android.systemui"
        val key = "${host.replace('.', '_')}_config_change"
        val value = "${prefsData.key}-${
            when (prefsData.value) {
                is Int -> "int"
                is String -> "string"
                is Boolean -> "boolean"
                is Set<*> -> "set"
                else -> "not_support"
            }
        }-${if (prefsData.value !is Set<*>) modulePrefs.get(prefsData) else null}"
        dataChannel(host).put(key, value)
        if (prefsData.key == "enable_log")
            dataChannel("android").put("${"android".replace('.', '_')}_config_change", value)
    }

    /**
     * 宿主注册接收 DataChannel 通知
     */
    fun YukiBaseHooker.register() {
        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_version_get") {
            dataChannel.put(key = "${packageName.replace('.', '_')}_version_result", value = YukiHookAPI.Status.compiledTimestamp.toString())
        }

        dataChannel.wait<String>(key = "${packageName.replace('.', '_')}_config_change") {
            when (it.split("-")[1]) {
                "boolean" -> {
                    PrefsUtils.XSharedPreferencesCaches.booleanData[it.split("-")[0]] =
                        it.split("-")[2].toBoolean()
                }
                "int" -> {
                    PrefsUtils.XSharedPreferencesCaches.intData[it.split("-")[0]] =
                        it.split("-")[2].toInt()
                }
                "string" -> {
                    PrefsUtils.XSharedPreferencesCaches.stringData[it.split("-")[0]] =
                        it.split("-")[2]
                }
                "set" -> {
                    PrefsUtils.XSharedPreferencesCaches.stringSetData.remove(it.split("-")[0])
                    PrefsUtils.XSharedPreferencesCaches.stringMapData.remove(it.split("-")[0])
                }
            }
            DataCacheUtils.clear()
        }
    }
}