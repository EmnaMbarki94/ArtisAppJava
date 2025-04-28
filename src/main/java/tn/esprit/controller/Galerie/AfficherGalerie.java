package tn.esprit.controller.Galerie;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceGalerie;
import javafx.scene.image.Image;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherGalerie {
    private Personne user= Session.getUser();
    @javafx.fxml.FXML
    private GridPane galerieGrid;
    @javafx.fxml.FXML
    private Label titreLabel;
    private ServiceGalerie serviceGalerie;
    @FXML
    private Button addG;
    @FXML
    private AnchorPane users_parent;
    @FXML
    private TextField searchField;

    public AfficherGalerie() {
        serviceGalerie = new ServiceGalerie();
    }

    // Méthode pour initialiser les galeries
    public void initialize() {
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            populateGaleries(galeries);

            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filtrerGaleries(newValue, galeries);
            });

            Integer userId = user.getId();
            System.out.println(userId);
            //addG.setVisible(true);
            if (userId != null && user.getRoles().contains("ROLE_ARTIST") && !serviceGalerie.userHasGallery(userId)) {
                addG.setVisible(true);
            } else {
               addG.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateGaleries(List<Galerie> galeries) {
        int column = 0;
        int row = 0;
        galerieGrid.getChildren().clear();

        galerieGrid.setHgap(30);
        galerieGrid.setVgap(30);

        if (galeries.isEmpty()) {
            Label noResultLabel = new Label("Aucune galerie trouvée.");
            noResultLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #9e9e9e;");
            noResultLabel.setAlignment(Pos.CENTER);
            galerieGrid.add(noResultLabel, 0, 0);
            GridPane.setColumnSpan(noResultLabel, 3);
            return;
        }

        for (Galerie galerie : galeries) {
            String imagePath = galerie.getPhoto_g();
            System.out.println("Chemin de l'image : " + imagePath);

            try {
                // Image
                ImageView imageView = new ImageView(new Image(imagePath));
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-background-radius: 15;");
                imageView.setClip(new javafx.scene.shape.Rectangle(200, 150) {{
                    setArcWidth(20);
                    setArcHeight(20);
                }});

                // Label
                Label label = new Label(galerie.getNom_g());
                label.setWrapText(true);
                label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6a1b9a;");

                // VBox
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(10);
                vBox.setPadding(new Insets(15));
                vBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");

                vBox.getChildren().addAll(imageView, label);

                galerieGrid.add(vBox, column, row);

                // Clic sur la carte
                int galerieId = galerie.getId();
                vBox.setOnMouseClicked(event -> {
                    System.out.println("Clique sur la galerie ID : " + galerieId);
                    openDetailsGalerie(galerieId);
                });

                // Effet Hover : Glow + Légère montée
                vBox.setOnMouseEntered(event -> {
                    vBox.setStyle("-fx-background-color: #f3e5f5; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(155,89,182,0.4), 12, 0, 0, 6);");
                    vBox.setCursor(Cursor.HAND);
                    TranslateTransition transition = new TranslateTransition(Duration.millis(150), vBox);
                    transition.setToY(-5);
                    transition.play();
                });

                vBox.setOnMouseExited(event -> {
                    vBox.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");
                    TranslateTransition transition = new TranslateTransition(Duration.millis(150), vBox);
                    transition.setToY(0);
                    transition.play();
                });

                // Gestion colonnes/rows
                column++;
                if (column == 3) { // 3 colonnes cette fois pour un affichage plus dense
                    column = 0;
                    row++;
                }

            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
    }



    private void openDetailsGalerie(int galerieId) {
        try {
            // Charger le fichier FXML pour les détails de la galerie
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
            AnchorPane root = loader.load();

            // Accéder au contrôleur et définir l'ID de la galerie
            DetailsGalerie detailsController = loader.getController();
            detailsController.setGalerieId(galerieId);

            // Créer une nouvelle scène et l'afficher
            users_parent.getChildren().clear(); // Nettoie tout ce qui est présent dans l'AnchorPane

            // Ajouter le nouvel AnchorPane pour les détails
            users_parent.getChildren().add(root);

            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void addG(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/AjoutGalerie.fxml"));
            AnchorPane ajoutRoot = loader.load();

            // Vérifier si galerieGrid est attaché à une scène
            if (galerieGrid.getScene() != null) {
                Stage stage = (Stage) galerieGrid.getScene().getWindow();
                // Nettoyer le contenu actuel de users_parent
                users_parent.getChildren().clear();
                // Ajouter le nouvel AnchorPane pour l'ajout de galerie
                users_parent.getChildren().add(ajoutRoot);
                stage.setTitle("Ajouter une Galerie");
                FadeTransition ft = new FadeTransition(Duration.millis(500), ajoutRoot); // 500 ms = 0.5s
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
            } else {
                // Gérer le cas où le GridPane n'est pas attaché à une scène
                System.out.println("Le GridPane n'est pas encore attaché à une scène.");
                return; // Sortie de la méthode pour éviter l'erreur
            }

        } catch (IOException e) {
            e.printStackTrace(); // Afficher les erreurs de chargement
        }
    }

    private void filtrerGaleries(String keyword, List<Galerie> toutesLesGaleries) {
        if (keyword == null || keyword.isEmpty()) {
            populateGaleries(toutesLesGaleries);
            return;
        }

        List<Galerie> resultats = toutesLesGaleries.stream()
                .filter(galerie -> galerie.getNom_g().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        populateGaleries(resultats);
    }
}
