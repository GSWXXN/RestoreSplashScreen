package com.gswxxn.restoresplashscreen.hook

import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.palette.graphics.Palette
import com.highcapable.yukihookapi.hook.log.loggerW


object Utils {
    /**
     * Original code from: https://www.jianshu.com/p/956f666eff96
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
     * Original code from: https://blog.csdn.net/xiaohanluo/article/details/52945791
     */
     fun roundBitmapByShader(bitmap: Bitmap?, isCircle: Boolean, isShrink: Boolean = false): Bitmap? {
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

        // 缩小图标
        if (isShrink && bitmap.width < 200) {
            val targetBitmap2 = Bitmap.createBitmap(bitmap.width * 2, bitmap.width * 2, Bitmap.Config.ARGB_8888)
            val targetCanvas2 = Canvas(targetBitmap2)

            targetCanvas2.drawBitmap(
                targetBitmap,
                (targetBitmap2.width * 0.5 - bitmap.width * 0.5).toFloat(),
                (targetBitmap2.height * 0.5 - bitmap.height * 0.5).toFloat(),
                null
            )
            return targetBitmap2
        }
        return  targetBitmap
    }

    fun getBgColor(bitmap: Bitmap):Int = Palette.from(bitmap).maximumColorCount(8).generate()
        .getLightVibrantColor(Color.parseColor("#F8F8FF"))

}