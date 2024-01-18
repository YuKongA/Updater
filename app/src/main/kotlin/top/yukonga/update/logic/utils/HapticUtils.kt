package top.yukonga.update.logic.utils

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View

object HapticUtils {

    private fun performHapticFeedback(view: View, feedbackConstant: Int) {
        if (AppUtils.atLeast(Build.VERSION_CODES.R)) view.performHapticFeedback(feedbackConstant) else view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun hapticConfirm(view: View) {
        performHapticFeedback(view, HapticFeedbackConstants.CONFIRM)
    }

    fun hapticReject(view: View) {
        performHapticFeedback(view, HapticFeedbackConstants.REJECT)
    }

}