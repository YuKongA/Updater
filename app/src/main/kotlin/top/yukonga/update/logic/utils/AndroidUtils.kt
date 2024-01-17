package top.yukonga.update.logic.utils

import android.os.Build

object AndroidUtils {

    fun atLeastAndroidP(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    fun atLeastAndroidQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun atLeastAndroidR(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    fun atLeastAndroidT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

}