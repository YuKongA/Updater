package top.yukonga.update.logic.utils

import android.view.HapticFeedbackConstants
import android.view.View

object HapticUtils {

    fun hapticConfirm(view: View) {
        if (AndroidUtils.atLeastAndroidR()) view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        else view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    fun hapticReject(view: View) {
        if (AndroidUtils.atLeastAndroidR()) view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        else view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

}