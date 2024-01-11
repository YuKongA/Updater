package top.yukonga.update.logic.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat.getString
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import top.yukonga.update.R
import top.yukonga.update.logic.utils.miuiStringToast.MiuiStringToast.showStringToast
import java.io.File

object FileUtils {
    private fun cookiesFile(context: Context): File {
        return File(context.filesDir, "cookies.json")
    }

    fun saveCookiesFile(context: Context, data: String) {
        cookiesFile(context).writeText(data)
    }

    fun readCookiesFile(context: Context): String {
        return cookiesFile(context).readText()
    }

    fun deleteCookiesFile(context: Context) {
        cookiesFile(context).delete()
    }

    fun isCookiesFileExists(context: Context): Boolean {
        return cookiesFile(context).exists()
    }

    fun downloadRomFile(context: Context, fileLink: String, fileName: String) {
        MaterialAlertDialogBuilder(context).apply {
            setTitle(R.string.download_method)
            setMessage(R.string.download_method_desc)
            setNegativeButton(R.string.android_default) { _, _ ->
                DownloadManager.Request(fileLink.toUri()).apply {
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    setAllowedOverRoaming(false)
                    setTitle(fileName)
                    setDescription(fileName)
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(this)
                }
                showStringToast(
                    context,
                    getString(context, R.string.download_start),
                    1
                )
            }
            setPositiveButton(R.string.other) { _, _ ->
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(fileLink)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }.let {
                    context.startActivity(it)
                }
            }
            setNeutralButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }
}