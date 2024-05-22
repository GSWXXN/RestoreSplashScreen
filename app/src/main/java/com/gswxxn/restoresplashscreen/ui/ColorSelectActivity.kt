package com.gswxxn.restoresplashscreen.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.*
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.palette.graphics.Palette
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.data.Padding
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.LinearContainerV
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextV
import cn.fkj233.ui.dialog.MIUIDialog
import com.gswxxn.restoresplashscreen.R
import com.gswxxn.restoresplashscreen.data.ConstValue
import com.gswxxn.restoresplashscreen.data.DataConst
import com.gswxxn.restoresplashscreen.databinding.ActivityColorSelectBinding
import com.gswxxn.restoresplashscreen.utils.BlockMIUIHelper.addBlockMIUIView
import com.gswxxn.restoresplashscreen.utils.CommonUtils.toast
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.drawable2Bitmap
import com.gswxxn.restoresplashscreen.utils.GraphicUtils.getBgColor
import com.gswxxn.restoresplashscreen.utils.IconPackManager
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.prefs
import java.util.*
import java.util.regex.Pattern

/**
 * 选择颜色的 Activity
 */
class ColorSelectActivity : BaseActivity<ActivityColorSelectBinding>() {
    companion object {
        lateinit var huePanel: Drawable
        /** 判断 [huePanel] 是否被初始化 */
        fun isHuePanelInitialized() = ::huePanel.isInitialized
    }
    private var currentColor: Int? = null
    private var isSettingOverallBgColor = false
    private lateinit var pkgName: String
    private var resetColor = false
    private var selectedColor = false

    private var isDarkMode = false

    private lateinit var seekBarR: SeekBar
    private lateinit var seekBarG: SeekBar
    private lateinit var seekBarB: SeekBar
    private lateinit var seekBarH: SeekBar
    private lateinit var seekBarS: SeekBar
    private lateinit var seekBarV: SeekBar

    private lateinit var statusR: TextView
    private lateinit var statusG: TextView
    private lateinit var statusB: TextView
    private lateinit var statusH: TextView
    private lateinit var statusS: TextView
    private lateinit var statusV: TextView

    private var hsvColor = floatArrayOf(0f, 0f, 0f)
    private var color = 0
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            resetColor = false
            seekBarR.progress = Color.red(color)
            seekBarG.progress = Color.green(color)
            seekBarB.progress = Color.blue(color)
            binding.colorString.text = "#${Integer.toHexString(value).substring(2).uppercase(Locale.ROOT)}"
            binding.demoImage.background = ColorDrawable(value)

