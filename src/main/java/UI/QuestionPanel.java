package UI;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Models.Question;

public class QuestionPanel extends JPanel {
    private JTextField questionField;
    private JTextField[] optionFields;
    private JComboBox<String> correctAnswerCombo;
    
    public QuestionPanel(int questionNumber) {
        this(questionNumber, null);
    }
    
    public QuestionPanel(int questionNumber, Question question) {
        setBorder(BorderFactory.createTitledBorder("Question " + questionNumber));
        setLayout(new GridLayout(7, 2, 5, 5));
        
        add(new JLabel("Question:"));
        questionField = new JTextField();
        if (question != null) {
            questionField.setText(question.getQuestionText());
        }
        add(questionField);
        
        optionFields = new JTextField[4];
        String[] options = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            add(new JLabel("Option " + options[i] + ":"));
            optionFields[i] = new JTextField();
            if (question != null) {
                switch(i) {
                    case 0: optionFields[i].setText(question.getOptionA()); break;
                    case 1: optionFields[i].setText(question.getOptionB()); break;
                    case 2: optionFields[i].setText(question.getOptionC()); break;
                    case 3: optionFields[i].setText(question.getOptionD()); break;
                }
            }
            add(optionFields[i]);
        }
        
        add(new JLabel("Correct Answer:"));
        correctAnswerCombo = new JComboBox<>(options);
        if (question != null) {
            correctAnswerCombo.setSelectedItem(String.valueOf(question.getCorrectOption()));
        }
        add(correctAnswerCombo);
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
