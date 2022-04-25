# RestoreSplashScreen

尝试恢复被MIUI阉割的SplashScreen



## 测试环境

> 小米11 Ultra  
> Android 12  
> MIUI 13



## 使用方法

1. 在Xposed管理器(LSPosed)中激活模块
2. 作用域勾选`com.android.systemui`
3. 重启系统界面或者重启手机



## 已知问题

1. 在目前版本MIUI的微信、QQ等没有Splash Screen，鉴于MIUI官方回复未来会对其加回启动遮罩，所以暂时不对其研究开发
2.  ~~启动图标错位~~
3. ~~存在方角图标~~



## TODO
- [x] ~~启动图标错位~~
- [x] ~~解决图标方角圆角混杂问题~~
- [ ] 自定义Splash Screen 图标、背景



## 无法使用

请先检查模块是否正常激活，并且作用域是否勾选。如果排查后仍有错误，请提交issue，附上Log，如有能力提取SystemUI, 最好一并提交。  
酷安[@迷璐](http://www.coolapk.com/u/1189245)


## 致谢
本模块UI界面改自 [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon)  
获取应用列表方式改自 [Hide My Applist](https://github.com/Dr-TSNG/Hide-My-Applist)  
模块使用 [Yuki Hook API](https://github.com/fankes/YukiHookAPI) 构建  
使用 [libsu](https://github.com/topjohnwu/libsu) 执行Shell命令
