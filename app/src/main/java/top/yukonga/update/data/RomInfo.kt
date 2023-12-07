package top.yukonga.update.data

import com.google.gson.annotations.SerializedName

data class RomInfo(
    // SerializedName注释的意思是，将json中的CurrentRom字段映射到currentRom变量中
    // 如果不加这个注释，必须和json中的字段名一致，包括大小写
    @SerializedName("CurrentRom") val currentRom: CurrentRom? = null,
    val DnsType: Int? = null,
    val FailedInterval: String? = null,
    val FailedThreshold: String? = null,
    val FailedTimes: String? = null,
    @SerializedName("LatestRom") val latestRom: LatestRom? = null,
    val MirrorList: List<String>? = null,
    val RetryTimes: String? = null,
    val SignalMonitor: String? = null,
    val StoreIconMirror: String? = null,
    val TraceId: String? = null,
    val UseGota: Int? = null,
    val UserLevel: Int? = null,
    val VersionBoot: String? = null,
)

data class CurrentRom(
    val bigversion: String? = null,
    val branch: String? = null,
    val codebase: String? = null,
    val descriptionUrl: String? = null,
    val device: String? = null,
    val filename: String? = null,
    val filesize: String? = null,
    val md5: String? = null,
    val name: String? = null,
    val type: String? = null,
    val version: String? = null,
    val changelog: Any? = null,
)

data class LatestRom(
    val bigversion: String? = null,
    val branch: String? = null,
    val codebase: String? = null,
    val descriptionUrl: String? = null,
    val device: String? = null,
    val filename: String? = null,
    val filesize: String? = null,
    val md5: String? = null,
    val name: String? = null,
    val type: String? = null,
    val version: String? = null,
)
