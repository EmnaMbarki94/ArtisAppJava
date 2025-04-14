package tn.esprit.controller.Reponse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ServiceReponse;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifierRpController {

    @FXML
    private Button AfficherBtRp;

    @FXML
    private Label DescLabel;

    @FXML
    private TextField DescriptionTextFieldRpMf;

    @FXML
    private Button EnregistrerBtRp;

    @FXML
    private DatePicker dateRpMf;

    private Reponse reponse;  // L'objet Reponse à modifier
    private final ServiceReponse serviceReponse = new ServiceReponse(); // Le service pour effectuer les opérations de base de données

    @FXML
    public void initialize() {
        // Initialiser tout le nécessaire si besoin
    }

    // Méthode pour recevoir l'objet Reponse depuis le contrôleur parent
    public void initData(Reponse reponse) {
        this.reponse = reponse;
        // Pré-remplir les champs avec les données actuelles
        DescriptionTextFieldRpMf.setText(reponse.getDescr_rep());
        dateRpMf.setValue(java.time.LocalDate.parse(reponse.getDate_rep()));  // Assurez-vous que la date est bien formatée
    }

    private final List<String> badWords = Arrays.asList("idiot", "nul", "stupide", "bête");



    @FXML
    void handleAjouter(ActionEvent event) {
        // Nettoyer les anciens messages d'erreur
        DescLabel.setText("");

        String desc = DescriptionTextFieldRpMf.getText().trim();
        String dateStr = (dateRpMf.getValue() != null) ? dateRpMf.getValue().toString() : "";

        boolean hasError = false;

        if (desc.isEmpty()) {
            DescLabel.setText("Veuillez saisir une description.");
            hasError = true;
        } else if (desc.length() < 5) {
            DescLabel.setText("La description doit contenir au moins 5 caractères.");
            hasError = true;
        } else if (containsBadWords(desc)) {
            DescLabel.setText("La description contient des mots inappropriés.");
            hasError = true;
        }

        if (dateStr.isEmpty()) {
            DescLabel.setText("Veuillez choisir une date.");
            hasError = true;
        }

        if (hasError) return;

        reponse.setDescr_rep(desc);
        reponse.setDate_rep(dateStr);

        try {
            serviceReponse.modifier(reponse);

            // ✅ Affiche un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réponse modifiée avec succès !");
            alert.showAndWait();

            // 🚪 Fermer la fenêtre
            ((Stage) EnregistrerBtRp.getScene().getWindow()).close();
        } catch (SQLException e) {
            DescLabel.setText("Erreur lors de la modification.");
            e.printStackTrace();
        }
    }


    private boolean containsBadWords(String text) {
        return badWords.stream().anyMatch(text.toLowerCase()::contains);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}
