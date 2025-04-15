package tn.esprit.controller.Galerie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Galerie;
import tn.esprit.services.ServiceGalerie;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;


public class ModifierGalerie {

    @FXML
    private TextField descgTF;
    @FXML
    private Label titreG;
    @FXML
    private TextField nomgTF;
    @FXML
    private TextField typegTF;
    @FXML
    private ImageView imageView;

    private ServiceGalerie serviceGalerie;
    private Galerie galerie; // Galerie à modifier
    private String imagePath; // Chemin de l'image
    private int galerieId;
    @FXML
    private Label typegErr;
    @FXML
    private Label descgErr;
    @FXML
    private Label nomgErr;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;

    public ModifierGalerie() {
        serviceGalerie = new ServiceGalerie();
    }

    @FXML
    public void initialize() {
        nomgTF.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNomGalerie(newValue);
        });
        descgTF.textProperty().addListener((observable, oldValue, newValue) -> {
            validateDescriptionGalerie(newValue);
        });
        typegTF.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTypeGalerie(newValue);
        });
    }
    private void validateNomGalerie(String nomGalerie) {
        if (nomGalerie.isEmpty()) {
            nomgErr.setVisible(true);
            nomgErr.setText("Le nom ne peut pas être vide.");
        } else if (!nomGalerie.matches("[a-zA-Z ]+")) {
            nomgErr.setVisible(true);
            nomgErr.setText("Veuillez uniquement utiliser des lettres.");
        } else {
            nomgErr.setVisible(false); // Cache le message d'erreur si valide
        }
    }

    private void validateDescriptionGalerie(String descGalerie) {
        if (descGalerie.isEmpty()) {
            descgErr.setVisible(true);
            descgErr.setText("La description ne peut pas être vide.");
        } else {
            descgErr.setVisible(false);
        }
    }

    private void validateTypeGalerie(String typeGalerie) {
        if (typeGalerie.isEmpty()) {
            typegErr.setVisible(true);
            typegErr.setText("Le type de galerie ne peut pas être vide.");
        } else if (!typeGalerie.matches("[a-zA-Z ]+")) {
            typegErr.setVisible(true);
            typegErr.setText("Le type ne doit contenir que des lettres.");
        }
        else {
            typegErr.setVisible(false);
        }
    }

    // Méthode pour charger les données de la galerie
    public void loadGalerie(Galerie galerie) {
        this.galerie = galerie;
        nomgTF.setText(galerie.getNom_g());
        descgTF.setText(galerie.getDesc_g());
        typegTF.setText(galerie.getType_g());
        imageView.setImage(new javafx.scene.image.Image(galerie.getPhoto_g())); // Affichage de l'image existante
        imagePath = galerie.getPhoto_g(); // Stocke le chemin actuel de l'image
        titreG.setText("Modifier la Galerie : " + galerie.getNom_g());
        this.galerieId = galerie.getId();
    }

    @FXML
    public void modifierG(ActionEvent actionEvent) {
        // Récupération des nouvelles valeurs
        String nom = nomgTF.getText();
        String desc = descgTF.getText();
        String type = typegTF.getText();

        if (nomgErr.isVisible() || descgErr.isVisible() || typegErr.isVisible()) {
            return; // Quitte la méthode si une erreur est détectée
        }
        boolean hasError = false;

        if (nom.isEmpty()) {
            nomgErr.setVisible(true);
            nomgErr.setText("Le nom ne peut pas être vide.");
            hasError = true;
        }

        if (desc.isEmpty()) {
            descgErr.setVisible(true);
            descgErr.setText("La description ne peut pas être vide.");
            hasError = true;
        }

        if (type.isEmpty()) {
            typegErr.setVisible(true);
            typegErr.setText("Le type de galerie ne peut pas être vide.");
            hasError = true;
        } else if (!type.matches("[a-zA-Z ]+")) {
            typegErr.setVisible(true);
            typegErr.setText("Le type ne doit contenir que des lettres.");
            hasError = true;
        }

        // Si une erreur est détectée, quitter la méthode
        if (hasError) {
            return; // Empêcher la modification de la galerie
        }

        try {
            // Création de la galerie à sauvegarder
            galerie.setNom_g(nom);
            galerie.setPhoto_g(imagePath);
            galerie.setDesc_g(desc);
            galerie.setType_g(type);

            serviceGalerie.modifier(galerie);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Modifiée avec succès!");
            alert.setHeaderText("Votre galerie a été modifiée avec succès.");
            alert.showAndWait();
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void importerImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog((Stage) nomgTF.getScene().getWindow());

        if (file != null) {
            imagePath = file.toURI().toString(); // Stocker le nouveau chemin de l'image
            imageView.setImage(new javafx.scene.image.Image(imagePath)); // Afficher l'image sélectionnée
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
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