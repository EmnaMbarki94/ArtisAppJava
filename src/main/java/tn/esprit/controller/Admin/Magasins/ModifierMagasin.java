package tn.esprit.controller.Admin.Magasins;

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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class ModifierMagasin {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfType;

    @FXML
    private ImageView imageViewMagasin;

    @FXML
    private Button btnModifier;

    @FXML
    private Label lblErrorNom;

    @FXML
    private Label lblErrorType;

    @FXML
    private Label lblErrorImage;

    private File imageMagasin;
    private Magasin magasinActuel;

    public void setMagasin(Magasin magasin) {
        this.magasinActuel = magasin;
        tfNom.setText(magasin.getNom_m());
        tfType.setText(magasin.getType_m());

        if (magasin.getPhoto_m() != null) {
            // Essayer d'abord le classpath (pour le runtime)
            InputStream is = getClass().getResourceAsStream("/image/magasin/" + magasin.getPhoto_m());
            if (is != null) {
                imageViewMagasin.setImage(new Image(is));
            } else {
                // Fallback sur le dossier uploads
                File imageFile = new File("/image/magasin/" + magasin.getPhoto_m());
                if (imageFile.exists()) {
                    imageViewMagasin.setImage(new Image(imageFile.toURI().toString()));
                }
            }
        }
    }

    @FXML
    private void initialize() {
        btnModifier.setOnAction(event -> modifierMagasin());
    }

    @FXML
    private void modifierMagasin() {
        boolean isValid = true;

        String nom = tfNom.getText();
        String type = tfType.getText();

        // Reset errors
        lblErrorNom.setText("");
        lblErrorType.setText("");
        lblErrorImage.setText("");

        if (nom == null || nom.trim().isEmpty()) {
            lblErrorNom.setText("Nom requis");
            isValid = false;
        }

        if (type == null || type.trim().isEmpty()) {
            lblErrorType.setText("Type requis");
            isValid = false;
        }

        if (!isValid) return;

        magasinActuel.setNom_m(nom);
        magasinActuel.setType_m(type);

        if (imageMagasin != null) {
            try {
                // Chemin pour le développement (stockage permanent)
                String devPath = "uploads/images/magasin/";
                new File(devPath).mkdirs();

                // Chemin pour les ressources (classpath)
                String resPath = "target/classes/image/magasin/";
                new File(resPath).mkdirs();

                String fileName = System.currentTimeMillis() + "_" + imageMagasin.getName();

                // Copie dans le dossier de développement
                File devFile = new File(devPath + fileName);
                Files.copy(imageMagasin.toPath(), devFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Copie dans le classpath (pour le runtime)
                File resFile = new File(resPath + fileName);
                Files.copy(imageMagasin.toPath(), resFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                magasinActuel.setPhoto_m(fileName);
            } catch (IOException e) {
                lblErrorImage.setText("Erreur lors de la sauvegarde de l'image");
                return;
            }
        }

        try {
            new ServiceMagasin().modifier(magasinActuel);
            retournerAccueil();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageMagasin = selectedFile;
            imageViewMagasin.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void retournerAccueil() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml"));
        AnchorPane pageAccueil = loader.load();

        AnchorPane root = (AnchorPane) tfNom.getScene().lookup("#contenuPane");
        root.getChildren().setAll(pageAccueil);
    }
}
