package BANK;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String url = "jdbc:mysql://localhost:3306/Banking_system";
        String username = "root";
        String password = "tiger";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            BankingSystem bankingApp = new BankingSystem(connection);

            System.out.println("Welcome to the Banking System!");
            System.out.println("Are you an existing user? (yes/no)");
            String userChoice = sc.next();

            if (userChoice.equalsIgnoreCase("yes")) {
                System.out.println("Enter your account type (Saving/Current/loan): ");
                String accountType = sc.next();
                String table_name = accountType.toLowerCase() + "_account";
                String query = "";
                switch (accountType.toLowerCase()) {
                    case "saving":
                        query = "SELECT * FROM saving_account";
                        break;
                    case "current":
                        query = "SELECT * FROM current_account";
                        break;
                    case "loan":
                        query = "SELECT * FROM loan_account";
                        break;

                    default:
                        System.out.println("Invalid account type.");
                        return;
                }

                System.out.println("Enter your account number: ");
                String accNo = sc.next();
                BankAccount account = null;

                int attempts = 3; 
                while (attempts > 0 && account == null) {
                    account = bankingApp.getAccountSql(accNo, table_name, connection);

                    if (account != null) {

                        int index = bankingApp.findEmptyIndex(); 
                        if (index != -1) {
                            bankingApp.accounts[index] = account;
                          
                        } else {
                           
                        }
                        bankingApp.run();
                       
                    } else {
                        attempts--;
                        if (attempts > 0) {
                            System.out.println("Account not found. You have " + attempts + " attempt(s) left.");
                            System.out.println("Enter your account number again: ");
                            accNo = sc.next(); 
                        } else {
                            System.out.println("No more attempts left. Exiting.");
                        }
                    }
                }

            } else if (userChoice.equalsIgnoreCase("no")) {
     
                System.out.println("Creating a new account...");
                bankingApp.openAccount();
                bankingApp.run();
            } else {
                System.out.println("Invalid choice. Exiting the program.");
                return;
            }

            sc.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}