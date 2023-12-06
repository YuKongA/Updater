package top.yukonga.update.utils

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import top.yukonga.update.utils.Crypto.miuiDecrypt
import top.yukonga.update.utils.Crypto.miuiEncrypt
import java.io.File
import java.util.Base64

object Utils {

    private const val miuiUrl = "https://update.miui.com/updates/miotaV3.php"

    fun generateJson(device: String, version: String, android: String, userId: String?): String {
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
        var securityKey = Crypto.securityKey
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
        val postData = mutableMapOf<String, Any>()
        postData["q"] = encryptedText
        postData["t"] = serviceToken
        postData["s"] = port
        Log.i("MIUI_UPDATE_INFO", Gson().toJson(postData))
        val requestedEncryptedText = request(Gson().toJson(postData))
        return miuiDecrypt(requestedEncryptedText ?: "", securityKey)
    }


    private fun request(data: String): String? {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = data.toRequestBody(mediaType)
        val request = Request.Builder().url(miuiUrl).post(body).build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                println("向服务器发送请求时出错: ${response.message}")
                null
            } else {
                response.body?.string()
            }
        }
    }
}
