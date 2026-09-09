import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * RSA 공개키 암호화 유틸리티
 * - 서버가 키 쌍(공개키/개인키)을 생성해서 공개키는 클라이언트에 배포하고
 * - 클라이언트는 공개키로 비밀번호를 암호화해서 전송하고
 * - 서버는 개인키로 복호화해서 검증하는, 실제 인터넷뱅킹 로그인과 동일한 구조를 재현한다.
 */
public class RSAUtil {

    /** RSA 키 쌍을 생성한다. (실무에서는 RSA-2048 이상을 사용) */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    /** 공개키로 평문을 암호화하고 Base64 문자열로 반환한다. (클라이언트 역할) */
    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /** 개인키로 Base64 암호문을 복호화하여 평문을 반환한다. (서버 역할) */
    public static String decrypt(String cipherTextBase64, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(cipherTextBase64);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
