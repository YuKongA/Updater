package top.yukonga.update.logic.utils

import android.content.Context
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
}