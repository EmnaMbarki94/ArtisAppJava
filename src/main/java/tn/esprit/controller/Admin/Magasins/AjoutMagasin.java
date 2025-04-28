package tn.esprit.controller.Admin.Magasins;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
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
import java.util.Collections;

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

        try {
            if (imageMagasin == null) {
                System.out.println("Aucune image sélectionnée");
                return;
            }

            // Chemin relatif dans votre projet
            String projectImageDir = "target/classes/image/magasin/";
            File directory = new File(projectImageDir);

            // Créer le dossier s'il n'existe pas
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Nom de fichier sécurisé avec timestamp
            String originalName = imageMagasin.getName();
            String safeFileName = System.currentTimeMillis() + "_" +
                    originalName.replaceAll("[^a-zA-Z0-9.-]", "_");

            // Chemin complet de destination
            File destFile = new File(projectImageDir + safeFileName);

            // Copier le fichier
            Files.copy(imageMagasin.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Image sauvegardée dans : " + destFile.getAbsolutePath());

            Magasin magasin = new Magasin();
            magasin.setNom_m(nom);
            magasin.setType_m(type);
            magasin.setPhoto_m(safeFileName); // Stocker uniquement le nom du fichier

            new ServiceMagasin().ajouter(magasin);
            retournerAccueil();
            System.out.println("Magasin ajouté avec succès !");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            // Ajouter un feedback utilisateur ici
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
            // Charger la page FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml"));
            AnchorPane pageAccueil = loader.load();

            // Obtenir la référence au contenuPane
            AnchorPane root = (AnchorPane) btnAjouter.getScene().lookup("#contenuPane");

            if (root != null) {
                // Solution 1: Utiliser une liste pour éviter l'ambiguïté
//                root.getChildren().setAll(Collections.singletonList(pageAccueil));

                // Solution alternative: Utiliser clear() puis add()
                 root.getChildren().clear();
                 root.getChildren().add(pageAccueil);

                // Obtenir le contrôleur
                AdminMagasinController controller = loader.getController();

                // Rafraîchir les données
                Platform.runLater(controller::rafraichirPage);
            } else {
                System.err.println("Erreur: contenuPane non trouvé dans la scène");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page d'accueil: " + e.getMessage());
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la page d'accueil.");
            alert.showAndWait();
        }
    }


}
