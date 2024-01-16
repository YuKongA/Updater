package top.yukonga.update.logic.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

object NetworkUtils {

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun getRequest(url: String): Response {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    @Throws(IOException::class)
    fun postRequest(url: String, body: RequestBody): Response {
        val request = Request.Builder().url(url).post(body).build()
        return client.newCall(request).execute()
    }

}
