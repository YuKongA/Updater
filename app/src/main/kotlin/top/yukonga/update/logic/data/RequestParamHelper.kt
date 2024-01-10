package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestParamHelper(
    val id: String,
    val c: String,
    val d: String,
    val f: String,
    val ov: String,
    val l: String,
    val r: String,
    val v: String
)
