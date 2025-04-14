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

public class AjouterRpController {

    @FXML
    private Button AfficherBtRp;

    @FXML
    private Button AjouterBtRp;

    @FXML
    private TextField DescriptionTextFieldRp;

    @FXML
    private Label DateLabel;

    @FXML
    private Label DescLabel;


    @FXML
    private DatePicker dateRp;

    private int reclamationId;

    private Runnable onReponseAjoute;

    public void setReclamationId(int id) {
        this.reclamationId = id;
    }

    public void setOnReponseAjoute(Runnable onReponseAjoute) {
        this.onReponseAjoute = onReponseAjoute;
    }

//    @FXML
//    void handleAjouter(ActionEvent event) {
//        String desc = DescriptionTextFieldRp.getText();
//        String date = (dateRp.getValue() != null) ? dateRp.getValue().toString() : "";
//
//        if (desc.isEmpty() || date.isEmpty()) {
//            System.out.println("Veuillez remplir tous les champs");
//            return;
//        }
//
//        Reponse rep = new Reponse();
//        rep.setDescr_rep(desc);
//        rep.setDate_rep(date);
//        rep.setReclamation_id(reclamationId);
//
//        ServiceReponse sr = new ServiceReponse();
//        try {
//            sr.ajouter(rep);
//
//            if (onReponseAjoute != null) {
//                onReponseAjoute.run(); // 🌀 Rafraîchit la liste
//            }
//
//            // 🚪 Ferme la fenêtre actuelle
//            Stage stage = (Stage) AjouterBtRp.getScene().getWindow();
//            stage.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
private final List<String> badWords = Arrays.asList("idiot", "nul", "stupide", "bête");

    @FXML
    void handleAjouter(ActionEvent event) {
        // Vider les anciens messages
        DescLabel.setText("");
        DateLabel.setText("");

        String desc = DescriptionTextFieldRp.getText().trim();
        String dateStr = (dateRp.getValue() != null) ? dateRp.getValue().toString() : "";

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
            DateLabel.setText("Veuillez choisir une date.");
            hasError = true;
        }

        if (hasError) return;

        // Si tout est valide
        Reponse rep = new Reponse();
        rep.setDescr_rep(desc);
        rep.setDate_rep(dateStr);
        rep.setReclamation_id(reclamationId);

        try {
            new ServiceReponse().ajouter(rep);

            if (onReponseAjoute != null) onReponseAjoute.run();

            // ✅ Afficher une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réponse ajoutée avec succès !");
            alert.showAndWait();

            // 🚪 Fermer la fenêtre
            ((Stage) AjouterBtRp.getScene().getWindow()).close();

        } catch (SQLException e) {
            DescLabel.setText("Erreur lors de l'ajout.");
            DescLabel.setStyle("-fx-text-fill: red;");
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
