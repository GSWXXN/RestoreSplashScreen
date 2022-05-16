package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.core.finder.FieldFinder.Result.Instance


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
        val size = if (drawable is AdaptiveIconDrawable) _size else drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 绘制图标圆角
     *
     * @param bitmap 待绘制圆角的 Bitmap
     * @param isCircle 是否绘制圆形图标
     * @param isShrink 是否缩小图标
     * @return [Bitmap]
     */
     fun roundBitmapByShader(bitmap: Bitmap?, isCircle: Boolean, isShrink: Boolean = false): Bitmap? {
        if (bitmap == null)  return null

        val radius = if (isCircle) bitmap.width / 2 else  bitmap.width / 4

        // 初始化目标bitmap
        val targetBitmap = Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)

        // 初始化画笔
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // 利用画笔将纹理图绘制到画布上面
        Canvas(targetBitmap).drawRoundRect(
            RectF(0F, 0F, bitmap.width.toFloat(), bitmap.width.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )

        // 缩小图标
        if (isShrink && bitmap.width < 200) {
            val shrankBitmap = Bitmap.createBitmap(
                bitmap.width * 2,
                bitmap.width * 2,
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

    /**
     * 根据 Bitmap 获取背景颜色
     *
     * @param bitmap 从中获取颜色的图片
     * @return [Int]
     */
    fun getBgColor(bitmap: Bitmap):Int = Palette.from(bitmap).maximumColorCount(8).generate()
        .getLightVibrantColor(Color.parseColor("#F8F8FF"))

    /**
     * 根据名称获取实例 的 Filed 实例处理类
     *
     * 需要获取 Field 的实例
     * @param fieldName Field 名称
     * @return [Instance]
     */
    fun Any.getField(fieldName : String) = this.javaClass.field { name = fieldName }.get(this)

    /**
     * 根据名称设置实例 的 Filed 实例内容
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
}