package top.yukonga.update.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    private val iv = "0102030405060708".toByteArray(Charsets.UTF_8)
    var securityKey = "miuiotavalided11".toByteArray(Charsets.UTF_8)

    private fun miuiCipher(mode: Int, key: ByteArray?): Cipher? {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(key ?: securityKey, "AES")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(mode, secretKeySpec, ivParameterSpec)
        return cipher
    }

    fun miuiEncrypt(jsonRequest: String, key: ByteArray?): String {
        val cipher = miuiCipher(Cipher.ENCRYPT_MODE, key ?: securityKey)
        val encrypted = cipher!!.doFinal(jsonRequest.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().encodeToString(encrypted)
    }

    fun miuiDecrypt(encryptedText: String, key: ByteArray?): String {
        val cipher = miuiCipher(Cipher.DECRYPT_MODE, key ?: securityKey)
        val decoded = Base64.getUrlDecoder().decode(encryptedText)
        val decrypted = cipher!!.doFinal(decoded)
        return String(decrypted, Charsets.UTF_8)
    }

}
