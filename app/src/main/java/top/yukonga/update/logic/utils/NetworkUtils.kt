package top.yukonga.update.logic.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

object NetworkUtils {

    fun getRequest(url: String): Response {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    fun postRequest(url: String, body: RequestBody): Response {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).post(body).build()
        return client.newCall(request).execute()
    }

}