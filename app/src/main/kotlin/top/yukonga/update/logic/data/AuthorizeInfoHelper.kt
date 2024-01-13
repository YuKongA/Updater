package top.yukonga.update.logic.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeInfoHelper(
    val description: String? = null,
    val location: String? = null,
    val nonce: Long? = null,
    val result: String? = null,
    val ssecurity: String? = null,
    val userId: Long? = null,
)