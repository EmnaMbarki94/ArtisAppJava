package tn.esprit.controller.Admin.Magasins;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import tn.esprit.entities.Article;
import tn.esprit.entities.Magasin;
import tn.esprit.services.ServiceArticle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AjoutArticle {

    @FXML
    private TextField tfNom, tfPrix, tfQuantite;

    @FXML
    private TextArea tfDescription;

    @FXML
    private Label lblErrorNom, lblErrorPrix, lblErrorQuantite, lblErrorDescription, lblErrorImage;

    @FXML
    private ImageView imageViewArticle;

    @FXML
    private Button btnAjouter;

    private File imageArticle;
    private int magasinId;


    @FXML
    private void initialize() {
        btnAjouter.setOnAction(this::ajouterArticle);
    }


    @FXML
    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imageArticle = selectedFile;
            Image image = new Image(selectedFile.toURI().toString());
            imageViewArticle.setImage(image);
        }
    }

    @FXML
    private void ajouterArticle(ActionEvent event) {
        resetErrors(); // Réinitialiser les messages d'erreur

        String nom = tfNom.getText().trim();
        String prixStr = tfPrix.getText().trim();
        String quantiteStr = tfQuantite.getText().trim();
        String description = tfDescription.getText().trim();
        boolean isValid = true;

        if (magasinId == 0) {
            System.out.println("Erreur : magasinId non défini.");
            return;
        }

        // Vérification du nom (lettres uniquement)
        if (nom.isEmpty()) {
            lblErrorNom.setText("Le nom est requis.");
            tfNom.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!nom.matches("[\\p{L}\\s]+")) {
            lblErrorNom.setText("Le nom doit contenir uniquement des lettres.");
            tfNom.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        // Vérification du prix
        double prix = 0;
        if (prixStr.isEmpty()) {
            lblErrorPrix.setText("Le prix est requis.");
            tfPrix.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                prix = Double.parseDouble(prixStr);
                if (prix <= 0) {
                    lblErrorPrix.setText("Le prix doit être positif.");
                    tfPrix.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                lblErrorPrix.setText("Prix invalide.");
                tfPrix.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Vérification de la quantité
        int quantite = 0;
        if (quantiteStr.isEmpty()) {
            lblErrorQuantite.setText("La quantité est requise.");
            tfQuantite.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            try {
                quantite = Integer.parseInt(quantiteStr);
                if (quantite <= 0) {
                    lblErrorQuantite.setText("La quantité doit être positive.");
                    tfQuantite.setStyle("-fx-border-color: red;");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                lblErrorQuantite.setText("Quantité invalide.");
                tfQuantite.setStyle("-fx-border-color: red;");
                isValid = false;
            }
        }

        // Vérification de la description
        if (description.isEmpty()) {
            lblErrorDescription.setText("La description est requise.");
            tfDescription.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        // Vérification de l'image
        if (imageArticle == null) {
            lblErrorImage.setText("Veuillez choisir une image.");
            isValid = false;
        }

        if (!isValid) {
            return; // Si des erreurs sont présentes, on arrête l'ajout
        }

        // Copie de l'image
        try {
            String destinationPath = "src/main/resources/image/article/";
            String fileName = System.currentTimeMillis() + "_" + imageArticle.getName();
            File destFile = new File(destinationPath + fileName);
            Files.copy(imageArticle.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Création de l'article
            Magasin magasin = new Magasin();
            magasin.setId(magasinId);

            Article article = new Article();
            article.setNom_a(nom);
            article.setPrix_a(prix);
            article.setDesc_a(description);
            article.setQuantite(quantite);
            article.setImage_path(fileName);
            article.setMagasin(magasin);

            new ServiceArticle().ajouter(article);
            System.out.println("✅ Article ajouté avec succès !");
            retournerListeArticles();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la copie de l'image.");
        }
    }


    private void resetErrors() {
        tfNom.setStyle(null);
        tfPrix.setStyle(null);
        tfQuantite.setStyle(null);
        tfDescription.setStyle(null);

        lblErrorNom.setText("");
        lblErrorPrix.setText("");
        lblErrorQuantite.setText("");
        lblErrorDescription.setText("");
        lblErrorImage.setText("");
    }

    public void setMagasinId(int magasinId) {
        this.magasinId = magasinId;
    }

    @FXML
    private void retournerListeArticles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/ArticleAdmin.fxml"));
            AnchorPane pageArticles = loader.load();

            // Récupère le contrôleur de la page chargée
            ArticleController articleController = loader.getController();

            // Passe le magasinId pour qu’il recharge les articles
            articleController.setMagasinId(magasinId);

            // Met à jour l’interface
            AnchorPane root = (AnchorPane) tfNom.getScene().lookup("#contenuPane");
            root.getChildren().setAll(pageArticles);

            System.out.println("Retour à la liste des articles !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
