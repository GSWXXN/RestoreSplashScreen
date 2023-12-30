<img src="https://raw.githubusercontent.com/GSWXXN/RestoreSplashScreen/master/doc/icon.svg" width="160">

# 启动遮罩进化

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ffbf5a0bdf954416a2e1d4347b1ea797)](https://app.codacy.com/gh/GSWXXN/RestoreSplashScreen?utm_source=github.com&utm_medium=referral&utm_content=GSWXXN/RestoreSplashScreen&utm_campaign=Badge_Grade)
[![Xposed](https://img.shields.io/badge/-Xposed-green?style=flat&logo=Android&logoColor=white)](https://github.com/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/)
[![GitHub](https://img.shields.io/github/license/GSWXXN/RestoreSplashScreen)](https://github.com/GSWXXN/RestoreSplashScreen/blob/master/LICENSE)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/GSWXXN/RestoreSplashScreen?label=version)](https://github.com/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/releases)
[![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/total?label=Downloads)](https://github.com/Xposed-Modules-Repo/com.gswxxn.restoresplashscreen/releases)

~~尝试恢复被MIUI阉割的SplashScreen~~  
~~自定义MIUI的Splash Screen~~  
为 Splash Screen 添加自定义选项

模块适配大部分安卓系统, 但目前还是以运行在高通 SoC 的 MIUI/HyperOS 为主, 如果在其他系统中使用遇到问题也欢迎反馈  

## 测试环境

> 小米12 Ultra  
> Android 14  
> HyperOS 1.0

## 模块功能

1. 为所有应用显示原生 Splash Screen 界面
2. 对于主动适配 Splash Screen 的应用使用默认静态图标
3. 替换获取图标方式, 使 Splash Screen 的图标与桌面图标一致(多用于主题)
4. 根据图标自适应 Splash Screen 背景颜色
5. 彻底关闭 Splash Screen 特性

## 使用方法

1. 在 Xposed 管理器 (LSPosed) 中激活模块
2. 作用域勾选: 系统界面(`com.android.systemui`) 和 系统框架(`android`)
3. 重启手机

## 已知问题

- ~~微信、QQ、支付宝的Splash Screen只能在MIUI内测22.4.25后显示~~ (v1.9 已支持)

- 开启模块后出现启动应用卡顿可能与调度模块同时开启有关，这个我也在想办法优化 (v2.0 优化过，反馈似乎还不错)

- ~~部分机型 (如Redmi Note 10 Pro、小米12Pro) 的系统版本 (如稳定版、开发版) 可能无法使用本模块，
请等待系统更新~~ (v1.9 已支持)

## 常见问题解答

[点击跳转](https://gswxxn.coding.net/public/restoresplashscreen/faq/git)

## 无法使用

请先检查模块是否正常激活，并且作用域是否勾选。如果排查后仍有错误，请提交 issue，并附上 LSPosed 的日志，如有能力提取
SystemUI, 最好一并提交。  
也可以联系酷安 [@迷璐](http://www.coolapk.com/u/1189245)

## 捐赠支持

点个 **Star** 也是对我的支持。

如果你想捐赠，觉得这个模块好用的不得了，我会非常感谢你的！！！如果这个模块对你来说只要还差一点点意思，就不要捐赠啦

<img
    src="https://raw.githubusercontent.com/GSWXXN/RestoreSplashScreen/master/doc/donate.png"
    width = "250"
    alt="donate"
/>

## 致谢

使用 [Yuki Hook API](https://github.com/fankes/YukiHookAPI) 构建模块  
UI界面改自 [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon)  
使用 [BlockMIUI](https://github.com/Block-Network/blockmiui) 的部分资源构建UI  
获取应用列表方式参考 [Hide My Applist](https://github.com/Dr-TSNG/Hide-My-Applist)  
曾使用 [libsu](https://github.com/topjohnwu/libsu) 执行Shell命令  
参考 [MIUIHomeR](https://github.com/qqlittleice/MiuiHome_R) 优化部分代码  
使用 [Sweet Dependency](https://github.com/HighCapable/SweetDependency) 自动装配和管理依赖  
使用 [Sweet Property](https://github.com/HighCapable/SweetProperty) 管理项目属性
