package top.yukonga.update.logic.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import java.io.File
import java.io.FileNotFoundException

object FileUtils {

    fun saveFile(context: Context, fileName: String, data: String) {
        val file = File(context.filesDir, fileName)
        file.writeText(data)
    }

    fun readFile(context: Context, fileName: String): String {
        return try {
            val file = File(context.filesDir, fileName)
            file.readText()
        } catch (e: FileNotFoundException) {
            ""
        }
    }

    fun downloadFile(context: Context, fileLink: String, fileName: String) {
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
    }
}