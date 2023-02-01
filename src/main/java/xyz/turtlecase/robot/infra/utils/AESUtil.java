package xyz.turtlecase.robot.infra.utils;

import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.exception.BaseException;

/**
 * AES加密工具
 */
@Slf4j
public final class AESUtil {
    /**
     * 生成密钥
     *
     * @return
     */
    @SneakyThrows
    public static String generateKey() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] byteKey = secretKey.getEncoded();
        return Hex.encodeHexString(byteKey);
    }

    /**
     * AES加密
     *
     * @param _key
     * @param data
     * @return
     */
    public static String encode(String _key, String data) {
        if (StringUtils.isBlank(_key)) {
            throw new BaseException("key is not null");
        }
        try {
            // 转换key
            Key key = new SecretKeySpec(Hex.decodeHex(_key), "AES");

            // 加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(data.getBytes());
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            log.error("AES encode", e);
            return null;
        }
    }

    /**
     * 解密
     *
     * @param _key
     * @param data
     * @return
     */
    public static String decode(String _key, String data) {
        try {
            // 转换key
            Key key = new SecretKeySpec(Hex.decodeHex(_key), "AES");

            // 解密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Hex.decodeHex(data));
            return new String(result);
        } catch (Exception e) {
            log.error("AES decode", e);
            return null;
        }
    }
}
