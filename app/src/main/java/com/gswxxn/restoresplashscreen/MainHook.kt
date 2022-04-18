package com.gswxxn.restoresplashscreen

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName == "com.android.systemui") {


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

            XposedHelpers.findClass(
                "com.android.launcher3.icons.BaseIconFactory",
                lpparam.classLoader
            ).let {

                XposedBridge.hookAllMethods(it, "createScaledBitmapWithoutShadow",
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            super.beforeHookedMethod(param)

                            param!!.args[1] = false

                            //XposedBridge.log("[RestoreSplashScreen] change shrink")
                        }
                    })
            }


        }
    }
}