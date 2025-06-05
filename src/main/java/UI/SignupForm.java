package UI;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DBLayer.UserDAO;
import Models.User;

public class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox teacherCheckbox;
    
    public SignupForm() {
        setTitle("Quiz App Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(5, 2, 10, 10));
        
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);
        
        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        add(new JLabel("Is Teacher:"));
        teacherCheckbox = new JCheckBox();
        add(teacherCheckbox);
        
        JButton signupButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");
        
        signupButton.addActionListener(e -> handleSignup());
        backButton.addActionListener(e -> showLoginForm());
        
        add(signupButton);
        add(backButton);
        
        setLocationRelativeTo(null);
    }
    
    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        boolean isTeacher = teacherCheckbox.isSelected();
        
        User newUser = new User(username, password, isTeacher);
        UserDAO userDAO = new UserDAO();
        
        if (userDAO.createUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Signup successful!");
            showLoginForm();
        } else {
            JOptionPane.showMessageDialog(this, "Signup failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showLoginForm() {
        new LoginForm().setVisible(true);
        this.dispose();
    }
}
