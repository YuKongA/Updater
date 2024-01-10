package top.yukonga.update.logic.utils

import kotlinx.serialization.json.Json

object JsonUtils {
    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T> String.parseJSON(): T {
        return json.decodeFromString<T>(this)
    }
}