package BANK;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.sql.Connection;

import java.util.Scanner;
class BankAccount implements Displayable {

    private Connection connection;
    String accno;
    private final String name;
    protected final  String acc_type;
    long balance;
    private  String Mpin;
     String securityQuestion;
    private final String securityAnswer;
    private final String password;
     boolean isLocked;
    private final Transaction[] transactionHistory;
    Scanner sc = new Scanner(System.in);
     BankAccount( Connection connection,String accno, String acc_type, String name, String Mpin, String password, long balance,String securityQuestion, String securityAnswer) {
        this.accno = accno;
        this.name = name;
        this.acc_type = acc_type;
        this.Mpin = Mpin;
        this.password = password;
        this.balance = balance;
        this.isLocked = false;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
         this.transactionHistory = new Transaction[100];
        this.connection =connection;
    }
     String getAccNo() {
        return accno;
    }
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%d,%s,%s",
                accno, acc_type, name, Mpin, password, balance, securityQuestion, securityAnswer);
    }
    String getSecurityAnswer(){
        return securityAnswer;
    }
    String getSecurityQuestion(){
        return securityQuestion;
    }

    String getAccountType() {
        return acc_type;
    }
    String getName()
{
    return name;
}

  String getMpin(){
    return Mpin;
  }
  String getPassword(){
    return password;
  }
  long getBalance(){
    return balance;
  }

     void showAccount() {


        if (isLocked) {
            System.out.println("Account is locked. Contact customer support.");
            return;
        }

        int remainingAttempts = 3;

        while (remainingAttempts > 0) {
            System.out.print("Enter Password of account " + accno + ": ");
            String pswrd = sc.next();
            if (pswrd.equals(password)) {
                remainingAttempts = 3;
                displayAccountInfo();
                break;
            } else {
                remainingAttempts--;
                if (remainingAttempts > 0) {
                    System.out.println("Incorrect Password! Try again.");
                    System.out.println("You have " + remainingAttempts + " Login Attempt(s) left");
                } else {
                    System.out.println("Incorrect Password! Account locked.");
                    isLocked = true;
                }
            }
        }
    }
    boolean verifySecurity(String question, String answer) {
        return securityQuestion.equals(question) && securityAnswer.equals(answer);
    }
    public void displayAccountInfo() {
        System.out.println("Name of account holder: " + name);
        System.out.println("Account no.: " + accno);
        System.out.println("Account type: " + acc_type);
        System.out.println("Balance: " + balance);
    }

