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
}