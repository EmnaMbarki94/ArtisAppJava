//package tn.esprit.controller.Reclamation;
//
//import javafx.collections.FXCollections;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.stage.Stage;
//import tn.esprit.entities.Reclamtion;
//import tn.esprit.services.ServiceReclamation;
//
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//public class ModifierReController {
//
//    @FXML
//    private Button AfficherBt;
//
//    @FXML
//    private TextField DescriptionTextField1;
//
//    @FXML
//    private Button ModifierBt;
//
//
//
//    @FXML
//    private ComboBox<String> TypeComboBox1;
//
//    @FXML
//    private DatePicker date1;
//
//    private final ServiceReclamation service = new ServiceReclamation();
//
//    private final List<String> badWords = Arrays.asList("idiot", "nul", "stupide", "bête");
//    private Reclamtion currentReclamation;
//
//    @FXML
//    void modifierReclamation(ActionEvent event) {
//        if (currentReclamation == null) return;
//
//        String desc = DescriptionTextField1.getText().trim();
//        String type = TypeComboBox1.getValue();
//        LocalDate localDate = date1.getValue();
//
//        if (desc.isEmpty() || type == null || localDate == null) {
//            showAlert("Veuillez remplir tous les champs.");
//            return;
//        }
//
//        if (desc.length() < 5) {
//            showAlert("La description doit contenir au moins 5 caractères.");
//            return;
//        }
//
//        if (containsBadWords(desc)) {
//            showAlert("La description contient des mots inappropriés.");
//            return;
//        }
//
//        currentReclamation.setDesc_r(desc);
//        currentReclamation.setType_r(type);
//        currentReclamation.setDate_r(localDate.toString());
//
//        try {
//            service.modifier(currentReclamation);
//            ((Stage) ModifierBt.getScene().getWindow()).close();
//        } catch (Exception e) {
//            showAlert("Erreur lors de la modification.");
//        }
//    }
//
//    public void setReclamationData(Reclamtion reclamation) {
//        this.currentReclamation = reclamation;
//        DescriptionTextField1.setText(reclamation.getDesc_r());
//        if (TypeComboBox1.getItems().isEmpty())
//            TypeComboBox1.setItems(FXCollections.observableArrayList("Technique", "Service", "Produit", "Autre"));
//        TypeComboBox1.setValue(reclamation.getType_r());
////        date1.setValue(LocalDate.parse(reclamation.getDate_r()));
//        try {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDate date = LocalDate.parse(reclamation.getDate_r(), formatter);
//            date1.setValue(date);
//        } catch (DateTimeParseException e) {
//            e.printStackTrace();
//            showAlert("Le format de la date est invalide : " + reclamation.getDate_r());
//        }
//
//    }
//
//    private boolean containsBadWords(String text) {
//        return badWords.stream().anyMatch(text.toLowerCase()::contains);
//    }
//
//    private void showAlert(String msg) {
//        Alert alert = new Alert(Alert.AlertType.WARNING);
//        alert.setContentText(msg);
//        alert.showAndWait();
//    }
//
//
//
//
//}
package tn.esprit.controller.Reclamation;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamtion;
import tn.esprit.services.ServiceReclamation;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ModifierReController {

    @FXML
    private TextField DescriptionTextField1;

    @FXML
    private Button ModifierBt;

    @FXML
    private ComboBox<String> TypeComboBox1;

    @FXML
    private DatePicker date1;

    @FXML private Label DescLabel1;
    @FXML private Label TypeLabel1;
    @FXML private Label DateLabel1;

    private final ServiceReclamation service = new ServiceReclamation();

    private final List<String> badWords = Arrays.asList("idiot", "nul", "stupide", "bête");

    private Reclamtion currentReclamation;

    @FXML
    void modifierReclamation(ActionEvent event) {
        if (currentReclamation == null) return;

        // Nettoyer les anciens messages
        DescLabel1.setText("");
        TypeLabel1.setText("");
        DateLabel1.setText("");

        String desc = DescriptionTextField1.getText().trim();
        String type = TypeComboBox1.getValue();
        LocalDate localDate = date1.getValue();

        boolean hasError = false;

        if (desc.isEmpty()) {
            DescLabel1.setText("Veuillez saisir une description.");
            hasError = true;
        } else if (desc.length() < 5) {
            DescLabel1.setText("La description doit contenir au moins 5 caractères.");
            hasError = true;
        } else if (containsBadWords(desc)) {
            DescLabel1.setText("La description contient des mots inappropriés.");
            hasError = true;
        }

        if (type == null) {
            TypeLabel1.setText("Veuillez sélectionner un type.");
            hasError = true;
        }

        if (localDate == null) {
            DateLabel1.setText("Veuillez choisir une date.");
            hasError = true;
        }

        if (hasError) return;

        // Si tout est valide, on met à jour la réclamation
        currentReclamation.setDesc_r(desc);
        currentReclamation.setType_r(type);
        currentReclamation.setDate_r(localDate.toString());

        try {
            service.modifier(currentReclamation);

            // ✅ Affiche une alerte de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Réclamation modifiée avec succès !");
            alert.showAndWait();

            ((Stage) ModifierBt.getScene().getWindow()).close();
        } catch (Exception e) {
            DescLabel1.setText("Erreur lors de la modification.");
            DescLabel1.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }


    public void setReclamationData(Reclamtion reclamation) {
        this.currentReclamation = reclamation;
        DescriptionTextField1.setText(reclamation.getDesc_r());

        if (TypeComboBox1.getItems().isEmpty())
            TypeComboBox1.setItems(FXCollections.observableArrayList("Technique", "Service", "Produit", "Autre"));

        TypeComboBox1.setValue(reclamation.getType_r());

        // Format attendu : "2025-04-10 00:00:00" → on prend juste la partie date
        try {
            String rawDate = reclamation.getDate_r().split(" ")[0]; // Prend "2025-04-10"
            LocalDate date = LocalDate.parse(rawDate); // format ISO 8601 : yyyy-MM-dd
            date1.setValue(date);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Le format de la date est invalide : " + reclamation.getDate_r());
        }
    }


    private boolean containsBadWords(String text) {
        return badWords.stream().anyMatch(text.toLowerCase()::contains);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
