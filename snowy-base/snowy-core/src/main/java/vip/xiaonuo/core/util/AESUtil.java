package vip.xiaonuo.core.util;



import org.apache.commons.codec.binary.Hex;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import vip.xiaonuo.core.consts.CommonConstant;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @version V1.0
 * @desc AES 加密工具类
 * @author 981743898@qq.com
 */
public class AESUtil {

    private static final String AES = "AES";
    private static final String UTF8 = "UTF-8";
    private static final String CIPHERALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String encrypt(String content) {
        try {
            byte[] encodeFormat = CommonConstant.AES_PASS.getBytes();
            SecretKeySpec key = new SecretKeySpec(encodeFormat, AES);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(CIPHERALGORITHM);
            // 加密内容进行编码
            byte[] byteContent = content.getBytes(UTF8);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 正式执行加密操作
            byte[] result = cipher.doFinal(byteContent);
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content) {
        try {
            // 密文使用Hex解码
            byte[] byteContent = Hex.decodeHex(content.toCharArray());
            byte[] encodeFormat = CommonConstant.AES_PASS.getBytes();
            SecretKeySpec key = new SecretKeySpec(encodeFormat, AES);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(AES);
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 正式执行解密操作
            byte[] result = cipher.doFinal(byteContent);
            return new String(result, UTF8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String s = "123456";
        String s1 = encrypt(s);
        System.out.println(s1);
        System.out.println(decrypt(s1));
    }
}
