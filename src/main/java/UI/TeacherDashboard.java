package UI;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import DBLayer.QuizDAO;
import Models.Quiz;

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
        JButton updateButton = new JButton("Update Quiz");
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(updateButton);
        
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
        updateButton.addActionListener(e -> updateSelectedQuiz());
        
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
        AddQuizDialog dialog = new AddQuizDialog(this, userId);
        dialog.setVisible(true);
        refreshQuizList(); // Refresh the list after adding a new quiz
    }
    
    private void deleteSelectedQuiz() {
        Quiz selectedQuiz = quizList.getSelectedValue();
        if (selectedQuiz != null) {
            System.out.println("Selected quiz ID: " + selectedQuiz.getId()); // Debug log
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete quiz: " + selectedQuiz.getTitle() + " (ID: " + selectedQuiz.getId() + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (quizDAO.deleteQuiz(selectedQuiz.getId())) {
                    refreshQuizList();
                    JOptionPane.showMessageDialog(this, "Quiz deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete quiz. Please check console for error details.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a quiz to delete.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateSelectedQuiz() {
        Quiz selectedQuiz = quizList.getSelectedValue();
        if (selectedQuiz != null) {
            Quiz fullQuiz = quizDAO.getQuizWithQuestions(selectedQuiz.getId());
            if (fullQuiz != null) {
                UpdateQuizDialog dialog = new UpdateQuizDialog(this, fullQuiz, userId);
                dialog.setVisible(true);
                refreshQuizList();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a quiz to update.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
}
