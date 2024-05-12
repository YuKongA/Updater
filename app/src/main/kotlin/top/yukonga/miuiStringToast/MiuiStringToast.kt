package top.yukonga.miuiStringToast

import StringToastBundle
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.serialization.json.Json
import top.yukonga.miuiStringToast.data.IconParams
import top.yukonga.miuiStringToast.data.Left
import top.yukonga.miuiStringToast.data.Right
import top.yukonga.miuiStringToast.data.StringToastBean
import top.yukonga.miuiStringToast.data.TextParams
import top.yukonga.update.BuildConfig
import top.yukonga.update.activity.MainActivity
import top.yukonga.update.logic.utils.AppUtils.atLeast
import top.yukonga.update.logic.utils.AppUtils.isLandscape
import top.yukonga.update.logic.utils.XiaomiUtils.isHyperOS
import top.yukonga.update.logic.utils.XiaomiUtils.isMiPad

object MiuiStringToast {

    @SuppressLint("WrongConstant")
    fun showStringToast(context: Context, text: String?, colorType: Int) {
        if ((!isMiPad() && isLandscape()) || !atLeast(Build.VERSION_CODES.TIRAMISU) || !isHyperOS()) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
            return
        }
        try {
            val textParams = TextParams(text, if (colorType == 1) Color.parseColor("#4CAF50") else Color.parseColor("#E53935"))
            val left = Left(textParams = textParams)
            val iconParams = IconParams(Category.DRAWABLE, FileType.SVG, "ic_launcher", 1)
            val right = Right(iconParams = iconParams)
            val stringToastBean = StringToastBean(left, right)
            val jsonStr = Json.encodeToString(StringToastBean.serializer(), stringToastBean)
            val bundle = StringToastBundle.Builder()
                .setPackageName(BuildConfig.APPLICATION_ID)
                .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP_INTENT)
                .setTarget(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
                .setParam(jsonStr)
                .onCreate()
            val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
            service.javaClass.getMethod(
                "setStatus", Int::class.javaPrimitiveType, String::class.java, Bundle::class.java
            ).invoke(service, 1, "strong_toast_action", bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
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

    object StrongToastCategory {
        const val VIDEO_TEXT = "video_text"
        const val VIDEO_BITMAP_INTENT = "video_bitmap_intent"
        const val TEXT_BITMAP = "text_bitmap"
        const val TEXT_BITMAP_INTENT = "text_bitmap_intent"
        const val VIDEO_TEXT_TEXT_VIDEO = "video_text_text_video"
    }

}