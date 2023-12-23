package top.yukonga.update.logic.data

import com.google.gson.annotations.SerializedName

object FastbootRomInfoHelper {

    data class RomInfo(
        @SerializedName("LatestFullRom") val latestFullRom: LatestFullRom? = null,
    )

    data class LatestFullRom(
        val filename: String? = null,
        val md5: String? = null,
        val filesize: String? = null,
    )
}