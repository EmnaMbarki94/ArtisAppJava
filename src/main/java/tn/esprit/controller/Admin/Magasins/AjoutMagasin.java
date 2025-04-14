package tn.esprit.controller.Admin.Magasins;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import tn.esprit.entities.Magasin;
import tn.esprit.services.ServiceMagasin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AjoutMagasin {

    @FXML
    private Button btnAjouter;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfType;

    @FXML
    private Label lblErrorNom;
    @FXML
    private Label lblErrorType;
    @FXML
    private Label lblErrorImage;

    @FXML
    private ImageView imageViewMagasin; // Assure-toi de l’avoir dans ton FXML

    private File imageMagasin;

    @FXML
    private void initialize() {

    }


    @FXML
    private void ajouterMagasin() {
        resetStyles(); // Nettoyer les anciens messages

        String nom = tfNom.getText().trim();
        String type = tfType.getText().trim();
        boolean valid = true;

        if (nom.isEmpty()) {
            lblErrorNom.setText("Le nom est requis.");
            tfNom.setStyle("-fx-border-color: red;");
            valid = false;
        } else if (!nom.matches("[a-zA-Z\\s]+")) {
            lblErrorNom.setText("Le nom doit contenir uniquement des lettres.");
            tfNom.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (type.isEmpty()) {
            lblErrorType.setText("Le type est requis.");
            tfType.setStyle("-fx-border-color: red;");
            valid = false;
        } else if (!type.matches("[a-zA-Z\\s]+")) {
            lblErrorType.setText("Le type doit contenir uniquement des lettres.");
            tfType.setStyle("-fx-border-color: red;");
            valid = false;
        }


        if (!valid) {
            return;
        }

        // Copie de l’image
        String destinationPath = "src/main/resources/image/magasin/";
        String fileName = System.currentTimeMillis() + "_" + imageMagasin.getName();
        File destFile = new File(destinationPath + fileName);

        try {
            Files.copy(imageMagasin.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            Magasin magasin = new Magasin();
            magasin.setNom_m(nom);
            magasin.setType_m(type);
            magasin.setPhoto_m(fileName);

            new ServiceMagasin().ajouter(magasin);
            retournerAccueil();
            System.out.println("Magasin ajouté avec succès !");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetStyles() {
        tfNom.setStyle(null);
        tfType.setStyle(null);
        lblErrorNom.setText("");
        lblErrorType.setText("");
        lblErrorImage.setText("");
    }

    @FXML
    private void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageMagasin = selectedFile;
            Image image = new Image(selectedFile.toURI().toString());
            imageViewMagasin.setImage(image);
        }
    }
    @FXML
    private void retournerAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml"));
            AnchorPane pageAccueil = loader.load();

            // Remplace l'affichage actuel par celui des magasins
            tfNom.getScene().lookup("#contenuPane"); // Assure-toi que contenuPane existe dans la page d'origine
            AnchorPane root = (AnchorPane) tfNom.getScene().lookup("#contenuPane");
            root.getChildren().setAll(pageAccueil);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
