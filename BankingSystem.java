package BANK;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class BankingSystem {
    private Connection connection;
    private static final int MAX_ACCOUNTS = 10;
    Scanner sc = new Scanner(System.in);
     BankAccount[] accounts;
    public BankingSystem(Connection connection) {
        this.connection = connection;
         accounts = new BankAccount[MAX_ACCOUNTS];
    }

    private static final String[] SECURITY_QUESTIONS = {
            "What is your mother's name?",
            "What is your favorite pet's name?",
            "In which city were you born?",
            "What is your favorite book?",
    };


    private boolean accountExists(String accNo) {
        for (BankAccount existingAccount : accounts) {
            if (existingAccount != null && existingAccount.getAccNo().equals(accNo)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 8 || password.length() > 16) {
            return false; 
        }
    
   
        if (!password.matches(".*[a-z  ].*")) {
            return false;
        }
    
        
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
    
        if (!password.matches(".*\\d.*")) {
            return false;
        }
    
        
        if (!password.matches(".*[@#$%^&+=].*")) {
            return false;
        }
    
        return true;
    }
    

    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    void openAccount() {
        try{
        System.out.println("Enter details for the customer:");
        System.out.print("Enter Account type (Saving/Current/Loan) : ");
        String acc_type = sc.next();

        String accno = generateAccountNumber();
            while (accountExists(accno)) {
                accno = generateAccountNumber();
            }
        System.out.print("Enter Name: ");
        String name = sc.next();
        System.out.print("Enter Mpin: ");
        String Mpin;
        do {
            try {
                System.out.print("Enter Mpin (4 digits only): ");
                Mpin = sc.next();
                if (Mpin.length() != 4 || !isNumeric(Mpin)) {
                    throw new IllegalArgumentException("MPIN must be exactly 4 digits and numeric.");
                }
                break; 
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        } while (true);

        System.out.println("\n** Password Requirements **\n" +
                "- Your password must be 8-16 characters long.\n" +
                "- It should include a combination of uppercase letters, lowercase letters, numbers, and special characters.\n" +
                "- Avoid using common words or phrases.\n" +
                "Please choose a strong and secure password to protect your account.\n" +
                "Password plays an essential part of keeping your financial information safe.\n");

        String password;
        do {
            System.out.print("Enter Password: ");
            password = sc.next();

            if (!isStrongPassword(password)) {
                System.out.println("Password does not meet the requirements. Please try again.");
            }
        } while (!isStrongPassword(password));

        System.out.print("Enter Balance: ");
        long balance = 0;
        do {
            try {
                balance = sc.nextLong();
                break; 
                
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid numeric value for balance.");
                sc.next();
            }
        } while (true);
        String securityQuestion;
        String securityAnswer;

        int questionChoice = new Random().nextInt(SECURITY_QUESTIONS.length);
        securityQuestion = SECURITY_QUESTIONS[questionChoice];
        System.out.print(securityQuestion);

        System.out.print("\nEnter Security Answer: ");
        securityAnswer = sc.next();
       
        BankAccount account = createAccount(acc_type, accno, name, Mpin, password, balance, securityQuestion, securityAnswer);
            int index = findEmptyIndex();
            if (index != -1) {
                accounts[index] = account;
                System.out.println("Account Number :"+accno);
                System.out.println("Account created successfully.");
               
            } else {
                System.out.println("Array is full. Cannot add more accounts.");
            }
        }
      
        catch(Exception e) {
            System.out.println("Caught an exception: " + e.getMessage());
        }
        
    for (BankAccount account : accounts) {
        if (account != null) {
           
            insertIntoTable(account);
   
        }
    }
    }

    private void insertIntoTable (BankAccount account) {
        if (account instanceof SavingAccount) {
            saving_account((SavingAccount) account);
            Utils.writeSavings();
 System.out.println("Account saved in SavingsFolder Successfully");
        } 
else if (account instanceof CurrentAccount) {
            current_account((CurrentAccount) account);
            Utils.writeCurrent();
 System.out.println("Account saved in CurrentFolder Successfully");
        } 
else if (account instanceof Loan) {
            loan_account((Loan) account);
            Utils.writeLoan();
 System.out.println("Account saved in LoanFolder Successfully");
        }
 }
    private void saving_account(SavingAccount savingAccount) {
        String sql = "INSERT INTO saving_account (accno, name, Mpin, password, balance, interestRate, securityQuestion, securityAnswer) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, savingAccount.getAccNo());
            preparedStatement.setString(2, savingAccount.getName());
            preparedStatement.setString(3, savingAccount.getMpin());
            preparedStatement.setString(4, savingAccount.getPassword());
            preparedStatement.setLong(5, savingAccount.getBalance());
            preparedStatement.setDouble(6, savingAccount.getInterestRate());
            preparedStatement.setString(7, savingAccount.getSecurityQuestion());
            preparedStatement.setString(8, savingAccount.getSecurityAnswer());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }

    private void current_account(CurrentAccount currentAccount) {
        String sql = "INSERT INTO current_account (accno, name, Mpin, password, balance, overdraftLimit, securityQuestion, securityAnswer) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, currentAccount.getAccNo());
            preparedStatement.setString(2, currentAccount.getName());
            preparedStatement.setString(3, currentAccount.getMpin());
            preparedStatement.setString(4, currentAccount.getPassword());
            preparedStatement.setLong(5, currentAccount.getBalance());
            preparedStatement.setDouble(6, currentAccount.getOverdraftLimit());
            preparedStatement.setString(7, currentAccount.getSecurityQuestion());
            preparedStatement.setString(8, currentAccount.getSecurityAnswer());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }

    private void loan_account(Loan loan) {
        String sql = "INSERT INTO loan_account (accno, name, Mpin, password, balance, loanAmount, interestRate, timeperiod, securityQuestion, securityAnswer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, loan.getAccNo());
            preparedStatement.setString(2, loan.getName());
            preparedStatement.setString(3, loan.getMpin());
            preparedStatement.setString(4, loan.getPassword());
            preparedStatement.setLong(5, loan.getBalance());
            preparedStatement.setLong(6, loan.getLoanAmount());
            preparedStatement.setDouble(7, loan.getInterestRate());
            preparedStatement.setInt(8, loan.getTimePeriod());
            preparedStatement.setString(9, loan.getSecurityQuestion());
            preparedStatement.setString(10, loan.getSecurityAnswer());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
          
        }
    }

    private BankAccount createAccount(String acc_type, String accno, String name, String Mpin, String password, long balance, String securityQuestion, String securityAnswer) {
        if ("Saving".equalsIgnoreCase(acc_type)) {
            double interestRate = 0.03;
            return new SavingAccount(connection,accno, name, Mpin, password, balance, interestRate, securityQuestion, securityAnswer);
        } else if ("Current".equalsIgnoreCase(acc_type)) {
            double overdraftLimit = 1000000.00;
            return new CurrentAccount(connection,accno, name, Mpin, password, balance, overdraftLimit, securityQuestion, securityAnswer);
        } else if ("Loan".equalsIgnoreCase(acc_type)) {
            // Customize this part based on your Loan class implementation
            System.out.print("Enter Loan Amount: ");
            long loanAmount = sc.nextLong();
            System.out.print("Enter time period till you want to take a loan (below 5 years): ");
            int timeperiod = sc.nextInt();
            double interestRate = 16;
            System.out.print("Loan Interest Rate on the " + loanAmount + " is " + interestRate +"\n");
            return new Loan(connection,accno, name, Mpin, password, balance, loanAmount, interestRate, timeperiod, securityQuestion, securityAnswer);
        } else {
            throw new IllegalArgumentException("Invalid account type: " + acc_type);
        }
    }

    int findEmptyIndex() {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] == null) {
                return i;
            }
        }
        return -1;
    }

    private String generateAccountNumber() {

        Random random = new Random();
        int accountNumber = 10000000 + random.nextInt(90000000);
        return Integer.toString(accountNumber);
    }

    void closeAccount(String accno) {
        try {
            String sql;
            String accType = null;
    
        
            BankAccount accountToRemove = null;
            for (BankAccount account : accounts) {
                if (account != null && account.getAccNo().equals(accno)) {
                    accountToRemove = account;
                    accType = account.getAccountType(); 
                    break;
                }
            }
    
    
            if (accountToRemove != null) {
                for (int i = 0; i < accounts.length; i++) {
                    if (accounts[i] != null && accounts[i].getAccNo().equals(accno)) {
                        accounts[i] = null;
                        break;
                    }
                }
    
            
                switch (accType) {
                    case "Saving":
                        sql = "DELETE FROM saving_account WHERE accno = ?";
                        break;
                    case "Current":
                        sql = "DELETE FROM current_account WHERE accno = ?";
                        break;
                    case "Loan":
                        sql = "DELETE FROM loan_account WHERE accno = ?";
                        break;
                    default:
                        System.out.println("Invalid account type. Account not closed.");
                        return;
                }
    
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, accno);
    
                    int rowsAffected = statement.executeUpdate();
    
                    if (rowsAffected > 0) {
                        System.out.println("Account " + accno + " successfully closed.");
                    } else {
                        System.out.println("Account not found in the database. Array updated, but database not modified.");
                    }
                }
            } else {
                System.out.println("Account not found. Account not closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     SavingAccount getSavingAccount(String accNo) {
        SavingAccount savingAccount = null;
        String query = "SELECT * FROM saving_account WHERE accno = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accNo);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    
                    String name = resultSet.getString("name");
                    String mpin = resultSet.getString("Mpin");
                    String password = resultSet.getString("password");
                    long balance = resultSet.getLong("balance");
                    double interestRate = resultSet.getDouble("interestRate");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    savingAccount = new SavingAccount(connection, accNo, name, mpin, password, balance, interestRate, securityQuestion, securityAnswer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return savingAccount;
    }
    public Loan getLoanAccount(String accNo) {
        Loan loanAccount = null;
        String query = "SELECT * FROM loan_account WHERE accno = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accNo);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                  
                    String name = resultSet.getString("name");
                    String mpin = resultSet.getString("Mpin");
                    String password = resultSet.getString("password");
                    long balance = resultSet.getLong("balance");
                    long loanAmount = resultSet.getLong("loanAmount");
                    double interestRate = resultSet.getDouble("interestRate");
                    int timePeriod = resultSet.getInt("timeperiod");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    loanAccount = new Loan(connection, accNo, name, mpin, password, balance, loanAmount, interestRate, timePeriod, securityQuestion, securityAnswer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    
        return loanAccount;
    }
    
    
    CurrentAccount getCurrentAccount(String accNo) {
        CurrentAccount currentAccount = null;
        String query = "SELECT * FROM current_account WHERE accno = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accNo);
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                   
                    String name = resultSet.getString("name");
                    String mpin = resultSet.getString("Mpin");
                    String password = resultSet.getString("password");
                    long balance = resultSet.getLong("balance");
                    double overdraftLimit = resultSet.getDouble("overdraftLimit");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    currentAccount = new CurrentAccount(connection,accNo, name, mpin, password, balance, overdraftLimit, securityQuestion, securityAnswer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    
        return currentAccount;
    }
    

    void run() {
        int choice;
        do {
            System.out.println("\n** Banking System Application **");
            System.out.println("1. Display account details");
            System.out.println("2. Deposit the amount");
            System.out.println("3. Withdraw the amount");
            System.out.println("4. Change MPIN");
            System.out.println("5. Pay Loan Installment");
            System.out.println("6. Account Transfer");
            System.out.println("7. Transaction History");
            System.out.println("8. Close Account");
            System.out.println("9. Customer Support");
            System.out.println("10. Exit");
            System.out.println("Press 1-10 to choose any option above");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter account no. to display all details: ");
                    String accNo = sc.next();
                    BankAccount account = getAccount(accNo);
                    if (account != null) {
                        account.showAccount();
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;
                case 2:
                    performDeposit();
                    break;
                case 3:
                    performWithdrawal();
                    break;
                case 4:
                    performMPINChange();
                    break;
                case 5:
                    performLoanInstallmentPayment();
                    break;
                case 6:
                    performAccountTransfer();
                    break;
                case 7:
                    performTransactionHistory();
                    break;
            
                case 8:
                    performAccountClosure();
                    break;
                case 9:
                    performCustomerSupport();
                    break;
                case 10:
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    break;
            }
        } while (choice != 10);

        sc.close();
    }

     void performDeposit() {
        System.out.print("Enter Account no.: ");
        String accNo = sc.next();
        BankAccount account = getAccount(accNo);
        if (account != null) {
            account.deposit();
             
        } else {
            System.out.println("Account not found.");
        }
    }

     void performWithdrawal() {
        System.out.print("Enter Account No.: ");
        String accNo = sc.next();
        BankAccount account = getAccount(accNo);
        if (account != null) {
            account.withdrawal();
        
        } else {
            System.out.println("Account not found.");
        }
    }

    private void performMPINChange() {
        System.out.print("Enter account no. for MPIN change: ");
        String mpinChangeAccNo = sc.next();
        BankAccount account = getAccount(mpinChangeAccNo);
        if (account != null) {
            account.pinChange();
        } else {
            System.out.println("Account not found for MPIN change.");
        }
    }

    private void performLoanInstallmentPayment() {
        System.out.print("Enter account no. for loan installment payment: ");
        String loanAccNo = sc.next();
        BankAccount account = getAccount(loanAccNo);
        if (account != null && account instanceof Loan) {
            ((Loan) account).payInstallment();
        } else {
            System.out.println("Loan account not found.");
        }
    }
    public BankAccount getAccountSql(String accNo, String tableName, Connection connection) {
        BankAccount account = null;
        try {
            String query = "SELECT * FROM " + tableName + " WHERE accno = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accNo);
    
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                String accountNo = resultSet.getString("accno");
                String name = resultSet.getString("name");
                String mpin = resultSet.getString("Mpin");
                String password = resultSet.getString("password");
                long balance = resultSet.getLong("balance");
 
                if (tableName.equals("saving_account")) {
                    double interestRate = resultSet.getDouble("interestRate");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    account = new SavingAccount(connection,accountNo, name, mpin, password, balance, interestRate, securityQuestion, securityAnswer);
                } else if (tableName.equals("current_account")) {
                    double overdraftLimit = resultSet.getDouble("overdraftLimit");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    account = new CurrentAccount(connection,accountNo, name, mpin, password, balance, overdraftLimit, securityQuestion, securityAnswer);
                } else if (tableName.equals("loan_account")) {
                    long loanAmount = resultSet.getLong("loanAmount");
                    double interestRate = resultSet.getDouble("interestRate");
                    int timePeriod = resultSet.getInt("timeperiod");
                    String securityQuestion = resultSet.getString("securityQuestion");
                    String securityAnswer = resultSet.getString("securityAnswer");
    
                    account = new Loan(connection,accountNo, name, mpin, password, balance, loanAmount, interestRate, timePeriod, securityQuestion, securityAnswer);
                }
            }
    
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    void performAccountTransfer() {
        try {
            System.out.print("Enter your account no. for the transfer: ");
            String senderAccNo = sc.next();
            BankAccount sender = getAccount(senderAccNo);
    
            if (sender != null) {
                System.out.print("Enter recipient's account no.: ");
                String recipientAccNo = sc.next();
    
                System.out.print("Enter recipient's account TYPE (saving/current/loan): ");
                String recipientAccType = sc.next().toLowerCase();
    
                BankAccount recipient = null;
    
                switch (recipientAccType) {
                    case "saving":
                        recipient = getSavingAccount(recipientAccNo);
                        break;
                    case "current":
                        recipient = getCurrentAccount(recipientAccNo);
                        break;
                    case "loan":
                        recipient = getLoanAccount(recipientAccNo);
                        break;
                    default:
                        System.out.println("Invalid account type: " + recipientAccType);
                        break;
                }
    
                if (recipient != null && !recipient.isAccountLocked()) {
                    System.out.print("Enter the amount to transfer: ");
                    long transferAmount = sc.nextLong();
    
                    if (transferAmount > 0) {
                        sender.transfer(recipient, transferAmount);
                        System.out.println("Transfer successful!");
                    } else {
                        System.out.println("Invalid transfer amount. Please enter a positive amount.");
                    }
                } else {
                    System.out.println("Recipient account not found, account type invalid, or account is locked.");
                }
            } else {
                System.out.println("Sender account not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid account numbers and a valid transfer amount.");
            sc.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private void performTransactionHistory() {
    try {
        System.out.print("Enter account no. for transaction history: ");
        String accNoForHistory = sc.next();

        BankAccount account = getAccount(accNoForHistory);
        if (account != null) {
            account.showTransactionHistory();
        } else {
            System.out.println("No account found with the provided account number.");
        }
    } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid account number.");
        sc.nextLine();
    } catch (Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
    }
}

    private void performAccountClosure() {
        System.out.print("Enter Account No. to close: ");
        String accNoToClose = sc.next();
        closeAccount(accNoToClose);
    }

    private void performCustomerSupport() {
        System.out.print("Enter Account No. to open a locked account: ");
        String accNoToOpen = sc.next();
        BankAccount account = getAccount(accNoToOpen);
        if (account != null && account.isAccountLocked()) {
            System.out.println("Account is locked. To open, answer the security question.");
            System.out.print("Security Question: " + account.securityQuestion + "\nEnter Answer: ");
            String answer = sc.next();

            if (account.verifySecurity(account.securityQuestion, answer)) {
                account.isLocked = false;
                System.out.println("Account successfully unlocked.");
            } else {
                System.out.println("Incorrect security answer. Account remains locked.");
            }
        } else {
            System.out.println("Account not found or is not locked.");
        }
    }

    private BankAccount getAccount(String accNo) {
        for (BankAccount existingAccount : accounts) {
            if (existingAccount != null && existingAccount.search(accNo)) {
                return existingAccount;
            }
        }
        return null;
    }
}