import java.security.PublicKey;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        Bank bank = new Bank();

        // 데모 사용자 등록 (실제 서버 DB에 비밀번호가 저장되어 있다고 가정)
        authService.register("demo", "1234");

        System.out.println("=== 미니 뱅킹 시스템 (RSA 로그인 데모) ===");
        if (!handleLogin(scanner, authService)) {
            System.out.println("로그인에 실패해 프로그램을 종료합니다.");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    openAccount(scanner, bank);
                    break;
                case "2":
                    deposit(scanner, bank);
                    break;
                case "3":
                    withdraw(scanner, bank);
                    break;
                case "4":
                    transfer(scanner, bank);
                    break;
                case "5":
                    printHistory(scanner, bank);
                    break;
                case "0":
                    running = false;
                    System.out.println("프로그램을 종료합니다.");
                    break;
                default:
                    System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
            }
        }
        scanner.close();
    }

    /** RSA 암호화 로그인 과정을 재현한다. */
    private static boolean handleLogin(Scanner scanner, AuthService authService) throws Exception {
        System.out.print("아이디 입력 (데모 계정: demo): ");
        String username = scanner.nextLine().trim();
        System.out.print("비밀번호 입력 (데모 비밀번호: 1234): ");
        String password = scanner.nextLine().trim();

        // 1) 서버가 공개키를 클라이언트에 배포했다고 가정
        PublicKey publicKey = authService.getPublicKey();
        // 2) 클라이언트(사용자 단말)가 공개키로 비밀번호를 암호화
        String encryptedPassword = RSAUtil.encrypt(password, publicKey);
        System.out.println("[클라이언트] RSA로 암호화된 비밀번호 전송: " + encryptedPassword.substring(0, 30) + "...");

        // 3) 서버가 개인키로 복호화 후 검증
        boolean success = authService.login(username, encryptedPassword);
        System.out.println(success ? "로그인 성공!" : "아이디 또는 비밀번호가 올바르지 않습니다.");
        return success;
    }

    private static void printMenu() {
        System.out.println("\n---- 메뉴 ----");
        System.out.println("1. 계좌 개설");
        System.out.println("2. 입금");
        System.out.println("3. 출금");
        System.out.println("4. 이체");
        System.out.println("5. 거래내역 조회");
        System.out.println("0. 종료");
        System.out.print("선택: ");
    }

    private static void openAccount(Scanner scanner, Bank bank) {
        System.out.print("예금주명: ");
        String name = scanner.nextLine().trim();
        System.out.print("초기 입금액: ");
        long amount = readLong(scanner);
        var account = bank.openAccount(name, amount);
        System.out.println("계좌 개설 완료! 계좌번호: " + account.getAccountNumber());
    }

    private static void deposit(Scanner scanner, Bank bank) {
        System.out.print("계좌번호: ");
        String accNo = scanner.nextLine().trim();
        System.out.print("입금액: ");
        long amount = readLong(scanner);
        boolean success = bank.deposit(accNo, amount);
        System.out.println(success ? "입금 완료" : "입금 실패 (계좌번호 또는 금액 확인)");
    }

    private static void withdraw(Scanner scanner, Bank bank) {
        System.out.print("계좌번호: ");
        String accNo = scanner.nextLine().trim();
        System.out.print("출금액: ");
        long amount = readLong(scanner);
        boolean success = bank.withdraw(accNo, amount);
        System.out.println(success ? "출금 완료" : "출금 실패 (잔액 부족 또는 계좌번호 확인)");
    }

    private static void transfer(Scanner scanner, Bank bank) {
        System.out.print("출금 계좌번호: ");
        String from = scanner.nextLine().trim();
        System.out.print("입금 계좌번호: ");
        String to = scanner.nextLine().trim();
        System.out.print("이체금액: ");
        long amount = readLong(scanner);
        boolean success = bank.transfer(from, to, amount);
        System.out.println(success ? "이체 완료" : "이체 실패 (계좌번호 또는 잔액 확인)");
    }

    private static void printHistory(Scanner scanner, Bank bank) {
        System.out.print("계좌번호: ");
        String accNo = scanner.nextLine().trim();
        var account = bank.findAccount(accNo);
        if (account == null) {
            System.out.println("존재하지 않는 계좌번호입니다.");
            return;
        }
        System.out.println("=== " + account.getOwnerName() + "님 거래내역 (잔액 " + account.getBalance() + "원) ===");
        for (String line : account.getHistory()) {
            System.out.println(line);
        }
    }

    private static long readLong(Scanner scanner) {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("숫자로 입력해주세요: ");
            }
        }
    }
}
