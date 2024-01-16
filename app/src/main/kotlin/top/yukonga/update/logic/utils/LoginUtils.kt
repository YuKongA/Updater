package top.yukonga.update.logic.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.yukonga.update.R
import top.yukonga.update.logic.data.AuthorizeInfoHelper
import top.yukonga.update.logic.data.LoginInfoHelper
import top.yukonga.update.logic.utils.FileUtils.deleteCookiesFile
import top.yukonga.update.logic.utils.FileUtils.saveCookiesFile
import top.yukonga.update.logic.utils.JsonUtils.json
import top.yukonga.update.logic.utils.NetworkUtils.getRequest
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import top.yukonga.update.logic.utils.miuiStringToast.MiuiStringToast.showStringToast
import java.security.MessageDigest
import java.util.Base64

class LoginUtils {

    private val loginUrl = "https://account.xiaomi.com/pass/serviceLogin"
    private val loginAuth2Url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
    private val mediaType = "application/x-www-form-urlencoded".toMediaType()

    suspend fun login(context: Context, account: String, password: String, global: String): Boolean {
        if (account.isBlank() || password.isBlank()) {
            showToast(context, R.string.account_or_password_empty, 0)
            return false
        } else {
            showToast(context, R.string.logging_in, 1)
        }

        val passwordHash = hashPassword(password)

        val response1 = getRequest(loginUrl)
        val _sign = response1.request.url.queryParameter("_sign")?.replace("2&V1_passport&", "")
        if (_sign == null) {
            showToast(context, R.string.request_sign_failed, 0)
            return false
        }

        val sid = if (global == "1") "miuiota_intl" else "miuiromota"
        val _locale = if (global == "1") "en_US" else "zh_CN"
        val data = "_json=true&bizDeviceType=&user=$account&hash=$passwordHash&sid=$sid&_sign=$_sign&_locale=$_locale"
        val requestBody = data.toRequestBody(mediaType)
        val response2 = postRequest(loginAuth2Url, requestBody)

        val authStr = response2.body!!.string().replace("&&&START&&&", "")
        val authJson = json.decodeFromString<AuthorizeInfoHelper>(authStr)
        val description = authJson.description
        val nonce = authJson.nonce
        val ssecurity = authJson.ssecurity
        val location = authJson.location
        val userId = authJson.userId.toString()
        val accountType = if (global == "1") "GL" else "CN"
        val authResult = if (authJson.result == "ok") "1" else "0"

        if (description != "成功") {
            showToast(context, if (description == "登录验证失败") R.string.login_error else description, 0)
            return false
        }
        if (nonce == null || ssecurity == null || location == null || userId.isEmpty()) {
            showToast(context, R.string.security_error, 0)
            return false
        }

        val clientSign = generateClientSign(nonce, ssecurity)

        val newUrl = "$location&_userIdNeedEncrypt=true&clientSign=$clientSign"
        val response3 = getRequest(newUrl)
        val cookies = response3.headers("Set-Cookie").joinToString("; ") { it.split(";")[0] }
        val serviceToken = cookies.split("serviceToken=")[1].split(";")[0]

        val loginInfo = LoginInfoHelper(accountType, authResult, description, ssecurity, serviceToken, userId)

        withContext(Dispatchers.Main) {
            saveCookiesFile(context, Json.encodeToString(loginInfo))
            showToast(context, R.string.login_successful, 1)
        }
        return true
    }

    private suspend fun showToast(context: Context, message: Int, duration: Int) {
        withContext(Dispatchers.Main) {
            showStringToast(context, context.getString(message), duration)
        }
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(password.toByteArray())
        return md.digest().joinToString("") { "%02x".format(it) }.uppercase()
    }

    private fun generateClientSign(nonce: String, ssecurity: String): String {
        val sha1 = MessageDigest.getInstance("SHA-1")
        sha1.update(("nonce=$nonce&$ssecurity").toByteArray())
        return Base64.getEncoder().encodeToString(sha1.digest())
    }

    suspend fun logout(context: Context) {
        withContext(Dispatchers.Main) {
            deleteCookiesFile(context)
        }
    }
}
