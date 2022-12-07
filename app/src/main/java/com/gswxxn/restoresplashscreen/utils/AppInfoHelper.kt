package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

class AppInfoHelper(private val context: Context, private val checkedList: Set<String>, private val configList: MutableMap<String, String>) {
    private lateinit var appInfoList: MutableList<MyAppInfo>

    data class MyAppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable,
        var config: String?,
        var isChecked: Int,
        val isSystemApp: Boolean
    )

    fun getAppInfoList(): MutableList<MyAppInfo> {
        if (::appInfoList.isInitialized)
            return appInfoList.apply {
                sortBy { it.appName }
                sortByDescending { it.config }
                sortByDescending {it.isChecked }
            }.toMutableList()
        appInfoList = mutableListOf()
        val pm = context.packageManager
        for (appInfo in pm.getInstalledApplications(0)) {
            MyAppInfo(
                appInfo.loadLabel(pm).toString(),
                appInfo.packageName,
                appInfo.loadIcon(pm),
                configList[appInfo.packageName],
                if (appInfo.packageName in checkedList) 1 else 0,
                appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            ).also { appInfoList.add(it) }
        }
        return appInfoList.apply {
            sortBy { it.appName }
            sortByDescending { it.config }
            sortByDescending { it.isChecked }
        }.toMutableList()
    }

    fun setChecked(info : MyAppInfo, status : Boolean) {
        appInfoList[appInfoList.indexOf(info)].isChecked = if (status) 1 else 0
    }

    fun setConfig(info : MyAppInfo, config : String?) {
        appInfoList[appInfoList.indexOf(info)].config = config
    }

    fun setConfig(index : Int, config : String?) {
        appInfoList[index].config = config
    }

    fun getIndex(info : MyAppInfo) = appInfoList.indexOf(info)
}