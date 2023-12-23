package top.yukonga.update.logic.utils.miuiStringToast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import top.yukonga.update.BuildConfig
import top.yukonga.update.logic.utils.AppUtils.getProp
import top.yukonga.update.logic.utils.miuiStringToast.res.IconParams
import top.yukonga.update.logic.utils.miuiStringToast.res.Left
import top.yukonga.update.logic.utils.miuiStringToast.res.Right
import top.yukonga.update.logic.utils.miuiStringToast.res.StringToastBean
import top.yukonga.update.logic.utils.miuiStringToast.res.StringToastBundle
import top.yukonga.update.logic.utils.miuiStringToast.res.TextParams
import java.lang.reflect.InvocationTargetException

object MiuiStringToast {
    fun newIconParams(category: String?, iconResName: String?, iconType: Int, iconFormat: String?): IconParams {
        val params = IconParams()
        params.setCategory(category)
        params.setIconResName(iconResName)
        params.setIconType(iconType)
        params.setIconFormat(iconFormat)
        return params
    }

    @SuppressLint("WrongConstant")
    fun showStringToast(context: Context, text: String?, colorType: Int?) {
        try {
            val textParams = TextParams()
            textParams.setText(text)
            textParams.setTextColor(if (colorType == 1) colorToInt("#4CAF50") else colorToInt("#E53935"))
            val left = Left()
            left.setTextParams(textParams)
            val iconParams: IconParams = newIconParams(Category.DRAWABLE, if (colorType == 1) "ic_update_toast" else "ic_update_toast_error", 1, FileType.SVG)
            val right = Right()
            right.setIconParams(iconParams)
            val stringToastBean = StringToastBean()
            stringToastBean.setLeft(left)
            stringToastBean.setRight(right)
            val gson = Gson()
            val str = gson.toJson(stringToastBean)
            val bundle: Bundle = StringToastBundle.Builder()
                .setPackageName(BuildConfig.APPLICATION_ID)
                .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP.value)
                .setTarget(null as PendingIntent?)
                .setDuration(2500L)
                .setLevel(0.0f)
                .setRapidRate(0.0f)
                .setCharge(null as String?)
                .setStringToastChargeFlag(0)
                .setParam(str)
                .setStatusBarStrongToast("show_custom_strong_toast")
                .onCreate()
            val miCode = if (getProp("ro.miui.ui.version.code") == "") 0 else getProp("ro.miui.ui.version.code").toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && miCode >= 816) {
                val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
                service.javaClass.getMethod("setStatus", Int::class.javaPrimitiveType, String::class.java, Bundle::class.java)
                    .invoke(service, 1, "strong_toast_action", bundle)
            } else {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
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