package top.yukonga.update.logic.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import top.yukonga.update.R
import top.yukonga.update.miuiStringToast.MiuiStringToast.showStringToast
import java.io.File

object FileUtils {

    private const val COOKIES_FILE_NAME = "cookies.json"
    private lateinit var cookiesFile: File

    private fun getCookiesFile(context: Context): File {
        if (!::cookiesFile.isInitialized) {
            cookiesFile = File(context.filesDir, COOKIES_FILE_NAME)
        }
        return cookiesFile
    }

    fun saveCookiesFile(context: Context, data: String) {
        getCookiesFile(context).writeText(data)
    }

    fun readCookiesFile(context: Context): String {
        return getCookiesFile(context).readText()
    }

    fun deleteCookiesFile(context: Context) {
        getCookiesFile(context).delete()
    }

    fun isCookiesFileExists(context: Context): Boolean {
        return getCookiesFile(context).exists()
    }

    fun downloadRomFile(context: Context, fileLink: String, fileName: String) {
        val request = DownloadManager.Request(fileLink.toUri()).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
            setTitle(fileName)
            setDescription(fileName)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        showStringToast(context, context.getString(R.string.download_start), 1)
    }

}