package com.gswxxn.restoresplashscreen.hook.base

import com.gswxxn.restoresplashscreen.hook.AndroidHooker.hook
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.param.HookParam
import java.lang.reflect.Member

/**
 * 用于进行Hook操作的HookManager类。
 * 由于底层框架不支持重复 Hook, 所以在这里存储 Member 和对应的 BeforeHooks 和 AfterHooks, 等到加载完所有的 Hooker 后, 一并执行 Hook
 *
 * @param member 要进行Hook的成员（Member）对象。
 */
class HookManager private constructor(private val member: Member) {

    companion object {
        /**
         * 创建HookManager实例。
         *
         * @param createCondition 创建 HookManager 实例的条件, 如对系统判断。
         * @param block 返回要进行 Hook 的成员对象的 lambda 表达式。
         * @return 创建的 HookManager 实例，如果创建失败则返回null。
         */
        fun create(createCondition: Boolean = true, block: () -> Member?): HookManager? {
            return if (createCondition) try {
                block()?.let { HookManager(it) }
            } catch (e: Throwable) {
                loggerE(e = e)
                null
            } else null
        }

        // 默认不执行 Hook, 后续获取到包名后, 可以将这里替换为只有当前为作用域内的应用, 才执行 Hook
        var defaultExecCondition: (() -> Boolean) = { false }
    }

    private val beforeHooks =  mutableListOf<HookParam.() -> Unit>()
    private val afterHooks = mutableListOf<HookParam.() -> Unit>()
    private var replaceHook: (HookParam.() -> Any?)? = null

    /**
     * 添加一个在Hook前执行的回调函数。
     *
     * @param execCondition 判断是否执行回调函数的条件，默认为 [defaultExecCondition]。
     * @param block 在Hook前执行的回调函数。
     * @return 当前的HookManager实例。
     */
    fun addBeforeHook(execCondition: (() -> Boolean) = defaultExecCondition, block: HookParam.() -> Unit): HookManager {
        beforeHooks += { if (execCondition()) block() }
        return this
    }

    /**
     * 添加一个在Hook后执行的回调函数。
     *
     * @param execCondition 判断是否执行回调函数的条件，默认为 [defaultExecCondition]。
     * @param block 在Hook后执行的回调函数。
     * @return 当前的HookManager实例。
     */
    fun addAfterHook(execCondition: (() -> Boolean) = defaultExecCondition, block: HookParam.() -> Unit): HookManager {
        afterHooks += { if (execCondition()) block() }
        return this
    }

    /**
     * 添加一个替换原函数的回调函数, ReplaceHook 只能存在一个, 后面添加的会把之前的覆盖
     *
     * @param execCondition 判断是否执行回调函数的条件，默认为 [defaultExecCondition]。
     * @param block 替换原函数的回调函数, 注意回调函数的返回值即为替换原函数的返回值。
     * @return 当前的HookManager实例。
     */
    fun addReplaceHook(execCondition: (() -> Boolean) = defaultExecCondition, block: HookParam.() -> Any?): HookManager {
        replaceHook = { if (execCondition()) block() else callOriginal()  }
        return this
    }

    /**
     * 开始进行Hook操作。
     *
     * 将 beforeHooks 和 afterHooks 分别整合到一起
     */
    fun startHook() {
        member.declaringClass.hook {
            injectMember {
                members(member)

                val hasReplaceHook = replaceHook != null
                val hasBeforeHooks = beforeHooks.isNotEmpty()
                val hasAfterHooks = afterHooks.isNotEmpty()

                if (hasReplaceHook && (hasBeforeHooks || hasAfterHooks))
                    loggerW(msg = "Conflict detected: ReplaceHook and Before/After Hooks cannot coexist. The before/after hooks will be ignored. Affected class: ${member.declaringClass}. Affected member: ${member.name}. Please ensure that only one type of hook is used at a time.")

                if (hasReplaceHook) replaceAny { replaceHook!!() }
                else {
                    if (hasBeforeHooks) beforeHook { beforeHooks.forEach { it() } }
                    if (hasAfterHooks) afterHook { afterHooks.forEach { it() } }
                }
            }
        }
    }
}