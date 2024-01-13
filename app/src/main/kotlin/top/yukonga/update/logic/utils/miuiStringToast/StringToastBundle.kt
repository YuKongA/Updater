package top.yukonga.update.logic.utils.miuiStringToast

import android.app.PendingIntent
import android.os.Bundle
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
class StringToastBundle {
    companion object {
        private var mBundle: Bundle = Bundle()
    }

    @Serializable
    class Builder {
        private var packageName: String? = null
        private var stringToastCategory: String? = null

        @Contextual
        private var target: PendingIntent? = null
        private var duration: Long = 0
        private var level: Float = 0f
        private var rapidRate: Float = 0f
        private var charge: String? = null
        private var stringToastChargeFlag: Int = 0
        private var param: String? = null
        private var statusBarStrongToast: String? = null

        fun setPackageName(name: String?): Builder {
            packageName = name
            return this
        }

        fun setStrongToastCategory(category: String?): Builder {
            this.stringToastCategory = category
            return this
        }

        fun setTarget(intent: PendingIntent?): Builder {
            target = intent
            return this
        }

        fun setDuration(duration: Long): Builder {
            this.duration = duration
            return this
        }

        fun setLevel(level: Float): Builder {
            this.level = level
            return this
        }

        fun setRapidRate(rate: Float): Builder {
            this.rapidRate = rate
            return this
        }

        fun setCharge(charge: String?): Builder {
            this.charge = charge
            return this
        }

        fun setStringToastChargeFlag(flag: Int): Builder {
            this.stringToastChargeFlag = flag
            return this
        }

        fun setParam(param: String?): Builder {
            this.param = param
            return this
        }

        fun setStatusBarStrongToast(status: String?): Builder {
            this.statusBarStrongToast = status
            return this
        }

        fun onCreate(): Bundle {
            mBundle.putString("package_name", packageName)
            mBundle.putString("strong_toast_category", stringToastCategory)
            mBundle.putParcelable("target", target)
            mBundle.putLong("duration", duration)
            mBundle.putFloat("level", level)
            mBundle.putFloat("rapid_rate", rapidRate)
            mBundle.putString("charge", charge)
            mBundle.putInt("string_toast_charge_flag", stringToastChargeFlag)
            mBundle.putString("param", param)
            mBundle.putString("status_bar_strong_toast", statusBarStrongToast)
            return mBundle
        }
    }
}