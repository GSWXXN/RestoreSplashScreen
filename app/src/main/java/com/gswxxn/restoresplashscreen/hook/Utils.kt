package com.gswxxn.restoresplashscreen.hook

import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette


object Utils {
    /**
     * Original code from: https://www.jianshu.com/p/956f666eff96
     */
     fun drawable2Bitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = if (drawable is AdaptiveIconDrawable) drawable.intrinsicWidth * 2 else drawable.intrinsicWidth
        val h = if (drawable is AdaptiveIconDrawable) drawable.intrinsicHeight * 2 else drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Original code from: https://blog.csdn.net/xiaohanluo/article/details/52945791
     */
     fun roundBitmapByShader(bitmap: Bitmap?, isCircle: Boolean): Bitmap? {
        if (bitmap == null) {
            return null
        }
        val radius = if (isCircle) bitmap.width / 2 else  bitmap.width / 4

        // 初始化绘制纹理图
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // 初始化目标bitmap
        val targetBitmap = Bitmap.createBitmap(bitmap.width, bitmap.width, Bitmap.Config.ARGB_8888)

        // 初始化目标画布
        val targetCanvas = Canvas(targetBitmap)

        // 初始化画笔
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = bitmapShader

        // 利用画笔将纹理图绘制到画布上面
        targetCanvas.drawRoundRect(
            RectF(0F, 0F, bitmap.width.toFloat(), bitmap.width.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        return targetBitmap
    }

    fun getBgColor(bitmap: Bitmap):Int = Palette.from(bitmap).maximumColorCount(8).generate()
        .getLightVibrantColor(Color.parseColor("#F8F8FF"))

}