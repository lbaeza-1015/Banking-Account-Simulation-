package Model;

/*
 * Transaction.java
 * An immutable record of a single money event. Once created, nothing changes.
 * Stores what happened (type), how much (amount), when (timestamp), and a note.
 * Every Account keeps a List<Transaction> as its full history.
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final String transactionId;
    private final String accountId;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(String transactionId, String accountId,
                       TransactionType type, double amount, String note) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.note = note;
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountId()     { return accountId; }
    public TransactionType getType() { return type; }
    public double getAmount()        { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote()          { return note; }

    @Override
    public String toString() {
        return "[" + timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "] "
                + type + " $" + String.format("%.2f", amount)
                + (note != null && !note.isEmpty() ? " — " + note : "");
    }
}
