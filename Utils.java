package BANK;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Utils{
		
public static void writeSavings() {
	String url = "jdbc:mysql://localhost:3306/Banking_system";
    String username = "root";
    String password = "tiger";
    	try {
    		Connection con = DriverManager.getConnection(url, username, password);
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery("Select * from saving_account");
    		
    		while(rs.next()) {
    			String account = rs.getString("accno");
    			String name = rs.getString("name");
    			String mpin = rs.getString("Mpin");
    			String pass = rs.getString("password");
    			long balance = rs.getLong("balance");
    			double interestrate = rs.getDouble("interestRate");
    			
    			
                String folderPath = "SavingsFolder";
                
                new File(folderPath).mkdirs();
               
                String fileName = folderPath + "/account_" + account + ".txt";

    		
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Account Number,Name,MPin,Password,Balance,Interest Rate");
    		
            bufferedWriter.newLine();
            bufferedWriter.write(account + "," + name + "," + mpin + "," + pass + "," + balance + "," + interestrate);

            bufferedWriter.close();
            fileWriter.close();
            
          
    		}
    		
    	}catch(SQLException | IOException e) {
    		e.printStackTrace();
    	}
    }


public static void writeCurrent() {
	String url = "jdbc:mysql://localhost:3306/Banking_system";
    String username = "root";
    String password = "tiger";
    	try {
    		Connection con = DriverManager.getConnection(url, username, password);
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery("Select * from current_account");
    		
    		while(rs.next()) {
    			String account = rs.getString("accno");
    			String name = rs.getString("name");
    			String mpin = rs.getString("Mpin");
    			String pass = rs.getString("password");
    			long balance = rs.getLong("balance");
    			double overdraftLimit = rs.getDouble("overdraftLimit");
    			
                String folderPath = "CurrentFolder";
                
                new File(folderPath).mkdirs();
               
                String fileName = folderPath + "/account_" + account + ".txt";

    		
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
        
            bufferedWriter.write("Account Number,Name,MPin,Password,Balance,Overdraft Limit");
    		
            bufferedWriter.newLine();
            bufferedWriter.write(account + "," + name + "," + mpin + "," + pass + "," + balance + "," + overdraftLimit);

            bufferedWriter.close();
            fileWriter.close();
            
          
    		}
    		
    	}catch(SQLException | IOException e) {
    		e.printStackTrace();
    	}
    }

public static void writeLoan() {
	String url = "jdbc:mysql://localhost:3306/Banking_system";
    String username = "root";
    String password = "tiger";
    	try {
    		Connection con = DriverManager.getConnection(url, username, password);
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery("Select * from loan_account");
    		
    		while(rs.next()) {
    			String account = rs.getString("accno");
    			String name = rs.getString("name");
    			String mpin = rs.getString("Mpin");
    			String pass = rs.getString("password");
    			long balance = rs.getLong("balance");
    			long loanAmount = rs.getLong("loanAmount");
    			double interestrate = rs.getDouble("interestRate");
    			int time = rs.getInt("timeperiod");
    			
    	
                String folderPath = "LoanFolder";
           
                new File(folderPath).mkdirs();
            
                String fileName = folderPath + "/account_" + account + ".txt";

    		
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
       
            bufferedWriter.write("Account Number,Name,MPin,Password,Balance,Loan Amount,Interest Rate,Time Period");
    		
            bufferedWriter.newLine();
            bufferedWriter.write(account + "," + name + "," + mpin + "," + pass + "," + balance + "," + loanAmount + "," + interestrate + "," + time);

            bufferedWriter.close();
            fileWriter.close();
            
            
    		}
    		
    	}catch(SQLException | IOException e) {
    		e.printStackTrace();
    	}
    }
}