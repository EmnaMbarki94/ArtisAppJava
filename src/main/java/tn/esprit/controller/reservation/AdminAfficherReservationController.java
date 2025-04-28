package tn.esprit.controller.reservation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminAfficherReservationController implements Initializable {

    @FXML
    private GridPane gridPaneReservations;
    @FXML
    private AnchorPane contenuPane;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private ServiceReservation serviceReservation;
    private final int COLONNES = 4;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceReservation = new ServiceReservation();
        chargerReservations();
    }

    private void chargerReservations() {
        try {
            ResultSet rs = serviceReservation.selectAll1();
            chargerReservationsDepuisResultSet(rs);
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerReservationsDepuisResultSet(ResultSet rs) throws SQLException {
        reservationList.clear();
        gridPaneReservations.getChildren().clear();

        int col = 0;
        int row = 0;

        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("id"));
            r.setNb_place(rs.getInt("nb_place"));
            r.setLibelle(rs.getString("libelle"));
            r.setEtat_e(rs.getString("etat_e"));
            r.setUser_id_id(rs.getInt("user_id_id"));

            Reservation rAvecEvent = serviceReservation.getReservationAvecEvent(r.getId());
            r.setEvent(rAvecEvent.getEvent());

            VBox card = creerCarteReservation(r);
            gridPaneReservations.add(card, col, row);

            col++;
            if (col == COLONNES) {
                col = 0;
                row++;
            }
        }
    }

    public void setEventId(int eventId) {
        try {
            ResultSet rs = serviceReservation.getReservationsByEventId(eventId);
            chargerReservationsDepuisResultSet(rs);

            // Ajouter un label si aucune réservation trouvée
            if (gridPaneReservations.getChildren().isEmpty()) {
                Label noResLabel = new Label("Aucune réservation trouvée pour cet événement");
                noResLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
                gridPaneReservations.add(noResLabel, 0, 0, COLONNES, 1);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de filtrage");
            alert.setContentText("Impossible de charger les réservations pour cet événement.");
            alert.showAndWait();
        }
    }

    private VBox creerCarteReservation(Reservation r) {
        VBox card = new VBox(5);
        card.setPrefWidth(200);
        card.setStyle("""
                -fx-background-color: #f5e6ff;
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.1), 4, 0, 2, 2);
                """);

        // Image de l'événement
        ImageView imageView = new ImageView();
        try {
            String imagePath = "/imagesEvent/" + r.getEvent().getPhoto_e();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/imagesEvent/default.png"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(120);
        imageView.setFitWidth(280);
        imageView.setPreserveRatio(false);

        // Infos réservation
        Label nomEvent = new Label("📛 " + r.getEvent().getNom());
        Label nbPlaces = new Label("👥 Places : " + r.getNb_place());
        Label dateEvent = new Label("📅 Date : " + r.getEvent().getDate_e());
        nomEvent.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Boutons d'action
        Button btnEdit = creerBoutonModifier(r);
        Button btnDelete = creerBoutonSupprimer(r);

        HBox actions = new HBox(10, btnEdit, btnDelete);
        actions.setStyle("-fx-alignment: center;");

        card.getChildren().addAll(imageView, nomEvent, nbPlaces, dateEvent, actions);
        return card;
    }

    private Button creerBoutonModifier(Reservation r) {
        Button btnEdit = new Button("Modifier");
        btnEdit.setStyle("-fx-background-color: #4B0082; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        btnEdit.setOnMouseEntered(e -> btnEdit.setStyle("-fx-background-color: #5a00a3; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 7, 0, 0, 2);"));

        btnEdit.setOnMouseExited(e -> btnEdit.setStyle("-fx-background-color: #4B0082; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"));

        btnEdit.setOnAction(ev -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/modifierReservation.fxml"));
                AnchorPane form = loader.load();
                ModifierReservationController controller = loader.getController();
                controller.setReservation(r);
                contenuPane.getChildren().setAll(form);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return btnEdit;
    }

    private Button creerBoutonSupprimer(Reservation r) {
        Button btnDelete = new Button("Supprimer");
        btnDelete.setStyle("-fx-background-color: #B399D4; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);");

        btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: #C5B0E6; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 7, 0, 0, 2);"));

        btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: #B399D4; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 8 15; " +
                "-fx-background-radius: 5; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);"));

        btnDelete.setOnAction(ev -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Supprimer");
            alert.setHeaderText("Supprimer la réservation ?");
            alert.setContentText("Voulez-vous supprimer la réservation de l'événement \"" + r.getEvent().getNom() + "\" ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        ServiceReservation service = new ServiceReservation();
                        service.supprimer1(r);
                        chargerReservations();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText("Suppression échouée");
                        errorAlert.setContentText("Une erreur est survenue lors de la suppression de la réservation.");
                        errorAlert.showAndWait();
                    }
                }
            });
        });

        return btnDelete;
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/EvenementAdmin.fxml"));
            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}