package top.yukonga.update.logic.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val iv = "0102030405060708"

    private fun miuiCipher(mode: Int, securityKey: ByteArray): Cipher {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKeySpec = SecretKeySpec(securityKey, "AES")
        val ivParameterSpec = IvParameterSpec(iv.toByteArray(Charsets.UTF_8))
        cipher.init(mode, secretKeySpec, ivParameterSpec)
        return cipher
    }

    fun miuiEncrypt(jsonRequest: String, securityKey: ByteArray): String {
        val cipher = miuiCipher(Cipher.ENCRYPT_MODE, securityKey)
        val encrypted = cipher.doFinal(jsonRequest.toByteArray(Charsets.UTF_8))
        return Base64.getUrlEncoder().encodeToString(encrypted)
    }

    fun miuiDecrypt(encryptedText: String, securityKey: ByteArray): String {
        val cipher = miuiCipher(Cipher.DECRYPT_MODE, securityKey)
        if (encryptedText.isEmpty()) return ""
        val encryptedTextBytes = Base64.getMimeDecoder().decode(encryptedText)
        val decryptedTextBytes = cipher.doFinal(encryptedTextBytes)
        return String(decryptedTextBytes, Charsets.UTF_8)
    }

}
