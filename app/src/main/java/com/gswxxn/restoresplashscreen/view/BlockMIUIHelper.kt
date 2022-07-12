package com.gswxxn.restoresplashscreen.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.*
import com.gswxxn.restoresplashscreen.ui.MainSettingsActivity.Companion.appContext
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object BlockMIUIHelper {

    fun LinearLayout.addBlockMIUIView(context: Context, itemData: InitView.ItemData.() -> Unit) {
        val dataList: HashMap<String, InitView.ItemData> = hashMapOf()

        InitView(dataList).register("BlockMIUIHelper", "", true, itemData)

        for (item: BaseView in dataList["BlockMIUIHelper"]?.itemList ?: arrayListOf()) {
            MainScope().launch {
                addItem(this@addBlockMIUIView, item, context)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun addItem(itemView: LinearLayout, item: BaseView, context: Context) {
        val callBacks: (() -> Unit)? = null

        itemView.addView(LinearLayout(context).apply { // 控件布局
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            background = context.getDrawable(R.drawable.ic_click_check)
            setPadding(dp2px(context, 30f), 0, dp2px(context, 30f), 0)
            when (item) {
                is SeekBarV -> { // 滑动条
                    addView(LinearLayout(context).apply {
                        setPadding(dp2px(context, 12f), 0, dp2px(context, 12f), 0)
                        addView(
                            item.create(context, callBacks), LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        )
                    })
                }
                is SeekBarWithTextV -> { // 滑动条 带文本
                    addView(
                        item.create(context, callBacks), LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )
                }
                is TextV -> { // 文本
                    addView(item.create(context, callBacks))
                    item.onClickListener?.let { unit ->
                        setOnClickListener {
                            unit()
                            callBacks?.let { it1 -> it1() }
                        }
                    }
                }
                is SwitchView -> addView(item.create(context, callBacks)) // 开关
                is TextWithSwitchView -> {
                    addView(item.create(context, callBacks)) // 带文本的开关
                    setOnTouchListener { _, motionEvent ->
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> if (item.switchV.switch.isEnabled) background =
                                context.getDrawable(
                                    R.drawable.ic_main_down_bg
                                )
                            MotionEvent.ACTION_UP -> {
                                if (item.switchV.switch.isEnabled) {
                                    item.switchV.click()
                                    callBacks?.let { it1 -> it1() }
                                    background = context.getDrawable(R.drawable.ic_main_bg)
                                }
                            }
                            else -> background = context.getDrawable(R.drawable.ic_main_bg)
                        }
                        true
                    }
                }
                is TitleTextV -> addView(item.create(context, callBacks)) // 标题文字
                is LineView -> addView(item.create(context, callBacks)) // 分割线
                is LinearContainerV -> addView(item.create(context, callBacks)) // 布局创建
                is AuthorV -> { // 作者框
                    addView(item.create(context, callBacks).apply {
                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0,0,0)
                    })
                    item.onClick?.let { unit ->
                        setOnClickListener {
                            unit()
                            callBacks?.let { it1 -> it1() }
                        }
                    }
                }
                is TextSummaryV -> { // 带箭头和提示的文本框
                    addView(item.create(context, callBacks))
                    item.onClickListener?.let { unit ->
                        setOnClickListener {
                            unit()
                            callBacks?.let { it1 -> it1() }
                        }
                    }
                }
                is SpinnerV -> { // 下拉选择框
                    addView(item.create(context, callBacks))
                }
                is TextSummaryWithSpinnerV, is TextWithSpinnerV -> {
                    addView(item.create(context, callBacks))
                    setOnClickListener {}
                    val spinner = when (item) {
                        is TextSummaryWithSpinnerV -> item.spinnerV
                        is TextWithSpinnerV -> item.spinnerV
                        else -> throw IllegalAccessException("Not is TextSummaryWithSpinnerV or TextWithSpinnerV")
                    }
                    setOnTouchListener { view, motionEvent ->
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            val popup = MIUIPopup(context, view, spinner.currentValue, spinner.dropDownWidth, {
                                spinner.select.text = it
                                spinner.currentValue = it
                                callBacks?.let { it1 -> it1() }
                                spinner.dataBindingSend?.send(it)
                            }, SpinnerV.SpinnerData().apply(spinner.data).arrayList)
                            if (view.width / 2 >= motionEvent.x) {
                                popup.apply {
                                    horizontalOffset = dp2px(context, 24F)
                                    setDropDownGravity(Gravity.LEFT)
                                }
                            } else {
                                popup.apply {
                                    horizontalOffset = -dp2px(context, 24F)
                                    setDropDownGravity(Gravity.RIGHT)
                                }
                            }
                            popup.show()
                        }
                        false
                    }
                }
                is TextSummaryArrowV -> {
                    addView(item.create(context, callBacks))
                    item.textSummaryV.onClickListener?.let { unit ->
                        setOnClickListener {
                            unit()
                            callBacks?.let { it1 -> it1() }
                        }
                    }
                }
                is TextSummaryWithSwitchView -> {
                    addView(item.create(context, callBacks))
                    setOnClickListener {
                        item.switchV.click()
                        callBacks?.let { it1 -> it1() }
                    }
                }
                is CustomViewV -> {
                    addView(item.create(context, callBacks))
                }
                is RadioViewV -> {
                    setPadding(0, 0, 0, 0)
                    addView(item.create(context, callBacks))
                }
            }
        })
    }

    fun InitView.ItemData.getDataBinding(pref : Any) = GetDataBinding({ pref }) { view, flags, data ->
        when (flags) {
            1 -> (view as Switch).isEnabled = data as Boolean
            2 -> view.visibility = if (data as Boolean) View.VISIBLE else View.GONE
            3 -> if (data as Boolean) (view as Switch).isChecked = true
            4 -> if (!(data as Boolean)) (view as Switch).isChecked = false
            6 -> (view as TextView).text = appContext.getString(com.gswxxn.restoresplashscreen.R.string.exception_mode_message, appContext.getString(if (data as Boolean) com.gswxxn.restoresplashscreen.R.string.will_not else com.gswxxn.restoresplashscreen.R.string.will_only))
            7 -> if ((data as String) == appContext.getString(com.gswxxn.restoresplashscreen.R.string.follow_system)) (view as Switch).isChecked = true
        }
    }
}