public boolean search(String accno) {
    return this.getAccNo().equals(accno);
}


    void deposit() {
        System.out.println("For security reasons, you have to enter your Mpin: ");
        String pin = sc.next();
        if (pin.equals(Mpin) && !isLocked) {
            System.out.println("Enter the amount you want to deposit: ");
            long amt = sc.nextLong();
            System.out.println("Balance before deposit: " + balance);
            if (amt > 0) {
                balance += amt;
                updateBalanceInDatabase(accno, balance,acc_type);

                if ("saving".equalsIgnoreCase(acc_type)) {
                    Utils.writeSavings();
                    System.out.println("Balance updated in SavingsFolder");
                } else if ("current".equalsIgnoreCase(acc_type)) {
                    Utils.writeCurrent();
                     System.out.println("Balance updated in CurrentFolder");
                } else if ("loan".equalsIgnoreCase(acc_type)) {
                    Utils.writeLoan();
                     System.out.println("Balance updated in LoanFolder");
                } else {
                    System.out.println("Invalid account type: " + acc_type);
                }
              
                int index = 0;
                while (index < transactionHistory.length && transactionHistory[index] != null) {
                    index++;
                }

               
                if (index < transactionHistory.length) {
                    Transaction depositTransaction = new Transaction("DEPOSIT", amt);
                    transactionHistory[index] = depositTransaction;
                    System.out.println("Balance after deposit: " + balance);
                } else {
                    System.out.println("Transaction history is full. Unable to add deposit transaction.");
                }
            } else {
                System.out.println("Invalid deposit amount.");
            }
        } else {
            System.out.println("Your Mpin is incorrect or the account is locked! Please try again.");
        }
    }
     void updateBalanceInDatabase(String accountNumber, long newBalance, String accountType) {
        
    
        String updateBalanceQuery;
    
        switch (accountType.toLowerCase()) {
            case "saving":
                updateBalanceQuery = "UPDATE saving_account SET balance = ? WHERE accno = ?";
                break;
            case "current":
                updateBalanceQuery = "UPDATE current_account SET balance = ? WHERE accno = ?";
                break;
            case "loan":
                updateBalanceQuery = "UPDATE loan_account SET balance = ? WHERE accno = ?";
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateBalanceQuery)) {
            preparedStatement.setLong(1, newBalance);
            preparedStatement.setString(2, accountNumber);
    
            int rowsAffected = preparedStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Balance updated in the database. New balance: " + newBalance);
            } else {
                System.out.println("No rows were updated in the database. Please check the account number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }
    

    void showTransactionHistory() {
        if (transactionHistory.length == 0) {
            System.out.println("No transactions found for this account.");
        } else {
            System.out.println("Transaction History for Account " + getAccNo() + ":");
            for (Transaction transaction : transactionHistory) {
                if(transaction != null){
                System.out.println(transaction);}
                else{
                break;  
                }
            }
           
        }
    }
    void withdrawal() {
        System.out.println("For security reasons, you have to enter your Mpin: ");
        String pin = sc.next();
        if (pin.equals(Mpin) && !isLocked) {
            System.out.println("Enter the amount you want to withdraw: ");
            long amt = sc.nextLong();
            System.out.println("Balance before withdrawal: " + balance);
            if (amt > 0 && amt <= balance) {
                balance -= amt;
                updateBalanceInDatabase(accno, balance,acc_type);
                 
               if ("saving".equalsIgnoreCase(acc_type)) {
                    Utils.writeSavings();
                    System.out.println("Balance updated in SavingsFolder");
                } else if ("current".equalsIgnoreCase(acc_type)) {
                    Utils.writeCurrent();
                     System.out.println("Balance updated in CurrentFolder");
                } else if ("loan".equalsIgnoreCase(acc_type)) {
                    Utils.writeLoan();
                     System.out.println("Balance updated in LoanFolder");
                } else {
                    System.out.println("Invalid account type: " + acc_type);
                }
              
                int index = 0;
                while (index < transactionHistory.length && transactionHistory[index] != null) {
                    index++;
                }

          
                if (index < transactionHistory.length) {
                    Transaction withdrawalTransaction = new Transaction("WITHDRAWAL", amt);
                    transactionHistory[index] = withdrawalTransaction;
                    System.out.println("Balance after withdrawal: " + balance);
                } else {
                    System.out.println("Transaction history is full. Unable to add withdrawal transaction.");
                }
            } else {
                System.out.println("Invalid withdrawal amount or insufficient balance.");
            }
        } else {
            System.out.println("Your Mpin is incorrect or the account is locked! Please try again.");
        }
    }

   void transfer(BankAccount recipient, long amount) {
        System.out.println("For security, please enter your Mpin: ");
        String pin = sc.next();
        if (pin.equals(Mpin) && !isLocked) {
            if (balance >= amount && amount > 0) { 
                balance -= amount;
                recipient.balance += amount;
    
                updateBalanceInDatabase(accno, balance, acc_type);
               if ("saving".equalsIgnoreCase(acc_type)) {
                    Utils.writeSavings();
                    System.out.println("Accountant Balance updated in SavingsFolder");
                } else if ("current".equalsIgnoreCase(acc_type)) {
                    Utils.writeCurrent();
                     System.out.println(" Accountant Balance updated in CurrentFolder");
                } else if ("loan".equalsIgnoreCase(acc_type)) {
                    Utils.writeLoan();
                     System.out.println("Accountant Balance updated in LoanFolder");
                } else {
                    System.out.println("Invalid account type: " + acc_type);
                }

                recipient.updateBalanceInDatabase(recipient.accno, recipient.balance, recipient.acc_type);
               if ("saving".equalsIgnoreCase(recipient.acc_type)) {
                    Utils.writeSavings();
                    System.out.println("Recipient Balance updated in SavingsFolder");
                } else if ("current".equalsIgnoreCase(recipient.acc_type)) {
                    Utils.writeCurrent();
                     System.out.println("Recipient Balance updated in CurrentFolder");
                } else if ("loan".equalsIgnoreCase(recipient.acc_type)) {
                    Utils.writeLoan();
                     System.out.println("Recipient Balance updated in LoanFolder");
                } else {
                    System.out.println("Invalid account type: " + acc_type);
                }

    
                addTransaction("TRANSFER_SENT", amount);
                recipient.addTransaction("TRANSFER_RECEIVED", amount);
    
                
            } else {
                System.out.println("Invalid transfer amount or insufficient balance.");
            }
        } else {
            System.out.println("Your Mpin is incorrect or the account is locked! Please try again.");
        }
    }
    
    
    private void addTransaction(String type, long amount) {
        int index = 0;
        while (index < transactionHistory.length && transactionHistory[index] != null) {
            index++;
        }
    
        if (index < transactionHistory.length) {
            Transaction transaction = new Transaction(type, amount);
            transactionHistory[index] = transaction;
        } else {
            System.out.println("Transaction history is full. Unable to add transactions.");
        }
    }

    void pinChange() {

        System.out.print("Enter old Mpin: ");
        String oldMpin = sc.next();
        if (oldMpin.equals(Mpin) && !isLocked) {
            System.out.print("Enter new Mpin: ");
            String newMpin = sc.next();
            Mpin = newMpin;
            updateMpinInDatabase(accno,newMpin, acc_type);
            System.out.println("Mpin updated");

            if ("saving".equalsIgnoreCase(acc_type)) {
                    Utils.writeSavings();
                    System.out.println("Mpin updated in SavingsFolder");
                } else if ("current".equalsIgnoreCase(acc_type)) {
                    Utils.writeCurrent();
                     System.out.println("Mpin updated in CurrentFolder");
                } else if ("loan".equalsIgnoreCase(acc_type)) {
                    Utils.writeLoan();
                     System.out.println("Mpin updated in LoanFolder");
                } else {
                    System.out.println("Invalid account type: " + acc_type);
                }
        } else {
            System.out.println("Wrong Mpin or the account is locked! Mpin not updated.");
        }
    }
     boolean isAccountLocked() {
        return isLocked;
    }
    void updateMpinInDatabase(String accountNumber, String newMpin, String accountType) {
        String updateMpinQuery;

        switch (accountType.toLowerCase()) {
            case "saving":
                updateMpinQuery = "UPDATE saving_account SET mpin = ? WHERE accno = ?";
                break;
            case "current":
                updateMpinQuery = "UPDATE current_account SET mpin = ? WHERE accno = ?";
                break;
            case "loan":
                updateMpinQuery = "UPDATE loan_account SET mpin = ? WHERE accno = ?";
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateMpinQuery)) {
            preparedStatement.setString(1, newMpin);
            preparedStatement.setString(2, accountNumber);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(rowsAffected + " rows updated in the database. MPIN changed.");
            } else {
                System.out.println("No rows were updated in the database. Please check the account number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
           
        }
    }
   }


