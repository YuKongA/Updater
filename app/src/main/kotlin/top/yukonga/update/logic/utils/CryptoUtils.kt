package top.yukonga.update.logic.utils;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String AES_ALGORITHM = "AES";
    private static final int IV_SIZE = 16;

    private static IvParameterSpec generateRandomIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static Cipher initializeCipher(int mode, byte[] securityKey, IvParameterSpec ivParameterSpec) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        SecretKeySpec secretKeySpec = new SecretKeySpec(securityKey, AES_ALGORITHM);
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher;
    }

    public static String encrypt(String jsonRequest, byte[] securityKey) throws Exception {
        IvParameterSpec ivParameterSpec = generateRandomIv();
        Cipher cipher = initializeCipher(Cipher.ENCRYPT_MODE, securityKey, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(jsonRequest.getBytes());
        String iv = Base64.getEncoder().encodeToString(ivParameterSpec.getIV());
        String encryptedData = Base64.getEncoder().encodeToString(encrypted);
        return iv + ":" + encryptedData;
    }

    public static String decrypt(String encryptedData, byte[] securityKey) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(parts[0]));
        Cipher cipher = initializeCipher(Cipher.DECRYPT_MODE, securityKey, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(parts[1]));
        return new String(decrypted);
    }
}
