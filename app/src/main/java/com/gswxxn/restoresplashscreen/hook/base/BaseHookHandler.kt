package com.gswxxn.restoresplashscreen.hook.base

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

/**
 * Hook处理程序的抽象基类。
 */
abstract class BaseHookHandler {
    lateinit var baseHooker: YukiBaseHooker

    val appContext get() = baseHooker.appContext
    val appClassLoader get() = baseHooker.appClassLoader
    val appUserId get() = baseHooker.appUserId
    val appResources get() = baseHooker.appResources
    val prefs get() = baseHooker.prefs

    /**
     * 在进行Hook时调用的方法。
     */
    abstract fun onHook()
}