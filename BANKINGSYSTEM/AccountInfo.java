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

public class AccountInfo {

    public static Scanner sc = new Scanner(System.in);
    public static boolean flow = true;
    public static StringBuilder str = new StringBuilder();

    private static String displayUserInfMenu() throws InterruptedException {
        String format = " | %-29s |%n";
        @SuppressWarnings("ReplaceStringBufferByString")
        StringBuilder s = new StringBuilder()
            .append(" |          ACOUNT MENU          |\n")
            .append(" +-------------------------------+\n")
            .append(String.format(format, "[1] Change Password"))
            .append(String.format(format, "[2] Change Username"))
            .append(String.format(format, "[3] Change Name"))
            .append(String.format(format, "[0] Back"))
            .append(" +-------------------------------+\n");
        return s.toString();
    }

    
    
    public static void accountInfo(String password, String username) throws InterruptedException {

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
        String infoQuery = "SELECT * FROM usersTable WHERE username = ?";
            try(PreparedStatement stmt = conn.prepareStatement(infoQuery)) {
                stmt.setString(1, user_log);
                ResultSet rs1 = stmt.executeQuery();

                if (rs1.next()) {
                String name = rs1.getString("account_holder_name");
                String currBal = rs1.getString("balance");
                String username1 = rs1.getString("username");


                String format = " | %-29s |%n";
                @SuppressWarnings("ReplaceStringBufferByString")
                StringBuilder s = new StringBuilder()
                    .append(" +-------------------------------+\n")
                    .append(" |          ACCOUNT INFO         |\n")
                    .append(" +-------------------------------+\n")
                    .append(String.format(format, "Name: " + name + "!"))
                    .append(String.format(format, "Balance: $" + currBal))
                    .append(String.format(format, "Username: " + username1))                   
                    .append(" +-------------------------------+");
                
                System.out.println(s.toString());
                
            }

                int choose;

                System.out.println(displayUserInfMenu());
                System.out.println("Enter your Choice: ");
                choose = sc.nextInt();

                do {
                switch (choose) {
                    case 1 -> {
                        AccountInfo.changePassword(conn, user_log, pass_log);
                        flow = false;
                    }

                    case 2 -> {
                        AccountInfo.changeUsername(conn, user_log);
                        flow = false;
                    }

                    case 3 -> {
                        AccountInfo.changeName(conn, user_log);
                        flow = false;
                    }

                    case 4 -> {
                        
                    }

                    case 0 -> {
                        Menu.mainMenu();
                    }

                    default -> System.out.println("Invalid choice. "); 
                }


            } while (flow);

            
            }
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

        //change password
        private static void changePassword(Connection conn, String user_log, String pass_log) {
        
            try (Scanner sc = new Scanner(System.in)) {

            System.out.print("Enter your new password: ");
            String newPass = sc.nextLine(); 
    
        
            String passQuery = "UPDATE usersTable SET password = ? WHERE username = ?";
    
            try (PreparedStatement stmt = conn.prepareStatement(passQuery)) {
            stmt.setString(1, newPass);
            stmt.setString(2, user_log);
    
            
            int rowsAffected = stmt.executeUpdate();
    
                if (rowsAffected > 0) {
                        System.out.println("Password Changed Successfully.");
                    } else {
                        System.out.println("Cannot change password. Username not found.");
                    }

            } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            }
        }
        } 

        //change username
        private static void changeUsername(Connection conn, String user_log) {
        
            Scanner sc = new Scanner(System.in); 
    
            System.out.print("Enter your new username: ");
            String newUsername = sc.nextLine(); 
        
            
            String userQuery = "UPDATE usersTable SET username = ? WHERE username = ?";
        
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, newUsername);
                stmt.setString(2, user_log);
        
               
                int rowsAffected = stmt.executeUpdate();
        
                if (rowsAffected > 0) {
                    System.out.println("Username Changed Successfully.");
                    } else {
                    System.out.println("Cannot change username. Username not found.");
                    }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
    
        } 

        //change name
        private static void changeName(Connection conn, String user_log) {
        
            try (Scanner sc = new Scanner(System.in)) {
    
            System.out.print("Enter your new name: ");
            String newName = sc.nextLine(); 
        
            
            String userQuery = "UPDATE usersTable SET account_holder_name = ? WHERE username = ?";
        
            try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                stmt.setString(1, newName);
                stmt.setString(2, user_log);
        
                
                int rowsAffected = stmt.executeUpdate();
        
                if (rowsAffected > 0) {
                    System.out.println("Name Changed Successfully.");
                    } else {
                    System.out.println("Cannot change name. Username not found.");
                    }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
            }
    
        }   
    }
