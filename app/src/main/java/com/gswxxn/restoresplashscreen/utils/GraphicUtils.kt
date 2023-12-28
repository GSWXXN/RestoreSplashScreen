package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.palette.graphics.Palette
import cn.fkj233.ui.activity.dp2px
import com.gswxxn.restoresplashscreen.data.RoundDegree
import android.graphics.Path

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
     * 从给定的 Drawable 中获取中心部分，并返回一个新的 BitmapDrawable。
     *
     * @param drawable 要提取中心部分的 Drawable。
     * @param centerPercentage 中心部分相对于原始 Drawable 的尺寸比例，范围在 0 到 1 之间。
     * @param resources 用于创建 BitmapDrawable 的 Resources 对象。可以为 null，但需要为可绘制资源提供资源。
     * @return 包含中心部分的新 BitmapDrawable。
     */
    fun getCenterDrawable(drawable: Drawable, centerPercentage: Float, resources: Resources): BitmapDrawable {
        // Create a bitmap with the same size as the LayerDrawable
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the LayerDrawable on the canvas
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        // Calculate the bounds of the center part
        val centerWidth = (width * centerPercentage).toInt()
        val centerHeight = (height * centerPercentage).toInt()
        val left = (width - centerWidth) / 2
        val top = (height - centerHeight) / 2

        // Crop the center part of the bitmap
        val centerBitmap = Bitmap.createBitmap(bitmap, left, top, centerWidth, centerHeight)

        // Convert the bitmap to a Drawable
        return BitmapDrawable(resources, centerBitmap)
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
     * 缩小图标, 以避免后续使用 setRenderEffect 模糊图标时出现毛边问题
     *
     * @param context 上下文，用于获取资源和创建RenderScript
     * @param drawable 需要创建阴影的Drawable
     * @param oriIconSize 原始图标大小
     * @param blurIconSize 待模糊图标大小
     * @return 待模糊图标Drawable，如果输入的Drawable或者Context为空则返回null
     */
    fun createShadowedIcon(context: Context?, drawable: Drawable?, oriIconSize: Int, blurIconSize: Int): Drawable? {
        if (drawable == null || context == null) {
            return null
        }

        // 计算缩放比例
        val originalSize = drawable.intrinsicWidth
        val ratio = (oriIconSize.toDouble() / originalSize).coerceAtMost(1.0).toFloat()

        // 创建缩放后的位图
        val scaledSize = (originalSize * ratio).toInt()
        val scaledBitmap = Bitmap.createBitmap(scaledSize, scaledSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(scaledBitmap)

        // 应用圆角裁剪
        val cornerRadius = scaledSize / 4f
        val path = Path().apply {
            addRoundRect(RectF(0f, 0f, scaledSize.toFloat(), scaledSize.toFloat()), cornerRadius, cornerRadius, Path.Direction.CW)
        }
        canvas.clipPath(path)

        drawable.setBounds(0, 0, scaledSize, scaledSize)
        drawable.draw(canvas)

        // 创建带阴影的位图
        val shadowSize = blurIconSize / 4
        val shadowBitmap = Bitmap.createBitmap(scaledSize + shadowSize, scaledSize + shadowSize, Bitmap.Config.ARGB_8888)
        val shadowCanvas = Canvas(shadowBitmap)
        shadowBitmap.setHasAlpha(true)
        val paint = Paint(3).apply { alpha = 90 }
        shadowCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        shadowCanvas.drawBitmap(scaledBitmap, ((shadowSize) / 2).toFloat(), ((shadowSize) / 2).toFloat(), paint)

        return BitmapDrawable(context.resources, shadowBitmap)
    }
}