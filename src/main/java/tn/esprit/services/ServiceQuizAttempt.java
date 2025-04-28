package tn.esprit.services;

import tn.esprit.controller.Cours.CoursStatsController;
import tn.esprit.entities.Quiz;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Quiz_attempt;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServiceQuizAttempt implements CRUD<Quiz_attempt> {
    private Connection connection;
    public ServiceQuizAttempt() {
        connection = DBConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(Quiz_attempt quizAttempt) throws SQLException {
        String sql = "INSERT INTO quiz_attempt (quiz_id, note,user_id) VALUES (?, ?,?)";
        PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1, quizAttempt.getQuiz().getId());
        pst.setInt(2, quizAttempt.getNote());
        pst.setInt(3, quizAttempt.getUser().getId());

        pst.executeUpdate();
    }

    @Override
    public boolean supprimer(Quiz_attempt quizAttempt) throws SQLException {
        return false;
    }

    @Override
    public void modifier(Quiz_attempt quizAttempt) throws SQLException {
    }

    @Override
    public List<Quiz_attempt> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
    }

    public Map<String, Integer> getTop5QuizzesByAttempts() {
        Map<String, Integer> quizAttempts = new LinkedHashMap<>(); // Keep insertion order for charts

        String query = "SELECT q.titre_c, COUNT(*) as attempts " +
                "FROM quiz_attempt qa " +
                "JOIN quiz q ON qa.quiz_id = q.id " +
                "GROUP BY q.titre_c " +
                "ORDER BY attempts DESC " +
                "LIMIT 5";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String quizTitle = rs.getString("titre_c");
                int attempts = rs.getInt("attempts");
                quizAttempts.put(quizTitle, attempts);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizAttempts;
    }

    public List<CoursStatsController.UserAttempts> getTop5UsersByAttempts() {
        List<CoursStatsController.UserAttempts> leaderboard = new ArrayList<>();

        String query = "SELECT user_id,email, COUNT(*) as attempts " +
                "FROM quiz_attempt qa " +
                "LEFT JOIN user u ON qa.user_id = u.id "+
                "GROUP BY user_id " +
                "ORDER BY attempts DESC " +
                "LIMIT 5";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                Personne user=new Personne();
                user.setId(userId);
                user.setEmail(rs.getString("email"));
                int attempts = rs.getInt("attempts");
                leaderboard.add(new CoursStatsController.UserAttempts(user, attempts));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
}
