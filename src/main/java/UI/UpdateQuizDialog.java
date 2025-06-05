package UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class UpdateQuizDialog extends JDialog {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private List<QuestionPanel> questionPanels;
    private JPanel questionsContainer;
    private final int userId;
    private Quiz quiz;
    
    public UpdateQuizDialog(JFrame parent, Quiz quiz, int userId) {
        super(parent, "Update Quiz", true);
        this.quiz = quiz;
        this.userId = userId;
        this.questionPanels = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setSize(600, 500);
        
        // Quiz details panel
        JPanel quizPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        quizPanel.add(new JLabel("Title:"));
        titleField = new JTextField(quiz.getTitle());
        quizPanel.add(titleField);
        quizPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setText(quiz.getDescription());
        quizPanel.add(new JScrollPane(descriptionArea));
        
        // Questions panel
        questionsContainer = new JPanel();
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(questionsContainer);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addQuestionButton = new JButton("Add Question");
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        
        addQuestionButton.addActionListener(e -> addQuestionPanel());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addQuestionButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components
        add(quizPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add existing questions
        for (Question question : quiz.getQuestions()) {
            addQuestionPanel(question);
        }
        
        setLocationRelativeTo(parent);
    }
    
    private void addQuestionPanel() {
        QuestionPanel panel = new QuestionPanel(questionPanels.size() + 1);
        questionPanels.add(panel);
        questionsContainer.add(panel);
        questionsContainer.revalidate();
        questionsContainer.repaint();
    }
    
    private void addQuestionPanel(Question question) {
        QuestionPanel panel = new QuestionPanel(questionPanels.size() + 1, question);
        questionPanels.add(panel);
        questionsContainer.add(panel);
        questionsContainer.revalidate();
        questionsContainer.repaint();
    }
    
    private void saveChanges() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quiz title!");
            return;
        }
        
        String newTitle = titleField.getText().trim();
        String newDescription = descriptionArea.getText().trim();
        
        // // Update quiz properties
        // quiz.setTitle(newTitle);
        // quiz.setDescription(newDescription);
        
        // Get updated questions
        List<Question> updatedQuestions = new ArrayList<>();
        for (QuestionPanel panel : questionPanels) {
            Question question = panel.getQuestion();
            if (question != null) {
                question.setQuizId(quiz.getId());
                updatedQuestions.add(question);
            } else {
                return; // Invalid question
            }
        }
        
        quiz.setQuestions(updatedQuestions);
        QuizDAO quizDAO = new QuizDAO();
        
        if (quizDAO.updateQuiz(quiz)) {
            JOptionPane.showMessageDialog(this, "Quiz updated successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update quiz!");
        }
    }
    
    private class QuestionPanel extends JPanel {
        private JTextField questionField;
        private JTextField[] optionFields;
        private JComboBox<String> correctAnswerCombo;
        
        public QuestionPanel(int questionNumber) {
            setBorder(BorderFactory.createTitledBorder("Question " + questionNumber));
            setLayout(new GridLayout(7, 2, 5, 5));
            
            add(new JLabel("Question:"));
            questionField = new JTextField();
            add(questionField);
            
            optionFields = new JTextField[4];
            String[] options = {"A", "B", "C", "D"};
            for (int i = 0; i < 4; i++) {
                add(new JLabel("Option " + options[i] + ":"));
                optionFields[i] = new JTextField();
                add(optionFields[i]);
            }
            
            add(new JLabel("Correct Answer:"));
            correctAnswerCombo = new JComboBox<>(options);
            add(correctAnswerCombo);
        }
        
        public QuestionPanel(int questionNumber, Question question) {
            this(questionNumber);
            
            questionField.setText(question.getQuestionText());
            optionFields[0].setText(question.getOptionA());
            optionFields[1].setText(question.getOptionB());
            optionFields[2].setText(question.getOptionC());
            optionFields[3].setText(question.getOptionD());
            correctAnswerCombo.setSelectedItem(String.valueOf(question.getCorrectOption()));
        }
        
        public Question getQuestion() {
            if (questionField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter the question text!");
                return null;
            }
            
            for (JTextField field : optionFields) {
                if (field.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all options!");
                    return null;
                }
            }
            
            return new Question(
                questionField.getText(),
                optionFields[0].getText(),
                optionFields[1].getText(),
                optionFields[2].getText(),
                optionFields[3].getText(),
                correctAnswerCombo.getSelectedItem().toString().charAt(0)
            );
        }
    }
}