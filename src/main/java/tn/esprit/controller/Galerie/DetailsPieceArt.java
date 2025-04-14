package tn.esprit.controller.Galerie;

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
import javafx.stage.Stage;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Piece_art;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceGalerie;
import tn.esprit.services.ServicePieceArt;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DetailsPieceArt {
    private Personne user = Session.getUser();
    @FXML
    private ImageView photoP;
    @FXML
    private TextField nomP;
    @FXML
    private Label detailsLabel;
    @FXML
    private TextField dateP;
    @FXML
    private TextField descP;
    private int galerieId;
    private int pieceArtId;
    private ServicePieceArt servicePieceArt;
    @FXML
    private Button suppP;
    @FXML
    private Button modP;
    private ServiceGalerie serviceGalerie;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;

    public DetailsPieceArt() {
        servicePieceArt = new ServicePieceArt();
        serviceGalerie = new ServiceGalerie();
    }

    public void setPieceArtId(int pieceArtId) {
        this.pieceArtId = pieceArtId;
        loadPieceArtDetails();
    }

    public void setGalerieId(int galerieId) {
        this.galerieId = galerieId;
    }

    private void loadPieceArtDetails() {
        try {
            Piece_art pieceArt = servicePieceArt.obtenirPieceParId(pieceArtId);
            if (pieceArt != null) {
               nomP.setText(pieceArt.getNom_p());
                if (pieceArt.getDate_crea() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String formattedDate = pieceArt.getDate_crea().format(formatter);
                    dateP.setText(formattedDate);
                } else {
                    dateP.setText("Date non disponible");
                }
               photoP.setImage(new Image(pieceArt.getPhoto_p()));
               descP.setText(pieceArt.getDesc_p());

                Integer currentUserId = user.getId();
                int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);

                System.out.println("Galerie ID: " + galerieId);

                Integer ownerUserId = null;
                Galerie galerie = serviceGalerie.obtenirGalerieParId(galerieId);

                if (galerie != null) {
                    ownerUserId = galerie.getUser();
                }
                if (currentUserId != null && currentUserId.equals(ownerUserId)) {
                    modP.setVisible(true);
                    suppP.setVisible(true);
                } else {
                    modP.setVisible(false);
                    suppP.setVisible(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void supprimerPiece(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette pièce d'art ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ServicePieceArt servicePieceArt = new ServicePieceArt();
                int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);
                servicePieceArt.supprimer(pieceArtId);
                System.out.println("Pièce d'art supprimée avec succès!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
                Parent root = loader.load();

                DetailsGalerie detailsGalerieController = loader.getController();
                detailsGalerieController.setGalerieId(galerieId);

                users_parent.getChildren().clear();
                users_parent.getChildren().add(root);

                Stage stage = (Stage) users_parent.getScene().getWindow();
                stage.setTitle("Détails de la Galerie");

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Erreur lors de la suppression");
                errorAlert.setContentText("Une erreur est survenue lors de la suppression de la pièce d'art.");
                errorAlert.showAndWait();
            }
        } else {
            System.out.println("La suppression a été annulée.");
        }
    }

    @FXML
    public void modifierPiece(ActionEvent actionEvent) {
        try {
            // Charger la vue pour modifier la pièce d'art
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/ModifierPieceArt.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la nouvelle fenêtre
            ModifierPieceArt modifierPieceArtController = loader.getController();

            // Passer l'ID de la pièce d'art et éventuellement les détails de la pièce d'art
            modifierPieceArtController.loadPieceArt(servicePieceArt.obtenirPieceParId(pieceArtId));

            // Afficher la nouvelle scène
            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur lors de la modification");
            errorAlert.setContentText("Une erreur est survenue lors de l'ouverture de la page de modification.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void retourVersDetailsG(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
            Parent root = loader.load();

            DetailsGalerie detailsGalerieController = loader.getController();
            int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);
            detailsGalerieController.setGalerieId(galerieId);

            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);

            //Stage stage = (Stage) retour.getScene().getWindow(); // Récupérer la fenêtre actuelle
            //stage.setTitle("Liste des Galeries");
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
