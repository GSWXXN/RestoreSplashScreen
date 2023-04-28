package com.gswxxn.restoresplashscreen.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

/**
 * 原始位置 [Hide-My-Applist](https://github.com/Dr-TSNG/Hide-My-Applist/blob/d377ab13ddf74a3f1b561fda1631a62e4fbc0ab0/app/src/main/java/com/tsng/hidemyapplist/app/helpers/AppInfoHelper.kt)
 */
class AppInfoHelper(private val context: Context, private val checkedList: Set<String>, private val configList: MutableMap<String, String>) {
    private lateinit var appInfoList: MutableList<MyAppInfo>

    /**
     * 数据类, 存储应用信息
     */
    data class MyAppInfo(
        val appName: String,
        val packageName: String,
        val icon: Drawable,
        // 该 config 用于存储具体应用配置, 例如最小持续时间, 单独设置的背景颜色等
        var config: String?,
        // 该 isChecked 用于存储应用是否被勾选, 0 为未勾选, 1 为勾选
        var isChecked: Int,
        val isSystemApp: Boolean
    )

    /**
     * 获取应用信息列表
     *
     * @return [MutableList] 应用信息列表
     */
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

    /**
     * 设置选项的勾选状态 ([MyAppInfo.isChecked])
     *
     * @param info 应用信息
     * @param status 勾选状态
     */
    fun setChecked(info : MyAppInfo, status : Boolean) {
        appInfoList[appInfoList.indexOf(info)].isChecked = if (status) 1 else 0
    }

    /**
     * 根据应用信息设置其参配置 ([MyAppInfo.config]),
     *
     * @param info 应用信息
     * @param config 参数
     */
    fun setConfig(info : MyAppInfo, config : String?) {
        appInfoList[appInfoList.indexOf(info)].config = config
    }

    /**
     * 根据索引设置其参配置 ([MyAppInfo.config]),
     *
     * @param index 应用信息的索引
     * @param config 参数
     */
    fun setConfig(index : Int, config : String?) {
        appInfoList[index].config = config
    }

    /**
     * 获取应用信息的索引
     *
     * @param info 应用信息
     * @return 应用信息的索引
     */
    fun getIndex(info : MyAppInfo) = appInfoList.indexOf(info)
}