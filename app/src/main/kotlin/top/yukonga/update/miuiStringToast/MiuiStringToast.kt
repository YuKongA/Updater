package top.yukonga.update.miuiStringToast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import kotlinx.serialization.json.Json
import top.yukonga.update.BuildConfig
import top.yukonga.update.activity.MainActivity
import top.yukonga.update.logic.utils.AppUtils.atLeast
import top.yukonga.update.logic.utils.AppUtils.isLandscape
import top.yukonga.update.logic.utils.XiaomiUtils.isHyperOS
import top.yukonga.update.logic.utils.XiaomiUtils.isMiPad
import top.yukonga.update.miuiStringToast.data.IconParams
import top.yukonga.update.miuiStringToast.data.Left
import top.yukonga.update.miuiStringToast.data.Right
import top.yukonga.update.miuiStringToast.data.StringToastBean
import top.yukonga.update.miuiStringToast.data.TextParams

object MiuiStringToast {

    fun newIconParams(
        category: String?,
        iconResName: String?,
        iconType: Int,
        iconFormat: String?
    ): IconParams {
        return IconParams(category, iconFormat, iconResName, iconType)
    }

    @SuppressLint("WrongConstant")
    fun showStringToast(
        context: Context,
        text: String?,
        colorType: Int?
    ) {
        if ((!isMiPad() && isLandscape()) || !atLeast(Build.VERSION_CODES.TIRAMISU) || !isHyperOS()) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val textParams = TextParams(text, if (colorType == 1) colorToInt("#4CAF50") else colorToInt("#E53935"))
            val left = Left(textParams = textParams)
            val iconParams: IconParams = newIconParams(Category.DRAWABLE, if (colorType == 1) "ic_update_toast" else "ic_update_toast_error", 1, FileType.SVG)
            val right = Right(iconParams = iconParams)
            val stringToastBean = StringToastBean(left, right)
            val jsonStr = Json.encodeToString(StringToastBean.serializer(), stringToastBean)
            val bundle = StringToastBundle.Builder()
                .setPackageName(BuildConfig.APPLICATION_ID)
                .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP_INTENT.value)
                .setTarget(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
                .setDuration(2500L)
                .setLevel(0.0f)
                .setRapidRate(0.0f)
                .setCharge(null as String?)
                .setStringToastChargeFlag(0)
                .setParam(jsonStr)
                .setStatusBarStrongToast("show_custom_strong_toast")
                .onCreate()

            val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
            service.javaClass.getMethod(
                "setStatus", Int::class.javaPrimitiveType, String::class.java, Bundle::class.java
            ).invoke(service, 1, "strong_toast_action", bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun colorToInt(color: String?): Int {
        val color1 = Color.parseColor(color)
        val color2 = Color.parseColor("#FFFFFF")
        val color3 = color1 xor color2
        return color3.inv()
    }

    object Category {
        const val RAW = "raw"
        const val DRAWABLE = "drawable"
        const val FILE = "file"
        const val MIPMAP = "mipmap"
    }

    object FileType {
        const val MP4 = "mp4"
        const val PNG = "png"
        const val SVG = "svg"
    }

    enum class StrongToastCategory(var value: String) {
        VIDEO_TEXT("video_text"),
        VIDEO_BITMAP_INTENT("video_bitmap_intent"),
        TEXT_BITMAP("text_bitmap"),
        TEXT_BITMAP_INTENT("text_bitmap_intent"),
        VIDEO_TEXT_TEXT_VIDEO("video_text_text_video")
    }

}