package com.gswxxn.restoresplashscreen.data

/**
 * 该对象定义了在应用程序中可用的不同类型启动窗口的常量
 */
object StartingWindowInfo {
    /**
     * Prefer nothing or not care the type of starting window.
     */
    const val STARTING_WINDOW_TYPE_NONE = 0

    /**
     * Prefer splash screen starting window.
     */
    const val STARTING_WINDOW_TYPE_SPLASH_SCREEN = 1

    /**
     * Prefer snapshot starting window.
     */
    const val STARTING_WINDOW_TYPE_SNAPSHOT = 2

    /**
     * Prefer solid color splash screen starting window.
     */
    const val STARTING_WINDOW_TYPE_SOLID_COLOR_SPLASH_SCREEN = 3

    /**
     * Represents a preference for using a legacy splash screen as the starting window.
     */
    const val STARTING_WINDOW_TYPE_LEGACY_SPLASH_SCREEN = 4
}