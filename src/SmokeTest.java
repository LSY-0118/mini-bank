import java.security.KeyPair;
import java.security.PublicKey;
import java.security.PrivateKey;

/**
 * 간단한 스모크 테스트 (별도 테스트 프레임워크 없이 javac/java로 바로 실행 가능).
 * 실행: javac *.java && java SmokeTest
 */
public class SmokeTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        testRsaRoundTrip();
        testAccountDepositAndWithdraw();
        testBankOpenAccountAndDeposit();

        System.out.println();
        System.out.println("결과: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testRsaRoundTrip() {
        try {
            KeyPair keyPair = RSAUtil.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String original = "1234";
            String encrypted = RSAUtil.encrypt(original, publicKey);
            String decrypted = RSAUtil.decrypt(encrypted, privateKey);

            check("RSAUtil 암호화/복호화 원문 복원", original.equals(decrypted));
        } catch (Exception e) {
            check("RSAUtil 암호화/복호화 원문 복원", false, e);
        }
    }

    private static void testAccountDepositAndWithdraw() {
        try {
            Account account = new Account("110-9999", "홍길동", 1000L);
            account.deposit(500L);
            boolean withdrawOk = account.withdraw(300L);

            check("Account 입금 후 잔액 반영", account.getBalance() == 1200L);
            check("Account 정상 출금 성공", withdrawOk);
        } catch (Exception e) {
            check("Account 입출금 동작", false, e);
        }
    }

    private static void testBankOpenAccountAndDeposit() {
        try {
            Bank bank = new Bank();
            Account account = bank.openAccount("홍길동", 1000L);
            boolean depositOk = bank.deposit(account.getAccountNumber(), 500L);

            check("Bank 계좌 개설 후 조회 가능", bank.findAccount(account.getAccountNumber()) != null);
            check("Bank 입금 성공 및 잔액 반영", depositOk && account.getBalance() == 1500L);
        } catch (Exception e) {
            check("Bank 계좌 개설/입금 동작", false, e);
        }
    }

    private static void check(String description, boolean condition) {
        check(description, condition, null);
    }

    private static void check(String description, boolean condition, Exception error) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description + (error != null ? " (" + error + ")" : ""));
        }
    }
}
