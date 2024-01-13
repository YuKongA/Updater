package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfoHelper(
    val accountType: String,
    val authResult: String,
    val description: String,
    val ssecurity: String,
    val serviceToken: String,
    val userId: String,
)