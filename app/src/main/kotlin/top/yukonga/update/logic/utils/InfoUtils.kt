package top.yukonga.update.logic.utils

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import top.yukonga.update.logic.data.LoginHelper
import top.yukonga.update.logic.data.RequestParamHelper
import top.yukonga.update.logic.utils.AppUtils.json
import top.yukonga.update.logic.utils.CryptoUtils.miuiDecrypt
import top.yukonga.update.logic.utils.CryptoUtils.miuiEncrypt
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.util.Base64

object InfoUtils {

    private const val CN_RECOVERY_URL = "https://update.miui.com/updates/miotaV3.php"
    private const val INTL_RECOVERY_URL = "https://update.intl.miui.com/updates/miotaV3.php"
    private var securityKey = "miuiotavalided11".toByteArray(Charsets.UTF_8)
    private var userId = ""
    private var accountType = "CN"
    private var serviceToken = ""
    private var port = "1"

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

    fun getRecoveryRomInfo(context: Context, codeNameExt: String, regionCode: String, romVersion: String, androidVersion: String): String {

        if (FileUtils.isCookiesFileExists(context)) {
            val cookiesFile = FileUtils.readCookiesFile(context)
            val cookies = json.decodeFromString<LoginHelper>(cookiesFile)
            val authResult = cookies.authResult
            if (authResult != "-1") {
                userId = cookies.userId.toString()
                accountType = cookies.accountType.toString().ifEmpty { "CN" }
                securityKey = Base64.getDecoder().decode((cookies.ssecurity))
                serviceToken = cookies.serviceToken.toString()
                port = "2"
            }
        }
        val jsonData = generateJson(codeNameExt, regionCode, romVersion, androidVersion, userId)
        val encryptedText = miuiEncrypt(jsonData, securityKey)
        val formBodyBuilder = FormBody.Builder().add("q", encryptedText).add("t", serviceToken).add("s", port).build()
        val recoveryUrl = if (accountType == "GL") INTL_RECOVERY_URL else CN_RECOVERY_URL
        val postRequest = postRequest(recoveryUrl, formBodyBuilder)
        val requestedEncryptedText = postRequest.body?.string() ?: ""
        return miuiDecrypt(requestedEncryptedText, securityKey)
    }

}