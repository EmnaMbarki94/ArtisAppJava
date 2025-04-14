package tn.esprit.services;

import tn.esprit.entities.Magasin;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.List;


public class ServiceMagasin implements CRUD<Magasin> {

    Connection connection;

    public ServiceMagasin(){
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Magasin magasin) throws SQLException {
        String sql = "INSERT INTO magasin (nom_m, type_m, photo_m) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, magasin.getNom_m()); // Ajout du nom
            stmt.setString(2, magasin.getType_m()); // Ajout du type

            // Vérification de photo_m
            if (magasin.getPhoto_m() == null || magasin.getPhoto_m().isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, magasin.getPhoto_m()); // Ajout de la photo en tant que chaîne
            }

            stmt.executeUpdate();
            System.out.println(" Magasin ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println(" Magasin Non Ajouté");
            e.printStackTrace();
        }
    }

    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM magasin"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }
    @Override
    public void modifier(Magasin magasin) throws SQLException {

        String sql = "UPDATE `magasin` SET `nom_m`=? ,`type_m`=?,`photo_m`=? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, magasin.getNom_m());
        pst.setString(2, magasin.getType_m());
        pst.setString(3,magasin.getPhoto_m());
        pst.setInt(4,magasin.getId());
        pst.executeUpdate();

    }

    public void supprimer1(Magasin magasin) throws SQLException {
        String sql = "DELETE FROM magasin WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, magasin.getId());  // Correction ici
            ps.executeUpdate();
            System.out.println("🗑️ Magasin supprimé avec succès !");
        }
    }

    @Override
    public boolean supprimer(Magasin magasin) throws SQLException {
        return false;
    }

    @Override
    public List<Magasin> selectAll() throws SQLException {
        return null;
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }
}
