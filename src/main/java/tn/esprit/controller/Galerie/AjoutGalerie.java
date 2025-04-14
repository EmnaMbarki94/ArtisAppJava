package tn.esprit.controller.Galerie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceGalerie;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class AjoutGalerie {

    private Personne user=Session.getUser();
    @javafx.fxml.FXML
    private TextField descgTF;
    @javafx.fxml.FXML
    private TextField nomgTF;
    @javafx.fxml.FXML
    private TextField typegTF;
    @javafx.fxml.FXML
    private Label titreG;
    @javafx.fxml.FXML
    private ImageView imageView;

    private Galerie galerie;
    private ServiceGalerie serviceGalerie;
    @javafx.fxml.FXML
    private Label typeErrorLabel;
    @javafx.fxml.FXML
    private Label descErrorLabel;
    @javafx.fxml.FXML
    private Label nomErrorLabel;
    @javafx.fxml.FXML
    private AnchorPane users_parent;
    @javafx.fxml.FXML
    private Button retour;

    public AjoutGalerie() {
        // Initialiser l'objet galerie
        galerie = new Galerie();
        this.serviceGalerie = new ServiceGalerie();
    }
    @javafx.fxml.FXML
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
            nomErrorLabel.setVisible(true);
            nomErrorLabel.setText("Le nom ne peut pas être vide.");
        } else if (!nomGalerie.matches("[a-zA-Z ]+")) {
            nomErrorLabel.setVisible(true);
            nomErrorLabel.setText("Veuillez uniquement utiliser des lettres.");
        } else {
            nomErrorLabel.setVisible(false); // Cache le message d'erreur si valide
        }
    }

    private void validateDescriptionGalerie(String descGalerie) {
        if (descGalerie.isEmpty()) {
            descErrorLabel.setVisible(true);
            descErrorLabel.setText("La description ne peut pas être vide.");
        } else {
            descErrorLabel.setVisible(false);
        }
    }

    private void validateTypeGalerie(String typeGalerie) {
        if (typeGalerie.isEmpty()) {
            typeErrorLabel.setVisible(true);
            typeErrorLabel.setText("Le type de galerie ne peut pas être vide.");
        } else if (!typeGalerie.matches("[a-zA-Z ]+")) {
            typeErrorLabel.setVisible(true);
            typeErrorLabel.setText("Le type ne doit contenir que des lettres.");
        }
        else {
            typeErrorLabel.setVisible(false);
        }
    }

    @javafx.fxml.FXML
    public void ajouterG(ActionEvent actionEvent) {
        // Réinitialiser les messages d'erreur
        nomErrorLabel.setVisible(false);
        descErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);

        // Récupérer les valeurs des champs de texte
        String nomGalerie = nomgTF.getText().trim();
        String descGalerie = descgTF.getText().trim();
        String typeGalerie = typegTF.getText().trim();

        // Vérification des champs vides
        boolean hasError = false;

        if (nomGalerie.isEmpty()) {
            nomErrorLabel.setVisible(true);
            nomErrorLabel.setText("Le nom ne peut pas être vide.");
            hasError = true;
        }

        if (descGalerie.isEmpty()) {
            descErrorLabel.setVisible(true);
            descErrorLabel.setText("La description ne peut pas être vide.");
            hasError = true;
        }

        if (typeGalerie.isEmpty()) {
            typeErrorLabel.setVisible(true);
            typeErrorLabel.setText("Le type de galerie ne peut pas être vide.");
            hasError = true;
        }

        // Si une erreur est détectée (un des champs est vide), quitter la méthode
        if (hasError) {
            return; // Empêcher l'ajout de la galerie если des erreurs existent
        }

        // Mettre à jour l'objet Galerie avec les valeurs fournies
        galerie.setNom_g(nomGalerie);
        galerie.setDesc_g(descGalerie);
        galerie.setType_g(typeGalerie);

        // Vérification du rôle de l'utilisateur
        if (user != null && user.getRoles().contains("ROLE_ARTISTE")) {
            galerie.setUser(user);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Accès refusé");
            alert.setHeaderText("Vous devez être un artiste pour ajouter une galerie.");
            alert.showAndWait();
            return;
        }

        // Appeler le service pour ajouter la galerie
        try {
            serviceGalerie.ajouter(galerie);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Ajout avec succès!");
            alert.setHeaderText("Votre galerie a été ajoutée avec succès.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'ajout");
            alert.setHeaderText("Une erreur s'est produite lors de l'ajout de la galerie.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        // Réinitialiser les champs
        nomgTF.clear();
        descgTF.clear();
        typegTF.clear();
        imageView.setImage(null);
        galerie = new Galerie(); // Réinitialiser l'objet Galerie
    }

    @javafx.fxml.FXML
    public void importerImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        // Ajouter des filtres d'extension pour ne permettre que certains types de fichiers
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));

        // Ouvrir la boîte de dialogue pour sélectionner un fichier
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Récupérer le chemin absolu du fichier et l'enregistrer dans l'objet galerie
            String cheminImage = file.toURI().toString();
            galerie.setPhoto_g(cheminImage);

            // Créer une image à partir du fichier et l'afficher dans l'ImageView
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }

    @javafx.fxml.FXML
    public void retourVersAffGal(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/AfficherGalerie.fxml"));
            Parent root = loader.load();

            // Remplacer le contenu actuel de users_parent par le nouveau contenu
            users_parent.getChildren().clear(); // Nettoie tout ce qui est présent dans l'AnchorPane
            users_parent.getChildren().add(root); // Ajoute la nouvelle vue

            // Optionnel : Vous pouvez également définir le titre de la fenêtre ici si nécessaire
            Stage stage = (Stage) retour.getScene().getWindow(); // Récupérer la fenêtre actuelle
            stage.setTitle("Liste des Galeries");

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de chargement");
            alert.setHeaderText("Une erreur est survenue lors du chargement de la vue des galeries.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}