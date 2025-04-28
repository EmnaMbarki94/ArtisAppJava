package tn.esprit.services;
import java.util.*;

import tn.esprit.entities.Cours;
import tn.esprit.entities.Quiz;
import tn.esprit.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuiz implements CRUD<Quiz> {

    private Connection connection;

    public ServiceQuiz(){
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quiz (cours_id, titre_c) VALUES (?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setLong(1, quiz.getCours().getId());
        pst.setString(2, quiz.getTitre_c());

        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            int generatedId = rs.getInt(1);
            quiz.setId(generatedId);
        } else {
            throw new SQLException("Impossible de récupérer l’ID généré pour le quiz.");
        }
    }

    @Override
    public boolean supprimer(Quiz quiz) throws SQLException {
        return false;
    }

    public void ajouterssc(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO `quiz`( `titre_c`) VALUES ('"+quiz.getTitre_c()+"')";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    @Override
    public void modifier(Quiz quiz) throws SQLException {

        String sql = "UPDATE `quiz` SET `cours_id`=? ,`titre_c`=? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);

        pst.setInt(1, quiz.getCours().getId());
        pst.setString(2, quiz.getTitre_c());

        pst.setInt(3,quiz.getId());
        pst.executeUpdate();

    }


    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM `quiz` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,id);
        ps.executeUpdate();

    }

    @Override
    public List<Quiz> selectAll() throws SQLException {
        List<Quiz> lquiz = new ArrayList<>();
        String sql = "Select * from quiz";
        Statement statement = connection.createStatement();
        ResultSet rs =statement.executeQuery(sql);

        while (rs.next()){
            Quiz q = new Quiz();
            q.setId(rs.getInt("id"));
            q.setTitre_c(rs.getString("titre_c"));

            Cours cours = new Cours();
            cours.setId(rs.getInt("cours_id"));
            q.setCours(cours);
            lquiz.add(q);
        }
        return lquiz;
    }
    public boolean quizExistsForCours(int coursId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz WHERE cours_id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, coursId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}
