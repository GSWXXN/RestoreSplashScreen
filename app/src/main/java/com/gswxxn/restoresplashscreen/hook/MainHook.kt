package com.gswxxn.restoresplashscreen.hook

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.gswxxn.restoresplashscreen.Data.DataConst
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy
import de.robv.android.xposed.XposedHelpers

@InjectYukiHookWithXposed
class MainHook : YukiHookXposedInitProxy {
    override fun onInit() = configs {
        debugTag = "RestoreSplashScreen"
        isDebug = false
    }

    override fun onHook() = encase {
        when {
            prefs.get(DataConst.ENABLE_MODULE).not() -> loggerW(msg = "Aborted Hook -> Hook Closed")
            else -> loadApp("com.android.systemui") {

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
                fun printLog (vararg msg : String){
                    if (prefs.get(DataConst.ENABLE_LOG))
                        msg.forEach {
                            loggerI(msg = it)
                        }
                    }


                findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer").hook {
                    // 关闭MIUI优化
                    injectMember {
                        method {
                            name = "isCTS"
                            emptyParam()
                        }
                        beforeHook {
                            result = true
                            printLog("isCTS(): return true")
                        }
                    }
                }


                // 为自适应图标绘制圆角
                findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder")
                    .hook {
                        injectMember {
                            method {
                                name = "processAdaptiveIcon"
                                param(Drawable::class.java)
                            }
                            beforeHook {
//                                printLog("set adaptable icon not adapt")
//                                result = false

                                val drawable = args(0).cast<Drawable>()
                                if (drawable is AdaptiveIconDrawable) {
                                    val size = drawable.intrinsicWidth
                                    args(0).set(BitmapDrawable(appContext.resources,
                                            Utils.roundBitmapByShader(
                                                drawable.let { Utils.drawable2Bitmap(it) },
                                                size,
                                                size / 4
                                            )
                                        )
                                    )
                                    printLog("processAdaptiveIcon(): argument is AdaptiveIcon, set round corner")
                                } else { printLog("processAdaptiveIcon(): argument is not AdaptiveIcon, jump over hook") }

                            }
                        }

                        injectMember {
                            method {
                                name = "build"
                                emptyParam()
                            }
                            beforeHook {
                                val packageName = (XposedHelpers.getObjectField(instance, "mActivityInfo") as ActivityInfo).packageName
                                val list = prefs.get(DataConst.CUSTOM_SCOPE_LIST)
                                val isException = prefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)

                                if (isException && (packageName in list)
                                    || !isException && packageName !in list
                                ) {
                                    // 设置SuggestType
                                    XposedHelpers.setIntField(instance, "mSuggestType", 5)
                                    // 设置背景
                                    if (XposedHelpers.getObjectField(instance, "mOverlayDrawable") == null){

                                        // 睡着时候写的，待优化
                                        val context = XposedHelpers.getObjectField(instance, "mContext") as Context

                                        val clazz = XposedHelpers.getObjectField(instance, "this$0")
                                        val mTmpAttrs = XposedHelpers.getObjectField(clazz, "mTmpAttrs")
                                        val mWindowBgResId = XposedHelpers.getIntField(mTmpAttrs, "mWindowBgResId")

                                        val drawable = context.getDrawable(mWindowBgResId)
                                        XposedHelpers.setObjectField(instance, "mOverlayDrawable", drawable)
                                    }

                                    printLog("***",
                                        "${packageName}:",
                                        "build(): this app is in exception list, set mSuggestType 5")
                                } else { printLog("***",
                                    "${packageName}:",
                                    "build(): mSuggestType is ${XposedHelpers.getIntField(instance, "mSuggestType")}") }
                            }
                        }

                    }

                // 为图标绘制圆角
                findClass("com.android.launcher3.icons.BaseIconFactory").hook {
                    injectMember {
                        method {
                            name = "createScaledBitmapWithoutShadow"
                            param(Drawable::class.java, BooleanType)
                        }
                        beforeHook {
                            val drawable= args(0).cast<Drawable>()
                            val size = drawable?.intrinsicWidth
                            args(0).set(BitmapDrawable(appContext.resources,
                                    Utils.roundBitmapByShader(
                                        drawable?.let { Utils.drawable2Bitmap(it) },
//                                        XposedHelpers.getIntField(instance, "mIconBitmapSize"),
//                                        Utils.dp2px(appContext, 45F)
                                        size!!,
                                        size / 4
                                    )
                                )
                            )
                            printLog("set not adaptable icon round corner")
                            args(1).set(false)
                            printLog("set large icon")
                        }
                    }
                }

            }

        }
    }
}