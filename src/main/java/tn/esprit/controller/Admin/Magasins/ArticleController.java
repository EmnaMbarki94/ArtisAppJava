package tn.esprit.controller.Admin.Magasins;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import tn.esprit.entities.Article;
import tn.esprit.services.ServiceArticle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ArticleController {

    @FXML
    private AnchorPane contenuPane;

    @FXML
    private FlowPane cardsContainer;
    @FXML
    private Button btnAjouterArticle;
    @FXML
    private Button btnRetourMagasins;

    @FXML
    private TextField searchField;

    private final ServiceArticle serviceArticle = new ServiceArticle();
    private int magasinId;

    @FXML
    public void initialize() {
    loadArticlesDuMagasin();
        btnAjouterArticle.setOnAction(event -> ouvrirFormulaireAjout());
        btnRetourMagasins.setOnAction(event -> retournerVersListeMagasins());
        // Ajoutez cet écouteur pour la recherche dynamique
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerArticles(newValue);
        });
    }

    private VBox createArticleCard(Article article) {
        // Création de la carte principale
        VBox card = new VBox();
        card.setSpacing(10);
        card.setAlignment(Pos.TOP_CENTER); // Alignement en haut pour une meilleure uniformité
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-padding: 15; -fx-pref-width: 280; -fx-pref-height: 400;"); // Hauteur fixe

        // Conteneur d'image avec taille fixe
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(250, 180); // Taille fixe
        imageContainer.setMinSize(250, 180);
        imageContainer.setMaxSize(250, 180);
        imageContainer.setStyle("-fx-background-radius: 8; -fx-background-color: #f5f5f5;");

        // ImageView avec configuration de redimensionnement
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(false); // Ne pas conserver le ratio
        imageView.setFitWidth(250); // Largeur fixe
        imageView.setFitHeight(180); // Hauteur fixe
        imageView.setSmooth(true); // Lissage de l'image

        // Chargement de l'image
        try {
            // 1. Essayer depuis le dossier uploads
            File imageFile = new File("/image/article/" + article.getImage_path());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), true); // Chargement asynchrone
                imageView.setImage(image);
            }
            // 2. Fallback: ressources
            else {
                InputStream is = getClass().getResourceAsStream("/image/article/" + article.getImage_path());
                if (is != null) {
                    imageView.setImage(new Image(is));
                }
                // 3. Image par défaut
                else {
                    URL defaultImageURL = getClass().getResource("/image/article/default.jpg");
                    if (defaultImageURL != null) {
                        imageView.setImage(new Image(defaultImageURL.toString()));
                    }
                }
            }

            // Clip pour coins arrondis
            Rectangle clip = new Rectangle(250, 180);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            imageView.setClip(clip);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback ultime
            imageView.setStyle("-fx-background-color: #e0e0e0;");
        }

        imageContainer.getChildren().add(imageView);

        // Conteneur pour le texte avec taille fixe
        VBox textContainer = new VBox(5);
        textContainer.setPrefHeight(150); // Hauteur fixe pour le texte
        textContainer.setAlignment(Pos.TOP_CENTER);

        // Nom du produit
        Label nomLabel = new Label(article.getNom_a());
        nomLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
        nomLabel.setMaxWidth(240);
        nomLabel.setWrapText(true);

        // Prix
        Label prixLabel = new Label(String.format("%.3f DT", article.getPrix_a()));
        prixLabel.setStyle("-fx-text-fill: #9d97f8;-fx-font-weight: bold;  ");

        // Description (avec hauteur fixe et scroll si nécessaire)
        TextArea descriptionArea = new TextArea(article.getDesc_a());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefHeight(60);
        descriptionArea.setMaxHeight(60);
        descriptionArea.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                "-fx-font-size: 12; -fx-text-fill: #7f8c8d;");

        // Quantité
        Label quantiteLabel = new Label("Stock: " + article.getQuantite());
        quantiteLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #2c3e50;");

        textContainer.getChildren().addAll(nomLabel, prixLabel, descriptionArea, quantiteLabel);

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button modifierBtn = createActionButton("Modifier", "#3498db");
        Button supprimerBtn = createActionButton("Supprimer", "#e74c3c");

        buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);

        // Ajout de tous les éléments à la carte
        card.getChildren().addAll(imageContainer, textContainer, buttonBox);

        // Événements
        modifierBtn.setOnAction(e -> modifierArticle(article));
        supprimerBtn.setOnAction(e -> supprimerArticle(article, card));

        return card;
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #a29bfe; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setPrefWidth(100);
        return button;
    }

    private void modifierArticle(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/ModifierArticle.fxml"));
            AnchorPane modifierView = loader.load();

            ModifierArticle controller = loader.getController();
            controller.setArticle(article);

            contenuPane.getChildren().setAll(modifierView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void supprimerArticle(Article article, VBox card) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet article ?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceArticle.supprimer1(article);
                cardsContainer.getChildren().remove(card); // ✅ supprimer de l'UI uniquement si suppression réussie
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText(null);
                success.setContentText("Article supprimé avec succès !");
                success.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();  // Afficher l'erreur pour déboguer
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Une erreur est survenue lors de la suppression.");
                error.showAndWait();
            }
        }
    }


    public void setMagasinId(int magasinId) {
        this.magasinId = magasinId;
        loadArticlesDuMagasin(); // Charge les articles dès réception de l'ID
//        System.out.println("Magasin ID reçu : " + magasinId);

    }

    private void loadArticlesDuMagasin() {
        try {
            System.out.println("Magasin ID reçu : " + magasinId); // Confirme qu’il reçoit bien

            final ServiceArticle articleService = new ServiceArticle();
            List<Article> articles = articleService.getArticlesByMagasinId(magasinId);

//            System.out.println("Nombre d'articles récupérés = " + articles.size());

            for (Article article : articles) {
                VBox articleCard = createArticleCard(article);
                cardsContainer.getChildren().add(articleCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ouvrirFormulaireAjout() {
        try {
            // Charger le fichier FXML pour le formulaire d'ajout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/AjoutArticle.fxml"));
            AnchorPane pageAjoutArticle = loader.load();  // Charge la page FXML

            // Récupérer le contrôleur de la page d'ajout
            AjoutArticle ajoutArticleController = loader.getController();

            // Passer l'ID du magasin au contrôleur
            ajoutArticleController.setMagasinId(this.magasinId);

            AnchorPane root = (AnchorPane) btnAjouterArticle.getScene().lookup("#contenuPane");
            if (root != null) {
                root.getChildren().setAll(pageAjoutArticle);
            } else {
                System.out.println("contenuPane non trouvé dans la scène.");
            }
        } catch (IOException e) {
            e.printStackTrace();  // Afficher l'erreur si le fichier FXML n'est pas trouvé
        }
    }
    private void retournerVersListeMagasins() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml"));
            AnchorPane pageMagasins = loader.load();

            AnchorPane root = (AnchorPane) btnRetourMagasins.getScene().lookup("#contenuPane");
            root.getChildren().setAll(pageMagasins);

//            System.out.println("Retour à la liste des magasins !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode pour filtrer les articles
    private void filtrerArticles(String recherche) {
        try {
            List<Article> tousLesArticles = serviceArticle.getArticlesByMagasinId(magasinId);
            cardsContainer.getChildren().clear();

            for (Article article : tousLesArticles) {
                if (articleCorrespond(article, recherche)) {
                    VBox articleCard = createArticleCard(article);
                    cardsContainer.getChildren().add(articleCard);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour vérifier si un article correspond à la recherche
    private boolean articleCorrespond(Article article, String recherche) {
        if (recherche == null || recherche.isEmpty()) {
            return true;
        }

        String rechercheLower = recherche.toLowerCase();
        return article.getNom_a().toLowerCase().contains(rechercheLower) ||
                article.getDesc_a().toLowerCase().contains(rechercheLower) ||
                String.valueOf(article.getPrix_a()).contains(recherche) ||
                String.valueOf(article.getQuantite()).contains(recherche);
    }


}
