package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import java.io.DataOutputStream

object CommonUtils {

    /**
     * 显示 Toast
     *
     * 需要显示 Toast 应用的 Context
     * @param message 显示文本内容
     * @return [Toast]
     */
    fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }

    fun Context.toastL(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_LONG)
        .apply { show() }

    /**
     * 执行 Shell 命令
     * @param command Shell 命令
     */
    fun execShell(command : String) {
        try {
            val p = Runtime.getRuntime().exec("su")
            val outputStream = p.outputStream
            val dataOutputStream = DataOutputStream(outputStream)
            dataOutputStream.writeBytes(command)
            dataOutputStream.flush()
            dataOutputStream.close()
            outputStream.close()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * 将值为类似 <[String]_[String]> 的 Set 转换成 <[String], [String]> 的 Map
     */
    fun Set<String>.toMap(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        forEach { item ->
            item.split("_").let {
                result += it[0] to it[1]
            }
        }
        return result
    }

    /**
     * 将类似 <[String], [String]> 的 Map 转换成值为类似 <[String]_[String]> 的 Set
     */
    fun MutableMap<String, String>.toSet(): MutableSet<String> {
        val result = mutableSetOf<String>()
        forEach { (key, value) ->
            result += "${key}_${value}"
        }
        return result
    }

    /**
     * 比较两个 [Collection] 的内容是否相同
     */
    infix fun Collection<*>.notEqualsTo(second: Collection<*>): Boolean = !(this equalTo second)
    private infix fun Collection<*>.equalTo(second: Collection<*>): Boolean {
        if (size != second.size) return false
        forEach { if (it !in second) return false }
        return true
    }

    /**
     * 检查 SDK 版本
     */
    val isAtLeastT = Build.VERSION.SDK_INT >= 33
}