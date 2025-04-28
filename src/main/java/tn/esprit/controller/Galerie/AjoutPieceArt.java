package tn.esprit.controller.Galerie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Piece_art;
import tn.esprit.entities.Galerie;
import tn.esprit.services.ServicePieceArt;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AjoutPieceArt {

    @FXML
    private Label ajoutPL;
    @FXML
    private TextField nomPTF;
    @FXML
    private Label descPL;
    @FXML
    private Label nomPL;
    @FXML
    private TextField descPTF;
    @FXML
    private Label datePL;
    @FXML
    private ImageView photoPTF;

    private ServicePieceArt servicePieceArt;
    private String photoPath;
    private int galerieId;
    @FXML
    private Label nompErrorLabel;
    @FXML
    private Label datepErrorLabel;
    @FXML
    private Label descpErrorLabel;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;
    @FXML
    private DatePicker datePTF;
    @FXML
    private AnchorPane formCard;


    public AjoutPieceArt() {
        this.servicePieceArt = new ServicePieceArt();
    }
    public void setGalerieId(int galerieId) {
        this.galerieId = galerieId;
    }

    @FXML
    public void initialize() {
        nomPTF.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNomPiece(newValue);
        });
        //datePTF.textProperty().addListener((observable, oldValue, newValue) -> {
           // validateDatePiece(newValue);
        //});
        descPTF.textProperty().addListener((observable, oldValue, newValue) -> {
            validateDescPiece(newValue);
        });
    }
    private void validateNomPiece(String nomPiece) {
        if (nomPiece.isEmpty()) {
            nompErrorLabel.setVisible(true);
            nompErrorLabel.setText("Le nom ne peut pas être vide.");
        } else if (!nomPiece.matches("[a-zA-Z ]+")) {
            nompErrorLabel.setVisible(true);
            nompErrorLabel.setText("Veuillez uniquement utiliser des lettres.");
        } else {
            nompErrorLabel.setVisible(false); // Cache le message d'erreur si valide
        }
    }

    private void validateDatePiece(String datePiece) {
        if (datePiece.isEmpty()) {
            datepErrorLabel.setVisible(true);
            datepErrorLabel.setText("La date ne peut pas être vide.");
        } else if (!datePiece.matches("\\d{4}-\\d{2}-\\d{2}")) { // Vérification du format (dd/MM/yyyy)
            datepErrorLabel.setVisible(true);
            datepErrorLabel.setText("Format: yyyy-MM-dd.");
        } else {
            datepErrorLabel.setVisible(false);
        }
    }

    private void validateDescPiece(String descPiece) {
        if (descPiece.isEmpty()) {
           descpErrorLabel.setVisible(true);
            descpErrorLabel.setText("La description ne peut pas être vide.");
        }
        else {
            descpErrorLabel.setVisible(false);
        }
    }

    // Méthode pour importer l'image
    @FXML
    public void importerPTF(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.JPG"));
        File file = fileChooser.showOpenDialog((Stage) ajoutPL.getScene().getWindow());

        if (file != null) {
            photoPath = file.toURI().toString(); // Stocker l'URL de l'image
            photoPTF.setImage(new javafx.scene.image.Image(photoPath));
        } else {
            ajoutPL.setText("Aucune image sélectionnée.");
        }
    }

    @FXML
    public void AjouterPiece(ActionEvent actionEvent) {
        String nom = nomPTF.getText();
        //String dateStr = datePTF.getText();
        LocalDate dateCrea = datePTF.getValue();
        String desc = descPTF.getText();

        // Validation des champs
        if (nompErrorLabel.isVisible() || descpErrorLabel.isVisible() || dateCrea==null) {
            return; // Quitte la méthode si une erreur est détectée
        }
        boolean hasError = false;

        if (nom.isEmpty()) {
            nompErrorLabel.setVisible(true);
            nompErrorLabel.setText("Le nom ne peut pas être vide.");
            hasError = true;
        }

        if (desc.isEmpty()) {
            descpErrorLabel.setVisible(true);
            descpErrorLabel.setText("La description ne peut pas être vide.");
            hasError = true;
        }

        if (dateCrea == null) {
            datepErrorLabel.setVisible(true);
            datepErrorLabel.setText("La date ne peut pas être vide.");
            hasError = true;
        }
        // else if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) { // Vérification du format
            //datepErrorLabel.setVisible(true);
            //datepErrorLabel.setText("Format: yyyy-MM-dd.");
           // hasError = true;
        //}

        // Si une erreur est détectée, quitter la méthode
        if (hasError) {
            return;
        }

        //LocalDate dateCrea;
        //try {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //dateCrea = LocalDate.parse(dateStr, formatter);
        //} catch (DateTimeParseException e) {
           // return;
        //}
        // Création de la pièce d'art
        Galerie galerie = new Galerie();
        galerie.setId(galerieId);
        Piece_art pieceArt = new Piece_art(nom, dateCrea, photoPath, desc, galerie);

        try {
            servicePieceArt.ajouter(pieceArt);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Pièce d'art ajoutée avec succès !");
            alert.showAndWait();
            clearFields();
            retourVersDetails(actionEvent);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Echec");
            alert.setHeaderText(null);
            alert.setContentText("Echec de l'ajout. Veuillez Réessayer plutard.");
            alert.showAndWait();
        }
    }

    private void clearFields() {
        nomPTF.clear();
        //datePTF.clear();
        descPTF.clear();
        photoPTF.setImage(null);
        photoPath = null;
    }

    @FXML
    public void retourVersDetails(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
            Parent root = loader.load();

            DetailsGalerie detailsGalerieController = loader.getController();
            detailsGalerieController.setGalerieId(galerieId);

            users_parent.getChildren().clear(); // Vider le contenu actuel
            users_parent.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}