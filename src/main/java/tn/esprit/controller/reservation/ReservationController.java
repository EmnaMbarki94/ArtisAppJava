package tn.esprit.controller.reservation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reservation;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceEvent;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReservationController {

    // ✅ Utilisateur connecté transmis depuis EvenementController
    private Personne user = Session.getUser();

    @FXML private TextField eventNameField;
    @FXML private TextField eventDateField;
    @FXML private TextField nbPlaceField;
    @FXML private TextField libelleField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private AnchorPane contenuPane;

    @FXML private Label errorNbPlace;
    @FXML private Label errorLibelle;
    @FXML private Label errorEtat;
    @FXML
    private Label eventTitle, eventDate, eventType, eventPrix;
    @FXML private ImageView qrCodeImageView;
    @FXML private VBox qrCodeContainer; // Ajoutez ce VBox dans votre FXML
    @FXML
    private TextField ticketCountField;

    private Event selectedEvent;

    // ✅ Setter utilisé par EvenementController
    public void setEventDetails(Event event) {
        if (event == null) {
            showAlert("Erreur", "Aucun événement sélectionné.");
            return;
        }

        this.selectedEvent = event;
        eventNameField.setText(event.getNom());
        eventDateField.setText(event.getDate_e().toString());
        etatComboBox.getItems().addAll("VIP", "Simple");
    }

    @FXML
    private void handleReservation() {
        clearErrors();

        String nbPlace = nbPlaceField.getText().trim();
        String libelle = libelleField.getText().trim();
        String etat = etatComboBox.getValue();

        boolean valid = true;
        int places = 0;

        if (nbPlace.isEmpty()) {
            errorNbPlace.setText("Champ requis.");
            valid = false;
        } else {
            try {
                places = Integer.parseInt(nbPlace);
                if (places <= 0) {
                    errorNbPlace.setText("Le nombre doit être positif.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                errorNbPlace.setText("Veuillez entrer un nombre valide.");
                valid = false;
            }
        }

        if (libelle.isEmpty()) {
            errorLibelle.setText("Champ requis.");
            valid = false;
        } else if (!libelle.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            errorLibelle.setText("Le libellé ne doit contenir que des lettres.");
            valid = false;
        }

        if (etat == null || etat.isEmpty()) {
            errorEtat.setText("Veuillez sélectionner un état.");
            valid = false;
        }

        if (!valid) return;

        Reservation reservation = new Reservation();
        reservation.setEvent(selectedEvent);
        reservation.setNb_place(places);
        reservation.setLibelle(libelle);
        reservation.setEtat_e(etat);

        if (user != null) {
            reservation.setUser_id_id(user.getId());
        } else {
            showAlert("Erreur", "Utilisateur non connecté.");
            return;
        }

        try {
            ServiceReservation service = new ServiceReservation();
            boolean success = service.reserverEtDecrementer(reservation);

            if (success) {
                showInfo("Succès", "Réservation confirmée pour l’événement : " + selectedEvent.getNom());
                generateAndShowQRCode(reservation);

                clearFields();
            } else {
                showAlert("Erreur", "Impossible de réserver. Il  reste que "  + selectedEvent.getNb_ticket()+"places disponibles");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la réservation.");
        }
    }

    private void clearFields() {
        nbPlaceField.clear();
        libelleField.clear();
        etatComboBox.setValue(null);
        clearErrors();
    }

    private void clearErrors() {
        errorNbPlace.setText("");
        errorLibelle.setText("");
        errorEtat.setText("");
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/Evenement.fxml"));
            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void generateAndShowQRCode(Reservation reservation) {
        try {
            // Générer les détails du ticket (vous pouvez le conserver ou le modifier)
            TicketGenerator.generateTicket(reservation, user, selectedEvent);

            // Construire les données du QR Code
            String qrData = String.format(
                    "Réservation:\n" +
                            "Événement: %s\n" +
                            "Date: %s\n" +
                            "Type: %s\n" +
                            "Nombre de places: %d\n" +
                            "Libellé: %s\n" +
                            "État: %s\n" +
                            "Utilisateur: %s %s",
                    selectedEvent.getNom(),
                    selectedEvent.getDate_e(),
                    selectedEvent.getType_e(),
                    reservation.getNb_place(),
                    reservation.getLibelle(),
                    reservation.getEtat_e(),
                    user.getFirst_Name(),
                    user.getLast_Name()
            );

            // Encoder les données pour le QR Code
            String encodedData = URLEncoder.encode(qrData, StandardCharsets.UTF_8.toString());

            // Générer l'URL du QR Code via une API externe
            String apiUrl = "https://api.qrserver.com/v1/create-qr-code/";
            String parameters = String.format("?size=200x200&data=%s", encodedData);
            String qrCodeUrl = apiUrl + parameters;

            // Récupérer le chemin de l'image de l'événement
            String imagePath = "/imagesEvent/" + selectedEvent.getPhoto_e(); // Assurez-vous que getPhoto_e() renvoie le nom du fichier image

            // Charger le fichier FXML pour afficher le ticket
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/qrcode.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur du ticket
            TicketController ticketController = loader.getController();

            // Passer les informations au contrôleur du ticket
            ticketController.setTicketData(
                    imagePath, // Passer le chemin de l'image à afficher
                    selectedEvent.getNom(),
                    selectedEvent.getDate_e().toString(),
                    selectedEvent.getType_e(),
                    "Places: " + reservation.getNb_place() + " | " + reservation.getLibelle(),
                    qrCodeUrl  // URL du QR Code généré
            );

            // Créer et afficher la nouvelle fenêtre pour le ticket
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Votre Ticket de Réservation");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le Ticket : " + e.getMessage());
        }
    }



}
