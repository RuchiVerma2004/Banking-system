package BANK;
import java.sql.Connection;
class CurrentAccount extends BankAccount {
   
    private final  double overdraftLimit;
     CurrentAccount(Connection connection,String accno, String name, String Mpin, String password, long balance, double overdraftLimit,String securityQuestion, String securityAnswer) {
        super(connection,accno, "Current", name, Mpin, password, balance,securityQuestion, securityAnswer);
        this.overdraftLimit = overdraftLimit;
    }
    
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Overdraft Limit: " + overdraftLimit);
        if (balance < 0 && balance <= -overdraftLimit) {
            System.out.println("Warning: Balance is reaching the overdraft limit.");
        }
    }
    double getOverdraftLimit(){
        return overdraftLimit;
    }
   
public String toFileString() {
    return super.toFileString() + String.format(",%f", overdraftLimit);
}


public boolean search(String accno) {
    return this.getAccNo().equals(accno);
}
}