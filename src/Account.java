import java.util.ArrayList;
import java.util.List;

/** 계좌 정보를 담는 모델 클래스 */
public class Account {
    private final String accountNumber;
    private final String ownerName;
    private long balance; // 원 단위
    private final List<String> history = new ArrayList<>();

    public Account(String accountNumber, String ownerName, long initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        history.add(String.format("[개설] 초기 입금액 %,d원", initialBalance));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount) {
        balance += amount;
        history.add(String.format("[입금] %,d원 (잔액 %,d원)", amount, balance));
    }

    /** 출금 성공 시 true, 잔액 부족 시 false 반환 */
    public boolean withdraw(long amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        history.add(String.format("[출금] %,d원 (잔액 %,d원)", amount, balance));
        return true;
    }

    public void addHistory(String message) {
        history.add(message);
    }

    public List<String> getHistory() {
        return history;
    }
}
