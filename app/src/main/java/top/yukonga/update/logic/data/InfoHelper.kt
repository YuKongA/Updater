package top.yukonga.update.logic.data

import com.google.gson.annotations.SerializedName

object InfoHelper {

    data class RomInfo(
        @SerializedName("CurrentRom") val currentRom: CurrentRom? = null,
        @SerializedName("LatestRom") val latestRom: LatestRom? = null,
    )

    data class CurrentRom(
        val bigversion: String? = null,
        val branch: String? = null,
        val codebase: String? = null,
        val device: String? = null,
        val filename: String? = null,
        val filesize: String? = null,
        val md5: String? = null,
        val name: String? = null,
        val version: String? = null,
        val changelog: HashMap<String, Changelog>? = null,
    )

    data class Changelog(
        val txt: List<String>
    )

    data class LatestRom(
        val filename: String? = null,
        val md5: String? = null,
    )
}