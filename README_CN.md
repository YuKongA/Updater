[English](https://github.com/YuKongA/Updater/blob/main/README.md) 丨 简体中文</b>

## 前言:

使用 [Kotlin MultiPlatform](https://developer.android.google.cn/kotlin/multiplatform?hl=zh-cn) 编写的跨平台版本已经完全复刻本应用的所有功能，[推荐切换到 KMP 版本使用](https://github.com/YuKongA/Updater-KMP)


## 使用:

获取 `正式版公测 (F)` 时, 系统版本后缀部分可使用 `AUTO` 实现自动补全<br />例如: `OS1.0.26.0.AUTO` / `V14.0.4.0.AUTO`

获取 `开发版公测 (X)` 时, 系统版本请自行输入完整 `DEV` 后缀<br />例如: `OS1.0.23.12.19.DEV` / `V14.0.23.5.8.DEV`

## 注意:

仅支持获取 <b>MIUI9</b> 及以上版本, 最极端的情况为: Redmi 1S(armani), MIUI9, Android4.4

仅 [DeviceInfoHelper](https://github.com/YuKongA/Updater/blob/main/app/src/main/kotlin/top/yukonga/update/logic/data/DeviceInfoHelper.kt#L62) 内存在的设备支持使用 `AUTO` 自动补全，其余设备仍需手动输入完整版本号

<b>未登录</b>小米账号时使用 miotaV3-v1 接口, 可正常获取任何<b>存在且公开</b>机型的 `正式版公测` 的<b>详情信息</b>

<b>登录</b>小米账号后使用 miotaV3-v2 接口, 可同时获取当前账号<b>拥有权限</b>对应机型的 `正式版内测`/`开发版公测` 的<b>详情信息</b>

## 引用:

- [Xiaomi-Update-Info](https://github.com/YuKongA/Xiaomi-Update-Info)
- [XiaoMiToolV2](https://github.com/francescotescari/XiaoMiToolV2)
- [Xiaomi-Community-AutoTask](https://github.com/CMDQ8575/Xiaomi-Community-AutoTask)
- [小米手机型号汇总](https://github.com/KHwang9883/MobileModels/blob/master/brands/xiaomi.md)
