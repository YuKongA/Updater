package top.yukonga.update.logic.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Properties

object PropUtils {

    fun getProps(names: List<String>): Map<String, String> {
        return names.associateWith { getProp(it) }
    }

    fun getProp(name: String): String {
        var prop = getPropByShell(name)
        if (prop.isEmpty()) prop = getPropByStream(name)
        if (prop.isEmpty() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) prop = getPropByReflect(name)
        return prop
    }

    private fun getPropByShell(propName: String): String {
        return try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            BufferedReader(InputStreamReader(p.inputStream), 1024).use { it.readLine() ?: "" }
        } catch (ignore: IOException) {
            ""
        }
    }

    private fun getPropByStream(key: String): String {
        return try {
            val prop = Properties()
            FileInputStream(File(Environment.getRootDirectory(), "build.prop")).use { prop.load(it) }
            prop.getProperty(key, "")
        } catch (_: Exception) {
            ""
        }
    }

    private fun getPropByReflect(key: String): String {
        return try {
            @SuppressLint("PrivateApi") val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get", String::class.java, String::class.java)
            getMethod.invoke(clz, key, "") as String
        } catch (_: Exception) {
            ""
        }
    }

}