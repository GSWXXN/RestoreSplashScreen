package com.gswxxn.restoresplashscreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.data.Padding
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.BaseView
import cn.fkj233.ui.activity.view.LinearContainerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import com.gswxxn.restoresplashscreen.R
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

class SeekBarWithTitleView(
    private val titleID: Int,
    private val pref: PrefsData<Int>? = null,
    private val min: Int,
    private val max: Int,
    private val defaultProgress: Int,
    private val isPercentage: Boolean,
    private val progressColor: Int,
    private val drawHuePanel: Boolean,
    private val dataBindingRecv: DataBinding.Binding.Recv? = null,
    private val dataBindingSend: DataBinding.Binding.Send? = null,
    private val onProgressChanged: ((value: Int) -> Unit)? = null
): BaseView {

    companion object {
        lateinit var huePanel: Drawable
        /** 判断 [huePanel] 是否被初始化 */
        fun isHuePanelInitialized() = ::huePanel.isInitialized
    }

    lateinit var context: Context

    private fun drawHuePanel() {
        if (isHuePanelInitialized()) return
        val bitmap = Bitmap.createBitmap(context.resources.displayMetrics.widthPixels - dp2px(context, 60f) + 1, dp2px(context, 31f), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val hueColors = IntArray(bitmap.width)
        val paint = Paint().apply { strokeWidth = 0.0f }

        var h = 0f
        for (i in hueColors.indices) {
            hueColors[i] = Color.HSVToColor(floatArrayOf(h, 1.0f, 1.0f))
            h += 360.0f / hueColors.size.toFloat()
        }

        for (i in hueColors.indices) {
            paint.color = hueColors[i]
            canvas.drawLine(
                i.toFloat(),
                0f,
                i.toFloat(),
                bitmap.height.toFloat(),
                paint)
        }

        val roundCornerBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        canvas.apply { setBitmap(roundCornerBitmap) }.drawRoundRect(
            0f,
            0f,
            bitmap.width.toFloat(),
            bitmap.height.toFloat(),
            bitmap.height.toFloat() / 2,
            bitmap.height.toFloat() / 2,
            paint.apply {
                isAntiAlias = true
                shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
        )
        huePanel =  BitmapDrawable(context.resources, roundCornerBitmap)
    }

    override fun getType(): BaseView = this

    @SuppressLint("SetTextI18n")
    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        this.context = context
        if (drawHuePanel) drawHuePanel()

        val statusTextView = TextV(
            if (isPercentage) "$defaultProgress% / $max%" else "$defaultProgress / $max",
            textSize = 13.75f,
            colorId = cn.fkj233.miui.R.color.author_tips,
            padding = Padding(0, 0, 0, 0)
        ).create(context, null) as TextView

        val titleTextView = LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(
            LayoutPair(
                TextSummaryV(context.getString(titleID)).apply { notShowMargins(true) }.create(context, null),
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            ),
            LayoutPair(
                statusTextView,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            )
        ), layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, dp2px(context, 17.75f),0, dp2px(context, 10f))
        }).create(context, null)

        val seekBar = SeekBar(context).also { view ->
            view.thumb = if (drawHuePanel) context.getDrawable(R.drawable.thumb_seek) else null
            view.splitTrack = false
            view.maxHeight = dp2px(context, 30f)
            view.minHeight = dp2px(context, 30f)
            view.background = null
            view.isIndeterminate = false

            view.progressDrawable = (context.getDrawable(cn.fkj233.miui.R.drawable.seekbar_progress_drawable) as LayerDrawable).apply {
                if (drawHuePanel) setDrawable(0, huePanel)
                ((getDrawable(1) as ClipDrawable).drawable as GradientDrawable).setColor(if (drawHuePanel) 0 else progressColor)
            }
            view.min = min
            view.max = max
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    onProgressChanged?.let { it(p1) }
                    dataBindingSend?.send(p1)
                    statusTextView.text = if (isPercentage) "$p1% / $max%" else "$p1 / $max"
                    pref?.let { context.prefs().edit { put(it, p1) } }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            view.setPadding(0, 0, 0, 0)
            view.invalidate()
        }

        val progress = pref?.let { context.prefs().get(it) } ?: defaultProgress
        statusTextView.text = if (isPercentage) "$progress% / $max%" else "$progress / $max"
        seekBar.progress = progress

        return LinearContainerV(
            LinearContainerV.VERTICAL,
            arrayOf(
                LayoutPair(titleTextView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(0, dp2px(context, 15.75f), 0, dp2px(context, 15.75f))
                }),
                LayoutPair(seekBar, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.setMargins(0, 0, 0, dp2px(context, 15.75f))
                })
            ),
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ).create(context, callBacks).also {
            dataBindingRecv?.setView(it)
        }
    }
}