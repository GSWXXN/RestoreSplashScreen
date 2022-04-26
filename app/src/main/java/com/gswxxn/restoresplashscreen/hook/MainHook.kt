package com.gswxxn.restoresplashscreen.hook

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.gswxxn.restoresplashscreen.Data.DataConst
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
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
                fun printLog (vararg msg : String){ if (prefs.get(DataConst.ENABLE_LOG)) msg.forEach { loggerI(msg = it) } }

                // 关闭MIUI优化
                findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer").hook {
                    injectMember {
                        method {
                            name = "isCTS"
                            emptyParam()
                        }
                        beforeHook {
                            result = true
                            printLog("**********", "isCTS(): return true")
                        }
                    }
                }

                findClass("com.android.wm.shell.startingsurface.SplashscreenContentDrawer\$StartingWindowViewBuilder")
                    .hook {
                        injectMember {
                            method {
                                name = "build"
                                emptyParam()
                            }
                            beforeHook {
                                val packageName = (XposedHelpers.getObjectField(instance, "mActivityInfo") as ActivityInfo).packageName
                                val list = prefs.get(DataConst.CUSTOM_SCOPE_LIST)
                                val isExceptionMode = prefs.get(DataConst.IS_CUSTOM_SCOPE_EXCEPTION_MODE)
                                val enableCustomScope = prefs.get(DataConst.ENABLE_CUSTOM_SCOPE)
                                val isException = (enableCustomScope && isExceptionMode && (packageName in list))
                                        || (enableCustomScope && !isExceptionMode && packageName !in list)

                                val isDefaultStyle = prefs.get(DataConst.ENABLE_DEFAULT_STYLE)
                                        && packageName in prefs.get(DataConst.DEFAULT_STYLE_LIST)

                                val clazz = XposedHelpers.getObjectField(instance, "this$0")
                                val mTmpAttrs = XposedHelpers.getObjectField(clazz, "mTmpAttrs")

                                // 是否在作用域外
                                if (isException) {
                                    // 设置SuggestType
                                    XposedHelpers.setIntField(instance, "mSuggestType", 5)
                                    // 设置背景
                                    if (XposedHelpers.getObjectField(instance, "mOverlayDrawable") == null){

                                        val context = XposedHelpers.getObjectField(instance, "mContext") as Context
                                        val mWindowBgResId = XposedHelpers.getIntField(mTmpAttrs, "mWindowBgResId")

                                        val drawable = context.getDrawable(mWindowBgResId)
                                        XposedHelpers.setObjectField(instance, "mOverlayDrawable", drawable)
                                    }

                                    printLog("${packageName}:",
                                        "build(): this app is in exception list, set mSuggestType 5")
                                } else { printLog("${packageName}:",
                                    "build(): mSuggestType is ${XposedHelpers.getIntField(instance, "mSuggestType")}") }

                                // 使用系统默认样式
                                if (isDefaultStyle) {
                                    XposedHelpers.setObjectField(mTmpAttrs, "mSplashScreenIcon", null)
                                    printLog("build(): use system default icon style")
                                }

                            }
                        }

                        // 自适应背景颜色
                        injectMember {
                            method {
                                name = "createIconDrawable"
                                paramCount(2)
                            }
                            beforeHook {
                                val enableChangeBgColor = prefs.get(DataConst.ENABLE_CHANG_BG_COLOR)
                                val isDarkMode = appContext.resources
                                    .configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
                                val isInExceptList = (XposedHelpers.getObjectField(instance, "mActivityInfo") as ActivityInfo).packageName in prefs.get(DataConst.BG_EXCEPT_LIST)

                                if (enableChangeBgColor && !isInExceptList && !isDarkMode) {
                                    val drawable = args(0).cast<Drawable>()
                                    val color = Utils.getBgColor(Utils.drawable2Bitmap(drawable!!)!!)
                                    XposedHelpers.setIntField(instance, "mThemeColor", color)
                                    printLog("createIconDrawable(): change background color")
                                }
                            }
                        }

                    }

                // 设置图标不缩小
                findClass("com.android.launcher3.icons.BaseIconFactory").hook {
                    injectMember {
                        method {
                            name = "createScaledBitmapWithoutShadow"
                            param(Drawable::class.java, BooleanType)
                        }
                        beforeHook {
                            args(1).set(false)
                            printLog("BaseIconFactory(): set icon large")
                        }
                    }
                }

                // 图标处理
                findClass("com.android.launcher3.icons.IconProvider").hook {
                    injectMember {
                        method {
                            name = "getIcon"
                            param(ActivityInfo::class.java, IntType)
                        }
                        afterHook {
                            val enableReplaceIcon = prefs.get(DataConst.ENABLE_REPLACE_ICON)
                            val isCircle = prefs.get(DataConst.IS_CIRCLE_ICON)

                            // 替换获取图标方式
                            val drawable = if (enableReplaceIcon) {
                                printLog("IconProvider(): replace Icon")
                                args(0).cast<ActivityInfo>()?.packageName
                                    ?.let { appContext.packageManager.getApplicationIcon(it) }!!
                            }else {
                                result<Drawable>()
                            }

                            // 绘制图标圆角
                            printLog("IconProvider(): draw round corner")
                            result = BitmapDrawable(appContext.resources,
                                Utils.roundBitmapByShader(drawable?.let { Utils.drawable2Bitmap(it) }, isCircle))

                        }
                    }
                }

            }

        }
    }
}