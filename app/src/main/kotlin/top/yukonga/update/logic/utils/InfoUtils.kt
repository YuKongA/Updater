package top.yukonga.update.logic.utils

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import top.yukonga.update.logic.data.LoginInfoHelper
import top.yukonga.update.logic.data.RequestParamHelper
import top.yukonga.update.logic.utils.CryptoUtils.miuiDecrypt
import top.yukonga.update.logic.utils.CryptoUtils.miuiEncrypt
import top.yukonga.update.logic.utils.JsonUtils.json
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.util.Base64

object InfoUtils {

    private const val CN_RECOVERY_URL = "https://update.miui.com/updates/miotaV3.php"
    private const val INTL_RECOVERY_URL = "https://update.intl.miui.com/updates/miotaV3.php"
    private const val DEFAULT_SECURITY_KEY = "miuiotavalided11"
    private const val DEFAULT_ACCOUNT_TYPE = "CN"
    private const val DEFAULT_PORT = "1"

    // Generate JSON string from given parameters
    private fun generateJson(codeNameExt: String, regionCode: String, romVersion: String, androidVersion: String, userId: String): String {
        val data = RequestParamHelper(
            id = userId,
            c = androidVersion,
            d = codeNameExt,
            f = "1",
            ov = romVersion,
            l = if (!codeNameExt.contains("_global")) "zh_CN" else "en_US",
            r = regionCode,
            v = "miui-$romVersion"
        )
        return Json.encodeToString(data)
    }

    // Get recovery ROM info
    fun getRecoveryRomInfo(context: Context, codeNameExt: String, regionCode: String, romVersion: String, androidVersion: String): String? {
        var userId = ""
        var accountType = DEFAULT_ACCOUNT_TYPE
        var securityKey = DEFAULT_SECURITY_KEY.toByteArray(Charsets.UTF_8)
        var serviceToken = ""
        var port = DEFAULT_PORT
        if (FileUtils.isCookiesFileExists(context)) {
            val cookiesFile = FileUtils.readCookiesFile(context)
            val cookies = json.decodeFromString<LoginInfoHelper>(cookiesFile)
            userId = cookies.userId
            accountType = cookies.accountType.ifEmpty { DEFAULT_ACCOUNT_TYPE }
            securityKey = Base64.getDecoder().decode((cookies.ssecurity))
            serviceToken = cookies.serviceToken
            port = "2"
        }
        val jsonData = generateJson(codeNameExt, regionCode, romVersion, androidVersion, userId)
        val encryptedText = miuiEncrypt(jsonData, securityKey)
        val requestBody = FormBody.Builder().add("q", encryptedText).add("t", serviceToken).add("s", port).build()
        val recoveryUrl = if (accountType == "GL") INTL_RECOVERY_URL else CN_RECOVERY_URL
        val postRequest = postRequest(recoveryUrl, requestBody)
        val requestedEncryptedText = postRequest.body?.string() ?: ""
        return try {
            miuiDecrypt(requestedEncryptedText, securityKey)
        } catch (e: Exception) {
            null
        }
    }
}
