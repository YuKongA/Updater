package top.yukonga.update.data

import com.google.gson.annotations.SerializedName

//这里面没用的参数可以删了，不会报错（点名jackson）
data class RomInfo(
    val AuthResult: Int,
    val Code: Code,
    val CrossRomCode: CrossRomCode,
    val Cta: Int,
//    SerializedName注释的意思是，将json中的CurrentRom字段映射到currentRom变量中
//    如果不加这个注释，必须和json中的字段名一致，包括大小写
    @SerializedName("CurrentRom") val currentRom: CurrentRom,
    val DnsType: Int,
    val FailedInterval: String,
    val FailedThreshold: String,
    val FailedTimes: String,
    val FileMirror: FileMirror,
    val LatestRom: LatestRom,
    val LatestRomCode: LatestRomCode,
    val MirrorList: List<String>,
    val RetryTimes: String,
    val SignalMonitor: String,
    val Signup: Signup,
    val StoreIconMirror: String,
    val TraceId: String,
    val UseGota: Int,
    val UserCmtLink: UserCmtLink,
    val UserLevel: Int,
    val VersionBoot: String,
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
    val Erase: Int,
    val Validate: String,
    val bigversion: String,
    val branch: String,
    val codebase: String,
    val descriptionUrl: String,
    val device: String,
    val filename: String,
    val filepath: Filepath,
    val filesize: String,
    val md5: String,
    val name: String,
    val type: String,
    val version: String,
    val changelog: Any
)

data class FileMirror(
    val headimage: String, val icon: String, val image: String, val video: String
)

data class LatestRom(
    val Erase: Int,
    val Validate: String,
    val bigversion: String,
    val branch: String,
    val codebase: String,
    val descriptionUrl: String,
    val device: String,
    val filename: String,
    val filepath: Filepath,
    val filesize: String,
    val md5: String,
    val name: String,
    val type: String,
    val version: String
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
