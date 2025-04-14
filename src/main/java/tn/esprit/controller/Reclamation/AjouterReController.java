
package tn.esprit.controller.Reclamation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReclamation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static tn.esprit.controller.users.ReponseController.user;

public class AjouterReController {
    private Personne user= Session.getUser();


    @FXML
    private Button AfficherBt;
    @FXML
    private Label DateLabel;

    @FXML
    private Label DescLabel;
    @FXML
    private Label TypeLabel;

    @FXML
    private Button AjouterBt;

    @FXML
    private TextField DescriptionTextField;


    @FXML
    private ComboBox<String> TypeComboBox;

    @FXML
    private DatePicker date;

    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final List<String> badWords = Arrays.asList("idiot", "nul", "stupide", "bête");

    @FXML
    public void initialize() {
        TypeComboBox.getItems().addAll("Technique", "Service", "Produit", "Autre");
    }

    @FXML
    void handleAjouter(ActionEvent event) {

        // Nettoyer les labels d'erreur
        DescLabel.setText("");
        TypeLabel.setText("");
        DateLabel.setText("");

        String desc = DescriptionTextField.getText().trim();
        String type = TypeComboBox.getValue();
        LocalDate localDate = date.getValue();

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

        if (type == null) {
            TypeLabel.setText("Veuillez sélectionner un type.");
            hasError = true;
        }

        if (localDate == null) {
            DateLabel.setText("Veuillez choisir une date.");
            hasError = true;
        }

        if (hasError) return;

        String dateStr = localDate.toString();
        Reclamtion r = new Reclamtion(desc, dateStr, type, user);

//        try {
//            serviceReclamation.ajouter(r);
//
//            // Message de succès dans le label de description (ou tu peux en ajouter un autre dédié)
//            DescLabel.setText("Réclamation ajoutée avec succès !");
//            DescLabel.setStyle("-fx-text-fill: green;");
//
//            // Réinitialiser les champs
//            DescriptionTextField.clear();
//            TypeComboBox.getSelectionModel().clearSelection();
//            date.setValue(null);
//
//            ((Stage) AjouterBt.getScene().getWindow()).close();
//
//        }
        try {
        serviceReclamation.ajouter(r);

        // ✅ Afficher une alerte de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Réclamation ajoutée avec succès !");
        alert.showAndWait();

        // Réinitialiser les champs
        DescriptionTextField.clear();
        TypeComboBox.getSelectionModel().clearSelection();
        date.setValue(null);

        // Fermer la fenêtre après l'ajout
        ((Stage) AjouterBt.getScene().getWindow()).close();

    }catch (SQLException e) {
            DescLabel.setText("Erreur lors de l'ajout : " + e.getMessage());
            DescLabel.setStyle("-fx-text-fill: red;");
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

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    @FXML
    void handleAfficher(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/AfficherRecl.fxml"));
            Parent root = loader.load();

            // Option 1 : si tu veux remplacer toute la fenêtre actuelle
//            Stage stage = (Stage) AfficherBt.getScene().getWindow();
//            stage.setScene(new Scene(root));
//            stage.setTitle("Liste des Réclamations");

//             Option 2 : si tu veux afficher dans une nouvelle fenêtre
//             Stage newStage = new Stage();
//             newStage.setScene(new Scene(root));
//             newStage.setTitle("Liste des Réclamations");
//             newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'afficher la page");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

}
