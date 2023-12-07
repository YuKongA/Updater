package top.yukonga.update.logic.utils

import com.google.gson.Gson

object JsonUtils {
    val gson = Gson()

    inline fun <reified T> String.parseJSON(): T {
        return gson.fromJson(this, T::class.java)
    }
}