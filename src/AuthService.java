import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인 인증 서비스.
 * - 서버(은행)만 개인키를 보관하고, 공개키는 클라이언트(사용자 단말)에 배포한다.
 * - 클라이언트는 비밀번호를 공개키로 암호화해서 서버로 보내고,
 *   서버는 개인키로 복호화한 뒤 저장된 비밀번호와 비교해 로그인을 처리한다.
 * - 실제 인터넷뱅킹에서 로그인창에 비밀번호를 입력하면 브라우저 단에서
 *   암호화가 일어나는 것과 같은 구조를 재현했다.
 */
public class AuthService {
    private final Map<String, String> userPasswords = new HashMap<>(); // username -> 평문 비밀번호(서버 DB 역할)
    private final KeyPair serverKeyPair;

    public AuthService() throws Exception {
        this.serverKeyPair = RSAUtil.generateKeyPair();
    }

    public void register(String username, String password) {
        userPasswords.put(username, password);
    }

    /** 서버 공개키를 클라이언트에 배포한다고 가정 */
    public PublicKey getPublicKey() {
        return serverKeyPair.getPublic();
    }

    /**
     * 클라이언트가 공개키로 암호화해서 보낸 비밀번호를 받아 로그인 처리.
     * @param username 사용자 아이디
     * @param encryptedPasswordBase64 공개키로 암호화된 비밀번호 (Base64)
     */
    public boolean login(String username, String encryptedPasswordBase64) throws Exception {
        PrivateKey privateKey = serverKeyPair.getPrivate();
        String decryptedPassword = RSAUtil.decrypt(encryptedPasswordBase64, privateKey);
        String storedPassword = userPasswords.get(username);
        return storedPassword != null && storedPassword.equals(decryptedPassword);
    }
}
