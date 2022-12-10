# MIUI遮罩进化
[![Xposed](https://img.shields.io/badge/-Xposed-green?style=flat&logo=Android&logoColor=white)](#)
[![GitHub](https://img.shields.io/github/license/GSWXXN/RestoreSplashScreen)](https://github.com/GSWXXN/RestoreSplashScreen/blob/master/LICENSE)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/GSWXXN/RestoreSplashScreen?label=version)](https://github.com/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/releases)
[![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/total?label=Downloads)](https://github.com/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/releases)

~~尝试恢复被MIUI阉割的SplashScreen~~   
自定义MIUI的Splash Screen

目前模块功能已趋于完善，如果你有好玩的新功能建议，欢迎反馈

## 测试环境

> 小米11 Ultra  
> Android 12  
> MIUI 13

## 模块功能
1. 为所有应用显示原生Splash Screen界面
2. 对于主动适配Splash Screen的应用使用默认静态图标
3. 替换获取图标方式 使Splash Screen的图标与桌面图标一致(多用于主题)
4. 根据图标自适应Splash Screen背景颜色
5. 彻底关闭 Splash Screen 特性

## 使用方法

1. 在Xposed管理器(LSPosed)中激活模块
2. 作用域勾选`com.android.systemui`
3. 重启系统界面或者重启手机



## 已知问题

- ~~微信、QQ、支付宝的Splash Screen只能在MIUI内测22.4.25后显示~~ (v1.9 已支持)

- 开启模块后出现启动应用卡顿可能与调度模块同时开启有关，这个我也在想办法优化 (v2.0 优化过，反馈似乎还不错)

- ~~部分机型 (如Redmi Note 10 Pro、小米12Pro) 的系统版本 (如稳定版、开发版) 可能无法使用本模块，请等待系统更新~~ (v1.9 已支持)


## 常见问题解答
[点击跳转](https://gswxxn.coding.net/public/restoresplashscreen/faq/git)


## 无法使用

请先检查模块是否正常激活，并且作用域是否勾选。如果排查后仍有错误，请提交issue，并附上LSPosed的日志，如有能力提取SystemUI, 最好一并提交。  
酷安[@迷璐](http://www.coolapk.com/u/1189245)


## 捐赠支持
点个 **Star** 也是对我的支持。

如果你想捐赠，觉得这个模块好用的不得了，我会非常感谢你的！！！如果这个模块对你来说只要还差一点点意思，就不要捐赠啦

<img src="https://raw.githubusercontent.com/GSWXXN/RestoreSplashScreen/master/Doc/donate.png" width = "250" alt="donate" />


## 致谢
使用 [Yuki Hook API](https://github.com/fankes/YukiHookAPI) 构建模块  
UI界面改自 [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon)  
使用 [BlockMIUI](https://github.com/Block-Network/blockmiui) 的部分资源构建UI  
获取应用列表方式参考 [Hide My Applist](https://github.com/Dr-TSNG/Hide-My-Applist)  
曾使用 [libsu](https://github.com/topjohnwu/libsu) 执行Shell命令  
参考 [MIUIHomeR](https://github.com/qqlittleice/MiuiHome_R) 优化部分代码

