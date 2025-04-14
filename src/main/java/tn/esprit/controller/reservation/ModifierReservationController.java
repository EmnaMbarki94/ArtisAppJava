package tn.esprit.controller.reservation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierReservationController {

    @FXML private TextField libelleField;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private TextField nbPlaceField;
    @FXML private TextField userIdField;
    @FXML private TextField eventIdField;
    @FXML private TextField eventNameField; // Champ non éditable pour afficher le nom de l'événement
    @FXML private AnchorPane contenuPane;

    @FXML private Label libelleErrorLabel;
    @FXML private Label nbPlaceErrorLabel;
    @FXML private Label userIdErrorLabel;
    @FXML private Label eventIdErrorLabel;

    private Reservation reservationModifie;

    public void setReservation(Reservation reservation) {
        this.reservationModifie = reservation;

        if (reservation != null) {
            libelleField.setText(reservation.getLibelle());
            nbPlaceField.setText(String.valueOf(reservation.getNb_place()));
            userIdField.setText(String.valueOf(reservation.getUser_id_id()));

            // Initialiser la ComboBox
            etatComboBox.getItems().addAll("VIP", "Simple", "en attente");
            etatComboBox.setValue(reservation.getEtat_e());

            // Vérification de l'événement associé
            if (reservation.getEvent() != null) {
                eventIdField.setText(String.valueOf(reservation.getEvent().getId()));
                eventNameField.setText(reservation.getEvent().getNom());
            } else {
                eventIdField.setText("");
                eventNameField.setText("Aucun événement lié");
                System.out.println("⚠ Aucun événement associé à cette réservation.");
            }
        }
    }

    @FXML
    private void handleSave() {
        boolean isValid = validateInputs(); // Valider les champs avant de sauvegarder

        if (!isValid) {
            return; // Ne pas enregistrer si les données sont invalides
        }

        if (reservationModifie == null) return;

        try {
            // Mise à jour des champs
            reservationModifie.setLibelle(libelleField.getText());
            reservationModifie.setEtat_e(etatComboBox.getValue());
            reservationModifie.setNb_place(Integer.parseInt(nbPlaceField.getText()));
            reservationModifie.setUser_id_id(Integer.parseInt(userIdField.getText()));

            // Mise à jour de l'ID de l'événement s’il est modifié manuellement
            if (reservationModifie.getEvent() != null) {
                reservationModifie.getEvent().setId(Integer.parseInt(eventIdField.getText()));
            }

            // Appel au service
            ServiceReservation service = new ServiceReservation();
            service.modifier(reservationModifie);

            showInfo("Succès", "Réservation modifiée avec succès !");
            handleRetour();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le nombre de places, l'ID utilisateur ou événement doit être un entier.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur SQL", "Une erreur est survenue lors de la mise à jour.");
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/afficherReservationA.fxml"));
            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Validation des champs
    private boolean validateInputs() {
        boolean isValid = true;

        // Validation du champ "Libelle"
        if (libelleField.getText().isEmpty()) {
            libelleErrorLabel.setText("Le libellé ne peut pas être vide");
            isValid = false;
        } else {
            libelleErrorLabel.setText("");
        }

        // Validation du champ "NbPlace" (doit être un entier positif)
        try {
            int nbPlaces = Integer.parseInt(nbPlaceField.getText());
            if (nbPlaces <= 0) {
                nbPlaceErrorLabel.setText("Le nombre de places doit être supérieur à zéro");
                isValid = false;
            } else {
                nbPlaceErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            nbPlaceErrorLabel.setText("Veuillez entrer un nombre valide pour le nombre de places");
            isValid = false;
        }

        // Validation du champ "UserID" (doit être un entier positif)
        try {
            Integer.parseInt(userIdField.getText()); // On vérifie si l'ID utilisateur est un entier
        } catch (NumberFormatException e) {
            userIdErrorLabel.setText("L'ID utilisateur doit être un entier valide");
            isValid = false;
        }

        // Validation du champ "EventID" (doit être un entier positif)
        try {
            Integer.parseInt(eventIdField.getText()); // On vérifie si l'ID de l'événement est un entier
        } catch (NumberFormatException e) {
            eventIdErrorLabel.setText("L'ID de l'événement doit être un entier valide");
            isValid = false;
        }

        return isValid;
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
