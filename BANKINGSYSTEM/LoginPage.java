package  BANKINGSYSTEM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;



public class LoginPage {
    public static String DB_URL = "jdbc:mysql://localhost:3306/MyBank";
    public static String USER = "[your database username]";
    public static String PASS = "[your database username]";
    public static String user_log;
    public static String pass_log;
    public static java.util.Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, InterruptedException {

        String Choose;
        boolean flow = true;

        do {
        
        System.out.println("WELCOME TO TRADEGRID BANK! ");
        System.out.println("Do you have an account? (y/n): ");
        Choose = sc.nextLine().toLowerCase();

        switch (Choose) {
            case "y" -> { 
                Login.login();
                flow = false;
            }

            case "n" -> {
                 Register.register();
                 flow = false;
            }

            default -> System.out.println("Invalid choice. Please enter 'y' or 'n'.");
        }
        } while (flow);

    }

    class Login {
        public static void login() throws SQLException, InterruptedException {

            boolean flow = true;

            do {
            System.out.println("Enter username: ");
            user_log = sc.nextLine();

            System.out.println("Enter password: ");
            pass_log = sc.nextLine();

            


            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String query = "SELECT * FROM usersTable WHERE username = ? AND password = ?";
                try (PreparedStatement logStatement = conn.prepareStatement(query)) {
                logStatement.setString(1, user_log);
                logStatement.setString(2, pass_log);
                ResultSet rs = logStatement.executeQuery();

                
                
                    if (rs.next()) {
                        System.out.println("Login Successful.");
                        Menu.mainMenu();
                        flow = false;
                    } else {
                        System.out.println("Invalid username or password.");
                        
                    }
                
            }
                
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (flow);
        }

    }

    class Register {
        public static void register() throws InterruptedException {

            System.out.println("Enter desired username: ");
            String reg_user = sc.nextLine();

            System.out.println("Enter desired password: ");
            String reg_pass = sc.nextLine();

            System.out.println("Enter desired name: ");
            String reg_name = sc.nextLine();

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)){
                String query = "INSERT INTO usersTable (username, password, account_holder_name) VALUES (?, ?, ?)";
                try (PreparedStatement regPreparedStatement = conn.prepareStatement(query)) {
                regPreparedStatement.setString(1, reg_user);
                regPreparedStatement.setString(2, reg_pass);
                regPreparedStatement.setString(3, reg_name);
                int rowsAffected = regPreparedStatement.executeUpdate();

                    if (rowsAffected > 0 ) {
                        System.out.println("Registered Successfully. ");
                        Login.login();
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }


        }
    }
}
