package tn.esprit.controller.users.magasins;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Article;
import tn.esprit.entities.Panier;

import java.net.URL;

public class DetailsArticles {

    // Composants FXML (tous annotés avec @FXML)
    @FXML private ImageView imageView;
    @FXML private Label nomLabel;
    @FXML private Label prixLabel;
    @FXML private Label descLabel;
    @FXML private Button ajouterPanierButton;
    @FXML private Spinner<Integer> quantiteSpinner;
    @FXML private Text stockMessage;

    private Article article;

    @FXML
    public void initialize() {
        // Vérification des composants injectés
        if (ajouterPanierButton == null || quantiteSpinner == null || stockMessage == null) {
            throw new IllegalStateException("Un composant FXML n'a pas été injecté correctement");
        }

        // Initialisation du spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1);
        quantiteSpinner.setValueFactory(valueFactory);

        // Listener pour vérifier le stock
        quantiteSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (article != null) {
                updateStockMessage();
            }
        });
    }

    public void setArticle(Article article) {
        if (article == null) {
            throw new IllegalArgumentException("L'article ne peut pas être null");
        }

        this.article = article;
        nomLabel.setText("🧾 " + article.getNom_a());
        prixLabel.setText("💰 " + article.getPrix_a() + " DT");
        descLabel.setText("📃 " + article.getDesc_a());

        updateSpinnerLimits();

        try {
            URL imageUrl = getClass().getResource("/image/article/" + article.getImage_path());
            if (imageUrl != null) {
                imageView.setImage(new Image(imageUrl.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
        }
    }

    private void updateSpinnerLimits() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                (SpinnerValueFactory.IntegerSpinnerValueFactory) quantiteSpinner.getValueFactory();

        int stock = article.getQuantite();
        valueFactory.setMax(stock > 0 ? stock : 1);

        updateStockMessage();
    }

    private void updateStockMessage() {
        int stock = article.getQuantite();
        int quantite = quantiteSpinner.getValue();

        if (stock <= 0) {
            stockMessage.setText("Rupture de stock!");
            stockMessage.getStyleClass().remove("success");
            stockMessage.getStyleClass().add("error");
            ajouterPanierButton.setDisable(true);
        } else if (quantite > stock) {
            stockMessage.setText("Quantité trop élevée! Stock ");
            stockMessage.getStyleClass().remove("success");
            stockMessage.getStyleClass().add("error");
            ajouterPanierButton.setDisable(true);
        } else {
            stockMessage.setText("Disponible dans le stock");
            stockMessage.getStyleClass().remove("error");
            stockMessage.getStyleClass().add("success");
            ajouterPanierButton.setDisable(false);
        }
    }

    @FXML
    private void ajouterAuPanier() {
        int quantite = quantiteSpinner.getValue();
        int stock = article.getQuantite();

        if (quantite > stock) {
            showAlert("Erreur", "Stock insuffisant",
                    "La quantité demandée (" + quantite + ") dépasse le stock disponible .",
                    Alert.AlertType.ERROR);
            return;
        }

        Panier.ajouterArticle(article, quantite);
        showAlert("Succès", "Article ajouté",
                quantite + " x " + article.getNom_a() + " ajouté(s) au panier!",
                Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void fermer() {
        Stage stage = (Stage) imageView.getScene().getWindow();
        stage.close();
    }
}