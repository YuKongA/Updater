package top.yukonga.update.data

import com.google.gson.annotations.SerializedName

// 这里面没用的参数可以删了，不会报错（点名jackson）
data class RomInfo(
    val AuthResult: Int? = null,
    val Code: Code? = null,
    val CrossRomCode: CrossRomCode? = null,
    val Cta: Int? = null,
    // SerializedName注释的意思是，将json中的CurrentRom字段映射到currentRom变量中
    // 如果不加这个注释，必须和json中的字段名一致，包括大小写
    @SerializedName("CurrentRom") val currentRom: CurrentRom? = null,
    val DnsType: Int? = null,
    val FailedInterval: String? = null,
    val FailedThreshold: String? = null,
    val FailedTimes: String? = null,
    val FileMirror: FileMirror? = null,
    @SerializedName("LatestRom") val latestRom: LatestRom? = null,
    val LatestRomCode: LatestRomCode? = null,
    val MirrorList: List<String>? = null,
    val RetryTimes: String? = null,
    val SignalMonitor: String? = null,
    val Signup: Signup? = null,
    val StoreIconMirror: String? = null,
    val TraceId: String? = null,
    val UseGota: Int? = null,
    val UserCmtLink: UserCmtLink? = null,
    val UserLevel: Int? = null,
    val VersionBoot: String? = null,
)

data class System(
    val txt: List<String>
)

data class Code(
    val code: Int, val message: String
)

data class CrossRomCode(
    val code: Int, val message: String
)

data class CurrentRom(
    val Erase: Int? = null,
    val Validate: String? = null,
    val bigversion: String? = null,
    val branch: String? = null,
    val codebase: String? = null,
    val descriptionUrl: String? = null,
    val device: String? = null,
    val filename: String? = null,
    val filepath: Filepath? = null,
    val filesize: String? = null,
    val md5: String? = null,
    val name: String? = null,
    val type: String? = null,
    val version: String? = null,
    val changelog: Any? = null,
)

data class FileMirror(
    val headimage: String, val icon: String, val image: String, val video: String
)

data class LatestRom(
    val Erase: Int? = null,
    val Validate: String? = null,
    val bigversion: String? = null,
    val branch: String? = null,
    val codebase: String? = null,
    val descriptionUrl: String? = null,
    val device: String? = null,
    val filename: String? = null,
    val filepath: Filepath? = null,
    val filesize: String? = null,
    val md5: String? = null,
    val name: String? = null,
    val type: String? = null,
    val version: String? = null,
)

data class LatestRomCode(
    val code: Int, val message: String
)

data class Signup(
    val rank: String, val total: String, val version: String
)

data class UserCmtLink(
    val sw: Int
)

data class Filepath(
    val icon: String, val image: String, val video: String
)
