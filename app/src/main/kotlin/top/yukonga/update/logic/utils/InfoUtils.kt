package top.yukonga.update.logic.utils

import android.content.Context
import com.google.gson.Gson
import okhttp3.FormBody
import top.yukonga.update.logic.utils.CryptoUtils.miuiDecrypt
import top.yukonga.update.logic.utils.CryptoUtils.miuiEncrypt
import top.yukonga.update.logic.utils.NetworkUtils.getRequest
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.util.Base64

object InfoUtils {

    private const val cnRecoveryUrl = "https://update.miui.com/updates/miotaV3.php"
    private const val intlRecoveryUrl = "https://update.intl.miui.com/updates/miotaV3.php"
    private const val fastbootUrl = "https://update.miui.com/updates/miota-fullrom.php"
    private var securityKey = "miuiotavalided11"

    private fun generateJson(device: String, region: String, version: String, android: String, userId: String): String {
        val data = mutableMapOf(
            "id" to userId,
            "c" to android,
            "d" to device,
            "f" to "1",
            "ov" to version,
            "l" to if (!device.contains("_global")) "zh_CN" else "en_US",
            "r" to region.replace("GL", "MI").replace("EEA", "EU"),
            "v" to "miui-$version"
        )
        return Gson().toJson(data)
    }

    fun getRecoveryRomInfo(context: Context, codeName: String, regionCode: String, romVersion: String, androidVersion: String): String {
        var userId = ""
        var accountType = "CN"
        var securityKey = securityKey.toByteArray(Charsets.UTF_8)
        var serviceToken = ""
        var port = "1"
        if (FileUtils.isCookiesFileExists(context)) {
            val cookiesFile = FileUtils.readCookiesFile(context)
            val cookies = Gson().fromJson(cookiesFile, MutableMap::class.java)
            userId = cookies["userId"].toString()
            accountType = cookies["accountType"].toString().ifEmpty { "CN" }
            securityKey = Base64.getDecoder().decode((cookies["ssecurity"].toString()))
            serviceToken = cookies["serviceToken"].toString()
            port = "2"
        }
        val jsonData = generateJson(codeName, regionCode, romVersion, androidVersion, userId)
        val encryptedText = miuiEncrypt(jsonData, securityKey)
        val requestBody = FormBody.Builder().add("q", encryptedText).add("t", serviceToken).add("s", port).build()
        val recoveryUrl = if (accountType == "GL") intlRecoveryUrl else cnRecoveryUrl
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
