package tn.esprit.controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServiceGalerie;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AdminArtisteController {

    public static Personne user;
    @FXML
    private ListView<Galerie> listGaleries;
    @FXML
    private TextField searchField;

    private ServiceGalerie serviceGalerie;

    public AdminArtisteController() {
        serviceGalerie = new ServiceGalerie();
    }

    @FXML
    public void initialize() {
        loadGaleries(); // Charger les galeries au démarrage

        // Listener pour le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterGareries(newValue));
    }

    private void loadGaleries() {
        try {
            List<Galerie> galeries = serviceGalerie.afficher();

            listGaleries.getItems().addAll(galeries);

            listGaleries.setCellFactory(param -> new ListCell<Galerie>() {
                private final HBox hbox = new HBox(10);
                private final VBox vbox = new VBox();
                private final Label nameLabel = new Label();
                private final Label descLabel = new Label();
                private final Label typeLabel = new Label();
                private final Label userLabel = new Label();

                // Création du bouton avec une icône
                private final Button deleteButton = new Button();
                private final ImageView deleteIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/corbeille.png"))));

                @Override
                protected void updateItem(Galerie gallery, boolean empty) {
                    super.updateItem(gallery, empty);
                    if (empty || gallery == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText("Nom : " + gallery.getNom_g());
                        descLabel.setText("Description : " + gallery.getDesc_g());
                        typeLabel.setText("Type : " + gallery.getType_g());
                        userLabel.setText("User ID : " + (gallery.getUser()));

                        deleteIcon.setFitHeight(20); // Ajustez la taille si nécessaire
                        deleteIcon.setFitWidth(20);
                        deleteButton.setGraphic(deleteIcon);
                        deleteButton.setOnAction(event -> removeGallery(gallery));

                        // Ajout des éléments dans hbox
                        vbox.getChildren().setAll(nameLabel, descLabel, typeLabel, userLabel);
                        HBox.setHgrow(vbox, javafx.scene.layout.Priority.ALWAYS);
                        hbox.getChildren().setAll(vbox, deleteButton);
                        setGraphic(hbox);
                    }
                }
            });
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'erreur selon vos besoins
        }
    }

    // Méthode pour filtrer les galeries selon la recherche
    private void filterGareries(String searchTerm) {
        listGaleries.getItems().clear();
        try {
            List<Galerie> galeries = serviceGalerie.afficher();
            for (Galerie gallery : galeries) {
                if (gallery.getNom_g().toLowerCase().contains(searchTerm.toLowerCase())) {
                    listGaleries.getItems().add(gallery);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'erreur selon vos besoins
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
                    // Actualiser la liste après la suppression
                    loadGaleries();
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
}