package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.entities.Reponse;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.List;

public class ServiceReclamation implements CRUD<Reclamtion> {
    private Connection cnx;
    public ServiceReclamation(){
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
        return false;
    }


    public void supprimer1(Reclamtion reclamtion) throws SQLException {
        String sql = "DELETE FROM `reclamation` WHERE id=?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, reclamtion.getId());
        ps.executeUpdate();
    }

    @Override
    public void modifier(Reclamtion reclamtion) throws SQLException {
        String sql = "UPDATE reclamation SET desc_r = ?, date_r = ?, type_r = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, reclamtion.getDesc_r());
            pst.setString(2, reclamtion.getDate_r());
            pst.setString(3, reclamtion.getType_r());
            pst.setInt(4, reclamtion.getUser().getId());
            pst.setInt(5, reclamtion.getId()); // N'oublie pas d'appeler setId(...) ou d’avoir un id valide
            pst.executeUpdate();
        }
    }

    @Override
    public List<Reclamtion> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }


    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM reclamation"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = cnx.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all reclamations: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
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
    public boolean hasResponseForReclamation(int reclamationId) {
        String query = "SELECT COUNT(*) FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, reclamationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si le nombre de réponses est supérieur à 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Reponse getReponseByReclamationId(int reclamationId) throws SQLException {
        String query = "SELECT * FROM reponse WHERE reclamation_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, reclamationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Reponse(
                        rs.getInt("idreponse"),
                        rs.getString("descr_rep"),
                        rs.getString("date_rep")
                );
            }
        }
        return null; // Si aucune réponse n'est trouvée
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






}
