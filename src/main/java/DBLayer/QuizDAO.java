package DBLayer;

import Models.Quiz;
import Models.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    DBConnector conObj;
    
    public QuizDAO() {
        conObj = new DBConnector();
    }

    public boolean createQuiz(Quiz quiz) {
        try (Connection conn = conObj.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert quiz
                String quizSql = "INSERT INTO quizzes (title, description, created_by) VALUES (?, ?, ?)";
                PreparedStatement quizStmt = conn.prepareStatement(quizSql, Statement.RETURN_GENERATED_KEYS);
                quizStmt.setString(1, quiz.getTitle());
                quizStmt.setString(2, quiz.getDescription());
                quizStmt.setInt(3, quiz.getCreatedBy());
                quizStmt.executeUpdate();
                
                ResultSet rs = quizStmt.getGeneratedKeys();
                if (rs.next()) {
                    int quizId = rs.getInt(1);
                    // Insert questions
                    for (Question question : quiz.getQuestions()) {
                        String questionSql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement questionStmt = conn.prepareStatement(questionSql);
                        questionStmt.setInt(1, quizId);
                        questionStmt.setString(2, question.getQuestionText());
                        questionStmt.setString(3, question.getOptionA());
                        questionStmt.setString(4, question.getOptionB());
                        questionStmt.setString(5, question.getOptionC());
                        questionStmt.setString(6, question.getOptionD());
                        questionStmt.setString(7, String.valueOf(question.getCorrectOption()));
                        questionStmt.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = conObj.getConnection()) {
            String sql = "SELECT * FROM quizzes";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("created_by")
                );
                quiz.setId(rs.getInt("id"));
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }
    
    public boolean deleteQuiz(int quizId) {
        try (Connection conn = conObj.getConnection()) {
            // First delete associated questions
            String deleteQuestions = "DELETE FROM questions WHERE quiz_id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(deleteQuestions);
            stmt1.setInt(1, quizId);
            stmt1.executeUpdate();
            
            // Then delete the quiz
            String deleteQuiz = "DELETE FROM quizzes WHERE id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(deleteQuiz);
            stmt2.setInt(1, quizId);
            return stmt2.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
