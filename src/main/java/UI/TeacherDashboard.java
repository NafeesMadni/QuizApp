package UI;

import DBLayer.QuizDAO;
import Models.Quiz;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private JList<Quiz> quizList;
    private DefaultListModel<Quiz> listModel;
    private final int userId;
    private QuizDAO quizDAO;
    
    public TeacherDashboard(int userId) {
        this.userId = userId;
        this.quizDAO = new QuizDAO();
        
        setTitle("Teacher Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        
        // Create components
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Quiz");
        JButton deleteButton = new JButton("Delete Quiz");
        JButton refreshButton = new JButton("Refresh");
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Setup quiz list
        listModel = new DefaultListModel<>();
        quizList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(quizList);
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddQuizDialog());
        deleteButton.addActionListener(e -> deleteSelectedQuiz());
        refreshButton.addActionListener(e -> refreshQuizList());
        
        refreshQuizList();
        setLocationRelativeTo(null);
    }
    
    private void refreshQuizList() {
        listModel.clear();
        List<Quiz> quizzes = quizDAO.getAllQuizzes();
        for (Quiz quiz : quizzes) {
            listModel.addElement(quiz);
        }
    }
    
    private void showAddQuizDialog() {
        // Implementation for adding a new quiz
        // Create a new dialog with form fields for quiz details
    }
    
    private void deleteSelectedQuiz() {
        Quiz selectedQuiz = quizList.getSelectedValue();
        if (selectedQuiz != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this quiz?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (quizDAO.deleteQuiz(selectedQuiz.getId())) {
                    refreshQuizList();
                    JOptionPane.showMessageDialog(this, "Quiz deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete quiz!");
                }
            }
        }
    }
}
