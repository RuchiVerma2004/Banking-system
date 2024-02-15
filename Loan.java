package BANK;
import java.sql.Connection;
import java.util.InputMismatchException;
class Loan extends BankAccount {
    private long loanAmount;
    private final  double interestRate;
    private  int timeperiod;
     Loan(Connection connection,String accno, String name, String Mpin, String password, long balance, long loanAmount, double interestRate, int timeperiod,String securityQuestion, String securityAnswer) {
        super(connection,accno, "Loan", name, Mpin, password, balance,securityQuestion,  securityAnswer);
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.timeperiod = timeperiod;
    }
  
     public void displayAccountInfo() {
        super.displayAccountInfo();
        System.out.println("Loan Amount: " + loanAmount);
        System.out.println("Interest Rate in percent: " + interestRate+ "%");
        System.out.println("Time Period in years : " + timeperiod);
    }
    int getTimePeriod(){
        return timeperiod;
    }
    double getInterestRate(){
        return interestRate;
    }
    long getLoanAmount(){
        return loanAmount;
    }
  
public String toFileString() {
    return super.toFileString() + String.format(",%d,%f,%d", loanAmount, interestRate, timeperiod);
}
public boolean search(String accno) {
    return this.getAccNo().equals(accno);
}

    void payInstallment() {
        if (isLocked) {
            System.out.println("Loan account is locked. Contact customer support.");
            return;
        }
       
        System.out.println("Loan Amount: " + loanAmount);
        System.out.println("Remaining Balance to be paid : " + loanAmount);
        try{
        System.out.print("Enter the installment amount: ");
        long installment = sc.nextLong();

        if (installment > 0 && balance >= installment && loanAmount >= installment && timeperiod <5)  {
            balance -= installment;
            updateBalanceInDatabase(accno, balance,acc_type);
            loanAmount -= installment;
        
            System.out.println("Installment paid successfully.");
            System.out.println("Balance left  in the account after installment: " + balance);
            double loanwithinterest = (loanAmount*interestRate*timeperiod)/100;
           loanAmount += loanwithinterest;
           updateBalanceInDatabase(accno, loanAmount,acc_type);
            System.out.println("Remaining loanAmount(with interest) "+interestRate+" to be paid after the installment  : " + (loanAmount));
            if (loanAmount == 0) {
                System.out.println("Loan fully paid off.");
                isLocked = true; // Lock the loan account when fully paid.
            }
        } else {
            System.out.println("Invalid installment amount or insufficient balance.");
        }
        
    }catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid numeric value.");
        sc.nextLine(); 
    }
}
}
