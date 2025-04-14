package tn.esprit.controller.Admin.Magasins;

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
import tn.esprit.services.ServiceArticle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ModifierArticle {

    @FXML
    private TextField tfNom, tfPrix, tfQuantite;

    @FXML
    private TextArea tfDescription;

    @FXML
    private Label lblErrorNom, lblErrorDescription, lblErrorPrix, lblErrorQuantite, lblErrorImage;

    @FXML
    private ImageView imageViewArticle;

    @FXML
    private Button btnModifier;

    private File nouvelleImage;
    private Article articleExistant;

    public void setArticle(Article article) {
        this.articleExistant = article;

        // Pré-remplir les champs
        tfNom.setText(article.getNom_a());
        tfPrix.setText(String.valueOf(article.getPrix_a()));
        tfQuantite.setText(String.valueOf(article.getQuantite()));
        tfDescription.setText(article.getDesc_a());

        if (article.getImage_path() != null) {
            String path = "/image/article/" + article.getImage_path();
            Image image = new Image(getClass().getResource(path).toExternalForm());
            imageViewArticle.setImage(image);
        }
    }

    @FXML
    private void initialize() {
        btnModifier.setOnAction(e -> modifierArticle());
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
            nouvelleImage = selectedFile;
            Image image = new Image(selectedFile.toURI().toString());
            imageViewArticle.setImage(image);
        }
    }

    @FXML
    private void modifierArticle() {
        resetStyles();

        String nom = tfNom.getText();
        String prixStr = tfPrix.getText();
        String quantiteStr = tfQuantite.getText();
        String description = tfDescription.getText();

        boolean valid = true;

        if (nom.isEmpty()) {
            lblErrorNom.setText("Le nom est requis.");
            tfNom.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (description.isEmpty()) {
            lblErrorDescription.setText("La description est requise.");
            tfDescription.setStyle("-fx-border-color: red;");
            valid = false;
        }

        double prix = 0;
        try {
            prix = Double.parseDouble(prixStr);
            if (prix < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblErrorPrix.setText("Prix invalide.");
            tfPrix.setStyle("-fx-border-color: red;");
            valid = false;
        }

        int quantite = 0;
        try {
            quantite = Integer.parseInt(quantiteStr);
            if (quantite < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblErrorQuantite.setText("Quantité invalide.");
            tfQuantite.setStyle("-fx-border-color: red;");
            valid = false;
        }

        if (!valid) return;

        try {
            articleExistant.setNom_a(nom);
            articleExistant.setPrix_a(prix);
            articleExistant.setQuantite(quantite);
            articleExistant.setDesc_a(description);

            if (nouvelleImage != null) {
                String destinationPath = "src/main/resources/image/article/";
                String fileName = System.currentTimeMillis() + "_" + nouvelleImage.getName();
                File destFile = new File(destinationPath + fileName);
                Files.copy(nouvelleImage.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                articleExistant.setImage_path(fileName);
            }

            new ServiceArticle().modifier(articleExistant);
            retournerListeArticles();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void resetStyles() {
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

    @FXML
    private void retournerListeArticles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/ArticleAdmin.fxml"));
            AnchorPane pageArticles = loader.load();
            ArticleController controller = loader.getController();
            controller.setMagasinId(articleExistant.getMagasin().getId());
            AnchorPane root = (AnchorPane) tfNom.getScene().lookup("#contenuPane");
            root.getChildren().setAll(pageArticles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
