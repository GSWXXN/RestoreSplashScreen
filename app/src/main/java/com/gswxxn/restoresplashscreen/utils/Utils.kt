package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.palette.graphics.Palette
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.data.RoundDegree
import com.highcapable.yukihookapi.hook.core.finder.FieldFinder.Result.Instance
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.log.loggerI
import java.io.DataOutputStream

object Utils {

    /**
     * Drawable 图标转 Bitmap
     *
     * @param drawable 待转换的 Drawable 图标
     * @param _size 如果图标为自适应图标，则生成此大小的 Bitmap；否则，此项无用
     * @return [Bitmap]
     */
    fun drawable2Bitmap(drawable: Drawable, _size : Int): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val size = _size / 2
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas().apply { setBitmap(bitmap) }
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        canvas.setBitmap(null)
        return bitmap
    }

    /**
     * 绘制图标圆角
     *
     * @param bitmap 待绘制圆角的 Bitmap
     * @param roundDegree 绘制圆形图标圆角程度
     * @param shrinkTrigger 若图标尺寸小于此数值则缩小图标；留空不缩小图标
     * @return [Bitmap]
     */
     fun roundBitmapByShader(bitmap: Bitmap?, roundDegree: RoundDegree, shrinkTrigger: Int = 0): Bitmap? {
        if (bitmap == null)  return null

        val radius = when (roundDegree) {
            RoundDegree.RoundCorner -> bitmap.width / 4
            RoundDegree.Circle -> bitmap.width / 2
            else -> 0
        }

        // 初始化目标bitmap
        val targetBitmap = Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)

        // 利用画笔将纹理图绘制到画布上面
        Canvas(targetBitmap).drawRoundRect(
            RectF(0F, 0F, bitmap.width.toFloat(), bitmap.width.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            Paint().apply{
                isAntiAlias = true
                shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
        )

        // 缩小图标
        if (bitmap.width < shrinkTrigger) {
            val shrankBitmap = Bitmap.createBitmap(
                (bitmap.width * 1.7).toInt(),
                (bitmap.width * 1.7).toInt(),
                Bitmap.Config.ARGB_8888
            )
            Canvas(shrankBitmap).drawBitmap(
                targetBitmap,
                (shrankBitmap.width * 0.5 - bitmap.width * 0.5).toFloat(),
                (shrankBitmap.height * 0.5 - bitmap.height * 0.5).toFloat(),
                null
            )
            return shrankBitmap
        }
        return  targetBitmap
    }

    fun Context.shrinkIcon(@DrawableRes id : Int) : Drawable {
        val px = dp2px(this, 50f)

        val bitmap = drawable2Bitmap(this.getDrawable(id)!!, px)!!
        val shrankBitmap = Bitmap.createBitmap(
            (px * 1.5).toInt(),
            (px * 1.5).toInt(),
            Bitmap.Config.ARGB_8888
        )

        Canvas(shrankBitmap).drawBitmap(
            bitmap,
            (shrankBitmap.width * 0.5  - bitmap.width * 0.5).toFloat(),
            (shrankBitmap.height * 0.5 - bitmap.height * 0.5).toFloat(),
            null
        )

        return BitmapDrawable(this.resources, shrankBitmap)
    }

    /**
     * 根据 Bitmap 获取背景颜色
     *
     * @param bitmap 从中获取颜色的图片
     * @param isLight 是否为浅色模式
     * @return [Int]
     */
    fun getBgColor(bitmap: Bitmap, isLight: Boolean):Int {
        val hsv = FloatArray(3)

        val color = Palette.from(bitmap).maximumColorCount(8).generate()
            .getDominantColor(Color.parseColor(if (isLight) "#F5F5F5" else "#1C2833"))
        Color.colorToHSV(color, hsv)
        if (isLight) {
            hsv[1] = hsv[1] - 0.4f // 减小饱和度
            hsv[2] = hsv[2] + 0.2f // 增大明度
        } else {
            hsv[1] = hsv[1] - 0.2f // 减小饱和度
            hsv[2] = hsv[2] - 0.7f // 减小明度
        }
        return Color.HSVToColor(hsv)
    }

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
     * 显示 Toast
     *
     * 需要显示 Toast 应用的 Context
     * @param message 显示文本内容
     * @return [Toast]
     */
    fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply { show() }

    /**
     *  根据 [DataConst.ENABLE_LOG] 标志向 XPosed 框架打印日志
     *
     *  @param msg 打印日志的内容
     */
    fun YukiBaseHooker.printLog(vararg msg: String) {
        if (this.prefs.get(DataConst.ENABLE_LOG)) msg.forEach { loggerI(msg = it) }
    }

    /**
     * 当前设备是否是 MIUI 定制 Android 系统
     * @return [Boolean] 是否符合条件
     */
    val isMIUI by lazy { "android.miui.R".hasClass }

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
}