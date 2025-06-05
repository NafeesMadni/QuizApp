package Models;

public class User {
    private int id;
    private String username;
    private String password;
    private boolean isTeacher;
    
    public User(String username, String password, boolean isTeacher) {
        this.username = username;
        this.password = password;
        this.isTeacher = isTeacher;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isTeacher() { return isTeacher; }
}
