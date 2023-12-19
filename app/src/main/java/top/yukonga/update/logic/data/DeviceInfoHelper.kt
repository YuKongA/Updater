package top.yukonga.update.logic.data

object DeviceInfoHelper {

    fun codeName(name: String): String {
        val device = devices.find { it.name == name } ?: return name
        return device.codeName
    }

    fun regions(codeName: String, regions: String): String {
        val device = devices.find { it.codeName == codeName } ?: return ""
        val newRegions = device.regions.find { it.version == regions } ?: return ""
        return newRegions.codeNameExt
    }

    fun isExistRegions(codeName: String, regions: String): Boolean {
        val device = devices.find { it.codeName == codeName } ?: return true // Need to be completed
        device.regions.find { it.version == regions } ?: return false
        return true
    }

    fun deviceCode(android: String, codeName: String, variant: String): String {
        val newAndroid = androids.find { it.version == android } ?: return ""
        val device = devices.find { it.codeName == codeName } ?: return ""
        return "${newAndroid.android}${device.deviceCode}${variant}${Xiaomi}"
    }

    data class AndroidVersion(val version: String, val android: String)

    data class DeviceRegions(val codeNameExt: String, val version: String)

    data class Device(val name: String, val codeName: String, val deviceCode: String, val regions: List<DeviceRegions>)

    private val androidS = AndroidVersion("12", "S")
    private val androidT = AndroidVersion("13", "T")
    private val androidU = AndroidVersion("14", "U")

    private val androids = listOf(androidS, androidT, androidU)

    private val CN = DeviceRegions("", "CN")
    private val GL = DeviceRegions("_global", "MI")
    private val EEA = DeviceRegions("_eea_global", "EU")
    private val RU = DeviceRegions("_ru_global", "RU")
    private val TW = DeviceRegions("_tw_global", "TW")
    private val ID = DeviceRegions("_id_global", "ID")
    private val TR = DeviceRegions("_tr_global", "TR")
    private val KR = DeviceRegions("_kr_global", "KR")
    private val JP = DeviceRegions("_jp_global", "JP")
    private val IN = DeviceRegions("_in_global", "IN")

    private val Xiaomi = "XM"

    private val devices = listOf(
        Device("Redmi K30 4G", "phoenix", "GH", listOf(CN)),
        Device("Poco X2", "phoenixin", "GH", listOf(IN)),
        Device("Redmi K30 5G / K30i 5G", "picasso", "GI", listOf(CN)),
        Device("Xiaomi 10", "umi", "JB", listOf(CN, GL, EEA, RU, ID, TR, IN)),
        Device("Xiaomi 10 Pro", "cmi", "JA", listOf(CN, GL, EEA)),
        Device("Redmi Note 9 Pro", "joyeuse", "JZ", listOf(GL, EEA, RU, TR, ID, TW)),
        Device("Redmi Note 9 Pro (India) / Note 9S / Note 10 Lite", "curtana", "JW", listOf(GL, EEA, RU, TR, IN)),
        Device("Redmi Note 9 Pro Max", "excalibur", "JX", listOf(IN)),
        Device("Redmi K30 Pro", "lmi", "JK", listOf(CN, GL, EEA, RU, ID, TR)),
        Device("Xiaomi 10 Lite", "monet", "JI", listOf(GL, TW, EEA)),
        Device("Xiaomi 10 Lite (China)", "vangogh", "JV", listOf(CN)),
        Device("Redmi Note 9 / 10X 4G", "merlin", "JO", listOf(CN, GL, TW, EEA, RU, ID, TR, IN)),
        Device("Redmi 10X 5G", "atom", "JH", listOf(CN)),
        Device("Redmi 10X Pro 5G", "bomb", "JL", listOf(CN)),
        Device("Xiaomi Note 10 Lite ", "toco", "FN", listOf(GL, EEA, RU, TR)),
        Device("Redmi 9 / 9 Prime", "lancelot", "JC", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("POCO M2 Pro", "gram", "JP", listOf(IN)),
        Device("Redmi K30 Ultra", "cezanne", "JN", listOf(CN)),
        Device("Xiaomi 10 Ultra", "cas", "JJ", listOf(CN)),
        // TODO ↓↓↓

        Device("Xiaomi 13 Ultra", "ishtar", "MA", listOf(CN, GL, EEA, RU, TW)),
        Device("Xiaomi 14", "houji", "NC", listOf(CN)),
        Device("Xiaomi 14 Pro", "shennong", "NB", listOf(CN)),
        // TODO ↓↓↓
    )
}