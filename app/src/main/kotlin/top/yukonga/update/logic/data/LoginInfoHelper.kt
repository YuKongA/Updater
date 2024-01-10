package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfoHelper(
    val description: String,
    val accountType: String,
    val userId: String,
    val ssecurity: String,
    val serviceToken: String,
    val authResult: String
)