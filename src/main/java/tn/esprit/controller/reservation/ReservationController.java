package tn.esprit.controller.reservation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reservation;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;

public class ReservationController {

    // ✅ Utilisateur connecté transmis depuis EvenementController
   private Personne user= Session.getUser();

    @FXML private TextField eventNameField;
    @FXML private TextField eventDateField;
    @FXML private TextField nbPlaceField;
    @FXML private TextField libelleField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private AnchorPane contenuPane;

    @FXML private Label errorNbPlace;
    @FXML private Label errorLibelle;
    @FXML private Label errorEtat;

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

        // ✅ Création de la réservation avec utilisateur connecté
        Reservation reservation = new Reservation();
        reservation.setEvent(selectedEvent);
        reservation.setNb_place(places);
        reservation.setLibelle(libelle);
        reservation.setEtat_e(etat);

        // ✅ Utilisation de userConnecte statique pour récupérer l'ID de l'utilisateur
        if (user != null) {
            reservation.setUser_id_id(user.getId());
        } else {
            showAlert("Erreur", "Utilisateur non connecté.");
            return;
        }

        try {
            ServiceReservation service = new ServiceReservation();
            service.ajouter(reservation);
            showInfo("Succès", "Réservation confirmée pour l’événement : " + selectedEvent.getNom());
            clearFields();
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
}
