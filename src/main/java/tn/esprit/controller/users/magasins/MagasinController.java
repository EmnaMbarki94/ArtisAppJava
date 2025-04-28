package tn.esprit.controller.users.magasins;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Article;
import tn.esprit.entities.Magasin;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServiceArticle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MagasinController implements Initializable {

    public static Personne user;

    @FXML
    private ListView<Article> articleListView;

    @FXML
    private GridPane articleGrid;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button btnOuvrirPanier;

    @FXML
    private AnchorPane contenuPane;

    @FXML
    private ComboBox<Magasin> magasinComboBox;

    private List<Article> allArticles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        articleGrid.setMaxWidth(Double.MAX_VALUE);

        // Initialiser la ComboBox
        initializeMagasinComboBox();

        // Charger tous les articles
        ServiceArticle articleService = new ServiceArticle();
        allArticles = articleService.getAllArticles();

        afficherArticles(allArticles);

        // Écouteur pour le changement de sélection
        magasinComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filtrerArticlesParMagasin(newVal);
        });

        // Écouteur pour le redimensionnement
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (magasinComboBox.getValue() == null) {
                afficherArticles(allArticles);
            } else {
                filtrerArticlesParMagasin(magasinComboBox.getValue());
            }
        });



    }

    private void initializeMagasinComboBox() {
        ServiceArticle articleService = new ServiceArticle();
        List<Magasin> magasins = articleService.getMagasins();
        System.out.println("Magasins chargés: " + magasins);
        for (Magasin m : magasins) {
            System.out.println("Magasin: " + m.getNom_m() + " (ID: " + m.getId() + ")");
        }

        magasinComboBox.getItems().clear();
        magasinComboBox.getItems().add(null); // Option "Tous les magasins"
        magasinComboBox.getItems().addAll(magasins);

        // Configurer l'affichage du nom du magasin dans la ComboBox
        magasinComboBox.setCellFactory(param -> new ListCell<Magasin>() {
            @Override
            protected void updateItem(Magasin magasin, boolean empty) {
                super.updateItem(magasin, empty);
                if (empty || magasin == null) {
                    setText("Tous les magasins");
                } else {
                    setText(magasin.getNom_m()); // Utilisez getNom_m() ici
                }
            }
        });

        magasinComboBox.setButtonCell(new ListCell<Magasin>() {
            @Override
            protected void updateItem(Magasin magasin, boolean empty) {
                super.updateItem(magasin, empty);
                if (empty || magasin == null) {
                    setText("Tous les magasins");
                } else {
                    setText(magasin.getNom_m()); // Utilisez getNom_m() ici
                }
            }
        });
    }

    private void filtrerArticlesParMagasin(Magasin magasin) {
        if (magasin == null) {
            System.out.println("Affichage de tous les articles");
            afficherArticles(allArticles);
        } else {
            System.out.println("Filtrage pour magasin: " + magasin.getNom_m());
            List<Article> articlesFiltres = allArticles.stream()
                    .filter(article -> article.getMagasin() != null && magasin.getId() == article.getMagasin().getId())
                    .collect(Collectors.toList());
            System.out.println("Nombre d'articles filtrés: " + articlesFiltres.size());
            afficherArticles(articlesFiltres);
        }
    }
    private void afficherArticles(List<Article> articles) {
        int column = 0;
        int row = 0;

        articleGrid.getChildren().clear();
        articleGrid.setHgap(20);
        articleGrid.setVgap(20);
        articleGrid.setStyle("-fx-padding: 20;");
        articleGrid.setAlignment(Pos.TOP_CENTER);

        double availableWidth = scrollPane.getWidth();
        int cardWidth = 220;
        int cardsPerRow = (int) (availableWidth / cardWidth);
        if (cardsPerRow < 1) cardsPerRow = 1;

        for (Article article : articles) {
            VBox card = new VBox();
            card.setSpacing(10);
            card.setPrefWidth(200);
            card.setMaxWidth(200);
            card.setAlignment(Pos.TOP_CENTER);

            // Ajouter une classe CSS différente selon le stock
            if (article.getQuantite() <= 0) {
                card.getStyleClass().add("card-out-of-stock");
                card.setDisable(true); // Désactiver la carte si stock épuisé
            } else {
                card.getStyleClass().add("card");
            }

            StackPane imageContainer = new StackPane();
            imageContainer.setPrefSize(160, 120);
            imageContainer.setMaxSize(160, 120);
            imageContainer.setMinSize(160, 120);
            imageContainer.getStyleClass().add("image-container");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(160);
            imageView.setFitHeight(120);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            // Ajouter un filtre gris si stock épuisé
            if (article.getQuantite() <= 0) {
                imageView.setOpacity(0.5);
            }

            imageContainer.getChildren().add(imageView);

            try {
                URL imageUrl = getClass().getResource("/image/article/" + article.getImage_path());
                if (imageUrl != null) {
                    imageView.setImage(new Image(imageUrl.toExternalForm()));
                } else {
                    URL defaultUrl = getClass().getResource("/image/magasin/imagedf.png");
                    if (defaultUrl != null) {
                        imageView.setImage(new Image(defaultUrl.toExternalForm()));
                    }
                }

                // Ne permettre le clic que si le stock est disponible
                if (article.getQuantite() > 0) {
                    card.setOnMouseClicked(e -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/DetailsArticles.fxml"));
                            Parent root = loader.load();
                            DetailsArticles controller = loader.getController();
                            controller.setArticle(article);

                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.setTitle("Détails de l'article");
                            stage.setScene(new Scene(root, 600, 700));
                            stage.setResizable(true);
                            stage.showAndWait();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Label nomLabel = new Label(article.getNom_a());
            nomLabel.getStyleClass().add("nom-label");

            Label prixLabel = new Label("💰 " + article.getPrix_a() + " DT");
            prixLabel.getStyleClass().add("prix-label");

            // Ajouter un label "En rupture" si stock épuisé
            if (article.getQuantite() <= 0) {
                Label outOfStockLabel = new Label("RUPTURE DE STOCK");
                outOfStockLabel.getStyleClass().add("out-of-stock-label");
                card.getChildren().addAll(imageContainer, nomLabel, prixLabel, outOfStockLabel);
            } else {
                card.getChildren().addAll(imageContainer, nomLabel, prixLabel);
            }

            articleGrid.add(card, column, row);

            column++;
            if (column >= cardsPerRow) {
                column = 0;
                row++;
            }
        }
    }

    @FXML
    private void ouvrirPanier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/ListPanier.fxml"));
            Parent panierView = loader.load();
            contenuPane.getChildren().setAll(panierView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void consulterHistorique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/HistoriqueCommandes.fxml"));
            Parent historiqueView = loader.load();
            contenuPane.getChildren().setAll(historiqueView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}