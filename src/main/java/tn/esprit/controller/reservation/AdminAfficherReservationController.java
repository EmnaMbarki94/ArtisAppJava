package tn.esprit.controller.reservation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminAfficherReservationController implements Initializable {

    @FXML private ListView<Reservation> listViewReservations;
    @FXML private AnchorPane contenuPane;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ServiceReservation serviceReservation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceReservation = new ServiceReservation();
        chargerReservations();
    }

    private void chargerReservations() {
        try {
            System.out.println("🔍 Chargement des réservations...");
            ResultSet rs = serviceReservation.selectAll1();

            if (rs == null) {
                System.out.println("❌ Aucun résultat retourné !");
                return;
            }

            reservationList.clear();  // Clear any existing data

            int rowCount = 0;
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setNb_place(rs.getInt("nb_place"));
                r.setLibelle(rs.getString("libelle"));
                r.setEtat_e(rs.getString("etat_e"));
                r.setUser_id_id(rs.getInt("user_id_id"));

                // Assure-toi que getEvent() ne lève pas d'exception si null
                if (r.getEvent() != null) {
                    r.getEvent().setId(rs.getInt("relation_id")); // Adapte selon ton schéma
                }

                reservationList.add(r);
                rowCount++;
            }

            System.out.println("✅ " + rowCount + " réservation(s) chargée(s).");

            listViewReservations.setItems(reservationList);
            listViewReservations.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Reservation r, boolean empty) {
                    super.updateItem(r, empty);
                    if (empty || r == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Description textuelle
                        Text text = new Text("🧾 Réservation: " + r.getLibelle() +
                                "\n👤 Utilisateur ID: " + r.getUser_id_id() +
                                "\n🎫 Événement ID: " + (r.getEvent() != null ? r.getEvent().getId() : "Non défini") +
                                "\n📅 Nombre de places: " + r.getNb_place() +
                                "\n📌 État: " + r.getEtat_e());
                        text.setWrappingWidth(600);
                        text.setStyle("-fx-font-size: 14px; -fx-fill: #4B0082;");

                        // Bouton modifier
                        Button editButton = new Button("✏️");
                        editButton.setStyle("""
                                -fx-background-color: transparent;
                                -fx-cursor: hand;
                                -fx-font-size: 20px;
                                -fx-text-fill: #800080;
                                """);
                        editButton.setOnAction(event -> {
                            try {
                                System.out.println("🔧 Modification: " + r.getLibelle());

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/modifierReservation.fxml"));
                                AnchorPane form = loader.load();

                                // Récupération du contrôleur
                                ModifierReservationController controller = loader.getController();

                                // 💡 Appeler le service qui récupère la réservation avec l'événement lié
                                ServiceReservation service = new ServiceReservation();
                                Reservation resAvecEvent = service.getReservationAvecEvent(r.getId());

                                // Transmettre la réservation enrichie
                                controller.setReservation(resAvecEvent);

                                // Afficher la vue de modification
                                contenuPane.getChildren().setAll(form);

                            } catch (IOException ex) {
                                System.out.println("❌ Erreur de chargement du formulaire de modification.");
                                ex.printStackTrace();
                            }
                        });



                        // Bouton supprimer
                        Button deleteButton = new Button("🗑️");
                        deleteButton.setStyle("""
                                -fx-background-color: transparent;
                                -fx-cursor: hand;
                                -fx-font-size: 20px;
                                -fx-text-fill: #800080;
                                """);
                        deleteButton.setOnAction(event -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation de suppression");
                            alert.setHeaderText("Supprimer la réservation ?");
                            alert.setContentText("Voulez-vous vraiment supprimer \"" + r.getLibelle() + "\" ?");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    supprimerReservation(r);
                                }
                            });
                        });

                        // Conteneur boutons + texte
                        HBox actionBox = new HBox(5, editButton, deleteButton);
                        actionBox.setStyle("-fx-alignment: top-right;");
                        HBox container = new HBox(20, text, actionBox);
                        container.setStyle("-fx-padding: 10; -fx-background-color: #eae6fa; -fx-background-radius: 10;");
                        setGraphic(container);
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void supprimerReservation(Reservation r) {
        try {
            serviceReservation.supprimer(r);
            reservationList.remove(r);
            System.out.println("🗑️ Réservation supprimée: " + r.getLibelle());
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/EvenementAdmin.fxml"));

            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
