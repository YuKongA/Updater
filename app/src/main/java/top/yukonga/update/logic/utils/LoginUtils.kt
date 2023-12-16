package top.yukonga.update.logic.utils

import android.content.Context
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import top.yukonga.update.R
import top.yukonga.update.logic.utils.FileUtils.saveFile
import top.yukonga.update.logic.utils.NetworkUtils.getRequest
import top.yukonga.update.logic.utils.NetworkUtils.postRequest
import java.security.MessageDigest
import java.util.Base64

class LoginUtils {

    private val loginUrl = "https://account.xiaomi.com/pass/serviceLogin"
    private val loginAuth2Url = "https://account.xiaomi.com/pass/serviceLoginAuth2"
    private val mediaType = "application/x-www-form-urlencoded".toMediaType()
    private val gson = GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER).disableHtmlEscaping().create()

    suspend fun login(context: Context, account: String, password: String): Boolean {
        withContext(Dispatchers.Main) {
            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.account_or_password_empty), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.logging_in), Toast.LENGTH_SHORT).show()
            }
        }

        val md = MessageDigest.getInstance("MD5")
        md.update(password.toByteArray())
        val passwordHash = md.digest().joinToString("") { "%02x".format(it) }.uppercase()

        val response1 = getRequest(loginUrl)
        val _sign = response1.request.url.queryParameter("_sign")?.replace("2&V1_passport&", "")
        if (_sign == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.request_sign_failed), Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val data = "_json=true&bizDeviceType=&user=$account&hash=$passwordHash&sid=miuiromota&_sign=$_sign&_locale=zh_CN"
        val requestBody = data.toRequestBody(mediaType)
        val response2 = postRequest(loginAuth2Url, requestBody)

        val auth = gson.fromJson(response2.body!!.string().replace("&&&START&&&", ""), Map::class.java)
        val description = auth["description"].toString()
        val nonce = auth["nonce"].toString()
        val ssecurity = auth["ssecurity"].toString()
        val location = auth["location"].toString()
        val userId = auth["userId"].toString()

        if (description != "成功") {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val sha1 = MessageDigest.getInstance("SHA-1")
        sha1.update(("nonce=$nonce&$ssecurity").toByteArray())
        val clientSign = Base64.getEncoder().encodeToString(sha1.digest())

        val newUrl = "$location&_userIdNeedEncrypt=true&clientSign=$clientSign"
        val response3 = getRequest(newUrl)
        val cookies = response3.headers("Set-Cookie").joinToString("; ") { it.split(";")[0] }
        val serviceToken = cookies.split("serviceToken=")[1].split(";")[0]

        val json = mutableMapOf<String, String>()
        json["description"] = description
        json["userId"] = userId
        json["ssecurity"] = ssecurity
        json["serviceToken"] = serviceToken

        withContext(Dispatchers.Main) {
            saveFile(context, "cookies.json", gson.toJson(json))
            Toast.makeText(context, context.getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
        }
        return true
    }
}