package tn.esprit.controller.Admin.Magasins;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Article;
import tn.esprit.services.ServiceArticle;

import java.io.IOException;
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
//    private Button supprimerBtn;


    private final ServiceArticle serviceArticle = new ServiceArticle();
    private int magasinId;

    @FXML
    public void initialize() {
    loadArticlesDuMagasin();
        btnAjouterArticle.setOnAction(event -> ouvrirFormulaireAjout());
        btnRetourMagasins.setOnAction(event -> retournerVersListeMagasins());

    }

    private VBox createArticleCard(Article article) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-padding: 20; -fx-pref-width: 260;");

        ImageView image = new ImageView();
        String imagePath = "/image/article/" + article.getImage_path();
        URL imageURL = getClass().getResource(imagePath);

        if (imageURL != null) {
            image.setImage(new Image(imageURL.toExternalForm()));
        } else {
            URL defaultImageURL = getClass().getResource("/image/magasin/imagedf.jpg");
            if (defaultImageURL != null) {
                image.setImage(new Image(defaultImageURL.toExternalForm()));
            }
        }

        image.setFitWidth(200);
        image.setFitHeight(120);

        Label nom = new Label(article.getNom_a());
        nom.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

        Label prix = new Label(article.getPrix_a() + " DT");
        prix.setStyle("-fx-font-size: 14; -fx-text-fill: #636e72;");

        Label description = new Label(article.getDesc_a());
        description.setWrapText(true);
        description.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d;");

        Label quantite = new Label("Quantité : " + article.getQuantite());
        quantite.setStyle("-fx-font-size: 13; -fx-text-fill: #2c3e50;");

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #a29bfe; -fx-text-fill: white; -fx-background-radius: 5;");

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #a29bfe; -fx-text-fill: white; -fx-background-radius: 5;");

        actions.getChildren().addAll(modifierBtn, supprimerBtn);

        card.getChildren().addAll(image, nom, prix, description, quantite, actions);

        // Tu peux lier ici tes méthodes de modification / suppression si tu veux
        modifierBtn.setOnAction(e -> modifierArticle(article));
        supprimerBtn.setOnAction(e -> supprimerArticle(article, card));


        return card;
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




}
