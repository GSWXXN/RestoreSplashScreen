package com.gswxxn.restoresplashscreen.ui

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

class AppInfoHelper(private val checkedList : Set<String>) {
    private lateinit var appInfoList: MutableList<MyAppInfo>

    data class MyAppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable,
        var isChecked: Int,
        val isSystemApp: Boolean
    )

    fun getAppInfoList(): MutableList<MyAppInfo> {
        if (::appInfoList.isInitialized)
            return appInfoList.apply {
                sortBy { it.appName }
                sortByDescending {it.isChecked }
            }.toMutableList()
        appInfoList = mutableListOf()
        val pm = MainActivity.appContext.packageManager
        for (appInfo in pm.getInstalledApplications(0)) {
            MyAppInfo(
                appInfo.loadLabel(pm).toString(),
                appInfo.packageName,
                appInfo.loadIcon(pm),
                if (appInfo.packageName in checkedList) 1 else 0,
                appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            ).also { appInfoList.add(it) }
        }
        return appInfoList.apply {
            sortBy { it.appName }
            sortByDescending { it.isChecked }
        }.toMutableList()
    }

    fun setChecked(info : MyAppInfo, status : Boolean) {
        appInfoList[appInfoList.indexOf(info)].isChecked = if (status) 1 else 0
    }
}