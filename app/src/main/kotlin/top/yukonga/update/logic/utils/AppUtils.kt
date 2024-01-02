package top.yukonga.update.logic.utils

import android.annotation.SuppressLint
import android.content.res.Resources.getSystem
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

object AppUtils {

    fun hapticConfirm(view: View) {
        if (atLeastAndroidR()) view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        else view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun hapticReject(view: View) {
        if (atLeastAndroidR()) view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        else view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun atLeastAndroidR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun atLeastAndroidT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isXiaomi(): Boolean = getProp("ro.miui.ui.version.code").isNotEmpty() && getProp("ro.miui.ui.version.name").isNotEmpty()

    fun isHyperOS(): Boolean = if (isXiaomi()) getProp("ro.miui.ui.version.code").toInt() >= 816 else false

    val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), getSystem().displayMetrics).toInt()

    val Int.px: Int get() = (this / getSystem().displayMetrics.density + 0.5f).toInt()

    fun getProp(name: String): String {
        var prop = getPropByShell(name)
        if (!TextUtils.isEmpty(prop)) return prop
        prop = getPropByStream(name)
        if (!TextUtils.isEmpty(prop)) return prop
        if (Build.VERSION.SDK_INT < 28) return getPropByReflect(name)
        return prop
    }

    private fun getPropByShell(propName: String): String {
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            val ret = input.readLine()
            if (ret != null) return ret
        } catch (ignore: IOException) {
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (_: IOException) {
                }
            }
        }
        return ""
    }

    private fun getPropByStream(key: String): String {
        try {
            val prop = Properties()
            val iS = FileInputStream(
                File(Environment.getRootDirectory(), "build.prop")
            )
            prop.load(iS)
            return prop.getProperty(key, "")
        } catch (_: Exception) {
        }
        return ""
    }

    private fun getPropByReflect(key: String): String {
        try {
            @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (_: Exception) {
        }
        return ""
    }
}