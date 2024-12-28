package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.StringRes
import java.io.DataOutputStream

/**
 * 通用工具类
 */
object CommonUtils {

    /**
     * 显示 Toast
     *
     * @receiver 需要显示 Toast 应用的 Context
     * @param message 显示文本内容
     * @return [Toast]
     */
    fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }

    /**
     * 显示 Toast, 内容来自字符串资源 ID
     *
     * @receiver 需要显示 Toast 应用的 Context
     * @param stringID 字符串资源的 ID
     * @return [Toast]
     */
    fun Context.toast(@StringRes stringID: Int) = toast(getString(stringID))

    /**
     * 执行 Shell 命令
     * @param command Shell 命令
     */
    fun execShell(command: String) {
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
            val separatorIndex = item.lastIndexOf("_")
            if (separatorIndex != -1 && separatorIndex < item.length - 1) {
                val packageName = item.substring(0, separatorIndex)
                val duration = item.substring(separatorIndex + 1)
                result[packageName] = duration
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

    /**
     * 是否处于深色模式
     */
    fun isDarkMode(context: Context) = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    /**
     * 将密度无关像素（dp）值转换为像素（px）值。
     *
     *
     * @param context 用于获取显示指标的上下文。
     * @param dpValue 要转换的 dp 值。
     * @return 转换后的像素值（整数类型）。
     */
    fun dp2px(context: Context, dpValue: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics).toInt()
}