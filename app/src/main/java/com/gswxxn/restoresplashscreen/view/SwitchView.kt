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
import android.widget.Toast
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.view.BaseView
import cn.fkj233.ui.switch.MIUISwitch
import com.gswxxn.restoresplashscreen.R
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * 改自 BlockMIUI, 为了适配 YukiHookAPI 的配置存储方式, 后续可能通过反射实现而不是把这个类复制过来
 */
class SwitchView(
    private val pref: PrefsData<Boolean>,
    private val dataBindingRecv: DataBinding.Binding.Recv? = null,
    private val dataBindingSend: DataBinding.Binding.Send? = null,
    private val onClickListener: ((Boolean) -> Unit)? = null
): BaseView {
    private lateinit var context : Context

    lateinit var switch: MIUISwitch

    override fun getType(): BaseView = this

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return MIUISwitch(context).also {
            switch = it
            dataBindingRecv?.setView(it)
            this.context = context
            it.isChecked = context.prefs().get(pref)
            it.setOnCheckedChangeListener { v, b ->
                if (!YukiHookAPI.Status.isXposedModuleActive) {
                    v.isChecked = !b
                    Toast.makeText(context, R.string.make_sure_active, Toast.LENGTH_SHORT).show()
                } else {
                    dataBindingSend?.let { send ->
                        send.send(b)
                    }
                    callBacks?.let { it1 -> it1() }
                    onClickListener?.let { it(b) }
                    context.prefs().edit { put(pref, b) }
                }
            }
        }
    }

    /** 切换开关状态 */
    fun click() { switch.isChecked = !switch.isChecked }
}