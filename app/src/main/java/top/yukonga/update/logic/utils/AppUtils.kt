package top.yukonga.update.logic.utils

import android.annotation.SuppressLint
import android.content.res.Resources.getSystem
import android.util.TypedValue

object AppUtils {

    val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), getSystem().displayMetrics).toInt()

    val Int.px: Int get() = (this / getSystem().displayMetrics.density + 0.5f).toInt()

    @SuppressLint("PrivateApi")
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getProp(mKey: String): String =
        Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(Class.forName("android.os.SystemProperties"), mKey).toString()

    @SuppressLint("PrivateApi")
    fun getProp(mKey: String, defaultValue: Boolean): Boolean =
        Class.forName("android.os.SystemProperties").getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            .invoke(Class.forName("android.os.SystemProperties"), mKey, defaultValue) as Boolean

    val deviceCodeList = arrayOf(
        "duchamp",
        "manet",
        "vermeer",
        "gale",
        "shennong",
        "houji",
        "sapphiren",
        "sapphire",
        "aristotle",
        "garnet",
        "gold",
        "zircon",
        "xun",
        "babylon",
        "corot",
        "yudi",
        "sky",
        "fire",
        "pearl",
        "yuechu",
        "sea",
        "ishtar",
        "liuqin",
        "pipa",
        "sweet_k6a",
        "topaz",
        "tapas",
        "marble",
        "water",
        "earth",
        "redwood",
        "rembrandt",
        "socrates",
        "mondrian",
        "nuwa",
        "fuxi",
        "moonstone",
        "sunstone",
        "ruby",
        "plato",
        "yunluo",
        "ziyi",
        "ice",
        "rock",
        "dagu",
        "diting",
        "zizhan",
        "mayfly",
        "thor",
        "unicorn",
        "daumier",
        "taoyao",
        "frost",
        "xaga",
        "zijin",
        "lilac",
        "dandelion",
        "fog",
        "rubens",
        "matisse",
        "munch",
        "viva",
        "lightcm",
        "light",
        "ingres",
        "spesn",
        "spes",
        "veux",
        "opal",
        "fleur",
        "zeus",
        "psyche",
        "cupid",
        "evergreen",
        "pissarro",
        "evergo",
        "selenes",
        "mona",
        "lisa",
        "agate",
        "vili",
        "selene",
        "odin",
        "elish",
        "enuma",
        "nabu",
        "chopin",
        "camellia",
        "biloba",
        "ares",
        "cetus",
        "renoir", // TODO ↑↑↑
        "star",
        "courbet",
        "vayu",
        "thyme",
        "sweet",
        "camellian",
        "rosemary",
        "mojito",
        "haydn",
        "alioth",
        "venus",
        "cannong",
        "gauguin",
        "cannon",
        "lime",
        "citrus",
        "apollo",
        "surya",
        "cas",
        "cezanne",
        "gram",
        "lancelot",
        "toco",
        "bomb",
        "atom",
        "merlin",
        "vangogh",
        "monet",
        "lmi",
        "excalibur",
        "curtana",
        "joyeuse",
        "cmi",
        "umi",
        "picasso",
        "phoenixin",
        "phoenix"
    )

    val androidDropDownList = arrayOf("12", "13", "14")

    val regionsDropDownList = arrayOf("CN", "MI", "EU", "RU", "TW", "ID", "TR", "KR", "JP", "IN")
}