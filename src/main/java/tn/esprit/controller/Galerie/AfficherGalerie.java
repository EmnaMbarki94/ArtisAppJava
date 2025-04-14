package tn.esprit.controller.Galerie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceGalerie;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
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

    public AfficherGalerie() {
        serviceGalerie = new ServiceGalerie();
    }

    // Méthode pour initialiser les galeries
    public void initialize() {
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            galerieGrid.setVgap(10);
            populateGaleries(galeries);
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

        for (Galerie galerie : galeries) {
            String imagePath = galerie.getPhoto_g();
            System.out.println("Chemin de l'image : " + imagePath); // Vérification du chemin

            try {
                ImageView imageView = new ImageView(new Image(imagePath));
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

                Label label = new Label(galerie.getNom_g());
                label.setWrapText(true);

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.setSpacing(5); // Espace entre l'image et le label
                vBox.setPadding(new Insets(10)); // Ajoute du padding autour du VBox

                vBox.setMinHeight(200); // Définir une hauteur minimale pour le VBox
                vBox.setMinWidth(150); // Définir une largeur minimale pour le VBox

                vBox.getChildren().addAll(imageView, label);
                galerieGrid.add(vBox, column, row); // Ajouter le VBox au GridPane

                int galerieId = galerie.getId();
                vBox.setOnMouseClicked(event -> {
                    System.out.println("Clique sur la galerie ID : " + galerieId);
                    openDetailsGalerie(galerieId);
                });

                column++;

                if (column == 2) { // Exemple avec 2 colonnes
                    column = 0;
                    row++;
                }

            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
                // Ajouter éventuellement une image de remplacement ou un message d'erreur
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
            } else {
                // Gérer le cas où le GridPane n'est pas attaché à une scène
                System.out.println("Le GridPane n'est pas encore attaché à une scène.");
                return; // Sortie de la méthode pour éviter l'erreur
            }

        } catch (IOException e) {
            e.printStackTrace(); // Afficher les erreurs de chargement
        }
    }
}
