package top.yukonga.update.logic.data

import com.google.gson.annotations.SerializedName

object InfoHelper {

    data class RomInfo(
        @SerializedName("AuthResult") val authResult: Int? = null,
        @SerializedName("Code") val code: Code? = null,
        @SerializedName("CrossRomCode") val crossRomCode: CrossRomCode? = null,
        @SerializedName("Cta") val cta: Int? = null,
        @SerializedName("CurrentRom") val currentRom: CurrentRom? = null,
        @SerializedName("DnsType") val dnsType: Int? = null,
        @SerializedName("FailedInterval") val failedInterval: String? = null,
        @SerializedName("FailedThreshold") val failedThreshold: String? = null,
        @SerializedName("FailedTimes") val failedTimes: String? = null,
        @SerializedName("FileMirror") val fileMirror: FileMirror? = null,
        @SerializedName("LatestRom") val latestRom: LatestRom? = null,
        @SerializedName("LatestRomCode") val latestRomCode: LatestRomCode? = null,
        @SerializedName("MirrorList") val mirrorList: List<String>? = null,
        @SerializedName("RetryTimes") val retryTimes: String? = null,
        @SerializedName("SignalMonitor") val signalMonitor: String? = null,
        @SerializedName("Signup") val signup: Signup? = null,
        @SerializedName("StoreIconMirror") val storeIconMirror: String? = null,
        @SerializedName("TraceId") val traceId: String? = null,
        @SerializedName("UseGota") val useGota: Int? = null,
        @SerializedName("UserCmtLink") val userCmtLink: UserCmtLink? = null,
        @SerializedName("UserLevel") val userLevel: Int? = null,
        @SerializedName("VersionBoot") val versionBoot: String? = null,
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
        @SerializedName("Erase") val erase: Int? = null,
        @SerializedName("Validate") val validate: String? = null,
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
        val changelog: HashMap<String, Changelog>? = null,
    )

    data class Changelog(
        val txt: List<String>
    )

    data class FileMirror(
        val headimage: String, val icon: String, val image: String, val video: String
    )

    data class LatestRom(
        @SerializedName("Erase") val erase: Int? = null,
        @SerializedName("Validate") val validate: String? = null,
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

}