package tn.esprit.controller.evenement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Event;
import tn.esprit.utils.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class modifierEvenementController {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextField infoField;
    @FXML private DatePicker datePicker;
    @FXML private TextField nbTicketsField;
    @FXML private TextField prixSField;
    @FXML private TextField prixVIPField;
    @FXML private Label nomErrorLabel;
    @FXML private Label typeErrorLabel;
    @FXML private Label infoErrorLabel;
    @FXML private Label nbTicketsErrorLabel;
    @FXML private Label prixSErrorLabel;
    @FXML private Label prixVIPErrorLabel;
    @FXML private AnchorPane contenuPane;

    private Event evenementModifie;

    public void setEvenementModifie(Event e) {
        this.evenementModifie = e;
        nomField.setText(e.getNom());
        typeField.setText(e.getType_e());
        infoField.setText(e.getInfo_e());
        datePicker.setValue(e.getDate_e());
        nbTicketsField.setText(String.valueOf(e.getNb_ticket()));
        prixSField.setText(String.valueOf(e.getPrix_s()));
        prixVIPField.setText(String.valueOf(e.getPrix_vip()));
    }

    @FXML
    private void handleSave() {
        boolean isValid = validateInputs(); // Valider les champs avant de sauvegarder

        if (!isValid) {
            return; // Ne pas enregistrer si les données sont invalides
        }

        if (evenementModifie == null) return;

        try {
            // Mettre à jour l'objet local
            evenementModifie.setNom(nomField.getText());
            evenementModifie.setType_e(typeField.getText());
            evenementModifie.setInfo_e(infoField.getText());
            evenementModifie.setDate_e(datePicker.getValue());
            evenementModifie.setNb_ticket(Integer.parseInt(nbTicketsField.getText()));
            evenementModifie.setPrix_s(Integer.parseInt(prixSField.getText()));
            evenementModifie.setPrix_vip(Integer.parseInt(prixVIPField.getText()));

            String query = "UPDATE event SET nom = ?, type_e = ?, info_e = ?, date_e = ?, nb_ticket = ?, prix_s = ?, prix_vip = ? WHERE id = ?";

            try (Connection conn = DBConnection.getInstance().getCnx();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, evenementModifie.getNom());
                stmt.setString(2, evenementModifie.getType_e());
                stmt.setString(3, evenementModifie.getInfo_e());
                stmt.setDate(4, java.sql.Date.valueOf(evenementModifie.getDate_e()));
                stmt.setInt(5, evenementModifie.getNb_ticket());
                stmt.setInt(6, evenementModifie.getPrix_s());
                stmt.setInt(7, evenementModifie.getPrix_vip());
                stmt.setInt(8, evenementModifie.getId());

                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    System.out.println("✅ Événement modifié avec succès !");
                    // Redirection vers la liste après sauvegarde
                    handleRetour();
                }

            } catch (SQLException e) {
                System.out.println("❌ Erreur lors de la mise à jour de l'événement : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur dans les champs saisis : " + e.getMessage());
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

    // Validation des champs
    private boolean validateInputs() {
        boolean isValid = true;

        // Validation du champ "Nom"
        if (nomField.getText().isEmpty()) {
            nomErrorLabel.setText("Le nom de l'événement ne peut pas être vide");
            isValid = false;
        } else {
            nomErrorLabel.setText("");
        }

        // Validation du champ "Type"
        if (typeField.getText().isEmpty()) {
            typeErrorLabel.setText("Le type de l'événement ne peut pas être vide");
            isValid = false;
        } else {
            typeErrorLabel.setText("");
        }

        // Validation du champ "Info"
        if (infoField.getText().isEmpty()) {
            infoErrorLabel.setText("Les informations ne peuvent pas être vides");
            isValid = false;
        } else {
            infoErrorLabel.setText("");
        }

        // Validation du champ "NbTickets" (doit être un nombre entier positif)
        try {
            int nbTickets = Integer.parseInt(nbTicketsField.getText());
            if (nbTickets <= 0) {
                nbTicketsErrorLabel.setText("Le nombre de tickets doit être supérieur à zéro");
                isValid = false;
            } else {
                nbTicketsErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            nbTicketsErrorLabel.setText("Veuillez entrer un nombre valide pour le nombre de tickets");
            isValid = false;
        }

        // Validation du champ "PrixS" (doit être un nombre positif)
        try {
            int prixS = Integer.parseInt(prixSField.getText());
            if (prixS < 0) {
                prixSErrorLabel.setText("Le prix standard ne peut pas être négatif");
                isValid = false;
            } else {
                prixSErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            prixSErrorLabel.setText("Veuillez entrer un prix valide");
            isValid = false;
        }

        // Validation du champ "PrixVIP" (doit être un nombre positif)
        try {
            int prixVIP = Integer.parseInt(prixVIPField.getText());
            if (prixVIP < 0) {
                prixVIPErrorLabel.setText("Le prix VIP ne peut pas être négatif");
                isValid = false;
            } else {
                prixVIPErrorLabel.setText("");
            }
        } catch (NumberFormatException e) {
            prixVIPErrorLabel.setText("Veuillez entrer un prix valide");
            isValid = false;
        }

        // Validation de la date
        if (datePicker.getValue() == null) {
            isValid = false;
            // Pas besoin de message d'erreur pour la date ici car DatePicker gère déjà cela
        }

        return isValid;
    }
}
