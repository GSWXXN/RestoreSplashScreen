package com.gswxxn.restoresplashscreen.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue.CREATE_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.data.ConstValue.OPEN_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.hook.factory.prefs
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime

/**
 * 改自 [MiuiHomeR](https://github.com/qqlittleice/MiuiHome_R/blob/9f3a298df6427b3a8ea6a47aaabfa0a56c4dd11e/app/src/main/java/com/yuk/miuiHomeR/utils/BackupUtils.kt#L47)
 * 用于备份和恢复数据
 */
object BackupUtils {

    /**
     * 打开文件, 用于选择备份文件
     *
     * @param activity [Activity]
     * @return [Unit]
     */
    fun openFile(activity: Activity) =
        activity.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }, OPEN_DOCUMENT_CODE)

    /**
     * 读取文件, 用于恢复数据
     *
     * @param activity [Activity]
     * @return [Unit]
     */
    fun saveFile(activity: Activity) =
        activity.startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "RestoreSplashScreen_${LocalDateTime.now()}.json")
        }, CREATE_DOCUMENT_CODE)

    /**
     * 处理打开文件, 处理并写出数据
     *
     * @param activity [Activity]
     * @param data [Uri]
     */
    fun handleReadDocument(activity: Activity, data: Uri?) {
        val uri = data ?: return
        try {
            activity.prefs().edit {
                clear()
                activity.contentResolver.openInputStream(uri)?.let { loadFile ->
                    BufferedReader(InputStreamReader(loadFile)).apply {
                        val sb = StringBuffer()
                        var line = readLine()
                        do {
                            sb.append(line)
                            line = readLine()
                        } while (line != null)
                        JSONObject(sb.toString()).apply {
                            val key = keys()
                            while (key.hasNext()) {
                                val keys = key.next()
                                when (val value = get(keys)) {
                                    is String -> {
                                        if (value.firstOrNull() == '[' && value.lastOrNull() == ']')
                                            putStringSet(
                                                keys,
                                                parseStringArray(value)
                                            )
                                        else putString(keys, value)
                                    }
                                    is Boolean -> putBoolean(keys, value)
                                    is Int -> putInt(keys, value)
                                }
                            }
                        }
                        close()
                    }
                }
            }
            activity.finish()
            activity.toast(activity.getString(R.string.restore_successful))
        } catch (e: Throwable) { activity.toast(activity.getString(R.string.restore_failed)) }
    }

    /**
     * 处理保存文件, 写出数据
     *
     * @param activity [Activity]
     * @param data [Uri]
     */
    fun handleCreateDocument(activity: Activity, data: Uri?) {
        val uri = data ?: return
        try {
            activity.contentResolver.openOutputStream(uri)?.let { saveFile ->
                BufferedWriter(OutputStreamWriter(saveFile)).apply {
                    write(JSONObject().also {
                        for (entry: Map.Entry<String, *> in activity.prefs().all()) {
                            it.put(entry.key, entry.value)
                        }
                    }.toString())
                    close()
                }
            }
            activity.toast(activity.getString(R.string.save_successful))
        } catch (_: Throwable) { activity.toast(activity.getString(R.string.save_failed)) }
    }

    /**
     * 解析字符串数组 "[value1, value2, value3]" 为 [MutableSet]
     *
     * @param value [String]
     * @return [MutableSet]<[String]>
     */
    private fun parseStringArray(value: String) =
        value.substring(1, value.lastIndex).split(", ").toMutableSet().apply { remove("") }
}