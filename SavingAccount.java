package BANK;
import java.sql.Connection;

class SavingAccount extends BankAccount {

    private final double interestRate;
     SavingAccount(Connection connection,String accno, String name, String Mpin, String password, long balance, double interestRate,String securityQuestion, String securityAnswer) {
        super(connection,accno, "Saving", name, Mpin, password, balance,securityQuestion,securityAnswer);
        this.interestRate = interestRate;
    }
    
    public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Interest Rate: " + interestRate);
        double updatedBalance = balance + (balance * interestRate);
        System.out.println("Balance before interest: " + balance);
        System.out.println("Balance after interest: " + updatedBalance);
        balance = (long) updatedBalance;
        updateBalanceInDatabase(accno, balance,acc_type);
    }
   
public String toFileString() {
    return super.toFileString() + String.format(",%f", interestRate);
}
public boolean search(String accno) {
    return this.getAccNo().equals(accno);
}
double getInterestRate(){
    return interestRate;
}

}

