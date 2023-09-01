package com.gswxxn.restoresplashscreen.wrapper

import android.graphics.drawable.Drawable
import com.highcapable.yukihookapi.hook.factory.current

/**
 * SplashScreenView.Builder 的包装类
 */
class SplashScreenViewBuilderWrapper private constructor(private val builder: Any) {

    companion object {
        private val instances: MutableMap<Any, SplashScreenViewBuilderWrapper> = mutableMapOf()

        /**
         * 获取给定的 SplashScreenView.Builder 对应的单例实例，如果已经存在则返回现有实例，
         * 如果不存在则创建新的实例并进行缓存。
         *
         * @param builder SplashScreenViewBuilder 对象，用于唯一标识需要的包装实例。
         * @return 对应的 SplashScreenViewBuilderWrapper 实例。
         * @throws IllegalArgumentException 如果传递的 builder 不是有效的 SplashScreenView.Builder 实例
         */
        fun getInstance(builder: Any): SplashScreenViewBuilderWrapper {
            if (builder.javaClass.name != "android.window.SplashScreenView\$Builder") {
                throw IllegalArgumentException("Builder must be of type SplashScreenViewBuilder")
            }

            return instances.getOrPut(builder) {
                SplashScreenViewBuilderWrapper(builder)
            }
        }
    }

    /**
     * Get the rectangle size for the center view.
     */
    fun getIconSize() =
        builder.current().field { name = "mIconSize" }.int()

    /**
     * Get the background color for the view.
     */
    fun getBackgroundColor() =
        builder.current().field { name = "mBackgroundColor" }.int()

    /**
     * Get the Drawable object to fill the entire view.
     */
    fun getOverlayDrawable() =
        builder.current().field { name = "mOverlayDrawable" }.cast<Drawable>()

    /**
     * Get the Drawable object to fill the center view.
     */
    fun getCenterViewDrawable() =
        builder.current().field { name = "mIconDrawable" }.cast<Drawable>()

    /**
     * Get the background color for the icon.
     */
    fun getIconBackground() =
        builder.current().field { name = "mIconBackground" }.cast<Drawable>()

    /**
     * Get the Drawable object and size for the branding view.
     */
    fun getBrandingDrawable() =
        builder.current().field { name = "mBrandingDrawable" }.cast<Drawable>()

    /**
     * Get whether this view can be copied and transferred to the client if the view is
     * an empty style splash screen.
     */
    fun getAllowHandleSolidColor() =
        builder.current().field { name = "mAllowHandleSolidColor" }.boolean()

    /**
     * Set the rectangle size for the center view.
     */
    fun setIconSize(iconSize: Int) =
        builder.current().method { name = "setIconSize" }.call(iconSize)

    /**
     * Set the background color for the view.
     */
    fun setBackgroundColor(backgroundColor: Int) =
        builder.current().method { name = "setBackgroundColor" }.call(backgroundColor)

    /**
     * Set the Drawable object to fill the entire view.
     */
    fun setOverlayDrawable(drawable: Drawable?) =
        builder.current().method { name = "setOverlayDrawable" }.call(drawable)

    /**
     * Set the Drawable object to fill the center view.
     */
    fun setCenterViewDrawable(drawable: Drawable?) =
        builder.current().method { name = "setCenterViewDrawable" }.call(drawable)

    /**
     * Set the background color for the icon.
     */
    fun setIconBackground(iconBackground: Drawable) =
        builder.current().method { name = "setIconBackground" }.call(iconBackground)

    /**
     * Set the Drawable object and size for the branding view.
     */
    fun setBrandingDrawable(branding: Drawable?, width: Int, height: Int) =
        builder.current().method { name = "setBrandingDrawable" }.call(branding, width, height)

    /**
     * Sets whether this view can be copied and transferred to the client if the view is
     * an empty style splash screen.
     */
    fun setAllowHandleSolidColor(allowHandleSolidColor: Boolean) =
        builder.current().method { name = "setAllowHandleSolidColor" }.call(allowHandleSolidColor)

}