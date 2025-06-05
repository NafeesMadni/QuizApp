/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DBLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author HP
 */
public class DBConnector {
    private String URL;
    private String USER;
    private String PASSWORD;
    private Connection con;
    
    
    public DBConnector(){
        URL = "jdbc:mysql://localhost:3306/quizapp";
        USER = "root";
        PASSWORD = "@Nafees123";
        establish_connection();
    }
   
    public Connection getConnection() {
        return con;
    }
    
    private void establish_connection() {
        System.out.println("Connecting...");
        
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.\nError: "+e.toString());
            e.printStackTrace();
        }
    }
}
