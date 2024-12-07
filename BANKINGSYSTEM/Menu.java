package BANKINGSYSTEM;

import static BANKINGSYSTEM.LoginPage.DB_URL;
import static BANKINGSYSTEM.LoginPage.PASS;
import static BANKINGSYSTEM.LoginPage.USER;
import static BANKINGSYSTEM.LoginPage.pass_log;
import static BANKINGSYSTEM.LoginPage.user_log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class Menu {

    private static final Scanner sc = new Scanner(System.in);

    private static String displayBankMenu() throws InterruptedException {
        String format = " | %-29s |%n";
        StringBuilder s = new StringBuilder()
            .append("\n")
            .append(" +-------------------------------+\n")
            .append(" |           MAIN MENU           |\n")
            .append(" +-------------------------------+\n")
            .append(String.format(format, "[1] Account Info"))
            .append(String.format(format, "[2] Withdraw"))
            .append(String.format(format, "[3] Deposit"))
            .append(String.format(format, "[4] View Transaction History")) 
            .append(String.format(format, "[0] Exit"))
            .append(" +-------------------------------+\n");
        return s.toString();
    }

    public static void displayTradeGridBanner() {
        System.out.println("\n" +
                "╔═════════════════════════════════════════════════════════════════════════════╗\n" +
                "║                                                                             ║\n" +
                "║ ████████╗██████╗   █████╗  ██████╗  ███████╗  ██████╗ ██████╗  ██╗ ██████╗  ║\n" +
                "║ ╚══██╔══╝██╔══██╗ ██╔══██╗ ██╔══██╗ ██╔════╝ ██╔════╝ ██╔══██╗ ██║ ██╔══██╗ ║\n" +
                "║    ██║   ██████╔╝ ███████║ ██║  ██║ █████╗  ██╔╝  ███╗██████╔╝ ██║ ██║  ██║ ║\n" +
                "║    ██║   ██╔══██╗ ██╔══██║ ██║  ██║ ██╔══╝   ██╗   ██║██╔══██╗ ██║ ██║  ██║ ║\n" +
                "║    ██║   ██║  ██║ ██║  ██║ ██████╔╝ ███████╗  ██████╔╝██║  ██║ ██║ ██████╔╝ ║\n" +
                "║    ╚═╝   ╚═╝  ╚═╝ ╚═╝  ╚═╝ ╚═════╝  ╚══════╝  ╚═════╝ ╚═╝  ╚═╝ ╚═╝ ╚═════╝  ║\n" +
                "╚═════════════════════════════< Copyright @ Gab >═════════════════════════════╝\n" +
                "                                                                               ");
    }

    public static void mainMenu() throws InterruptedException {

        Menu.displayTradeGridBanner();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String mainQuery = "SELECT * FROM usersTable WHERE username = ?";
            try (PreparedStatement mainStmt = conn.prepareStatement(mainQuery)) {
                mainStmt.setString(1, user_log);
                ResultSet rs = mainStmt.executeQuery();

                if (rs.next()) {

                    String name = rs.getString("account_holder_name");
                    double balance = rs.getDouble("balance");

                    String format = " | %-29s |%n";
                    StringBuilder s = new StringBuilder()
                        .append(" +-------------------------------+\n")
                        .append(" |            WELCOME            |\n")
                        .append(" +-------------------------------+\n")
                        .append(String.format(format, "Name: " + name + "!"))
                        .append(String.format(format, "Balance: $" + balance))                  
                        .append(" +-------------------------------+");

                    System.out.println(s.toString());

                    boolean flow = true;

                    int ch;

                    do { 
                        System.out.println(Menu.displayBankMenu());
                        System.out.print("Enter your choice: ");
                        while (!sc.hasNextInt()) { 
                            System.out.println("Invalid input. Please enter a number.");
                            sc.next(); 
                            System.out.print("Enter your choice: ");
                        }
                        ch = sc.nextInt();
                        sc.nextLine(); 

                        switch (ch) {
                            case 1 -> { 
                                AccountInfo.accountInfo(pass_log, user_log);
                            }
                            case 2 -> { 
                                withdrawal(conn, user_log, balance);
                            }
                            case 3 -> { 
                                deposit(conn, user_log, balance);
                            }
                            case 4 -> {
                                viewTransactionHistory(conn, user_log);
                            }
                            case 0 -> {
                                System.out.println("Exiting...");
                                Thread.sleep(2000);
                                System.out.println("Program Terminated.");
                                System.exit(0);
                            }
                            default -> System.out.println("Invalid choice. Please try again.");
                        } 
                    } while (flow); 
                }

            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deposit(Connection conn, String user_log, double balance) throws InterruptedException {
        System.out.print("Enter deposit amount: ");
        double depositAmount;

        try {
            depositAmount = sc.nextDouble();
            sc.nextLine(); 

            if (depositAmount <= 0) {
                System.out.println("Deposit amount must be at least $1.");
                return;
            }

            
            double newBalance = balance + depositAmount;

            
            String updateQuery = "UPDATE usersTable SET balance = ? WHERE username = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setDouble(1, newBalance);
                updateStmt.setString(2, user_log);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    
                    int accountId = getAccountId(conn, user_log);
                    if (accountId != -1) {
                        
                        logTransaction(conn, accountId, "DEPOSIT", depositAmount);
                    }

                   
                    displayBalance(newBalance);

                    
                    System.out.println("[0] Back to Main Menu");
                    int ch = sc.nextInt();
                    sc.nextLine(); 
                    if (ch == 0) {
                        mainMenu();
                    }

                } else {
                    System.out.println("Failed to update balance. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during deposit: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void withdrawal(Connection conn, String user_log, double balance) throws InterruptedException {
        System.out.print("Enter withdrawal amount: ");
        double withdrawAmount;

        try {
            withdrawAmount = sc.nextDouble();
            sc.nextLine(); 

            if (withdrawAmount <= 0) {
                System.out.println("Withdrawal amount must be greater than $0.");
                return;
            }

            if (withdrawAmount > balance) {
                System.out.println("Insufficient balance for this withdrawal.");
                return;
            }

          
            double newBalanceW = balance - withdrawAmount;

           
            String updateQuery = "UPDATE usersTable SET balance = ? WHERE username = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setDouble(1, newBalanceW);
                updateStmt.setString(2, user_log);
                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                   
                    int accountId = getAccountId(conn, user_log);
                    if (accountId != -1) {
                        
                        logTransaction(conn, accountId, "withraw", withdrawAmount);
                    }

                   
                    displayBalance(newBalanceW);

                    
                    System.out.println("[0] Back to Main Menu");
                    int ch = sc.nextInt();
                    sc.nextLine();
                    if (ch == 0) {
                        mainMenu();
                    }

                } else {
                    System.out.println("Failed to update balance. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during withdrawal: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void logTransaction(Connection conn, int accountId, String transactionType, double amount) {
        String insertTransactionQuery = "INSERT INTO transactionTable (account_id, transaction_type, amount) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertTransactionQuery)) {
            insertStmt.setInt(1, accountId);
            insertStmt.setString(2, transactionType);
            insertStmt.setDouble(3, amount);
            insertStmt.executeUpdate();
            System.out.println("Transaction logged successfully.");
        } catch (SQLException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    private static int getAccountId(Connection conn, String username) {
        String query = "SELECT user_id FROM usersTable WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                System.out.println("User not found.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account ID: " + e.getMessage());
            return -1;
        }
    }

    private static void displayBalance(double balance) {
        String format = " | %-25s |%n";
        StringBuilder s = new StringBuilder()
            .append(" +---------------------------+\n")
            .append(" |      UPDATED BALANCE      |\n")
            .append(" +---------------------------+\n")
            .append(String.format(format, "Balance: $" + balance))                  
            .append(" +---------------------------+");
        System.out.println(s.toString());
    }

    private static void logTransaction(Connection conn, String user_log, String transactionType, double amount) {
        
        int accountId = getAccountId(conn, user_log);
        if (accountId != -1) {
            logTransaction(conn, accountId, transactionType, amount);
        }
    }

    private static void viewTransactionHistory(Connection conn, String user_log) {
        String query = """
            SELECT t.transaction_type, t.amount, t.date
            FROM transactionTable t
            JOIN usersTable u ON t.account_id = u.user_id
            WHERE u.username = ?
            ORDER BY t.date DESC;
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user_log);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nTransaction History:");
            System.out.println("+-------------------+----------+-------------------------+");
            System.out.println("| Type              | Amount   | Date                    |");
            System.out.println("+-------------------+----------+-------------------------+");

            while (rs.next()) {
                System.out.printf("| %-17s | %-8.2f | %-23s |\n",
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("date"));
            }
            System.out.println("+-------------------+----------+-------------------------+");
        } catch (SQLException e) {
            System.out.println("Error retrieving transaction history: " + e.getMessage());
        }
    }
}
