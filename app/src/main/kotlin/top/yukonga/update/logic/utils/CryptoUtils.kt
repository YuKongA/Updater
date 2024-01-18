package top.yukonga.update.logic.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private val iv = "0102030405060708".toByteArray()
    private const val transformation = "AES/CBC/PKCS5Padding"

    private fun miuiCipher(mode: Int, securityKey: ByteArray): Cipher {
        val cipher = Cipher.getInstance(transformation)
        val secretKeySpec = SecretKeySpec(securityKey, "AES")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(mode, secretKeySpec, ivParameterSpec)
        return cipher
    }

    fun miuiEncrypt(jsonRequest: String, securityKey: ByteArray): String {
        val cipher = miuiCipher(Cipher.ENCRYPT_MODE, securityKey)
        val encrypted = cipher.doFinal(jsonRequest.toByteArray())
        return Base64.getUrlEncoder().encodeToString(encrypted)
    }

    fun miuiDecrypt(encryptedText: String, securityKey: ByteArray): String {
        if (encryptedText.isEmpty()) return ""
        val cipher = miuiCipher(Cipher.DECRYPT_MODE, securityKey)
        val encryptedTextBytes = Base64.getMimeDecoder().decode(encryptedText)
        val decryptedTextBytes = cipher.doFinal(encryptedTextBytes)
        return String(decryptedTextBytes)
    }

}