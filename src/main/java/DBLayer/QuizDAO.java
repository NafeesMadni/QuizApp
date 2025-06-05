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
                quiz.setId(rs.getInt("id")); // Make sure ID is set
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching quizzes: " + e.getMessage());
            e.printStackTrace();
        }
        return quizzes;
    }
    
    public boolean deleteQuiz(int quizId) {
        Connection conn = null;
        try {
            conn = conObj.getConnection();
            conn.setAutoCommit(false);
            
            // First delete results associated with the quiz
            String deleteResults = "DELETE FROM results WHERE quiz_id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(deleteResults);
            stmt1.setInt(1, quizId);
            stmt1.executeUpdate();

            // Delete questions
            String deleteQuestions = "DELETE FROM questions WHERE quiz_id = ?";
            PreparedStatement stmt2 = conn.prepareStatement(deleteQuestions);
            stmt2.setInt(1, quizId);
            stmt2.executeUpdate();

            // Delete the quiz
            String deleteQuiz = "DELETE FROM quizzes WHERE id = ?";
            PreparedStatement stmt3 = conn.prepareStatement(deleteQuiz);
            stmt3.setInt(1, quizId);
            int rowsAffected = stmt3.executeUpdate();
            
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean updateQuiz(Quiz quiz) {
        Connection conn = null;
        try {
            conn = conObj.getConnection();
            conn.setAutoCommit(false);
            
            // Update quiz details
            String updateQuiz = "UPDATE quizzes SET title=?, description=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(updateQuiz);
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setInt(3, quiz.getId());
            int rowsAffected = stmt.executeUpdate();
            
            // Delete existing questions
            String deleteQuestions = "DELETE FROM questions WHERE quiz_id=?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuestions);
            deleteStmt.setInt(1, quiz.getId());
            deleteStmt.executeUpdate();
            
            // Insert new questions
            for (Question question : quiz.getQuestions()) {
                String insertQuestion = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement questionStmt = conn.prepareStatement(insertQuestion);
                questionStmt.setInt(1, quiz.getId());
                questionStmt.setString(2, question.getQuestionText());
                questionStmt.setString(3, question.getOptionA());
                questionStmt.setString(4, question.getOptionB());
                questionStmt.setString(5, question.getOptionC());
                questionStmt.setString(6, question.getOptionD());
                questionStmt.setString(7, String.valueOf(question.getCorrectOption()));
                questionStmt.executeUpdate();
            }
            
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Error updating quiz: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Quiz getQuizWithQuestions(int quizId) {
        Quiz quiz = null;
        Connection conn = null;
        try {
            conn = conObj.getConnection();
            
            // Get quiz details
            String quizSql = "SELECT * FROM quizzes WHERE id=?";
            PreparedStatement quizStmt = conn.prepareStatement(quizSql);
            quizStmt.setInt(1, quizId);
            ResultSet quizRs = quizStmt.executeQuery();
            
            if (quizRs.next()) {
                quiz = new Quiz(
                    quizRs.getString("title"),
                    quizRs.getString("description"),
                    quizRs.getInt("created_by")
                );
                quiz.setId(quizId);
                
                // Get questions
                String questionSql = "SELECT * FROM questions WHERE quiz_id=?";
                PreparedStatement questionStmt = conn.prepareStatement(questionSql);
                questionStmt.setInt(1, quizId);
                ResultSet questionRs = questionStmt.executeQuery();
                
                while (questionRs.next()) {
                    Question question = new Question(
                        questionRs.getString("question_text"),
                        questionRs.getString("option_a"),
                        questionRs.getString("option_b"),
                        questionRs.getString("option_c"),
                        questionRs.getString("option_d"),
                        questionRs.getString("correct_option").charAt(0)
                    );
                    question.setId(questionRs.getInt("id"));
                    quiz.getQuestions().add(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return quiz;
    }
}
