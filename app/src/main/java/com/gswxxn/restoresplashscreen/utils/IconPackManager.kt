package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.pm.PackageManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlPullParserException
import android.content.Intent
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 图标包处理类
 *
 * @param mContext 上下文 - 必填
 * @param packageName 图标包包名 - 如不需获取图标, 可不填
 */
class IconPackManager(private val mContext: Context, private val packageName: String? = null) {

    private var mLoaded = false
    private val mPackagesDrawables = HashMap<String?, String?>()
    private var totalIcons = 0
    private var iconPackRes: Resources? = null

    private fun load() {
        // load appfilter.xml from the icon pack package
        val pm = mContext.packageManager
        try {
            var xpp: XmlPullParser? = null
            iconPackRes = pm.getResourcesForApplication(packageName!!)
            val appFilterID = iconPackRes!!.getIdentifier("appfilter", "xml", packageName)
            if (appFilterID > 0) {
                xpp = iconPackRes!!.getXml(appFilterID)
            } else {
                // no resource found, try to open it from assests folder
                try {
                    val appFilterStream = iconPackRes!!.assets.open("appfilter.xml")
                    val factory = XmlPullParserFactory.newInstance()
                    factory.isNamespaceAware = true
                    xpp = factory.newPullParser()
                    xpp.setInput(appFilterStream, "utf-8")
                } catch (e: IOException) {
                    //Ln.d("No appfilter.xml file");
                }
            }
            if (xpp != null) {
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.name == "item") {
                            var componentName: String? = null
                            var drawableName: String? = null
                            for (i in 0 until xpp.attributeCount) {
                                if (xpp.getAttributeName(i) == "component") {
                                    componentName = xpp.getAttributeValue(i)
                                } else if (xpp.getAttributeName(i) == "drawable") {
                                    drawableName = xpp.getAttributeValue(i)
                                }
                            }
                            if (!mPackagesDrawables.containsKey(componentName)) {
                                mPackagesDrawables[componentName] = drawableName
                                totalIcons += 1
                            }
                        }
                    }
                    eventType = xpp.next()
                }
            }
            mLoaded = true
        } catch (e: PackageManager.NameNotFoundException) {
            //Ln.d("Cannot load icon pack");
        } catch (e: XmlPullParserException) {
            //Ln.d("Cannot parse icon pack appfilter.xml");
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadDrawable(drawableName: String): Drawable? {
        val id = iconPackRes!!.getIdentifier(drawableName, "drawable", packageName)
        if (id > 0) {
            return iconPackRes!!.getDrawable(id, mContext.theme)
        }
        return null
    }

    /**
     * 根据应用名获取图标
     *
     * @param appPackageName 需要获取图标的应用包名
     * @return [Drawable]
     */
    fun getIconForPackage(appPackageName: String?): Drawable? {
        if (!mLoaded) load()
        val pm = mContext.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(appPackageName!!)
        var componentName: String? = null
        if (launchIntent != null) componentName = pm.getLaunchIntentForPackage(appPackageName)!!
            .component.toString()
        var drawableName = mPackagesDrawables[componentName]
        if (drawableName != null) {
            return loadDrawable(drawableName)
        } else {
            // try to get a resource with the component filename
            if (componentName != null) {
                val start = componentName.indexOf("{") + 1
                val end = componentName.indexOf("}", start)
                if (end > start) {
                    drawableName =
                        componentName.substring(start, end).lowercase(Locale.getDefault())
                            .replace(".", "_").replace("/", "_")
                    if (iconPackRes!!.getIdentifier(
                            drawableName,
                            "drawable",
                            packageName
                        ) > 0
                    ) return loadDrawable(drawableName)
                }
            }
        }
        return null
    }

    /**
     * 获取可用的图标包列表
     *
     * @return [HashMap] key: 图标包应用名; value: 图标包包名. 默认添加一个键值均为 None 的键值对
     */
    fun getAvailableIconPacks(): HashMap<String, String> {
        val iconPacks = HashMap<String, String>()
        iconPacks["None"] = "None"

        // find apps with intent-filter "com.gau.go.launcherex.theme" and return build the HashMap
        val pm = mContext.packageManager
        val adwLauncherThemes = pm.queryIntentActivities(
            Intent("org.adw.launcher.THEMES"),
            PackageManager.GET_META_DATA
        )
        val goLauncherThemes = pm.queryIntentActivities(
            Intent("com.gau.go.launcherex.theme"),
            PackageManager.GET_META_DATA
        )

        // merge those lists
        val rInfo: MutableList<ResolveInfo> = ArrayList(adwLauncherThemes)
        rInfo.addAll(goLauncherThemes)
        for (ri in rInfo) {
            val packageName = ri.activityInfo.packageName
            try {
                val ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val appName = mContext.packageManager.getApplicationLabel(ai).toString()
                iconPacks[appName] = packageName
            } catch (e: PackageManager.NameNotFoundException) {
                // shouldn't happen
                e.printStackTrace()
            }
        }
        return iconPacks
    }
}