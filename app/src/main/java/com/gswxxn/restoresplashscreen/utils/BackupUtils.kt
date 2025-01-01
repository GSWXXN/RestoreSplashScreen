package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.ui.MainActivity
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.highcapable.yukihookapi.hook.factory.prefs
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.system.exitProcess

/**
 * 改自 [MiuiHomeR](https://github.com/qqlittleice/MiuiHome_R/blob/9f3a298df6427b3a8ea6a47aaabfa0a56c4dd11e/app/src/main/java/com/yuk/miuiHomeR/utils/BackupUtils.kt#L47)
 * 用于备份和恢复数据
 */
object BackupUtils {
    /**
     * 处理打开文件, 处理并写出数据
     *
     * @param context [Context]
     * @param data [Uri]
     */
    fun handleReadDocument(context: Context, data: Uri?) {
        val uri = data ?: return
        try {
            context.prefs().edit {
                clear()
                context.contentResolver.openInputStream(uri)?.let { loadFile ->
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
            context.toast(R.string.restore_successful)
            Thread {
                Thread.sleep(500)
                val intent =
                    Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
                exitProcess(0)
            }.start()
        } catch (e: Throwable) { context.toast(R.string.restore_failed) }
    }

    /**
     * 处理保存文件, 写出数据
     *
     * @param context [Context]
     * @param data [Uri]
     */
    fun handleCreateDocument(context: Context, data: Uri?) {
        val uri = data ?: return
        try {
            context.contentResolver.openOutputStream(uri)?.let { saveFile ->
                BufferedWriter(OutputStreamWriter(saveFile)).apply {
                    write(JSONObject().also {
                        for (entry: Map.Entry<String, *> in context.prefs().all()) {
                            it.put(entry.key, entry.value)
                        }
                    }.toString())
                    close()
                }
            }
            context.toast(context.getString(R.string.save_successful))
        } catch (_: Throwable) { context.toast(R.string.save_failed) }
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