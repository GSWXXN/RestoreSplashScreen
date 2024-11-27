package com.gswxxn.restoresplashscreen.wrapper

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.factory.current

/**
 * 透明背景的 AdaptiveIconDrawable
 */
class TransparentAdaptiveIconDrawable(
    foregroundDrawable: Drawable
) : AdaptiveIconDrawable(ColorDrawable(Color.TRANSPARENT), foregroundDrawable) {
    private var mLayersShader: Shader?
        get() = this.current().field {
            name = "mLayersShader"
            superClass()
        }.cast<Shader>()
        set(value) {
            this.current().field {
                name = "mLayersShader"
                superClass()
            }.set(value)
        }
    private val mCanvas
        get() = this.current().field {
            name = "mCanvas"
            superClass()
        }.cast<Canvas>()!!
    private val mLayersBitmap
        get() = this.current().field {
            name = "mLayersBitmap"
            superClass()
        }.cast<Bitmap>()
    private val mPaint
        get() = this.current().field {
            name = "mPaint"
            superClass()
        }.cast<Paint>()!!
    private val mMaskScaleOnly
        get() = this.current().field {
            name = "mMaskScaleOnly"
            superClass()
        }.cast<Path>()

    /**
     * 继承修改自 AdaptiveIconDrawable
     * 详见 [AdaptiveIconDrawable.draw]
     */
    override fun draw(canvas: Canvas) {
        if (mLayersBitmap == null) {
            return
        }
        if (mLayersShader == null) {
            // 修改为透明色清空画布
            mCanvas.setBitmap(mLayersBitmap)
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            // 绘制背景图层
            background?.draw(mCanvas)

            // 绘制前景图层
            foreground?.setBounds(0, 0, bounds.width(), bounds.height())
            foreground?.draw(mCanvas)

            // 创建位图着色器
            mLayersShader =
                BitmapShader(mLayersBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mPaint.setShader(mLayersShader)
        }
        if (mMaskScaleOnly != null) {
            canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())
            canvas.drawPath(mMaskScaleOnly!!, mPaint)
            canvas.translate(-bounds.left.toFloat(), -bounds.top.toFloat())
        }
    }
}