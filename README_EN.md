![Updater](https://socialify.git.ci/YuKongA/Updater/image?description=1&descriptionEditable=Get%20HyperOS%2FMIUI%20update%20info&language=1&name=1&owner=1&pattern=Signal&theme=Auto)

<div align="center">

English 丨 [简体中文](https://github.com/YuKongA/Updater/blob/main/README.md)</b>

[![Crowdin](https://badges.crowdin.net/updater-miota/localized.svg)](https://crowdin.com/project/updater-miota)

</div>

## Preface:
The Multi-platform version written using [Kotlin MultiPlatform](https://developer.android.google.cn/kotlin/multiplatform) has completely replicated all the feature of this application. [It is recommended to switch to the KMP version](https://github.com/YuKongA/Updater-KMP)

## Usage:
When obtaining the `Pubilc Release Version (F)`, the suffix of the system version can be automatically completed using `AUTO`<br />For example: `OS1.0.26.0.AUTO` / `V14.0.4.0.AUTO`

When obtaining `Beta Development Version (X)`, please enter the complete system version<br />For example: `OS1.0.23.12.19.DEV` / `V14.0.23.5.8.DEV`

## Notes:

Only supported `MIUI9` and above versions. The most extreme case is: Redmi 1S (armani), MIUI9, Android4.4

Only devices in the list of [DeviceInfoHelper](https://github.com/YuKongA/Updater/blob/main/app/src/main/kotlin/top/yukonga/update/logic/data/DeviceInfoHelper.kt#L62) are supported use `AUTO` to complete automatically, other devices still need to manually enter the full system version

When you are not logged in with a Xiaomi account, you can use the miotaV3-v1 interface to obtain any detailed information of the `Pubilc Release Version` of any model.

After logging in to your Xiaomi account, you will use the miotaV3-v2 interface to obtain detailed information about the `Beta Release Version` or the `Public Development Version`, corresponding to the internal test permissions you have.

## Credits:

- [Xiaomi-Update-Info](https://github.com/YuKongA/Xiaomi-Update-Info)
- [XiaoMiToolV2](https://github.com/francescotescari/XiaoMiToolV2)
- [Xiaomi-Community-AutoTask](https://github.com/CMDQ8575/Xiaomi-Community-AutoTask)
- [MobileModels-Xiaomi](https://github.com/KHwang9883/MobileModels/blob/master/brands/xiaomi.md)