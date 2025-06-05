package UI;

import DBLayer.UserDAO;
import Models.User;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public LoginForm() {
        setTitle("Quiz App Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(4, 2, 10, 10));
        
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");
        
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> showSignupForm());
        
        add(loginButton);
        add(signupButton);
        
        setLocationRelativeTo(null);
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);
        
        if (user != null) {
            if (user.isTeacher()) {
                new TeacherDashboard(user.getId()).setVisible(true);
                this.dispose();
            } else {
                // Handle student login
                JOptionPane.showMessageDialog(this, "Student dashboard not implemented yet!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSignupForm() {
        new SignupForm().setVisible(true);
        this.dispose();
    }
}
