package tn.esprit.services;

import tn.esprit.entities.Reponse;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.List;

public class ServiceReponse implements CRUD<Reponse>{
    private Connection cnx;
    public ServiceReponse(){
        cnx = DBConnection.getInstance().getCnx();
    }

//    @Override
//    public void ajouter(Reponse reponse) throws SQLException {
//        String sql = "INSERT INTO reponse (reclamation_id, desc_rep, date_rep) VALUES (?, ?, ?)";
//        PreparedStatement stm = cnx.prepareStatement(sql);
//        stm.setInt(1, 1); // 📝 À adapter ! Tu dois passer un vrai `reclamation_id` ici
//        stm.setString(2, reponse.getDescr_rep());
//        stm.setString(3, reponse.getDate_rep());
//        stm.executeUpdate();
//    }
@Override
public void ajouter(Reponse reponse) throws SQLException {
    String sql = "INSERT INTO reponse (reclamation_id, desc_rep, date_rep) VALUES (?, ?, ?)";
    PreparedStatement stm = cnx.prepareStatement(sql);
    stm.setInt(1, reponse.getReclamation_id()); // ✅ Utiliser l'ID dynamique
    stm.setString(2, reponse.getDescr_rep());
    stm.setString(3, reponse.getDate_rep());
    stm.executeUpdate();
}

    @Override
    public boolean supprimer(Reponse reponse) throws SQLException {
        return false;
    }


//    @Override
//    public void supprimer(Reponse reponse) throws SQLException {
//        String sql= "DELETE FROM `reponse` WHERE id=?";
//        PreparedStatement stm = cnx.prepareStatement(sql);
//        stm.setInt(1,reponse.getIdreponse());
//        stm.executeUpdate();
//    }

public void supprimer1(Reponse reponse) throws SQLException {
    String sql = "DELETE FROM reponse WHERE id=?";
    try (PreparedStatement stm = cnx.prepareStatement(sql)) {
        stm.setInt(1, reponse.getIdreponse());
        stm.executeUpdate();
    }
}


    //    @Override
//    public void modifier(Reponse reponse) throws SQLException {
//    String sql= "UPDATE `reponse` SET `reclamation_id`=?,`desc_rep`=?,`date_rep`=?,`rating`=? WHERE id=?";
//    }
@Override
public void modifier(Reponse reponse) throws SQLException {
    String sql = "UPDATE reponse SET reclamation_id = ?, desc_rep = ?, date_rep = ? WHERE id = ?";
    try (PreparedStatement ps = cnx.prepareStatement(sql)) {
        ps.setInt(1, reponse.getReclamation_id());
        ps.setString(2, reponse.getDescr_rep());
        ps.setString(3, reponse.getDate_rep()); // Si c'est un String. Sinon utilise `setDate(...)`
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

    }


    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM reponse"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = cnx.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all Reponses: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }
}
