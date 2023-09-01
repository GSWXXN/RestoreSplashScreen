package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.palette.graphics.Palette
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.data.RoundDegree

/**
 * 图形工具类
 */
object GraphicUtils {
    /**
     * Drawable 图标转 Bitmap
     *
     * @param drawable 待转换的 Drawable 图标
     * @param size 生成此大小的 Bitmap
     * @return [Bitmap]
     */
    fun drawable2Bitmap(drawable: Drawable, size : Int): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
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
            RoundDegree.MIUIWidget -> bitmap.width / 10
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

    /**
     * 缩小图标尺寸的一半
     *
     * @param id 图标资源 id
     * @return [Drawable]
     */
    fun Context.shrinkIcon(@DrawableRes id : Int) : Drawable {
        val px = dp2px(this, 50f)

        val bitmap = drawable2Bitmap(this.getDrawable(id)!!, px / 2)
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
}