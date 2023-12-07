package top.yukonga.update.logic.utils

import android.content.res.Resources.getSystem

object AppUtils {

    val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

    val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()

}