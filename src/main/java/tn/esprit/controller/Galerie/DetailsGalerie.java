package tn.esprit.controller.Galerie;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Piece_art;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceGalerie;
import tn.esprit.services.ServicePieceArt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DetailsGalerie {
    private Personne user = Session.getUser();
    @javafx.fxml.FXML
    private GridPane gridPieces;
    @javafx.fxml.FXML
    private TextField descG;
    @javafx.fxml.FXML
    private TextField typeG;
    @javafx.fxml.FXML
    private Label detailsLabel;
    @javafx.fxml.FXML
    private TextField nomG;
    @javafx.fxml.FXML
    private ImageView photoG;
    private ServiceGalerie serviceGalerie;
    private ServicePieceArt servicePieceArt;
    private int galerieId;
    @FXML
    private Button modG;
    @FXML
    private Button suppG;
    @FXML
    private Button ajoutP;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;

    public DetailsGalerie() {
        serviceGalerie = new ServiceGalerie();
        servicePieceArt = new ServicePieceArt();
    }
    public void setGalerieId(int galerieId) {
        this.galerieId = galerieId;
        loadGalerieDetails();
    }

    public void initialize() {
    }

    private void loadGalerieDetails() {
        try {
            // Récupérer les détails de la galerie
            Galerie galerie = serviceGalerie.obtenirGalerieParId(galerieId);
            if (galerie != null) {
                nomG.setText(galerie.getNom_g());
                descG.setText(galerie.getDesc_g());
                photoG.setImage(new Image(galerie.getPhoto_g()));

                String photoPath = galerie.getPhoto_g();
                if (photoPath != null && !photoPath.isEmpty()) {
                    photoG.setImage(new Image(photoPath));
                } else {
                    System.out.println("Erreur: Chemin de l'image de galerie vide ou null.");
                }

                typeG.setText(galerie.getType_g());

                Integer currentUserId = user.getId();
                if (currentUserId != null && currentUserId.equals(galerie.getUser())) {
                    modG.setVisible(true);
                    suppG.setVisible(true);
                    ajoutP.setVisible(true);
                } else {
                    modG.setVisible(false);
                    suppG.setVisible(false);
                    ajoutP.setVisible(false);
                }
                List<Piece_art> piecesArt = servicePieceArt.afficherByGalerieId(galerieId);
                populateGridWithArtPieces(piecesArt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateGridWithArtPieces(List<Piece_art> piecesArt) {
        int column = 0;
        int row = 0;

        for (Piece_art piece : piecesArt) {
            ImageView pieceImageView = new ImageView(new Image(piece.getPhoto_p()));
            pieceImageView.setFitWidth(200);
            pieceImageView.setFitHeight(100);
            //pieceImageView.setPreserveRatio(true);

            Label pieceLabel = new Label(piece.getNom_p());

            GridPane pieceLayout = new GridPane();
            pieceLayout.add(pieceImageView, 0, 0);
            pieceLayout.add(pieceLabel, 0, 1);

            pieceLayout.setOnMouseClicked(event -> {
                openDetailsPieceArt(piece.getId());
            });

            gridPieces.add(pieceLayout, column, row);
            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private void openDetailsPieceArt(int pieceArtId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsPieceArt.fxml"));
            Parent root = loader.load();

            DetailsPieceArt detailsPieceArtController = loader.getController();
            detailsPieceArtController.setPieceArtId(pieceArtId);

            Stage stage = (Stage) users_parent.getScene().getWindow(); // On suppose que users_parent est déjà attaché à une scène

            // Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Nettoyer le contenu actuel de users_parent avant d'ajouter la nouvelle vue
            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterPiece(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Obtient le stage par l'événement

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/AjoutPieceArt.fxml"));
            Parent root = loader.load();

            AjoutPieceArt ajoutPieceArtController = loader.getController();
            ajoutPieceArtController.setGalerieId(galerieId);

            // Nettoyer le contenu actuel de users_parent avant d'ajouter la nouvelle vue
            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

        } catch (IOException e) {
            // Gérer l'erreur d'affichage
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur de chargement");
            errorAlert.setContentText("Une erreur est survenue lors du chargement de la vue d'ajout de pièce d'art.");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierG(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/ModifierGalerie.fxml"));
            Parent root = loader.load();

            ModifierGalerie modifierGalerieController = loader.getController();
            Galerie galerieToUpdate = serviceGalerie.obtenirGalerieParId(galerieId);
            modifierGalerieController.loadGalerie(galerieToUpdate);

            // Changer la scène
            Stage stage = (Stage) gridPieces.getScene().getWindow();
            // Nettoyer le contenu actuel de users_parent
            users_parent.getChildren().clear();
            // Ajouter le nouvel AnchorPane pour l'ajout de galerie
            users_parent.getChildren().add(root);
            stage.setTitle("Ajouter une Galerie");
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void supprimerGalerie(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette galerie ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Si l'utilisateur confirme la suppression
            try {
                serviceGalerie.supprimer(galerieId);
                System.out.println("Galerie supprimée avec succès!");
                retourVersAffGal(actionEvent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("La suppression a été annulée.");
        }
    }

    @FXML
    public void retourVersAffGal(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML pour l'affichage des galeries
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/AfficherGalerie.fxml"));
            Parent root = loader.load();

            // Remplacer le contenu actuel de users_parent par le nouveau contenu
            users_parent.getChildren().clear(); // Nettoie tout ce qui est présent dans l'AnchorPane
            users_parent.getChildren().add(root); // Ajoute la nouvelle vue
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            // Optionnel : Vous pouvez également définir le titre de la fenêtre ici si nécessaire
            //Stage stage = (Stage) retour.getScene().getWindow(); // Récupérer la fenêtre actuelle
            //stage.setTitle("Liste des Galeries"); // Définir le titre de la scène
        } catch (IOException e) {
            // Gérer l'erreur en affichant un message d'erreur à l'utilisateur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
            e.printStackTrace(); // Afficher la pile d'appels pour le débogage
        }
    }
}
