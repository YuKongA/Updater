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
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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

    fun View.addInsetsByPadding(
        top: Boolean = false,
        bottom: Boolean = false,
        left: Boolean = false,
        right: Boolean = false
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val inset = Insets.max(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()),
                insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            )
            if (top) {
                val lastTopPadding = view.getTag(R.id.view_add_insets_padding_top_tag) as? Int ?: 0
                val newTopPadding = inset.top
                view.setTag(R.id.view_add_insets_padding_top_tag, newTopPadding)
                view.updatePadding(top = view.paddingTop - lastTopPadding + newTopPadding)
            }
            if (bottom) {
                val lastBottomPadding =
                    view.getTag(R.id.view_add_insets_padding_bottom_tag) as? Int ?: 0
                val newBottomPadding = inset.bottom
                view.setTag(R.id.view_add_insets_padding_bottom_tag, newBottomPadding)
                view.updatePadding(bottom = view.paddingBottom - lastBottomPadding + newBottomPadding)
            }
            if (left) {
                val lastLeftPadding = view.getTag(R.id.view_add_insets_padding_left_tag) as? Int ?: 0
                val newLeftPadding = inset.left
                view.setTag(R.id.view_add_insets_padding_left_tag, newLeftPadding)
                view.updatePadding(left = view.paddingLeft - lastLeftPadding + newLeftPadding)
            }
            if (right) {
                val lastRightPadding = view.getTag(R.id.view_add_insets_padding_right_tag) as? Int ?: 0
                val newRightPadding = inset.right
                view.setTag(R.id.view_add_insets_padding_right_tag, newRightPadding)
                view.updatePadding(right = view.paddingRight - lastRightPadding + newRightPadding)
            }
            return@setOnApplyWindowInsetsListener insets
        }
    }

    fun View.addInsetsByMargin(
        top: Boolean = false,
        bottom: Boolean = false,
        left: Boolean = false,
        right: Boolean = false
    ) {
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val inset = Insets.max(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()),
                insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            )
            if (top) {
                val lastTopMargin = view.getTag(R.id.view_add_insets_margin_top_tag) as? Int ?: 0
                val newTopMargin = inset.top
                view.setTag(R.id.view_add_insets_margin_top_tag, newTopMargin)
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
                    layoutParams.topMargin = layoutParams.topMargin - lastTopMargin + newTopMargin
                    view.layoutParams = layoutParams
                }
            }
            if (bottom) {
                val lastBottomMargin = view.getTag(R.id.view_add_insets_margin_bottom_tag) as? Int ?: 0
                val newBottomMargin = inset.bottom
                view.setTag(R.id.view_add_insets_margin_bottom_tag, newBottomMargin)
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
                    layoutParams.bottomMargin = layoutParams.bottomMargin - lastBottomMargin + newBottomMargin
                    view.layoutParams = layoutParams
                }
            }
            if (left) {
                val lastLeftMargin = view.getTag(R.id.view_add_insets_margin_left_tag) as? Int ?: 0
                val newLeftMargin = inset.left
                view.setTag(R.id.view_add_insets_margin_left_tag, newLeftMargin)
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
                    layoutParams.leftMargin = layoutParams.leftMargin - lastLeftMargin + newLeftMargin
                    view.layoutParams = layoutParams
                }
            }
            if (right) {
                val lastRightMargin = view.getTag(R.id.view_add_insets_margin_right_tag) as? Int ?: 0
                val newRightMargin = inset.right
                view.setTag(R.id.view_add_insets_margin_right_tag, newRightMargin)
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { layoutParams ->
                    layoutParams.rightMargin = layoutParams.rightMargin - lastRightMargin + newRightMargin
                    view.layoutParams = layoutParams
                }
            }
            return@setOnApplyWindowInsetsListener insets
        }
    }
}