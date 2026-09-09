import java.util.HashMap;
import java.util.Map;

/**
 * 계좌 개설/입출금/이체를 담당하는 은행 핵심 로직.
 * 실제 은행 코어 시스템의 "수신(예금) 업무"를 아주 단순화한 형태로 재현했다.
 */
public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();
    private int nextAccountNumber = 1001;

    public Account openAccount(String ownerName, long initialBalance) {
        String accountNumber = "110-" + nextAccountNumber++;
        Account account = new Account(accountNumber, ownerName, initialBalance);
        accounts.put(accountNumber, account);
        return account;
    }

    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean deposit(String accountNumber, long amount) {
        Account account = accounts.get(accountNumber);
        if (account == null || amount <= 0) {
            return false;
        }
        account.deposit(amount);
        return true;
    }

    public boolean withdraw(String accountNumber, long amount) {
        Account account = accounts.get(accountNumber);
        if (account == null || amount <= 0) {
            return false;
        }
        return account.withdraw(amount);
    }

    /**
     * 이체 처리.
     * 주의: 출금과 입금 사이에 실패가 발생하면 자금이 사라지는 문제가 있을 수 있다.
     * (BUGLOG.md의 BUG-001 참고 — 일부러 남겨둔 SM 실습용 이슈)
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, long amount) {
        Account from = accounts.get(fromAccountNumber);
        Account to = accounts.get(toAccountNumber);
        if (from == null || to == null || amount <= 0) {
            return false;
        }
        if (!from.withdraw(amount)) {
            return false;
        }
        to.deposit(amount);
        from.addHistory(String.format("[이체출금] %s 계좌로 %,d원 이체", toAccountNumber, amount));
        to.addHistory(String.format("[이체입금] %s 계좌에서 %,d원 입금", fromAccountNumber, amount));
        return true;
    }
}
