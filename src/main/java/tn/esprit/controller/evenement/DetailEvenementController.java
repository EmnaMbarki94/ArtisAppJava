package tn.esprit.controller.evenement;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import tn.esprit.entities.Event;
import tn.esprit.controller.reservation.ReservationController;

import java.io.IOException;

public class DetailEvenementController {

    @FXML
    private ImageView eventImage;

    @FXML
    private Label eventNom, eventDate, eventType, eventInfo, prixStandard, prixVIP, ticketsRestants, statut;

    @FXML
    private Button reserverButton;

    @FXML
    private Button retourButton;

    @FXML
    private AnchorPane users_parent;

    private Event currentEvent;

    // Méthode pour définir les détails de l'événement
    public void setEventDetails(Event e) {
        this.currentEvent = e;

        eventNom.setText(e.getNom());
        eventDate.setText("Date : " + e.getDate_e());
        eventType.setText("Type : " + e.getType_e());
        eventInfo.setText("Description : " + e.getInfo_e());
        prixStandard.setText("Prix Standard : " + e.getPrix_s() + " DT");
        prixVIP.setText("Prix VIP : " + e.getPrix_vip() + " DT");
        ticketsRestants.setText("Tickets Restants : " + e.getNb_ticket());

        // Chargement de l'image de l'événement
        try {
            String imagePath = "/imagesEvent/" + e.getPhoto_e();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            eventImage.setImage(image);
        } catch (Exception ex) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/imagesEvent/default.png"));
            eventImage.setImage(defaultImage);
        }

        // Gérer le statut
        if (e.getNb_ticket() == 0) {
            statut.setText("Événement Complet");
            statut.setStyle("-fx-text-fill: red;");
            reserverButton.setDisable(true);
        } else {
            statut.setText("Événement Disponible");
            statut.setStyle("-fx-text-fill: green;");
            reserverButton.setDisable(false);
        }
    }

    // Méthode pour gérer le retour à la liste des événements
    @FXML
    private void handleRetourAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/Evenement.fxml"));
            AnchorPane retourView = loader.load();
            users_parent.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode d'initialisation du contrôleur
    @FXML
    private void initialize() {
        reserverButton.setOnAction(event -> handleReserverAction());
        eventImage.setOnMouseEntered(e -> {
            ScaleTransition zoomIn = new ScaleTransition(Duration.millis(200), eventImage);
            zoomIn.setToX(1.1);
            zoomIn.setToY(1.1);
            zoomIn.play();
        });

        eventImage.setOnMouseExited(e -> {
            ScaleTransition zoomOut = new ScaleTransition(Duration.millis(200), eventImage);
            zoomOut.setToX(1.0);
            zoomOut.setToY(1.0);
            zoomOut.play();
        });
    }

    // Méthode pour gérer l'action de réservation
    private void handleReserverAction() {
        try {
            System.out.println("Chargement FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/reservation.fxml"));
            AnchorPane reservationView = loader.load();

            System.out.println("FXML chargé avec succès.");

            // Vérifier l'accès au contrôleur
            ReservationController controller = loader.getController();
            if (controller != null) {
                // Passer l'événement courant au contrôleur
                controller.setEventDetails(currentEvent);
                System.out.println("Contrôleur initialisé.");
            } else {
                System.out.println("Erreur : contrôleur null !");
            }

            // Assurer que users_parent n’est pas null
            if (users_parent != null) {
                users_parent.getChildren().setAll(reservationView);
                System.out.println("Vue de réservation insérée !");
            } else {
                System.out.println("Erreur : users_parent est null !");
            }
        } catch (IOException e) {
            System.out.println("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
