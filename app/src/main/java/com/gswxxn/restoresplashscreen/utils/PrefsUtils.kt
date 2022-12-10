package com.gswxxn.restoresplashscreen.utils

import com.gswxxn.restoresplashscreen.utils.CommonUtils.toMap
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

class PrefsUtils(private val pp : PackageParam) {
    object XSharedPreferencesCaches {
        /** 缓存的 [Boolean] 键值数据 */
        var booleanData = HashMap<String, Boolean>()

        /** 缓存的 [Int] 键值数据 */
        var intData = HashMap<String, Int>()

        /** 缓存的 [String] 键值数据 */
        var stringData = HashMap<String, String>()

        /** 缓存的 [Set]<[String]> 键值数据 */
        var stringSetData = HashMap<String, Set<String>>()

        /** 缓存的 [Map]<[String], [String]> 键值数据 */
        var stringMapData = HashMap<String, Map<String, String>>()
    }


    inline fun <reified T> get(prefs: PrefsData<T>, value: T = prefs.value): T =
        getPrefsData(prefs.key, value) as T

    fun getPrefsData(key: String, value: Any?): Any = when (value) {
        is Int -> getInt(key, value)
        is Boolean -> getBoolean(key, value)
        is String -> getString(key, value)
        is Set<*> -> getStringSet(key, value as? Set<String> ?: error("Key-Value type ${value.javaClass.name} is not allowed"))
        is Map<*, *> -> getStringMap(key)
        else -> error("Key-Value type ${value?.javaClass?.name} is not allowed")
    }

    private fun getBoolean(key: String, value: Boolean = false) =
        XSharedPreferencesCaches.booleanData[key].let {
            it ?: pp.prefs.getBoolean(key, value).let { value ->
                XSharedPreferencesCaches.booleanData[key] = value
                value
            }
        }

    private fun getInt(key: String, value: Int = 0) =
        XSharedPreferencesCaches.intData[key].let {
            it ?: pp.prefs.getInt(key, value).let { value ->
                XSharedPreferencesCaches.intData[key] = value
                value
            }
        }

    private fun getStringSet(key: String, value: Set<String>) =
        XSharedPreferencesCaches.stringSetData[key].let {
            (it ?: pp.prefs.getStringSet(key, value)).let { value ->
                XSharedPreferencesCaches.stringSetData[key] = value
                value
            }
        }

    private fun getStringMap(key: String) =
        XSharedPreferencesCaches.stringMapData[key].let {
            (it ?: getStringSet(key, setOf()).toMap()).let { value ->
                XSharedPreferencesCaches.stringMapData[key] = value
                value
            }
        }

    private fun getString(key: String, value: String = "") =
        XSharedPreferencesCaches.stringData[key].let {
            (it ?: pp.prefs.getString(key, value)).let { value ->
                XSharedPreferencesCaches.stringData[key] = value
                value
            }
        }
}

