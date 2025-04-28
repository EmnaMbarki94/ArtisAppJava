package tn.esprit.controller.Admin.Magasins;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tn.esprit.entities.ArticleStatDTO;
import tn.esprit.entities.Magasin;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServiceLigneCommande;
import tn.esprit.services.ServiceMagasin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminMagasinController {

    public static Personne user = null;

    @FXML
    private AnchorPane contenuPane;

    @FXML
    private FlowPane cardsContainer; // Remplace le ListView

    @FXML
    private Button btnAjouterMagasin;


    @FXML
    private TextField searchField;
    @FXML
    private Button btnStats;


    //    private final ViewFactory viewFactory = new ViewFactory();
    private final ServiceMagasin magasinService = new ServiceMagasin();

    @FXML
    public void initialize() {
        loadCardsData(); // Changé de loadListData()
        btnAjouterMagasin.setOnAction(event -> ouvrirFormulaireAjout());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerMagasins(newValue);
        });
        // Ajoutez ceci pour configurer le bouton stats
        btnStats.setOnAction(event -> afficherStatsGraphiques());
        btnStats.setStyle("-fx-background-color: #6c5ce7; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    private void afficherStatsGraphiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/StatsMagasins.fxml"));
            AnchorPane statsView = loader.load();

            // Optionnel: passer des données au contrôleur des stats si nécessaire
            StatsMagasinsController statsController = loader.getController();
            statsController.initialiserDonnees();

            contenuPane.getChildren().setAll(statsView);
        } catch (IOException e) {
            e.printStackTrace();
//            showAlert("Erreur", "Impossible de charger les statistiques", Alert.AlertType.ERROR);
        }



    }

    private void loadCardsData() {

        ResultSet resultSet = null;

        try {
            resultSet = magasinService.selectAll1();

            if (resultSet == null) {
                System.out.println("Le ResultSet est nul, aucune donnée récupérée.");
                return;
            }

            while (resultSet.next()) {
                Magasin magasin = new Magasin();
                magasin.setId(resultSet.getInt("id"));
                magasin.setNom_m(resultSet.getString("nom_m"));
                magasin.setType_m(resultSet.getString("type_m"));
                magasin.setPhoto_m(resultSet.getString("photo_m"));

                // Création de la carte
                VBox card = createMagasinCard(magasin);
                cardsContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private VBox createMagasinCard(Magasin magasin) {
        // Création de la carte
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); " +
                "-fx-padding: 20; -fx-spacing: 10; -fx-pref-width: 230; -fx-cursor: hand;");

        // Conteneur pour l'image avec taille fixe
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(230, 120);
        imageContainer.setStyle("-fx-background-radius: 10; -fx-background-color: #f5f5f5;");

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(230);
        imageView.setFitHeight(120);

// Chargement de l'image
        try {
            String imagePath = "/image/magasin/" + magasin.getPhoto_m();
            URL imageURL = getClass().getResource(imagePath);
            if (imageURL != null) {
                Image image = new Image(imageURL.toURI().toASCIIString());
                imageView.setImage(image);

        // Masquer les parties de l'image qui dépassent
                Rectangle clip = new Rectangle(260, 140);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                imageView.setClip(clip);
            } else {
        // Image par défaut
                URL defaultImageURL = getClass().getResource("/image/magasin/imagedf.jpg");
                if (defaultImageURL != null) {
                    imageView.setImage(new Image(defaultImageURL.toURI().toASCIIString()));
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        // Fallback si erreur
            URL defaultImageURL = getClass().getResource("/image/magasin/imagedf.jpg");
            if (defaultImageURL != null) {
                imageView.setImage(new Image(defaultImageURL.toString()));
            }
        }
        imageContainer.getChildren().add(imageView);

        // Titre et type
        Label nomLabel = new Label(magasin.getNom_m());
        nomLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

        Label typeLabel = new Label(magasin.getType_m());
        typeLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #636e72;");

        // Boutons d'action
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #a29bfe; -fx-text-fill: white; -fx-background-radius: 5;");

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #a29bfe; -fx-text-fill: white; -fx-background-radius: 5;");

        buttonsBox.getChildren().addAll(modifierBtn, supprimerBtn);

        // Ajout des éléments à la carte
        card.getChildren().addAll(imageContainer, nomLabel, typeLabel, buttonsBox);

        // Événements
        card.setOnMouseClicked(event -> ouvrirArticlesParMagasin(magasin.getId()));

        modifierBtn.setOnAction(event -> {
            modifierMagasin(magasin);
            rafraichirPage();
            event.consume(); // Empêche la propagation du clic à la carte
        });

        supprimerBtn.setOnAction(event -> {
            supprimerMagasin(magasin);
            rafraichirPage();
            event.consume(); // Empêche la propagation du clic à la carte
        });

        // Effet hover
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0)",
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0)"));
            card.setTranslateY(-2);
        });

        card.setOnMouseExited(e -> {
            card.setStyle(card.getStyle().replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0)",
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0)"));
            card.setTranslateY(0);
        });

        return card;
    }


    private void ouvrirArticlesParMagasin(int magasinId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/ArticleAdmin.fxml"));
            AnchorPane articlesView = loader.load();

            // Récupère le contrôleur et lui passe l'ID du magasin
            ArticleController controller = loader.getController();
            controller.setMagasinId(magasinId);

            contenuPane.getChildren().setAll(articlesView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierMagasin(Magasin magasin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/ModifierMagasin.fxml"));
            AnchorPane modifierView = loader.load();

            ModifierMagasin controller = loader.getController();
            controller.setMagasin(magasin);

            contenuPane.getChildren().setAll(modifierView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerMagasin(Magasin magasin) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer ce magasin ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                magasinService.supprimer1(magasin);
                System.out.println("Magasin supprimé avec succès");

                // Optionnel : afficher une alerte d'information
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Succès");
                info.setHeaderText(null);
                info.setContentText("Le magasin a été supprimé avec succès.");
                info.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();

                // Optionnel : alerte d'erreur
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText("Une erreur est survenue lors de la suppression.");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        } else {
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }


    public void ouvrirFormulaireAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/AjoutMagasin.fxml"));
            AnchorPane ajoutView = loader.load();
            contenuPane.getChildren().setAll(ajoutView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void rafraichirPage() {
        cardsContainer.getChildren().clear(); // Vide le conteneur des anciennes cartes
        loadCardsData(); // Recharge les données
    }

    private void filtrerMagasins(String recherche) {
        cardsContainer.getChildren().clear();

        try {
            ResultSet resultSet = magasinService.selectAll1();

            while (resultSet.next()) {
                String nomMagasin = resultSet.getString("nom_m");
                String typeMagasin = resultSet.getString("type_m");

                if (nomMagasin.toLowerCase().contains(recherche.toLowerCase()) ||
                        typeMagasin.toLowerCase().contains(recherche.toLowerCase())) {
                    Magasin magasin = new Magasin();
                    magasin.setId(resultSet.getInt("id"));
                    magasin.setNom_m(nomMagasin);
                    magasin.setType_m(typeMagasin);
                    magasin.setPhoto_m(resultSet.getString("photo_m"));

                    VBox card = createMagasinCard(magasin);
                    cardsContainer.getChildren().add(card);
                }
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Stat

}


