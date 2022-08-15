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

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.view.BaseView

class LineView(
    private val isShort: Boolean,
    private val dataBindingRecv: DataBinding.Binding.Recv? = null
) : BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return View(context).also {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp2px(context, 0.9f)
            )
            layoutParams.setMargins(
                if (isShort) dp2px(context, 20f) else 0,
                dp2px(context, 23f),
                0,
                dp2px(context, 23f)
            )
            it.layoutParams = layoutParams
            it.setBackgroundColor(context.resources.getColor(R.color.line, null))
            dataBindingRecv?.setView(it)
        }
    }

}