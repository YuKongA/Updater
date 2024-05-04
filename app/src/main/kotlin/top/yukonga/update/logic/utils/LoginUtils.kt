package top.yukonga.update.logic.utils

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.yukonga.miuiStringToast.MiuiStringToast.showStringToast
import top.yukonga.update.R
import top.yukonga.update.logic.data.AuthorizeHelper
import top.yukonga.update.logic.data.LoginHelper
import top.yukonga.update.logic.utils.AppUtils.json
import top.yukonga.update.logic.utils.FileUtils.deleteCookiesFile
import top.yukonga.update.logic.utils.FileUtils.saveCookiesFile
import top.yukonga.update.logic.utils.NetworkUtils.getRequest
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.security.MessageDigest
import java.util.Base64

class LoginUtils {

    private val loginUrl = "https://account.xiaomi.com/pass/serviceLogin"
    private val loginAuth2Url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
    private val mediaType = "application/x-www-form-urlencoded".toMediaType()

    fun login(context: Context, account: String, password: String, global: String, savePassword: String, autoLogin: Boolean = false): Boolean {
        if (account.isEmpty() || password.isEmpty()) {
            showStringToast(context, context.getString(R.string.account_or_password_empty), 0)
            return false
        } else {
            if (!autoLogin) showStringToast(context, context.getString(R.string.logging_in), 1)
        }

        if (savePassword == "1") saveAccountAndPassword(context, account, password) else deleteAccountAndPassword(context)

        val md = MessageDigest.getInstance("MD5")
        md.update(password.toByteArray())
        val passwordHash = md.digest().joinToString("") { "%02x".format(it) }.uppercase()

        val response1 = getRequest(loginUrl)
        val sign = response1.request.url.queryParameter("_sign")?.replace("2&V1_passport&", "")
        if (sign == null) {
            showStringToast(context, context.getString(R.string.request_sign_failed), 0)
            return false
        }

        val sid = if (global == "1") "miuiota_intl" else "miuiromota"
        val locale = if (global == "1") "en_US" else "zh_CN"
        val data = "_json=true&bizDeviceType=&user=$account&hash=$passwordHash&sid=$sid&_sign=$sign&_locale=$locale"
        val requestBody = data.toRequestBody(mediaType)
        val response2 = postRequest(loginAuth2Url, requestBody)

        val authStr = response2.body!!.string().replace("&&&START&&&", "")
        val authJson = json.decodeFromString<AuthorizeHelper>(authStr)
        val description = authJson.description
        val nonce = authJson.nonce
        val ssecurity = authJson.ssecurity
        val location = authJson.location
        val userId = authJson.userId.toString()
        val accountType = if (global == "1") "GL" else "CN"
        val authResult = if (authJson.result == "ok") "1" else "0"

        if (description != "成功") {
            if (description == "登录验证失败") showStringToast(context, context.getString(R.string.login_error), 0)
            else showStringToast(context, description, 0)
            return false
        }

        if (nonce == null || ssecurity == null || location == null || userId.isEmpty()) {
            showStringToast(context, context.getString(R.string.security_error), 0)
            return false
        }

        val sha1 = MessageDigest.getInstance("SHA-1")
        sha1.update(("nonce=$nonce&$ssecurity").toByteArray())
        val clientSign = Base64.getEncoder().encodeToString(sha1.digest())

        val newUrl = "$location&_userIdNeedEncrypt=true&clientSign=$clientSign"
        val response3 = getRequest(newUrl)
        val cookies = response3.headers("Set-Cookie").joinToString("; ") { it.split(";")[0] }
        val serviceToken = cookies.split("serviceToken=")[1].split(";")[0]

        val loginInfo = LoginHelper(accountType, authResult, description, ssecurity, serviceToken, userId)
        saveCookiesFile(context, Json.encodeToString(loginInfo))
        showStringToast(context, context.getString(R.string.login_successful), 1)
        return true
    }

    fun logout(context: Context) {
            deleteCookiesFile(context)
            showStringToast(context, context.getString(R.string.logout_successful), 1)
    }

    private fun saveAccountAndPassword(context: Context, account: String, password: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()

        KeyStoreUtils.generateKey()
        val cipherAccount = KeyStoreUtils.getEncryptionCipher()
        val encryptedAccount = cipherAccount.doFinal(account.toByteArray())
        val cipherPassword = KeyStoreUtils.getEncryptionCipher()
        val encryptedPassword = cipherPassword.doFinal(password.toByteArray())

        editor.putString("account", Base64.getEncoder().encodeToString(encryptedAccount))
            .putString("password", Base64.getEncoder().encodeToString(encryptedPassword))
            .putString("account_iv", Base64.getEncoder().encodeToString(cipherAccount.iv))
            .putString("password_iv", Base64.getEncoder().encodeToString(cipherPassword.iv)).apply()
    }

    fun getAccountAndPassword(context: Context): Pair<String, String> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val encryptedAccountBase64 = sharedPreferences.getString("account", "") ?: ""
        val encryptedPasswordBase64 = sharedPreferences.getString("password", "") ?: ""
        val accountIvBase64 = sharedPreferences.getString("account_iv", "") ?: ""
        val passwordIvBase64 = sharedPreferences.getString("password_iv", "") ?: ""

        if (encryptedAccountBase64.isEmpty() || encryptedPasswordBase64.isEmpty() || accountIvBase64.isEmpty() || passwordIvBase64.isEmpty()) {
            return Pair("", "")
        }

        val encryptedAccount = Base64.getDecoder().decode(encryptedAccountBase64)
        val encryptedPassword = Base64.getDecoder().decode(encryptedPasswordBase64)

        val accountIv = Base64.getDecoder().decode(accountIvBase64)
        val accountCipher = KeyStoreUtils.getDecryptionCipher(accountIv)
        val account = String(accountCipher.doFinal(encryptedAccount))

        val passwordIv = Base64.getDecoder().decode(passwordIvBase64)
        val passwordCipher = KeyStoreUtils.getDecryptionCipher(passwordIv)
        val password = String(passwordCipher.doFinal(encryptedPassword))

        return Pair(account, password)
    }

    private fun deleteAccountAndPassword(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove("account").remove("password").remove("account_iv").remove("password_iv").apply()
    }

}