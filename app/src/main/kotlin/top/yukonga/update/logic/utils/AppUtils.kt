package top.yukonga.update.logic.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
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

    fun View.hapticConfirm() {
        performHapticFeedback(if (atLeastAndroidR()) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun View.hapticReject() {
        performHapticFeedback(if (atLeastAndroidR()) HapticFeedbackConstants.REJECT else HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun atLeastAndroidP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    fun atLeastAndroidQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun atLeastAndroidR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun atLeastAndroidT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun isXiaomi(): Boolean = getProp("ro.miui.ui.version.code").isNotEmpty() && getProp("ro.miui.ui.version.name").isNotEmpty()

    fun isHyperOS(): Boolean = isXiaomi() && getProp("ro.miui.ui.version.code").toInt() >= 816

    fun isTablet(): Boolean =
        getSystem().configuration.smallestScreenWidthDp >= 600 || getProp("ro.build.characteristics") == "tablet" || (getSystem().configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

    fun isLandscape(): Boolean = getSystem().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), getSystem().displayMetrics).toInt()

    val Int.px: Int get() = (this / getSystem().displayMetrics.density + 0.5f).toInt()

    fun getProp(name: String): String {
        var prop = getPropByShell(name)
        if (prop.isNotEmpty()) return prop
        prop = getPropByStream(name)
        if (prop.isNotEmpty()) return prop
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) getPropByReflect(name) else prop
    }

    private fun getPropByShell(propName: String): String {
        var input: BufferedReader? = null
        return try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            input.readLine() ?: ""
        } catch (ignore: IOException) {
            ""
        } finally {
            input?.close()
        }
    }

    private fun getPropByStream(key: String): String {
        return try {
            val prop = Properties()
            val iS = FileInputStream(File(Environment.getRootDirectory(), "build.prop"))
            prop.load(iS)
            prop.getProperty(key, "")
        } catch (_: Exception) {
            ""
        }
    }

    @SuppressLint("PrivateApi")
    private fun getPropByReflect(key: String): String {
        return try {
            val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            getMethod.invoke(clz, key, "") as String
        } catch (_: Exception) {
            ""
        }
    }
}
