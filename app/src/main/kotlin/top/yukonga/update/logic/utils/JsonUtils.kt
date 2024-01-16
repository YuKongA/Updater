package top.yukonga.update.logic.utils

import kotlinx.serialization.json.Json

object JsonUtils {
    fun getJson(ignoreUnknownKeys: Boolean = true): Json {
        return Json { this.ignoreUnknownKeys = ignoreUnknownKeys }
    }
}
