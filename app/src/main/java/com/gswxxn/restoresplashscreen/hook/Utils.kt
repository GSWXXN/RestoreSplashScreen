package com.gswxxn.restoresplashscreen.hook

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue


object Utils {
    /**
     * Original code from: https://www.jianshu.com/p/956f666eff96
     */
     fun drawable2Bitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Original code from: https://blog.csdn.net/xiaohanluo/article/details/52945791
     */
     fun roundBitmapByShader(bitmap: Bitmap?, size: Int, radius: Int): Bitmap? {
        if (bitmap == null) {
            throw NullPointerException("Bitmap can't be null")
        }
        // 初始化缩放比
        val widthScale = (size * 1.0f) / bitmap.width
        val heightScale = (size * 1.0f) / bitmap.height
        val matrix = Matrix()
        matrix.setScale(widthScale, heightScale)

        // 初始化绘制纹理图
        val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        // 根据控件大小对纹理图进行拉伸缩放处理
        bitmapShader.setLocalMatrix(matrix)

        // 初始化目标bitmap
        val targetBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        // 初始化目标画布
        val targetCanvas = Canvas(targetBitmap)

        // 初始化画笔
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = bitmapShader

        // 利用画笔将纹理图绘制到画布上面
        targetCanvas.drawRoundRect(
            RectF(0F, 0F, size.toFloat(), size.toFloat()),
            radius.toFloat(),
            radius.toFloat(),
            paint
        )
        return targetBitmap
    }

    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }

}