package top.yukonga.update.logic.utils

import android.content.Context
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import top.yukonga.update.logic.utils.FileUtils.saveFile
import java.security.MessageDigest
import java.util.Base64

class LoginUtils {

    suspend fun login(context: Context, account: String, password: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "$account：登录中", Toast.LENGTH_SHORT).show()
        }

        val client = OkHttpClient()

        val md = MessageDigest.getInstance("MD5")
        md.update(password.toByteArray())
        val passwordHash = md.digest().joinToString("") { "%02x".format(it) }

        val url1 = "https://account.xiaomi.com/pass/serviceLogin"
        val request1 = Request.Builder().url(url1).build()
        val response1 = client.newCall(request1).execute()
        val _sign = response1.request.url.queryParameter("_sign")?.replace("2&V1_passport&", "")
        if (_sign == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "$account：请求 _sign 失败", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val url3 = "https://account.xiaomi.com/pass/serviceLoginAuth2"
        val data = "_json=true&bizDeviceType=&user=$account&hash=${passwordHash.uppercase()}&sid=miuiromota&_sign=$_sign&_locale=zh_CN"
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = data.toRequestBody(mediaType)
        val request2 = Request.Builder().url(url3).post(body).build()
        val response2 = client.newCall(request2).execute()
        val auth = JSONObject(response2.body!!.string().replace("&&&START&&&", ""))
        if (auth.getString("description") != "成功") {
            withContext(Dispatchers.Main) {
                if (auth.getString("description") == "登录验证失败") {
                    Toast.makeText(context, "$account：登录验证失败", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "$account：登录失败", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val ssecurity = auth.getString("ssecurity")
        val userId = auth.getInt("userId")
        val sha1 = MessageDigest.getInstance("SHA-1")
        sha1.update(("nonce=" + auth.getString("nonce") + "&" + ssecurity).toByteArray())
        val clientSign = Base64.getEncoder().encodeToString(sha1.digest())

        val newUrl = auth.getString("location") + "&_userIdNeedEncrypt=true&clientSign=$clientSign"
        val request3 = Request.Builder().url(newUrl).build()
        val response3 = client.newCall(request3).execute()
        val cookies = response3.headers("Set-Cookie").joinToString("; ") { it.split(";")[0] }
        val serviceToken = cookies.split("serviceToken=")[1].split(";")[0]

        val gson = GsonBuilder().disableHtmlEscaping().create()
        val json = mutableMapOf<String, Any>()
        json["userId"] = userId
        json["ssecurity"] = ssecurity
        json["serviceToken"] = serviceToken

        withContext(Dispatchers.Main) {
            saveFile(context, "cookies.json", gson.toJson(json))
            Toast.makeText(context, "$account：登录成功", Toast.LENGTH_SHORT).show()
        }
    }
}