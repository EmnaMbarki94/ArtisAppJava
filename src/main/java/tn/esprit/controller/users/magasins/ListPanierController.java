package tn.esprit.controller.users.magasins;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Article;
import tn.esprit.entities.Panier;
import tn.esprit.entities.PanierItem;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ListPanierController implements Initializable {
    private static final double FRAIS_LIVRAISON = 8.0;

    @FXML
    private AnchorPane contenuPane;
    @FXML
    private Label subTotalLabel;
    @FXML
    private Label deliveryLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private VBox articlesContainer;
    @FXML
    private Button btnRetourMagasins;
    @FXML
    private Label quantityLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        afficherArticlesPanier();
    }

    public void afficherArticlesPanier() {
        articlesContainer.getChildren().clear();

        for (PanierItem item : Panier.getItems()) {
            Article article = item.getArticle();

            // Création de la carte
            HBox card = new HBox(15);
            card.getStyleClass().add("article-card");
            card.setAlignment(Pos.CENTER_LEFT);

            // Image de l'article
            ImageView imageView = new ImageView();
            Image image = loadProductImage(article); // Utilisation unique de la méthode de chargement

            imageView.setImage(image);
            imageView.getStyleClass().add("article-image");
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            System.out.println("Tentative de chargement de: " + article.getImage_path());

            // Informations de l'article
            VBox infoBox = new VBox(5);
            Label nameLabel = new Label(article.getNom_a());
            nameLabel.getStyleClass().add("article-name");

//            Label descLabel = new Label(article.getDesc_a());
//            descLabel.getStyleClass().add("article-desc");
//            descLabel.setMaxWidth(400);
//            descLabel.setWrapText(true);

            Label priceLabel = new Label(String.format("%.2f TND", article.getPrix_a()));
            priceLabel.getStyleClass().add("article-price");

            infoBox.getChildren().addAll(nameLabel,priceLabel);

            // Contrôle de quantité
            HBox quantityBox = new HBox(5);
            quantityBox.getStyleClass().add("quantity-control");
            quantityBox.setAlignment(Pos.CENTER);

            Button minusBtn = new Button("-");
            minusBtn.getStyleClass().add("quantity-btn");
            minusBtn.setOnAction(e -> {
                if (item.getQuantite() > 1) {
                    item.setQuantite(item.getQuantite() - 1);
                    updateQuantity(item, quantityLabel);
                    updateTotal();
                }
            });

            Label quantityLabel = new Label(String.valueOf(item.getQuantite()));
            quantityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Button plusBtn = new Button("+");
            plusBtn.getStyleClass().add("quantity-btn");
            plusBtn.setOnAction(e -> {
                item.setQuantite(item.getQuantite() + 1);
                updateQuantity(item, quantityLabel);
                updateTotal();
            });

            Button removeBtn = new Button("×");
            removeBtn.getStyleClass().add("quantity-btn");
            removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            removeBtn.setOnAction(e -> {
                Panier.getItems().remove(item);
                afficherArticlesPanier();
                updateTotal();
            });

            quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn, removeBtn);

            // Ajout à la carte
            card.getChildren().addAll(imageView, infoBox, quantityBox);
            articlesContainer.getChildren().add(card);
        }

        updateTotal();
    }
    private Image loadProductImage(Article article) {
        if (article == null || article.getImage_path() == null) {
            return getDefaultImage();
        }

        String imageName = article.getImage_path();
        String imagePath = "/image/article/" + imageName;

        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement: " + imagePath);
        }

        return getDefaultImage();
    }

    private Image getDefaultImage() {
        return new Image(getClass().getResourceAsStream("/image/magasin/imagedf.jpg"));
    }

    private void updateQuantity(PanierItem item, Label quantityLabel) {
        quantityLabel.setText(String.valueOf(item.getQuantite()));
    }

    private void updateTotal() {
        try {
            // Calcul du sous-total
            double sousTotal = Panier.getItems().stream()
                    .mapToDouble(item -> item.getArticle().getPrix_a() * item.getQuantite())
                    .sum();

            // Mise à jour des labels
            subTotalLabel.setText(String.format("%.2f TND", sousTotal));
            deliveryLabel.setText(String.format("+%.2f TND", FRAIS_LIVRAISON));
            totalLabel.setText(String.format("%.2f TND", sousTotal + FRAIS_LIVRAISON));

        } catch (Exception e) {
            System.err.println("Erreur dans updateTotal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void viderPanier() {
        Panier.viderPanier();
        afficherArticlesPanier();
        showAlert("Panier vidé !");
    }

    @FXML
    private void passerCommande() {
        if (Panier.estVide()) {
            showAlert("Votre panier est vide !");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/Commande.fxml"));
            Parent commandeView = loader.load();

            AnchorPane root = (AnchorPane) contenuPane.getScene().lookup("#contenuPane");
            if (root != null) {
                root.getChildren().setAll(commandeView);
            } else {
                contenuPane.getScene().setRoot(commandeView);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'ouverture de la page de commande.");
        }
    }

    @FXML
    private void retourMagasin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/Magasin.fxml"));
            AnchorPane magasinView = loader.load();
            contenuPane.getChildren().setAll(magasinView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour au magasin.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Panier");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}