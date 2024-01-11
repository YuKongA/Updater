package top.yukonga.update.logic.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
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
        DownloadManager.Request(fileLink.toUri()).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
            setTitle(fileName)
            setDescription(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(this)
            showStringToast(context, ContextCompat.getString(context, R.string.download_start), 1)
        }
    }
}