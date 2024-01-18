package top.yukonga.update.logic.utils

object XiaomiUtils {

    fun isDeviceType(vararg types: String): Boolean {
        val fingerprint = PropUtils.getProp("ro.build.fingerprint")
        return types.any { fingerprint.contains(it) }
    }

    fun isXiaomiFamily(): Boolean = isDeviceType("Xiaomi", "Redmi", "POCO")

    fun isRunningMiui(): Boolean {
        val list = listOf("persist.miui.density_v2", "ro.miui.ui.version.code", "ro.miui.ui.version.name")
        return isXiaomiFamily() && PropUtils.getProps(list).all { it.value.isNotEmpty() }
    }

    fun isMiui(): Boolean = if (isRunningMiui()) PropUtils.getProp("ro.miui.ui.version.code").toInt() <= 140 else false

    fun isHyperOS(): Boolean = if (isRunningMiui()) PropUtils.getProp("ro.miui.ui.version.code").toInt() >= 816 else false

    fun isMiPad(): Boolean = if (isRunningMiui()) Class.forName("miui.os.Build").getDeclaredField("IS_TABLET").get(null) as Boolean else false

}