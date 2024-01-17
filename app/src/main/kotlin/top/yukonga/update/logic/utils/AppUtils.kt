package top.yukonga.update.logic.utils

import android.content.res.Configuration
import android.content.res.Resources.getSystem
import android.util.TypedValue

object AppUtils {
    fun isLandscape(): Boolean = getSystem().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), getSystem().displayMetrics).toInt()

    val Int.px: Int get() = (this / getSystem().displayMetrics.density + 0.5f).toInt()

}