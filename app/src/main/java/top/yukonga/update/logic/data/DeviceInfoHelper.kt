package top.yukonga.update.logic.data

object DeviceInfoHelper {

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
    private val JP = DeviceRegions("_jp_global", "JP")
    private val IN = DeviceRegions("_in_global", "IN")

    private const val Xiaomi = "XM"

    private val devices = listOf(
        Device("Redmi K30 4G", "phoenix", "GH", listOf(CN)),
        Device("POCO X2", "phoenixin", "GH", listOf(IN)),
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
        Device("Redmi 10 / 10 Prime / Note 11 4G", "selene", "KU", listOf(CN, GL, EEA, RU, TR, ID, TW, IN)),
        Device("Xiaomi 11T Pro", "vili", "KD", listOf(GL, EEA, RU, TR, ID, TW, IN, JP)),
        Device("Xiaomi 11T", "agate", "KW", listOf(GL, EEA, RU, TR, ID, TW)),
        Device("Xiaomi 11 LE / 11 Lite NE", "lisa", "KO", listOf(CN, GL, EEA, RU, TR, TW, IN)),
        Device("Xiaomi Civi", "mona", "KV", listOf(CN)),
        Device("Redmi Note 11 / Note 11T", "evergo", "GB", listOf(CN, IN)),
        Device("Xiaomi 11i / Redmi Note 11 Pro / Note 11 Pro+", "pissarro", "GC", listOf(CN, RU, TW, TR, EEA, GL, IN)),
        Device("POCO M4 Pro", "evergreen", "GB", listOf(GL, EEA, RU, TR, TW)),
        Device("Xiaomi 12", "cupid", "LC", listOf(CN, GL, EEA, RU, TW, ID, TR)),
        Device("Xiaomi 12X", "psyche", "LD", listOf(CN, GL, EEA, RU, TW)),
        Device("Xiaomi 12 Pro", "zeus", "LB", listOf(CN, GL, IN, EEA, RU, TW, ID, TR)),
        Device("Redmi Note 11S 4G / POCO M4 Pro 4G", "fleur", "KE", listOf(IN, GL, EEA, RU, TW, ID, TR)),
        Device("Redmi Note 11S", "opal", "GL", listOf(EEA, GL, RU, TW)),
        Device("Redmi Note 11E Pro / Note 11 Pro / POCO X4 Pro", "veux", "KC", listOf(CN, JP, EEA, RU, TW, IN, ID, TR)),
        Device("Redmi Note 11", "spes", "GC", listOf(GL, IN, TR)),
        Device("Redmi Note 11 NFC", "spesn", "GK", listOf(GL, EEA, RU, ID)),
        Device("Redmi K50G / POCO F4 GT", "ingres", "LJ", listOf(CN, EEA, RU, TR, ID, TW)),
        Device("Redmi 10 / 11 Prime / Note 11E / POCO M4", "light", "LS", listOf(CN, GL, IN, TR, EEA, RU, ID, TW)),
        Device("Redmi Note 11R", "lightcm", "LS", listOf(CN)),
        Device("Redmi Note 11 Pro 4G", "viva", "GD", listOf(GL, EEA, TW, ID, RU, TR)),
        Device("Redmi K40S / POCO F4", "munch", "LM", listOf(CN, EEA, IN, ID, TW, TR, GL, RU)),
        Device("Redmi K50 Pro", "matisse", "LK", listOf(CN)),
        Device("Redmi K50", "rubens", "LN", listOf(CN)),
        Device("Redmi 10C", "fog", "GE", listOf(GL, ID, EEA, IN, RU, TW, TR)),
        Device("Redmi 9A / 9i / 9AT / 10A", "dandelion", "CD", listOf(CN, GL, EEA, IN, RU, TW, TR, ID)),
        Device("Redmi Note 10T", "lilac", "KN", listOf(JP)),
        Device("Xiaomi Civi 1S", "zijin", "LP", listOf(CN)),
        Device("Redmi K50i / Note 11T Pro / POCO X4 GT", "xaga", "LO", listOf(CN, GL, EEA, RU, TW, IN, TR)),
        Device("POCO C40", "frost", "GF", listOf(GL, EEA, RU, TW, TR)),
        Device("Xiaomi 12 Lite", "taoyao", "LI", listOf(GL, EEA, RU, TW, ID, TR)),
        Device("Xiaomi 12 Pro Dimensity", "daumier", "LG", listOf(CN)),
        Device("Xiaomi 12S Pro", "unicorn", "LE", listOf(CN)),
        Device("Xiaomi 12S", "mayfly", "LT", listOf(CN)),
        Device("Xiaomi 12S Ultra", "thor", "LA", listOf(CN)),
        Device("Xiaomi MIX Fold 2", "zizhan", "LR", listOf(CN)),
        Device("Xiaomi 12T Pro / Redmi K50 Ultra", "diting", "LF", listOf(CN, GL, EEA, JP, TR, RU, TW)),
        Device("Xiaomi Pad 5 Pro 12.4", "dagu", "LZ", listOf(CN)),
        Device("Redmi 11 Prime 4G / POCO M5", "rock", "LU", listOf(GL, EEA, IN, TW, TR, ID, RU)),
        Device("Redmi A1 / POCO C50", "ice", "GM", listOf(EEA, GL, IN, ID, RU, TW)),
        Device("Xiaomi 13 Lite / Civi 2 ", "ziyi", "LL", listOf(CN, GL, EEA, RU, TW, TR)),
        Device("Redmi Pad", "yunluo", "LY", listOf(CN, GL, IN, RU, TW, TR, EEA, ID)),
        Device("Xiaomi 12T", "plato", "LQ", listOf(GL, ID, RU, TW, TR, EEA)),
        Device("Redmi Note 12 Pro", "ruby", "MO", listOf(CN, GL, ID, RU, TW, IN, EEA)),
        Device("Redmi Note 12 / Note 12R Pro", "sunstone", "MQ", listOf(CN, GL, TW, IN, EEA)),
        Device("POCO X5", "moonstone", "MP", listOf(GL, TW, IN, ID, EEA, RU, TR)),
        Device("Xiaomi 13", "fuxi", "MC", listOf(CN, GL, TW, EEA, RU, TR)),
        Device("Xiaomi 13 Pro", "nuwa", "MB", listOf(CN, GL, TW, IN, EEA, RU, TR)),
        Device("Redmi K60 / POCO F5 Pro", "mondrian", "MN", listOf(CN, GL, TW, EEA, RU, TR)),
        Device("Redmi K60 Pro", "socrates", "MK", listOf(CN)),
        Device("Redmi K60E", "rembrandt", "MM", listOf(CN)),
        Device("Redmi Note 12 Pro Speed / POCO X5 Pro", "redwood", "MS", listOf(CN, EEA, ID, TW, TR, GL, IN, RU)),
        Device("Redmi 12C / POCO C55", "earth", "CV", listOf(CN, GL, TW, IN, EEA, ID, RU, TR)),
        Device("Redmi A2 / POCO C51", "water", "GO", listOf(GL, TW, EEA, IN, ID, RU)),
        Device("Redmi Note 12 Turbo / POCO F5", "marble", "MR", listOf(CN, GL, TW, EEA, IN, ID, RU, TR)),
        Device("Redmi Note 12 4G", "tapas", "MT", listOf(GL, IN, TR)),
        Device("Redmi Note 12 4G NFC", "topaz", "MG", listOf(GL, EEA, ID, RU)),
        Device("Xiaomi Pad 6", "pipa", "MZ", listOf(CN, GL, TW, EEA, IN, ID, RU, TR)),
        Device("Xiaomi Pad 6 Pro", "liuqin", "MY", listOf(CN)),
        Device("Xiaomi 13 Ultra", "ishtar", "MA", listOf(CN, GL, EEA, RU, TW)),
        Device("Redmi Note 12S", "sea", "HZ", listOf(TR, GL, EEA, RU, TW)),
        Device("Xiaomi Civi 3", "yuechu", "MI", listOf(CN)),
        Device("Redmi Note 12T Pro", "pearl", "LH", listOf(CN)),
        Device("Redmi 12", "fire", "MX", listOf(GL, EEA, IN, ID, RU, TR)),
        Device("Redmi Note 12 / Note 12R / POCO M6 Pro", "sky", "MW", listOf(CN, EEA, GL, IN, TW)),
        Device("Xiaomi Pad 6 Max 14", "yudi", "MH", listOf(CN)),
        Device("Xiaomi 13T Pro / Redmi K60 Ultra", "corot", "ML", listOf(CN, EEA, GL, RU, TW, TR)),
        Device("Xiaomi MIX Fold 3", "babylon", "MV", listOf(CN)),
        Device("Redmi Pad SE", "xun", "MU", listOf(CN, EEA, GL, ID, RU, TW, TR)),
        Device("Redmi Note 13 Pro+", "zircon", "NO", listOf(CN)),
        Device("Redmi Note 13 / 13R Pro", "gold", "NQ", listOf(CN)),
        Device("Redmi Note 13 Pro", "garnet", "NR", listOf(CN)),
        Device("Xiaomi 13T", "aristotle", "MF", listOf(GL, EEA, ID, RU, TW, TR)),
        Device("Xiaomi 14", "houji", "NC", listOf(CN)),
        Device("Xiaomi 14 Pro", "shennong", "NB", listOf(CN)),
        Device("Redmi 13C / POCO C65", "gale", "GP", listOf(GL, EEA, IN, TW)),
        Device("Redmi K70 / POCO F6 Pro", "vermeer", "NK", listOf(CN)),
        Device("Redmi K70 Pro", "manet", "NM", listOf(CN)),
        Device("Redmi K70E / POCO X6 Pro", "duchamp", "NL", listOf(CN)),
    )

    val deviceNames = devices.map { it.deviceName }

    val codeNames = devices.map { it.codeName }


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

    fun existRegions(codeName: String): List<String> {
        val device = devices.find { it.codeName == codeName } ?: return emptyList()
        return device.regions.map { it.version }
    }

    fun deviceCode(android: String, codeName: String, variant: String): String {
        val newAndroid = androids.find { it.version == android } ?: return ""
        val device = devices.find { it.codeName == codeName } ?: return ""
        return "${newAndroid.android}${device.deviceCode}${variant}${Xiaomi}"
    }
}