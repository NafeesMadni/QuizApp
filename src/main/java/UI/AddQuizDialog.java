package UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import DBLayer.QuizDAO;
import Models.Question;
import Models.Quiz;

public class AddQuizDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private List<QuestionPanel> questionPanels;
    private JPanel questionsContainer;
    private final int userId;
    
    public AddQuizDialog(JFrame parent, int userId) {
        super(parent, "Add New Quiz", true);
        this.userId = userId;
        this.questionPanels = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setSize(600, 500);
        
        // Quiz details panel
        JPanel quizPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        quizPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        quizPanel.add(titleField);
        quizPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        quizPanel.add(new JScrollPane(descriptionArea));
        
        // Questions panel
        questionsContainer = new JPanel();
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsContainer);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addQuestionButton = new JButton("Add Question");
        JButton saveButton = new JButton("Save Quiz");
        JButton cancelButton = new JButton("Cancel");
        
        addQuestionButton.addActionListener(e -> addQuestionPanel());
        saveButton.addActionListener(e -> saveQuiz());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addQuestionButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components
        add(quizPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add initial question
        addQuestionPanel();
        
        setLocationRelativeTo(parent);
    }
    
    private void addQuestionPanel() {
        QuestionPanel panel = new QuestionPanel(questionPanels.size() + 1);
        questionPanels.add(panel);
        questionsContainer.add(panel);
        questionsContainer.revalidate();
        questionsContainer.repaint();
    }
    
    private void saveQuiz() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quiz title!");
            return;
        }
        
        Quiz quiz = new Quiz(titleField.getText(), descriptionArea.getText(), userId);
        List<Question> questions = new ArrayList<>();
        
        for (QuestionPanel panel : questionPanels) {
            Question question = panel.getQuestion();
            if (question != null) {
                questions.add(question);
            } else {
                return; // Invalid question, stop saving
            }
        }
        
        quiz.setQuestions(questions);
        QuizDAO quizDAO = new QuizDAO();
        
        if (quizDAO.createQuiz(quiz)) {
            JOptionPane.showMessageDialog(this, "Quiz created successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create quiz!");
        }
    }
}
