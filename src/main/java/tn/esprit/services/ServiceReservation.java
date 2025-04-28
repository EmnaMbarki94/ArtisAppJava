package tn.esprit.services;

import javafx.scene.control.Alert;
import tn.esprit.entities.Event;
import tn.esprit.entities.Reservation;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Reservation> getReservationsParEvenement(int eventId) {
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT * FROM reservation WHERE relation_id = ?";
        try (Connection conn = DBConnection.getInstance().getCnx();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setNb_place(rs.getInt("nb_place"));
                r.setLibelle(rs.getString("libelle"));
                r.setEtat_e(rs.getString("etat_e"));
                r.setUser_id_id(rs.getInt("user_id_id"));

                // Tu peux aussi ajouter l'objet Event lié ici si nécessaire
                // r.setEvent(...);

                reservations.add(r);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors du chargement des réservations pour l'événement " + eventId);
            e.printStackTrace();
        }

        return reservations;
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
    public boolean reserverEtDecrementer(Reservation r) throws SQLException {
        Connection cnx = DBConnection.getInstance().getCnx();

        // 1. Récupérer le nombre de places disponibles pour l'événement
        String checkQuery = "SELECT nb_ticket FROM event WHERE id = ?";
        PreparedStatement checkStmt = cnx.prepareStatement(checkQuery);
        checkStmt.setInt(1, r.getEvent().getId());

        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            return false; // Événement introuvable
        }

        int availablePlaces = rs.getInt("nb_ticket");

        // 2. Vérifier la disponibilité
        if (r.getNb_place() > availablePlaces) {
            return false; // Pas assez de places
        }

        // 3. Insérer la réservation
        String insertQuery = "INSERT INTO reservation (nb_place, libelle, etat_e, user_id_id, relation_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = cnx.prepareStatement(insertQuery);
        insertStmt.setInt(1, r.getNb_place());
        insertStmt.setString(2, r.getLibelle());
        insertStmt.setString(3, r.getEtat_e());
        insertStmt.setInt(4, r.getUser_id_id());
        insertStmt.setInt(5, r.getEvent().getId());

        int insertResult = insertStmt.executeUpdate();

        if (insertResult <= 0) {
            return false; // Échec insertion
        }

        // 4. Mettre à jour le nombre de tickets
        String updateQuery = "UPDATE event SET nb_ticket = nb_ticket - ? WHERE id = ?";
        PreparedStatement updateStmt = cnx.prepareStatement(updateQuery);
        updateStmt.setInt(1, r.getNb_place());
        updateStmt.setInt(2, r.getEvent().getId());

        updateStmt.executeUpdate();

        return true;
    }

    public static int getAvailablePlaces(int eventId) {
        int availablePlaces = 0;
        // Effectuer la requête SQL pour obtenir le nombre de places disponibles
        // Par exemple :
        String query = "SELECT nb_ticket FROM event WHERE id = ?";
        // Exécuter la requête et récupérer la valeur de places_disponibles
        return availablePlaces;
    }

    // Méthode pour mettre à jour le nombre de places disponibles
    public static void updateAvailablePlaces(int eventId, int newAvailablePlaces) {
        // Effectuer la mise à jour dans la base de données pour l'événement spécifié
        String query = "UPDATE event SET nb_ticket = ? WHERE id = ?";
        // Exécuter la requête pour mettre à jour places_disponibles
    }
    public ResultSet getReservationsByEventId(int eventId) throws SQLException {
        String query = "SELECT * FROM reservation WHERE relation_id = ?";
        Connection conn = DBConnection.getInstance().getCnx();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, eventId);
        return pstmt.executeQuery();
}
    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT r.*, e.nom AS event_nom, e.date_e AS event_date " +
                "FROM reservation r " +
                "JOIN event e ON r.relation_id = e.id " +
                "WHERE r.user_id_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setNb_place(rs.getInt("nb_place"));
                r.setLibelle(rs.getString("libelle"));
                r.setEtat_e(rs.getString("etat_e"));
                r.setUser_id_id(rs.getInt("user_id_id"));

                Event e = new Event();
                e.setId(rs.getInt("relation_id")); // id de l'événement
                e.setNom(rs.getString("event_nom"));
                e.setDate_e(rs.getDate("event_date").toLocalDate()); // IMPORTANT : conversion en LocalDate

                r.setEvent(e);

                reservations.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
    public static List<Reservation> getReservationsByUser2(int userId) {
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT r.*, e.nom AS event_nom, e.date_e AS event_date " +
                "FROM reservation r " +
                "JOIN event e ON r.relation_id = e.id " +
                "WHERE r.user_id_id = ?";

        // Use a connection from your DBConnection utility
        try (Connection connection = DBConnection.getInstance().getCnx(); // Assuming you're using a DBConnection utility
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId); // Set the user ID parameter in the query
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setNb_place(rs.getInt("nb_place"));
                r.setLibelle(rs.getString("libelle"));
                r.setEtat_e(rs.getString("etat_e"));
                r.setUser_id_id(rs.getInt("user_id_id"));

                // Create the Event object and set its details
                Event e = new Event();
                e.setId(rs.getInt("relation_id")); // Ensure 'relation_id' is the correct column for event ID
                e.setNom(rs.getString("event_nom"));
                e.setDate_e(rs.getDate("event_date").toLocalDate()); // Convert SQL Date to LocalDate

                // Set the Event object in Reservation
                r.setEvent(e);

                // Add the Reservation object to the list
                reservations.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public void supprimerReservation(int id) {
        String query = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Map<String, Object>> getReservationStatsByEvent() {
        String sql = "SELECT e.id AS event_id, e.nom AS event_name, COUNT(r.id) AS reservation_count " +
                "FROM event e " +
                "LEFT JOIN reservation r ON r.relation_id = e.id " +
                "GROUP BY e.id, e.nom";

        List<Map<String, Object>> stats = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> eventStat = new HashMap<>();
                eventStat.put("event_id", rs.getInt("event_id"));
                eventStat.put("event_name", rs.getString("event_name"));
                eventStat.put("reservation_count", rs.getLong("reservation_count"));

                stats.add(eventStat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
    public List<Map<String, Object>> getVIPTicketsStatsByEvent() {
        List<Map<String, Object>> stats = new ArrayList<>();
        String query = "SELECT e.nom AS event_name, COUNT(r.id) AS vip_count " +
                "FROM reservation r JOIN event e ON r.relation_id = e.id " +
                "WHERE r.etat_e = 'VIP' " +
                "GROUP BY e.nom";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("event_name", rs.getString("event_name"));
                stat.put("vip_count", rs.getLong("vip_count"));
                stats.add(stat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public List<Map<String, Object>> getRevenueStatsByEvent() {
        List<Map<String, Object>> stats = new ArrayList<>();
        String query = "SELECT e.nom AS event_name, SUM(r.prix_vip)  AS revenue " +
                "FROM reservation r " +
                "JOIN event e ON r.relation_id = e.id " +
                "GROUP BY e.nom";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("event_name", rs.getString("event_name"));
                stat.put("revenue", rs.getDouble("revenue"));
                stats.add(stat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }


}
