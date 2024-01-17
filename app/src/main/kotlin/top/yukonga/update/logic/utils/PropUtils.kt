package top.yukonga.update.logic.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

object PropUtils {

    fun getProp(name: String): String {
        var prop = getPropByShell(name)
        if (!TextUtils.isEmpty(prop)) return prop
        prop = getPropByStream(name)
        if (!TextUtils.isEmpty(prop)) return prop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return getPropByReflect(name)
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