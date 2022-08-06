/*
 * BlockMIUI
 * Copyright (C) 2022 fkj@fkj233.cn
 * https://github.com/577fkj/BlockMIUI
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License v2.1
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU Lesser General Public License v2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License v2.1
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/BlockMIUI/blob/main/LICENSE>.
 */

package com.gswxxn.restoresplashscreen.view

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.*
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.getDisplay

class NewMIUIDialog(context: Context, val build: NewMIUIDialog.() -> Unit) : Dialog(context, R.style.CustomDialog) {
    private val title by lazy {
        TextView(context).also { textView ->
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(0, dp2px(context, 20f), 0, dp2px(context, 25f))
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            textView.setTextColor(context.getColor(R.color.whiteText))
            textView.gravity = Gravity.CENTER
            textView.setPadding(0, dp2px(context, 10f), 0, 0)
        }
    }

    private val message by lazy {
        TextView(context).also { textView ->
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.setMargins(dp2px(context, 25f), 0, dp2px(context, 25f), dp2px(context, 5f))
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            textView.setTextColor(context.getColor(R.color.author_tips))
            textView.gravity = Gravity.START
            textView.visibility = View.GONE
            textView.setPadding(dp2px(context, 10f), 0, dp2px(context, 10f), 0)
        }
    }

    private val editText by lazy {
        EditText(context).also { editText ->
            editText.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(context, 55f)).also {
                it.setMargins(dp2px(context, 25f), dp2px(context, 10f), dp2px(context, 25f), 0)
            }
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
            editText.setTextColor(context.getColor(R.color.whiteText))
            editText.gravity = Gravity.CENTER
            editText.setPadding(dp2px(context, 8f), dp2px(context, 8f), dp2px(context, 8f), dp2px(context, 8f))
            editText.visibility = View.GONE
            editText.background = context.getDrawable(R.drawable.editview_background)
            val mHeight = dp2px(context, 55f)
            val maxHeight = getDisplay(context).height / 2
            editText.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    editText.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val params = editText.layoutParams as LinearLayout.LayoutParams
                    if (editText.lineCount <= 1) {
                        params.height = mHeight
                    } else {
                        var tempHeight = mHeight
                        for (i in 0..editText.lineCount) {
                            tempHeight += mHeight / 2 - 20
                        }
                        params.height = if (tempHeight >= maxHeight) maxHeight else tempHeight
                    }
                    editText.layoutParams = params
                }
            })
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val params = editText.layoutParams as LinearLayout.LayoutParams
                    if (editText.lineCount <= 1) {
                        params.height = mHeight
                    } else {
                        var tempHeight = mHeight
                        for (i in 0..editText.lineCount) {
                            tempHeight += mHeight / 2 - 20
                        }
                        params.height = if (tempHeight >= maxHeight) maxHeight else tempHeight
                    }
                    editText.layoutParams = params
                }
            })
        }
    }

    private val view by lazy {
        LinearLayout(context).also { linearLayout ->
            linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.addView(message)
            linearLayout.addView(editText)
        }
    }

    var bView: LinearLayout

    private val root = RelativeLayout(context).also { viewRoot ->
        viewRoot.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        viewRoot.addView(LinearLayout(context).also { viewLinearLayout ->
            viewLinearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            viewLinearLayout.orientation = LinearLayout.VERTICAL
            viewLinearLayout.addView(title)
            viewLinearLayout.addView(view)
            viewLinearLayout.addView(LinearLayout(context).also { linearLayout ->
                linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.gravity = Gravity.CENTER_HORIZONTAL
                }
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setPadding(0, dp2px(context, 16f), 0, dp2px(context, 35f))
                bView = linearLayout
            })
        })
    }

    fun Button(text: CharSequence?, enable: Boolean = true, cancelStyle: Boolean = false, callBacks: (View) -> Unit) {
        bView.addView(Button(context).also { buttonView ->
            buttonView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(context, 50f), 1f).also {
                it.setMargins(dp2px(context, 35f), dp2px(context, 10f), dp2px(context, 35f), 0)
                it.gravity = Gravity.CENTER
            }
            buttonView.setTextColor(context.getColor(if (cancelStyle) R.color.whiteText else R.color.white))
            buttonView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            buttonView.text = text
            buttonView.isEnabled = enable
            buttonView.stateListAnimator = null
            buttonView.background = context.getDrawable(if (cancelStyle) R.drawable.l_button_background else R.drawable.r_button_background)
            buttonView.setOnClickListener {
                callBacks(it)
            }
        })
    }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setContentView(root)
        window?.setBackgroundDrawable(GradientDrawable().apply {
            cornerRadius = dp2px(context, 25f).toFloat()
            setColor(context.getColor(R.color.dialog_background))
        })
    }

    fun addView(mView: View) {
        view.addView(mView)
    }

    override fun setTitle(title: CharSequence?) {
        this.title.text = title
    }

    override fun setTitle(titleId: Int) {
        this.title.setText(titleId)
    }

    override fun show() {
        build()
        window!!.setWindowAnimations(R.style.DialogAnim)
        super.show()
        val layoutParams = window!!.attributes
        layoutParams.dimAmount = 0.3F
        layoutParams.width = getDisplay(context).width - dp2px(context, 50f)
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.verticalMargin = 0.02F
        window!!.attributes = layoutParams
    }

    fun setMessage(textId: Int) {
        message.apply {
            setText(textId)
            visibility = View.VISIBLE
        }
    }

    fun setMessage(text: CharSequence?) {
        message.apply {
            this.text = text
            visibility = View.VISIBLE
        }
    }

    fun setEditText(text: String, hint: String, editCallBacks: ((String) -> Unit)? = null) {
        editText.apply {
            setText(text.toCharArray(), 0, text.length)
            this.hint = hint
            visibility = View.VISIBLE
            editCallBacks?.let {
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(var1: Editable?) {
                        it(var1.toString())
                    }

                    override fun beforeTextChanged(var1: CharSequence?, var2: Int, var3: Int, var4: Int) {}
                    override fun onTextChanged(var1: CharSequence?, var2: Int, var3: Int, var4: Int) {}
                })
            }
        }
    }

    fun getEditText(): String = editText.text.toString()
}
