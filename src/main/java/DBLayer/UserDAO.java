package DBLayer;

import Models.User;
import java.sql.*;

public class UserDAO {
    DBConnector conObj;
    
    public UserDAO() {
        conObj = new DBConnector();
    }

    public boolean createUser(User user) {
        
        try (Connection conn = conObj.getConnection()) {
            String sql = "INSERT INTO users (username, password, is_teacher) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // In production, hash the password
            stmt.setBoolean(3, user.isTeacher());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public User validateUser(String username, String password) {
        try (Connection conn = conObj.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // In production, hash the password
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User(username, password, rs.getBoolean("is_teacher"));
                user.setId(rs.getInt("id"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
