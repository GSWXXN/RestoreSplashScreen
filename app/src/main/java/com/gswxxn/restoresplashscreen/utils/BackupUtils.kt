package com.gswxxn.restoresplashscreen.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue.CREATE_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.data.ConstValue.OPEN_DOCUMENT_CODE
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.utils.YukiHelper.sendToHost
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime

object BackupUtils {
    fun openFile(activity: Activity) =
        activity.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }, OPEN_DOCUMENT_CODE)

    fun saveFile(activity: Activity) =
        activity.startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "RestoreSplashScreen_${LocalDateTime.now()}.json")
        }, CREATE_DOCUMENT_CODE)

    fun handleReadDocument(activity: Activity, data: Uri?) {
        val uri = data ?: return
        try {
            activity.contentResolver.openInputStream(uri)?.let { loadFile ->
                BufferedReader(InputStreamReader(loadFile)).apply {
                    val sb = StringBuffer()
                    var line = readLine()
                    sb.append(line)
                    do {
                        sb.append(line)
                        line = readLine()
                    } while (line != null)
                    JSONObject(sb.toString()).apply {
                        val key = keys()
                        while (key.hasNext()) {
                            val keys = key.next()
                            @Suppress("IMPLICIT_CAST_TO_ANY")
                            when (val value = get(keys)) {
                                is String -> {
                                    if (value.firstOrNull() == '[' && value.lastOrNull() == ']')
                                        parseStringArray(value).also { activity.modulePrefs.putStringSet(keys, it) }
                                    else value.also { activity.modulePrefs.putString(keys, it) }
                                }
                                is Boolean -> value.also { activity.modulePrefs.putBoolean(keys, it) }
                                is Int -> value.also { activity.modulePrefs.putInt(keys, it) }
                                else -> null
                            }?.let { activity.sendToHost(keys, it) }
                        }
                    }
                    close()
                }
            }
            activity.finish()
            activity.toast(activity.getString(R.string.restore_successful))
        } catch (e: Throwable) { activity.toast(activity.getString(R.string.restore_failed)) }
    }

    fun handleCreateDocument(activity: Activity, data: Uri?) {
        val uri = data ?: return
        try {
            activity.contentResolver.openOutputStream(uri)?.let { saveFile ->
                BufferedWriter(OutputStreamWriter(saveFile)).apply {
                    write(JSONObject().also {
                        for (entry: Map.Entry<String, *> in activity.modulePrefs.all()) {
                            it.put(entry.key, entry.value)
                        }
                    }.toString())
                    close()
                }
            }
            activity.toast(activity.getString(R.string.save_successful))
        } catch (_: Throwable) { activity.toast(activity.getString(R.string.save_failed)) }
    }

    private fun parseStringArray(value: String) =
        value.substring(1, value.lastIndex).split(", ").toMutableSet().apply { remove("") }
}