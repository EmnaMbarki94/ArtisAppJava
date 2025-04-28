package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.List;

public class ServiceReclamation implements CRUD<Reclamtion> {
    private Connection cnx;

    public ServiceReclamation() {
        cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reclamtion reclamtion) throws SQLException {
        String sql = "INSERT INTO reclamation(desc_r, date_r, type_r, user_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, reclamtion.getDesc_r());
            pst.setString(2, reclamtion.getDate_r());
            pst.setString(3, reclamtion.getType_r());
            pst.setInt(4, reclamtion.getUser().getId());
            pst.executeUpdate();
        }
    }

    @Override
    public boolean supprimer(Reclamtion reclamtion) throws SQLException {
        // D'abord supprimer les réponses associées
        String deleteReponses = "DELETE FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(deleteReponses)) {
            pst.setInt(1, reclamtion.getId());
            pst.executeUpdate();
        }

        // Puis supprimer la réclamation
        String deleteReclamation = "DELETE FROM reclamation WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(deleteReclamation)) {
            pst.setInt(1, reclamtion.getId());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public void modifier(Reclamtion reclamtion) throws SQLException {
        String sql = "UPDATE reclamation SET desc_r = ?, date_r = ?, type_r = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, reclamtion.getDesc_r());
            pst.setString(2, reclamtion.getDate_r());
            pst.setString(3, reclamtion.getType_r());
            pst.setInt(4, reclamtion.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public List<Reclamtion> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // Implémentation alternative si nécessaire
    }

    public ResultSet selectByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM reclamation WHERE user_id = ? ORDER BY date_r DESC";
        PreparedStatement ps = cnx.prepareStatement(query);
        ps.setInt(1, userId);
        return ps.executeQuery();
    }

    public ResultSet selectAll1() throws SQLException {
        String query = "SELECT r.*, u.email FROM reclamation r JOIN user u ON r.user_id = u.id";
        PreparedStatement ps = cnx.prepareStatement(query);
        return ps.executeQuery();
    }

    public boolean hasResponseForReclamation(int reclamationId) {
        String query = "SELECT COUNT(*) FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, reclamationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getReponseForReclamation(int reclamationId) {
        String reponse = "";
        try {
            String query = "SELECT desc_rep FROM reponse WHERE reclamation_id = ?";
            PreparedStatement pst = cnx.prepareStatement(query);
            pst.setInt(1, reclamationId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                reponse = rs.getString("desc_rep");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reponse;
    }
    public boolean existeReponsePourReclamation(int reclamationId) {
        String query = "SELECT COUNT(*) FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, reclamationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getReponsePourReclamation(int idReclamation) {
        String contenu = "";
        String sql = "SELECT desc_rep FROM reponse WHERE reclamation_id = ?";
        try {
            PreparedStatement stmt = cnx.prepareStatement(sql);
            stmt.setInt(1, idReclamation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                contenu = rs.getString("desc_rep");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contenu;
    }
}
