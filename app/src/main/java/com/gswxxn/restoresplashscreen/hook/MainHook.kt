package com.gswxxn.restoresplashscreen.hook

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val r = 45F
        if (lpparam?.packageName == "com.android.systemui") {

//            XposedHelpers.findClass(
//                "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder",
//                lpparam.classLoader
//            )
//                .let {
//
//                    XposedHelpers.findAndHookMethod(it, "build",
//                        object : XC_MethodHook() {
//                            override fun beforeHookedMethod(param: MethodHookParam?) {
//                                super.beforeHookedMethod(param)
//
//                                XposedBridge.log(
//                                    "[RestoreSplashScreen] " +
//                                            "${
//                                                XposedHelpers.getObjectField(
//                                                    XposedHelpers.getObjectField(
//                                                        param?.thisObject,
//                                                        "mActivityInfo"
//                                                    ) as ActivityInfo, "packageName"
//                                                )
//                                            }" +
//                                            " mSuggestType = " +
//                                            "${XposedHelpers.getIntField(param?.thisObject, "mSuggestType")}"
//                                )
//                            }
//                        })
//                }

//                    XposedHelpers.findClass(
//                "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder",
//                lpparam.classLoader
//            ).let {
//
//                XposedBridge.hookAllMethods(it, "fillViewWithIcon",
//                    object : XC_MethodHook() {
//                        override fun beforeHookedMethod(param: MethodHookParam?) {
//                            super.beforeHookedMethod(param)
//
//                            if (XposedHelpers.getObjectField(
//                                    XposedHelpers.getObjectField(
//                                        param?.thisObject,
//                                        "mActivityInfo"
//                                    ) as ActivityInfo, "packageName"
//                                ) == "com.coolapk.market"){
//                                XposedHelpers.setIntField(param?.thisObject, "mThemeColor", Color.parseColor("#800080"))
//                            }
//                                XposedBridge.log("[RestoreSplashScreen] change color")
//                        }
//                    })
//            }

            XposedHelpers.findClass(
                "com.android.wm.shell.startingsurface.SplashscreenContentDrawer",
                lpparam.classLoader
            )
                .let {

                    XposedHelpers.findAndHookMethod(it, "isCTS",
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                super.beforeHookedMethod(param)

                                param?.result = true
                            }
                        })
                }



            XposedHelpers.findClass(
                 "com.android.launcher3.icons.BaseIconFactory",
                lpparam.classLoader
            ).let {

                XposedBridge.hookAllMethods(it, "createScaledBitmapWithoutShadow",
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            super.beforeHookedMethod(param)

                            val bitmap = Utils.drawable2Bitmap(param!!.args[0] as Drawable)
                            val context = XposedHelpers.getObjectField(param.thisObject, "mContext") as Context
                            val size = XposedHelpers.getIntField(param.thisObject, "mIconBitmapSize")

                            param.args[0] = BitmapDrawable(
                                Utils.roundBitmapByShader(
                                    bitmap,
                                    size,
                                    size,
                                    Utils.dp2px(context, r)
                                )
                            )
                            param.args[1] = false

                        }
                    })
            }

            XposedHelpers.findClass(
                "com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder",
                lpparam.classLoader
            ).let {

                    XposedBridge.hookAllMethods(it, "processAdaptiveIcon",
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                super.beforeHookedMethod(param)

                                param?.result = false
                            }
                        })
                }

            }
    }


}