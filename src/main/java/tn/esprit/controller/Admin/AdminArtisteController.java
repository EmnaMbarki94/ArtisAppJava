package tn.esprit.controller.Admin;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tn.esprit.controller.Galerie.DetailsGalerie;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServiceGalerie;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AdminArtisteController {

    public static Personne user;
    @FXML
    private TextField searchField;

    private ServiceGalerie serviceGalerie;
    @FXML
    private StackPane mainContainer;
    @FXML
    private GridPane galerieGrid;
    @FXML
    private MenuButton trier;
    @FXML
    private Button buttonStat;
    @FXML
    private AnchorPane parent;
    @FXML
    private Button commentsAdmin;

    public AdminArtisteController() {
        serviceGalerie = new ServiceGalerie();
    }

    @FXML
    public void initialize() {
        List<Galerie> galeries = null;
        try {
            galeries = serviceGalerie.afficher();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadGaleries(galeries);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterGareries(newValue));
    }

    private void loadGaleries(List<Galerie> galeries) {
        int column = 0;
        int row = 0;

        galerieGrid.getChildren().clear();
        galerieGrid.setHgap(20); // Espace entre les colonnes
        galerieGrid.setVgap(20); // Espace entre les lignes

        for (Galerie galerie : galeries) {
            try {
                // Chargement de l'image
                ImageView imageView = new ImageView(new Image(galerie.getPhoto_g()));
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setEffect(new DropShadow(10, Color.GRAY));

                // Nom de la galerie
                Label label = new Label(galerie.getNom_g());
                label.setWrapText(true);
                label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4B0082;");

                // Bouton de suppression
                Button deleteButton = new Button();
                Image deleteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/corbeille.png")));
                ImageView deleteIcon = new ImageView(deleteImage);
                deleteIcon.setFitWidth(20);
                deleteIcon.setFitHeight(20);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setOnAction(event -> removeGallery(galerie));

                // VBox contenant tout
                VBox vBox = new VBox(imageView, label, deleteButton);
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(10);
                vBox.setPadding(new Insets(10));
                vBox.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-color: #d1c4e9; " +
                        "-fx-border-width: 1;");
                vBox.setPrefWidth(220);

                // Effet au survol
                vBox.setOnMouseEntered(e -> vBox.setStyle("-fx-background-color: #f0e8ff; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-color: #c5b8e0; " +
                        "-fx-border-width: 1;"));
                vBox.setOnMouseExited(e -> vBox.setStyle("-fx-background-color: white; " +
                        "-fx-background-radius: 15; " +
                        "-fx-border-radius: 15; " +
                        "-fx-border-color: #d1c4e9; " +
                        "-fx-border-width: 1;"));

                // Ajout dans la grille
                galerieGrid.add(vBox, column, row);

                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }

            } catch (Exception e) {
                System.out.println("Erreur chargement galerie: " + e.getMessage());
            }
        }
    }


    private void filterGareries(String searchTerm) {
        galerieGrid.getChildren().clear();
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            for (Galerie gallery : galeries) {
                if (gallery.getNom_g().toLowerCase().contains(searchTerm.toLowerCase()) || gallery.getType_g().toLowerCase().contains(searchTerm.toLowerCase())) {
                    loadGalleryCell(gallery);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeGallery(Galerie gallery) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette galerie ?");
        alert.setContentText("Nom de la galerie : " + gallery.getNom_g());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Appel de la méthode supprimer avec l'identifiant de la galerie
                    serviceGalerie.supprimer(gallery.getId());
                    refreshGaleries();

                } catch (SQLException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur de Suppression");
                    errorAlert.setHeaderText("Une erreur est survenue lors de la suppression.");
                    errorAlert.setContentText("Erreur : " + e.getMessage());
                    errorAlert.showAndWait();
                }
            }
        });
    }

    // Méthode pour rafraîchir l'affichage des galeries
    private void refreshGaleries() {
        galerieGrid.getChildren().clear();
        List<Galerie> galeries;
        try {
            galeries = serviceGalerie.afficher();
            loadGaleries(galeries);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGalleryCell(Galerie galerie) {
        try {
            // Chargement de l'image
            ImageView imageView = new ImageView(new Image(galerie.getPhoto_g()));
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setEffect(new DropShadow(10, Color.GRAY));

            // Nom de la galerie
            Label label = new Label(galerie.getNom_g());
            label.setWrapText(true);
            label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4B0082;");

            // Bouton de suppression
            Button deleteButton = new Button();
            Image deleteImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/corbeille.png")));
            ImageView deleteIcon = new ImageView(deleteImage);
            deleteIcon.setFitWidth(20);
            deleteIcon.setFitHeight(20);
            deleteButton.setGraphic(deleteIcon);
            deleteButton.setStyle("-fx-background-color: transparent;");
            deleteButton.setOnAction(event -> removeGallery(galerie));

            // VBox contenant l'image, le label, et le bouton
            VBox vBox = new VBox(imageView, label, deleteButton);
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(10);
            vBox.setPadding(new Insets(10));
            vBox.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-color: #d1c4e9; " +
                    "-fx-border-width: 1;");
            vBox.setPrefWidth(220);

            // Effet au survol
            vBox.setOnMouseEntered(e -> vBox.setStyle("-fx-background-color: #f0e8ff; " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-color: #c5b8e0; " +
                    "-fx-border-width: 1;"));
            vBox.setOnMouseExited(e -> vBox.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-radius: 15; " +
                    "-fx-border-color: #d1c4e9; " +
                    "-fx-border-width: 1;"));

            // Ajout dans la grille
            int column = galerieGrid.getChildren().size() % 2;
            int row = galerieGrid.getChildren().size() / 2;
            galerieGrid.add(vBox, column, row);

        } catch (Exception e) {
            System.out.println("Erreur chargement cellule galerie: " + e.getMessage());
        }
    }



    @FXML
    public void triParNom(ActionEvent actionEvent) {
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            galeries.sort((g1, g2) -> g1.getNom_g().compareToIgnoreCase(g2.getNom_g())); // Sort by name
            loadGaleries(galeries);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void triParId(ActionEvent actionEvent) {
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            galeries.sort((g1, g2) -> Integer.compare(g1.getId(), g2.getId()));
            loadGaleries(galeries);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void statistiques(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/StatG.fxml"));
            Parent root = loader.load();

            StatG stat = loader.getController();

            parent.getChildren().clear();
            parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void commentsAdmin(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/CommentsA.fxml"));
            Parent root = loader.load();

            CommentsA comment = loader.getController();

            parent.getChildren().clear();
            parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}