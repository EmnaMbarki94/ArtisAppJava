package tn.esprit.services;

import tn.esprit.entities.Event;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements CRUD<Event>{
     Connection connection;

    @Override
    public boolean supprimer(Event event) throws SQLException {
        return false;
    }

    @Override
    public List<Event> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    public ServiceEvent() {
        connection = DBConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(Event e) throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Connexion fermée avant l'ajout !");
            connection = DBConnection.getInstance().getCnx();
        }

        String req = "INSERT INTO event (nom, type_e, info_e, date_e, photo_e, prix_s, prix_vip, nb_ticket) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, e.getNom());
        ps.setString(2, e.getType_e());
        ps.setString(3, e.getInfo_e());
        ps.setDate(4, Date.valueOf(e.getDate_e()));
        ps.setString(5, e.getPhoto_e());
        ps.setInt(6, e.getPrix_s());
        ps.setInt(7, e.getPrix_vip());
        ps.setInt(8, e.getNb_ticket());

        System.out.println("Requête préparée exécutée !");
        ps.executeUpdate();
        System.out.println("✅ Événement ajouté avec succès");
    }


    /*public void ajouter(Event e) throws SQLException {
        String req = "INSERT INTO event (nom, type, info, date, photo, prix_standard, prix_vip, nb_ticket) " +
                "VALUES ('" + e.getNom() + "', '" + e.getType_e() + "', '" + e.getInfo_e() + "', " +
                "'" + e.getDate_e() + "', '" + e.getPhoto_e() + "', " + e.getPrix_s() + ", " +
                e.getPrix_vip() + ", " + e.getNb_ticket() + ")";

        Statement statement = connection.createStatement();
        statement.executeUpdate(req);
        System.out.println("Événement ajouté");

    }*/



    @Override
    public void modifier(Event event) throws SQLException {
        String sql = "UPDATE `event` SET `nom` = ?, `type_e` = ?, `info_e` = ?, `date_e` = ?, `photo_e` = ?, `prix_s` = ?, `prix_vip` = ?, `nb_ticket` = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, event.getNom());
        pst.setString(2, event.getType_e());
        pst.setString(3, event.getInfo_e());
        pst.setDate(4, Date.valueOf(event.getDate_e()));
        pst.setString(5, event.getPhoto_e());
        pst.setDouble(6, event.getPrix_s());
        pst.setDouble(7, event.getPrix_vip());
        pst.setInt(8, event.getNb_ticket());
        pst.setInt(9, event.getId());
        pst.executeUpdate();
    }

    public void supprimer1(Event event) throws SQLException {
        String sql = "DELETE FROM `event` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, event.getId());
        ps.executeUpdate();
    }
    /*@Override
    public List<Event> afficher() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            Event e = new Event();
            e.setId(rs.getInt("id"));
            e.setNom(rs.getString("nom"));
            e.setType_e(rs.getString("type_e"));
            e.setInfo_e(rs.getString("info_e"));
            e.setDate_e(rs.getDate("date_e").toLocalDate());
            e.setPhoto_e(rs.getString("photo_e"));
            e.setPrix_s(rs.getInt("prix_s"));
            e.setPrix_vip(rs.getInt("prix_vip"));
            e.setNb_ticket(rs.getInt("nb_ticket"));

            events.add(e);
        }
        return events;
    }*/


    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM event"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

}
