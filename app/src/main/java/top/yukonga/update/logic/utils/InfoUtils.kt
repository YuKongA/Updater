package top.yukonga.update.logic.utils

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import top.yukonga.update.logic.utils.CryptoUtils.miuiDecrypt
import top.yukonga.update.logic.utils.CryptoUtils.miuiEncrypt
import java.io.File
import java.util.Base64

object InfoUtils {

    private const val miuiUrl = "https://update.miui.com/updates/miotaV3.php"
    private var securityKey = "miuiotavalided11"

    private fun generateJson(device: String, version: String, android: String, userId: String?): String {
        val data = mutableMapOf<String, Any>()
        data["id"] = userId ?: "000000000"
        data["c"] = android
        data["d"] = device
        data["f"] = "1"
        data["ov"] = version
        data["l"] = if (!device.contains("_global")) "zh_CN" else "en_US"
        data["r"] = if (!device.contains("_global")) "CN" else "GL"
        data["v"] = "miui-${version.replace("OS1", "V816")}"
        return Gson().toJson(data)
    }

    fun getRomInfo(codename: String, romVersion: String, androidVersion: String): String {
        var userId = ""
        var serviceToken = ""
        var securityKey = securityKey.toByteArray(Charsets.UTF_8)
        var port = "1"
        val cookiesFile = File("cookies.json")
        if (cookiesFile.exists()) {
            val cookies = Gson().fromJson(cookiesFile.readText(), Map::class.java)
            userId = cookies["userId"] as String
            securityKey = Base64.getDecoder().decode(cookies["ssecurity"] as String)
            serviceToken = cookies["serviceToken"] as String
        }
        val jsonData = generateJson(codename, romVersion, androidVersion, userId)
        val encryptedText = miuiEncrypt(jsonData, securityKey)
        if (serviceToken.isNotEmpty()) port = "2"
        val postData = "q=${encryptedText}&t=${serviceToken}&s=${port}"
        val requestedEncryptedText = request(postData)
        return miuiDecrypt(requestedEncryptedText, securityKey)
    }

    private fun request(jsonStr: String): String {
        val okHttpClient = OkHttpClient()
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body: RequestBody = jsonStr.toRequestBody(mediaType)
        val request = Request.Builder().url(miuiUrl).post(body).build()
        val response = okHttpClient.newCall(request).execute()
        return response.body?.string() ?: ""
    }
}
