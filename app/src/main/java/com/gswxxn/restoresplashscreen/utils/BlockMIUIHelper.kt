package com.gswxxn.restoresplashscreen.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.*
import com.gswxxn.restoresplashscreen.view.*
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.current
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 复制自 [BlockMIUI](https://github.com/Block-Network/blockmiui/blob/6a2cf743d5c6904f6807634750e03ce3ecc6bbad/src/main/java/cn/fkj233/ui/activity/fragment/MIUIFragment.kt)
 *
 */
object BlockMIUIHelper {

    fun LinearLayout.addBlockMIUIView(context: Context, itemData: BlockMIUIItemData.() -> Unit) =
        BlockMIUIItemData().apply(itemData).itemList.forEach {
            MainScope().launch { addItem(this@addBlockMIUIView, it, context) }
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
                is TextSummaryWithSwitchView -> {
                    val switch: SwitchView = item.switchV
                    addView(item.create(context, callBacks)) // 带文本的开关
                    setOnTouchListener { _, motionEvent ->
                        when (motionEvent.action) {
                            MotionEvent.ACTION_DOWN -> if (switch.switch.isEnabled) background =
                                context.getDrawable(
                                    R.drawable.ic_main_down_bg
                                )
                            MotionEvent.ACTION_UP -> {
                                if (switch.switch.isEnabled) {
                                    switch.click()
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
                is LineV -> addView(item.create(context, callBacks)) // 分割线
                is LinearContainerV -> addView(item.create(context, callBacks)) // 布局创建
                is ImageTextV -> { // 作者框
                    addView(item.create(context, callBacks).apply {
                        (layoutParams as LinearLayout.LayoutParams).setMargins(0, 0,0,0)
                    })
                    item.current().field { type = Function0::class.java }.cast<(() -> Unit)?>()?.let { unit ->
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
                        is TextSummaryWithSpinnerV -> item.current().field { type = SpinnerV::class.java }.cast<SpinnerV>()
                        is TextWithSpinnerV -> item.current().field { type = SpinnerV::class.java }.cast<SpinnerV>()
                        else -> throw IllegalAccessException("Not is TextSummaryWithSpinnerV or TextWithSpinnerV")
                    }!!
                    setOnTouchListener { view, motionEvent ->
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            if (!YukiHookAPI.Status.isXposedModuleActive) {
                                Toast.makeText(context, com.gswxxn.restoresplashscreen.R.string.make_sure_active, Toast.LENGTH_SHORT).show()
                                return@setOnTouchListener false
                            }
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
                is TextSummaryWithArrowV -> {
                    addView(item.create(context, callBacks))
                    item.current().field { type = TextSummaryV::class.java }.cast<TextSummaryV>()!!.onClickListener?.let { unit ->
                        setOnClickListener {
                            if (!YukiHookAPI.Status.isXposedModuleActive) {
                                Toast.makeText(context, com.gswxxn.restoresplashscreen.R.string.make_sure_active, Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            unit()
                            callBacks?.let { it1 -> it1() }
                        }
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
}