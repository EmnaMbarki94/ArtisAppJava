package tn.esprit.services;

import tn.esprit.entities.Reponse;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceReponse implements CRUD<Reponse> {
    private Connection cnx;

    public ServiceReponse() {
        cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reponse reponse) throws SQLException {
        String sql = "INSERT INTO reponse (reclamation_id, desc_rep, date_rep) VALUES (?, ?, ?)";
        PreparedStatement stm = cnx.prepareStatement(sql);
        stm.setInt(1, reponse.getReclamation_id());
        stm.setString(2, reponse.getDescr_rep());
        stm.setString(3, reponse.getDate_rep());
        stm.executeUpdate();
    }

    @Override
    public boolean supprimer(Reponse reponse) throws SQLException {
        return false;
    }

    public void supprimer1(Reponse reponse) throws SQLException {
        String sql = "DELETE FROM reponse WHERE id=?";
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            stm.setInt(1, reponse.getIdreponse());
            stm.executeUpdate();
        }
    }

    @Override
    public void modifier(Reponse reponse) throws SQLException {
        String sql = "UPDATE reponse SET reclamation_id = ?, desc_rep = ?, date_rep = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, reponse.getReclamation_id());
            ps.setString(2, reponse.getDescr_rep());
            ps.setString(3, reponse.getDate_rep());
            ps.setInt(4, reponse.getIdreponse());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Reponse> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // Implementation if needed
    }

    public ResultSet selectAll1() throws SQLException {
        String query = "SELECT * FROM reponse";
        PreparedStatement ps = cnx.prepareStatement(query);
        return ps.executeQuery();
    }

    public Map<String, Integer> getReclamationTypeStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT type_rec, COUNT(*) as count FROM reclamation GROUP BY type_rec";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                stats.put(rs.getString("type_rec"), rs.getInt("count"));
            }
        }
        return stats;
    }

    public int getTreatedReclamationsCount() throws SQLException {
        String query = "SELECT COUNT(DISTINCT reclamation_id) as count FROM reponse";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }

    public int getTotalReclamationsCount() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM reclamation";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs.next() ? rs.getInt("count") : 0;
        }
    }
}