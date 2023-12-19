package top.yukonga.update.logic.utils

import android.content.Context
import com.google.gson.Gson
import okhttp3.FormBody
import top.yukonga.update.logic.utils.CryptoUtils.miuiDecrypt
import top.yukonga.update.logic.utils.CryptoUtils.miuiEncrypt
import top.yukonga.update.logic.utils.FileUtils.readFile
import top.yukonga.update.logic.utils.NetworkUtils.getRequest
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.util.Base64

object InfoUtils {

    private const val recoveryUrl = "https://update.miui.com/updates/miotaV3.php"
    private const val fastbootUrl = "https://update.miui.com/updates/miota-fullrom.php"
    private var securityKey = "miuiotavalided11"

    private fun generateJson(device: String, version: String, android: String, userId: String): String {
        val data = mutableMapOf<String, Any>()
        data["id"] = userId
        data["c"] = android
        data["d"] = device
        data["f"] = "1"
        data["ov"] = version
        data["l"] = if (!device.contains("_global")) "zh_CN" else "en_US"
        data["r"] = if (!device.contains("_global")) "CN" else "GL"
        data["v"] = "miui-${version}"
        return Gson().toJson(data)
    }

    fun getRecoveryRomInfo(context: Context, codename: String, romVersion: String, androidVersion: String): String {
        var userId = ""
        var securityKey = securityKey.toByteArray(Charsets.UTF_8)
        var serviceToken = ""
        var port = "1"
        val cookiesFile = readFile(context, "cookies.json")
        if (cookiesFile.isNotEmpty()) {
            val cookies = Gson().fromJson(cookiesFile, Map::class.java)
            userId = cookies["userId"] as String
            securityKey = Base64.getDecoder().decode((cookies["ssecurity"] as String))
            serviceToken = cookies["serviceToken"] as String
            port = "2"
        }
        val jsonData = generateJson(codename, romVersion, androidVersion, userId)
        val encryptedText = miuiEncrypt(jsonData, securityKey)
        val requestBody = FormBody.Builder().add("q", encryptedText).add("t", serviceToken).add("s", port).build()
        val postRequest = postRequest(recoveryUrl, requestBody)
        val requestedEncryptedText = postRequest.body?.string() ?: ""
        return miuiDecrypt(requestedEncryptedText, securityKey)
    }

    fun getFastbootRomInfo(codename: String): String {
        val url = "$fastbootUrl?d=$codename&b=F&r=cn"
        val getRequest = getRequest(url)
        return getRequest.body?.string() ?: ""
    }
}
