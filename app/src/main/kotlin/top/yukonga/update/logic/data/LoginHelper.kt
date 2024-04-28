package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class LoginHelper(
    val accountType: String?= null,
    val authResult: String?= null,
    val description: String?= null,
    val ssecurity: String?= null,
    val serviceToken: String?= null,
    val userId: String?= null,
)