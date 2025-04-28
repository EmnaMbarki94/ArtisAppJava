package tn.esprit.controller.Galerie;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Piece_art;
import tn.esprit.services.ServicePieceArt;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ModifierPieceArt {
    @FXML
    private TextField nompTF;
    @FXML
    private Label titreG;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField descpTF;
    @FXML
    private TextField datepTF;

    private ServicePieceArt servicePieceArt;
    private Piece_art pieceArt;
    private String imagePath;
    @FXML
    private Label descErrLabel;
    @FXML
    private Label nomErrLabel;
    @FXML
    private Label dateErrLabel;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;
    @FXML
    private AnchorPane formCard;

    public ModifierPieceArt() {
        servicePieceArt = new ServicePieceArt();
    }

    @FXML
    public void initialize() {
        // Validation dynamique des champs de texte
        nompTF.textProperty().addListener((observable, oldValue, newValue) -> validateNom(newValue));
        descpTF.textProperty().addListener((observable, oldValue, newValue) -> validateDescription(newValue));
        datepTF.textProperty().addListener((observable, oldValue, newValue) -> validateDate(newValue));
    }


    public void loadPieceArt(Piece_art pieceArt) {
        this.pieceArt = pieceArt;
        nompTF.setText(pieceArt.getNom_p());

        // Formatage de la date
        if (pieceArt.getDate_crea() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = pieceArt.getDate_crea().format(formatter);
            datepTF.setText(formattedDate);
        }

        imageView.setImage(new Image(pieceArt.getPhoto_p())); // Afficher l'image actuelle
        imagePath = pieceArt.getPhoto_p(); // Stocke le chemin actuel de l'image
        descpTF.setText(pieceArt.getDesc_p());
        titreG.setText("Modifier la Pièce d'Art : " + pieceArt.getNom_p());
    }

    private void validateNom(String nom) {
        if (nom.isEmpty()) {
            nomErrLabel.setVisible(true);
            nomErrLabel.setText("Le nom ne peut pas être vide.");
        } else if (!nom.matches("[a-zA-Z ]+")) {
            nomErrLabel.setVisible(true);
            nomErrLabel.setText("Le nom doit contenir uniquement des lettres.");
        } else {
            nomErrLabel.setVisible(false); // Cache le message d'erreur si valide
        }
    }

    private void validateDescription(String desc) {
        if (desc.isEmpty()) {
            descErrLabel.setVisible(true);
            descErrLabel.setText("La description ne peut pas être vide.");
        } else {
            descErrLabel.setVisible(false);
        }
    }

    private void validateDate(String dateStr) {
        if (dateStr.isEmpty()) {
            dateErrLabel.setVisible(true);
            dateErrLabel.setText("La date ne peut pas être vide.");
        } else if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            dateErrLabel.setVisible(true);
            dateErrLabel.setText("Format: yyyy-MM-dd.");
        } else {
            dateErrLabel.setVisible(false);
        }
    }

    @FXML
    public void importerImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) nompTF.getScene().getWindow(); // Get the stage from the TextField
        File file = fileChooser.showOpenDialog(stage); // Show file chooser dialog

        if (file != null) {
            imagePath = file.toURI().toString(); // Stocker le chemin de l'image
            imageView.setImage(new Image(imagePath)); // Afficher l'image sélectionnée
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
    }

    @FXML
    public void modifierP(ActionEvent actionEvent) {
        // Récupération des nouvelles valeurs
        String nom = nompTF.getText();
        String desc = descpTF.getText();
        String dateStr = datepTF.getText(); // Date sous forme de chaîne

        if (nomErrLabel.isVisible() || descErrLabel.isVisible() || dateErrLabel.isVisible()) {
            return;
        }

        if (nomErrLabel.isVisible() || descErrLabel.isVisible() || dateErrLabel.isVisible()) {
            return; // Quitte la méthode si une erreur est détectée
        }

        boolean hasError = false;

        // Validation des champs
        if (nom.isEmpty()) {
            nomErrLabel.setVisible(true);
            nomErrLabel.setText("Le nom ne peut pas être vide.");
            hasError = true;
        }

        if (desc.isEmpty()) {
            descErrLabel.setVisible(true);
            descErrLabel.setText("La description ne peut pas être vide.");
            hasError = true;
        }

        if (dateStr.isEmpty()) {
            dateErrLabel.setVisible(true);
            dateErrLabel.setText("La date ne peut pas être vide.");
            hasError = true;
        } else {

            try {
                // Récupérer la date sous forme de LocalDate
                LocalDate dateCrea = null;
                if (!dateStr.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    dateCrea = LocalDate.parse(dateStr, formatter); // Parsing de la date
                }

                // Mettre à jour la pièce d'art
                pieceArt.setNom_p(nom);
                pieceArt.setDate_crea(Date.valueOf(dateCrea).toLocalDate()); // Conversion en java.sql.Date
                pieceArt.setPhoto_p(imagePath); // Le chemin de l'image
                pieceArt.setDesc_p(desc);
                servicePieceArt.modifier(pieceArt);
                System.out.println("La pièce d'art a été modifiée avec succès !");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Modifiée avec succès!");
                alert.setHeaderText("Votre pièce d'art a été modifiée avec succès.");
                alert.showAndWait();
                retourVersDetails(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erreur : " + e.getMessage());
            }
        }
    }

    @FXML
    public void retourVersDetails(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsPieceArt.fxml"));
            Parent root = loader.load();

            DetailsPieceArt detailsPieceArtController = loader.getController();
            detailsPieceArtController.setPieceArtId(pieceArt.getId());

            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
        }
    }
}