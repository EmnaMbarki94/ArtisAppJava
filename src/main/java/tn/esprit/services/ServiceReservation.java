package tn.esprit.services;

import javafx.scene.control.Alert;
import tn.esprit.entities.Event;
import tn.esprit.entities.Reservation;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements CRUD<Reservation>{
    private Connection connection;

    @Override
    public List<Reservation> selectAll() throws SQLException {
        return List.of();
    }

    public ServiceReservation() {
        connection = DBConnection.getInstance().getCnx();
    }
    public void ajouter(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservation (relation_id, nb_place, libelle, etat_e, user_id_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = null;

        try {
            pst = connection.prepareStatement(sql);
            pst.setInt(1, r.getEvent().getId());
            pst.setInt(2, r.getNb_place());
            pst.setString(3, r.getLibelle());
            pst.setString(4, r.getEtat_e());
            pst.setInt(5, r.getUser_id_id());

            // Exécution de la requête d'insertion
            pst.executeUpdate();

            System.out.println("✅ Réservation ajoutée avec succès !");

        } catch (SQLException e) {
            // Affichage de l'erreur détaillée
            System.out.println("❌ Erreur lors de l'ajout de la réservation : " + e.getMessage());
            e.printStackTrace();  // Affiche la trace de la pile d'exécution pour plus de détails

            // Tu peux aussi ajouter une boîte de dialogue pour informer l'utilisateur de l'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'ajout");
            alert.setHeaderText("Une erreur est survenue lors de l'ajout de la réservation.");
            alert.setContentText("Détails de l'erreur : " + e.getMessage());
            alert.showAndWait();
        } finally {
            // Fermeture de la ressource PreparedStatement, si nécessaire
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean supprimer(Reservation reservation) throws SQLException {
        return false;
    }

    public void modifier(Reservation r) throws SQLException {
        String sql = "UPDATE reservation SET relation_id = ?, nb_place = ?, libelle = ?, etat_e = ?, user_id_id = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, r.getEvent().getId());
        pst.setInt(2, r.getNb_place());
        pst.setString(3, r.getLibelle());
        pst.setString(4, r.getEtat_e());
        pst.setInt(5, r.getUser_id_id());
        pst.setInt(6, r.getId());
        pst.executeUpdate();
    }
    public void supprimer1(Reservation reservation) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, reservation.getId());
        pst.executeUpdate();
    }
    /*public List<Reservation> afficher() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("id"));
            r.setRelation_id(rs.getInt("relation_id"));
            r.setNb_place(rs.getInt("nb_place"));
            r.setLibelle(rs.getString("libelle"));
            r.setEtat_e(rs.getString("etat_e"));
            r.setUser_id_id(rs.getInt("user_id_id"));

            reservations.add(r);
        }

        return reservations;
    }*/

    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM reservation"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all reservations: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    public boolean reserver(Event event, int nbPlaces, String libelle) {
            // Logique pour enregistrer la réservation dans la base de données
            // Vérifier si le nombre de places est valide et suffisant
            if (event.getNb_ticket() >= nbPlaces) {
                // Mettre à jour le nombre de tickets restants, enregistrer la réservation
                event.setNb_ticket(event.getNb_ticket() - nbPlaces);
                // Ajouter la réservation à la base de données (logiciel d'accès à la base de données ici)
                return true;
            }
            return false;

    }
    public Reservation getReservationAvecEvent(int id) {
        Reservation r = null;

        String query = "SELECT r.*, e.id AS event_id, e.nom AS event_nom " +
                "FROM reservation r " +
                "LEFT JOIN event e ON r.relation_id = e.id " +
                "WHERE r.id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setLibelle(rs.getString("libelle"));
                r.setEtat_e(rs.getString("etat_e"));
                r.setNb_place(rs.getInt("nb_place"));
                r.setUser_id_id(rs.getInt("user_id_id"));

                // Créer l’objet Event seulement s’il est présent
                int eventId = rs.getInt("event_id");
                if (!rs.wasNull()) {
                    Event e = new Event();
                    e.setId(eventId);
                    e.setNom(rs.getString("event_nom"));
                    r.setEvent(e);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return r;
    }


}