            statusR.text = "${ Color.red(color) } / 255"
            statusG.text = "${ Color.green(color) } / 255"
            statusB.text = "${ Color.blue(color) } / 255"
            statusH.text = "${ hsvColor[0].toInt() } / 360"
            statusS.text = "%.2f / 100".format(hsvColor[1] * 100)
            statusV.text = "%.2f / 100".format(hsvColor[2] * 100)
        }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        drawHuePanel()

        if (intent.getBooleanExtra(ConstValue.EXTRA_MESSAGE_OVERALL_BG_COLOR, false)) {
            isSettingOverallBgColor = true
            pkgName = packageName
            currentColor = Color.parseColor( prefs().get(
                if (isDarkMode)
                    DataConst.OVERALL_BG_COLOR_NIGHT
                else
                    DataConst.OVERALL_BG_COLOR
            ))
        } else {
            pkgName = intent.getStringExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME)!!
            currentColor = intent.getStringExtra(ConstValue.EXTRA_MESSAGE_CURRENT_COLOR)?.let { Color.parseColor(it) }
        }

        seekBarR = createSeekBar(255, 0xFFF36060.toInt()) { Color.valueOf(color).apply { setRGBColor(Color.valueOf(((it - 0.5)/255).toFloat(), green(), blue()).toArgb()) } }
        seekBarG = createSeekBar(255, 0xFF5FF25F.toInt()) { Color.valueOf(color).apply { setRGBColor(Color.valueOf(red(), ((it - 0.5)/255).toFloat(), blue()).toArgb()) } }
        seekBarB = createSeekBar(255, 0xFF5F5FF3.toInt()) { Color.valueOf(color).apply { setRGBColor(Color.valueOf(red(), green(),((it - 0.5)/255).toFloat()).toArgb()) } }
        seekBarH = createSeekBar(360, 0) { setHsvColor(0, it.toFloat()) }
        seekBarS = createSeekBar(10000) { setHsvColor(1, it.toFloat() / 10000) }
        seekBarV = createSeekBar(10000){ setHsvColor(2, it.toFloat() / 10000) }

        fun textV() = TextV("", textSize = 13.75f, colorId = cn.fkj233.ui.R.color.author_tips, padding = Padding(0, 0, 0, 0)).create(this, null) as TextView
        statusR = textV()
        statusG = textV()
        statusB = textV()
        statusH = textV()
        statusS = textV()
        statusV = textV()

        // 配置中间图标
        val icon = (
                IconPackManager(this, prefs().get(DataConst.ICON_PACK_PACKAGE_NAME))
                    .getIconByPackageName(pkgName)                      // 优先获取图标包中的图标
                    ?:packageManager.getApplicationIcon(pkgName)        // 使用默认方式获取图标
                ).let {
                    binding.demoIcon.setImageDrawable(it)
                    drawable2Bitmap(it, 48)
                }

        val magnifierSize = dp2px(this@ColorSelectActivity, 100f)
        val magnifier = Magnifier.Builder(binding.demoLayout)
            .setDefaultSourceToMagnifierOffset(0, -dp2px(this@ColorSelectActivity, 80f))
            .setInitialZoom(5f)
            .setOverlay(getDrawable(R.drawable.ic_collimation))
            .setSize(magnifierSize, magnifierSize)
            .setCornerRadius((magnifierSize / 2).toFloat())
            .build()

        binding.demoIcon.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val viewPosition = IntArray(2)
                    binding.demoLayout.getLocationOnScreen(viewPosition)
                    magnifier.show(event.rawX - viewPosition[0], event.rawY - viewPosition[1])
                    val iconX = event.x / v.width * icon.width
                    val iconY = event.y / v.height * icon.height
                    if (iconX >= 0 && iconX < icon.width && iconY >= 0 && iconY < icon.height) {
                        selectedColor = true
                        setRGBColor(icon.getPixel(iconX.toInt(), iconY.toInt()))
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    magnifier.dismiss()
                }
            }
            true
        }
        val palette = Palette.from(icon).maximumColorCount(8).generate()
        val defaultColor = when {
            !isSettingOverallBgColor -> getBgColor(icon, !isDarkMode)
            isDarkMode -> Color.parseColor("#000000")
            else -> Color.parseColor("#FFFFFF")
        }

        setRGBColor(currentColor ?: defaultColor)

        //返回按钮点击事件
        binding.titleBackIcon.setOnClickListener { onBackPressed() }
        binding.settingItems.addBlockMIUIView(this) {
            TextSummaryArrow(TextSummaryV(textId = R.string.manual_input) {
                MIUIDialog(this@ColorSelectActivity) {
                    setTitle(R.string.manual_input)
                    setMessage(R.string.manual_input_hint)
                    setEditText(Integer.toHexString(color).substring(2).uppercase(Locale.ROOT), "")
                    this.javaClass.method {
                        emptyParam()
                        returnType = EditText::class.java
                    }.get(this).invoke<EditText>()?.apply {
                        addTextChangedListener(object : TextWatcher{
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun afterTextChanged(s: Editable?) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                val checkedText = Pattern.compile("[^a-fA-F0-9]").matcher(getEditText())
                                    .replaceAll("").trim()
                                    .run { if (this.length > 6) substring(0, 6) else this }
                                if (checkedText != getEditText()) { setText(checkedText); setSelection(checkedText.length) }
                            }
                        })
                    }
                    setRButton(R.string.button_okay) {
                        try {
                            setRGBColor(Color.parseColor("#${getEditText()}"))
                            selectedColor = true
                            dismiss()
                        }catch (_: IllegalArgumentException){
                            toast(getString(R.string.color_input_invalid))
                        }
                    }
                    setLButton(R.string.button_cancel) { dismiss() }
                }.show()
            })
            Line()
            TitleText(textId = R.string.rgb_color_space)
            CustomView(createTextWithStatusV(R.string.rgb_r, statusR))
            CustomView(seekBarR)
            CustomView(createTextWithStatusV(R.string.rgb_g, statusG))
            CustomView(seekBarG)
            CustomView(createTextWithStatusV(R.string.rgb_b, statusB))
            CustomView(seekBarB)
            Line()
            TitleText(textId = R.string.hsv_color_space)
            CustomView(createTextWithStatusV(R.string.hue, statusH))
            CustomView(seekBarH)
            CustomView(createTextWithStatusV(R.string.saturation, statusS))
            CustomView(seekBarS)
            CustomView(createTextWithStatusV(R.string.value, statusV))
            CustomView(seekBarV)
            Line()
            Text(textId = R.string.undo_modification, colorInt = 0xFF3A7FF7.toInt()) { setRGBColor(currentColor ?: defaultColor); onBackPressed() }
            Text(textId = R.string.reset, colorInt = 0xFFD73E37.toInt()) { setRGBColor(defaultColor); resetColor = true; onBackPressed()}
        }

        binding.title.text = if (isSettingOverallBgColor) getString(R.string.set_custom_bg_color)
        else packageManager.run { getApplicationInfo(pkgName, 0).loadLabel(this).toString() }

        val colors = listOf(
            palette.getDominantColor(0),
            palette.getLightVibrantColor(0),
            palette.getVibrantColor(0),
            palette.getDarkVibrantColor(0),
            palette.getLightMutedColor(0),
            palette.getMutedColor(0),
            palette.getDarkMutedColor(0)
        ).distinct()

        repeat(colors.size) { i ->
            if (colors[i] != 0)
                binding.sampleColor.addView(
                    TextView(this).apply {
                        gravity = Gravity.CENTER
                        background = GradientDrawable().apply { setColor(colors[i]); cornerRadius = dp2px(this@ColorSelectActivity, 15f).toFloat() }
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp2px(this@ColorSelectActivity, 30f)).apply {
                            topMargin =  dp2px(this@ColorSelectActivity, 10f)
                            }
                        setOnClickListener { setRGBColor(colors[i]); selectedColor = true }
                    }
                )
        }
    }

    private fun setHsvColor(index: Int, value: Float) {
        hsvColor[index] = value
        color = Color.HSVToColor(hsvColor)
    }

    private fun setRGBColor(colorInt: Int) {
        if (colorInt == 0) setRGBColor(0xFFFFFFFF.toInt())
        else {
            val hsv = FloatArray(3).also { Color.colorToHSV(colorInt, it) }.also { hsvColor = it }
            seekBarH.progress = hsv[0].toInt()
            seekBarS.progress = (hsv[1] * 10000).toInt()
            seekBarV.progress = (hsv[2] * 10000).toInt()
            color = colorInt
        }
    }

    private fun drawHuePanel() {
        if (isHuePanelInitialized()) return
        val bitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels - dp2px(this, 60f) + 1, dp2px(this, 31f), Bitmap.Config.ARGB_8888)
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
        huePanel =  BitmapDrawable(resources, roundCornerBitmap)
    }

    private fun createSeekBar(max: Int, progressColor: Int = 0xFF0d7AEC.toInt(), callBacks: ((value: Int) -> Unit)?) =
        SeekBar(this).also { view ->
            view.thumb = if (progressColor == 0) getDrawable(R.drawable.thumb_seek) else null
            view.splitTrack = false
            view.maxHeight = dp2px(this, 30f)
            view.minHeight = dp2px(this, 30f)
            view.background = null
            view.isIndeterminate = false

            view.progressDrawable = (getDrawable(cn.fkj233.ui.R.drawable.seekbar_progress_drawable) as LayerDrawable).apply {
                if (progressColor == 0) setDrawable(0, huePanel)
                ((getDrawable(1) as ClipDrawable).drawable as GradientDrawable).setColor(progressColor)
            }
            view.min = 0
            view.max = max
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) callBacks?.let { it(p1); selectedColor = true }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            view.setPadding(0, 0, 0, 0)
            view.invalidate()
        }

    private fun createTextWithStatusV(titleID: Int, status: TextView): View {
        return LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(
            LayoutPair(
                TextSummaryV(getString(titleID)).apply { notShowMargins(true) }.create(this, null),
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            ),
            LayoutPair(
                status,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            )
        ), layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0, dp2px(this, 17.75f),0, dp2px(this, 10f))
        }).create(this, null)
    }

    override fun onBackPressed() {
        if (!isSettingOverallBgColor)
            setResult(
                when {
                    resetColor -> ConstValue.DEFAULT_COLOR
                    color == currentColor || !selectedColor -> ConstValue.UNDO_MODIFY
                    else -> ConstValue.SELECTED_COLOR
                     },
                Intent().apply {
                    putExtra(ConstValue.EXTRA_MESSAGE_SELECTED_COLOR, binding.colorString.text)
                    putExtra(ConstValue.EXTRA_MESSAGE_PACKAGE_NAME, pkgName)
                    putExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, intent.getIntExtra(ConstValue.EXTRA_MESSAGE_APP_INDEX, -1))
                })
        else {
            prefs().edit {
                put(
                    if (isDarkMode)
                        DataConst.OVERALL_BG_COLOR_NIGHT
                    else
                        DataConst.OVERALL_BG_COLOR,
                    binding.colorString.text.toString()
                )
            }
        }


        super.finishAfterTransition()
    }
}