import android.app.PendingIntent
import android.os.Bundle

class StringToastBundle private constructor() {

    companion object {
        private var mBundle: Bundle = Bundle()
    }

    class Builder {
        private var packageName: String? = null
        private var stringToastCategory: String? = null
        private var target: PendingIntent? = null
        private var param: String? = null
        private var duration: Long = 2500L
        private var level: Float = 0f
        private var rapidRate: Float = 0f
        private var charge: String? = null
        private var stringToastChargeFlag: Int = 0
        private var statusBarStrongToast: String? = "show_custom_strong_toast"

        fun setPackageName(packageName: String?) = apply { this.packageName = packageName }
        fun setStrongToastCategory(category: String) = apply { stringToastCategory = category }
        fun setTarget(target: PendingIntent?) = apply { this.target = target }
        fun setParam(param: String?) = apply { this.param = param }
        fun setDuration(duration: Long) = apply { this.duration = duration }
        fun setLevel(level: Float) = apply { this.level = level }
        fun setRapidRate(rapidRate: Float) = apply { this.rapidRate = rapidRate }
        fun setCharge(charge: String?) = apply { this.charge = charge }
        fun setStringToastChargeFlag(stringToastChargeFlag: Int) = apply { this.stringToastChargeFlag = stringToastChargeFlag }
        fun setStatusBarStrongToast(statusBarStrongToast: String?) = apply { this.statusBarStrongToast = statusBarStrongToast }

        fun onCreate(): Bundle {
            mBundle.putString("package_name", packageName)
            mBundle.putString("strong_toast_category", stringToastCategory)
            mBundle.putParcelable("target", target)
            mBundle.putString("param", param)
            mBundle.putLong("duration", duration)
            mBundle.putFloat("level", level)
            mBundle.putFloat("rapid_rate", rapidRate)
            mBundle.putString("charge", charge)
            mBundle.putInt("string_toast_charge_flag", stringToastChargeFlag)
            mBundle.putString("status_bar_strong_toast", statusBarStrongToast)
            return mBundle
        }
    }
}