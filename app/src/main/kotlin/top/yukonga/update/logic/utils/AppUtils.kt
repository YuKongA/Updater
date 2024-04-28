package top.yukonga.update.logic.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources.getSystem
import android.net.Uri
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.serialization.json.Json
import top.yukonga.miuiStringToast.MiuiStringToast.showStringToast
import top.yukonga.update.R
import top.yukonga.update.logic.utils.FileUtils.downloadRomFile
import top.yukonga.update.logic.utils.HapticUtils.hapticConfirm
import top.yukonga.update.logic.utils.HapticUtils.hapticReject

object AppUtils {

    val json = Json { ignoreUnknownKeys = true }

    fun atLeast(version: Int): Boolean = Build.VERSION.SDK_INT >= version

    fun isLandscape(): Boolean = getSystem().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val Int.dp: Int get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), getSystem().displayMetrics).toInt()

    val Int.px: Int get() = (this / getSystem().displayMetrics.density + 0.5f).toInt()

    private fun copyText(context: Context, text: CharSequence?) {
        val cm: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText(context.packageName, text))
    }

    fun hideKeyBoard(context: Context, view: View) {
        if (atLeast(Build.VERSION_CODES.R)) {
            view.windowInsetsController?.hide(WindowInsets.Type.ime())
        } else {
            val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun MaterialButton.setDownloadClickListener(context: Context, fileName: String?, fileLink: String) {
        setOnClickListener {
            fileName?.let {
                hapticConfirm(this)
                MaterialAlertDialogBuilder(context).apply {
                    setTitle(R.string.download_method)
                    setMessage(R.string.download_method_desc)
                    setNegativeButton(R.string.android_default) { _, _ ->
                        hapticConfirm(this@setDownloadClickListener)
                        downloadRomFile(context, fileLink, it)
                    }
                    setPositiveButton(R.string.other) { _, _ ->
                        hapticConfirm(this@setDownloadClickListener)
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse(fileLink)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }.let {
                            context.startActivity(it)
                        }
                    }
                    setNeutralButton(R.string.cancel) { dialog, _ ->
                        hapticReject(this@setDownloadClickListener)
                        dialog.dismiss()
                    }
                }.show()
            }
        }
    }

    fun View.setCopyClickListener(context: Context, text: CharSequence?) {
        setOnClickListener {
            hapticConfirm(this)
            copyText(context, text)
            showStringToast(context, context.getString(R.string.toast_copied_to_pasteboard), 1)
        }
    }

}