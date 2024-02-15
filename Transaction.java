package BANK;
import java.util.Date;

class Transaction {
    private String type;
    private Date timestamp;
    private long amount;
    Transaction(String type, long amount) {
        this.type = type;
        this.timestamp = new Date();
        this.amount = amount;
    }

    public String toString() {
        return "Type: " + type + " | Timestamp: " + timestamp + " | Amount: " + amount;
    }
}