package top.yukonga.update.logic.utils

object XiaomiUtils {

    private fun isXiaomi(): Boolean = PropUtils.getProp("ro.build.fingerprint").contains("Xiaomi")

    private fun isRedmi(): Boolean = PropUtils.getProp("ro.build.fingerprint").contains("Redmi")

    private fun isPOCO(): Boolean = PropUtils.getProp("ro.build.fingerprint").contains("POCO")

    fun isXiaomiFamily(): Boolean = isXiaomi() || isRedmi() || isPOCO()

    fun isRunningMiui(): Boolean = isXiaomiFamily() && PropUtils.getProp("persist.miui.density_v2").isNotEmpty() && PropUtils.getProp("ro.miui.ui.version.code").isNotEmpty()

    fun isMiui(): Boolean = if (isRunningMiui()) PropUtils.getProp("ro.miui.ui.version.code").toInt() <= 140 else false

    fun isHyperOS(): Boolean = if (isRunningMiui()) PropUtils.getProp("ro.miui.ui.version.code").toInt() >= 816 else false

    fun isMiPad(): Boolean = if (isRunningMiui()) Class.forName("miui.os.Build").getDeclaredField("IS_TABLET").get(null) as Boolean else false

}