package Models;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private int createdBy;
    private List<Question> questions;
    
    public Quiz(String title, String description, int createdBy) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.questions = new ArrayList<>();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getCreatedBy() { return createdBy; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
