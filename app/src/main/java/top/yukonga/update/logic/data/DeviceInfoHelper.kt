package top.yukonga.update.logic.data

object DeviceInfoHelper {

    fun codeName(deviceName: String): String? {
        val device = devices.find { it.deviceName == deviceName } ?: return null
        return device.codeName
    }

    fun deviceName(codeName: String): String? {
        val device = devices.find { it.codeName == codeName } ?: return null
        return device.deviceName
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

    data class Device(val deviceName: String, val codeName: String, val deviceCode: String, val regions: List<DeviceRegions>)

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
        Device("Redmi K30 / K30i", "picasso", "GI", listOf(CN)),
        Device("Xiaomi 10", "umi", "JB", listOf(CN, GL, EEA, RU, ID, TR, IN)),
        Device("Xiaomi 10 Pro", "cmi", "JA", listOf(CN, GL, EEA)),
        Device("Redmi Note 9 Pro", "joyeuse", "JZ", listOf(GL, EEA, RU, TR, ID, TW)),
        Device("Redmi Note 9 Pro (India) / Note 9S / Note 10 Lite", "curtana", "JW", listOf(GL, EEA, RU, TR, IN)),
        Device("Redmi Note 9 Pro Max", "excalibur", "JX", listOf(IN)),
        Device("Redmi K30 Pro", "lmi", "JK", listOf(CN, GL, EEA, RU, ID, TR)),
        Device("Xiaomi 10 Lite", "monet", "JI", listOf(GL, TW, EEA)),
        Device("Xiaomi 10 Lite (China)", "vangogh", "JV", listOf(CN)),
        Device("Redmi Note 9 / 10X 4G", "merlin", "JO", listOf(CN, GL, TW, EEA, RU, ID, TR, IN)),
        Device("Redmi 10X", "atom", "JH", listOf(CN)),
        Device("Redmi 10X Pro", "bomb", "JL", listOf(CN)),
        Device("Xiaomi Note 10 Lite ", "toco", "FN", listOf(GL, EEA, RU, TR)),
        Device("Redmi 9 / 9 Prime", "lancelot", "JC", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("POCO M2 Pro", "gram", "JP", listOf(IN)),
        Device("Redmi K30 Ultra", "cezanne", "JN", listOf(CN)),
        Device("Xiaomi 10 Ultra", "cas", "JJ", listOf(CN)),
        Device("POCO X3 NFC", "surya", "JG", listOf(GL, EEA, RU, TR, ID, IN)),
        Device("Xiaomi 10T / 10T Pro / Redmi K30S Ultra", "apollo", "JD", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("POCO M3", "citrus", "JF", listOf(GL, EEA, RU, TR, ID, TW, IN)),
        Device("Redmi 9T / 9 Power / Note 9 4G", "lime", "JQ", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("Redmi Note 9", "cannon", "JE", listOf(CN)),
        Device("Xiaomi 10T Lite / 10i / Redmi Note 9 Pro", "gauguin", "JS", listOf(CN, GL, EEA, TR, TW, IN)),
        Device("Redmi Note 9T", "cannong", "JE", listOf(RU, GL, TW, TR, EEA)),
        Device("Xiaomi 11", "venus", "KB", listOf(CN, GL, EEA, RU, TR, ID, TW)),
        Device("Redmi K40 / POCO F3", "alioth", "KH", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("Xiaomi 11i / Redmi K40 Pro / Pro+", "haydn", "KK", listOf(CN, GL, EEA, IN)),
        Device("Redmi Note 10", "mojito", "KG", listOf(GL, ID, RU, IN, TR, EEA)),
        Device("Redmi Note 10S / POCO M5s", "rosemary", "KL", listOf(GL, ID, RU, TR, EEA, TW)),
        Device("Redmi Note 10 (Global) / Note 10T / POCO M3 Pro", "camellian", "KS", listOf(GL, ID, RU, TR, EEA, TW)),
        Device("Redmi Note 10 Pro", "sweet", "KF", listOf(GL, ID, RU, TR, EEA, TW, ID)),
        Device("Redmi Note 12 Pro 4G", "sweet", "HG", listOf(GL, ID, RU, TR, EEA, TW, ID)),
        Device("Redmi Note 10 Pro (India) / Pro Max", "sweetin", "KF", listOf(IN)),
        Device("Xiaomi 10S", "thyme", "GA", listOf(CN)),
        Device("POCO X3 Pro", "vayu", "JU", listOf(GL, EEA, RU, TR, ID, TW)),
        Device("Xiaomi 11 Lite 4G", "courbet", "KQ", listOf(GL, RU, EEA, IN, ID)),
        Device("Xiaomi 11 Ultra / Pro", "star", "KA", listOf(CN, IN, EEA, GL, ID)),
        Device("Xiaomi 11 Lite", "renoir", "KI", listOf(CN, EEA, GL, RU, JP, TW)),
        Device("Xiaomi MIX Fold", "cetus", "JT", listOf(CN)),
        Device("Redmi K40 Gaming / POCO F3 GT", "ares", "KJ", listOf(CN, IN)),
        Device("Redmi Note 8 (2021)", "biloba", "CU", listOf(EEA, RU)),
        Device("Redmi Note 10 / Note 11SE / Note 10T / POCO M3 Pro", "camellia", "KS", listOf(CN, IN)),
        Device("Redmi Note 10 Pro (China) / POCO X3 GT", "chopin", "KP", listOf(CN, ID, TR, GL)),
        Device("Xiaomi Pad 5", "nabu", "KX", listOf(CN, IN, TW, TR, EEA, RU, GL)),
        Device("Xiaomi Pad 5 Pro WiFi", "elish", "KY", listOf(CN)),
        Device("Xiaomi Pad 5 Pro", "enuma", "KZ", listOf(CN)),
        Device("Xiaomi MIX 4", "odin", "KM", listOf(CN)),
        Device("Redmi 10 / 10 Prime/ Note 11 4G", "selene", "KU", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("Xiaomi 11T Pro", "vili", "KD", listOf(GL, EEA, RU, TR, ID, TW, IN, JP)),
        Device("Xiaomi 11T", "agate", "KW", listOf(GL, EEA, RU, TR, ID, TW)),
        // TODO ↓↓↓

        Device("Xiaomi 13 Ultra", "ishtar", "MA", listOf(CN, GL, EEA, RU, TW)),
        Device("Xiaomi 14", "houji", "NC", listOf(CN)),
        Device("Xiaomi 14 Pro", "shennong", "NB", listOf(CN)),
        // TODO ↓↓↓
    )

    val deviceNames = devices.map { it.deviceName }
}