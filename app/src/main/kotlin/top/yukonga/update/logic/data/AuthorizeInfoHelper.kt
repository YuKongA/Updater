package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeInfoHelper(
    val ssecurity: String,
    val code: Int,
    val passToken: String,
    val description: String,
    val securityStatus: Int,
    val nonce: Long,
    val userId: Long,
    val cUserId: String,
    val result: String,
    val psecurity: String,
    val captchaUrl: String?,
    val location: String,
    val pwd: Int,
    val child: Int,
    val desc: String,
)