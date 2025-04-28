package tn.esprit.controller.evenement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import tn.esprit.controller.reservation.AdminAfficherReservationController;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServiceEvent;
import tn.esprit.utils.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminEvenementController implements Initializable {

    public static Personne user;
    @FXML
    private ListView<Event> listViewEvenements;
    private final ObservableList<Event> evenementList = FXCollections.observableArrayList();
    @FXML
    private Button addEventButton;
    @FXML
    private AnchorPane contenuPane;
    @FXML
    private Button showGridViewButton;
    @FXML
    private Button showReservationsButton;
    @FXML
    private StackPane mainContainer;
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField searchField;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab reservationTab;
    @FXML
    private AnchorPane reservationPane;
    @FXML
    private Button showStatsButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialize() appelé");
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterEvenements(newValue);
            });
        }
        showStatsButton.setOnAction(event -> afficherStatistiques());

        if (addEventButton != null) {
            System.out.println("addEventButton n'est pas null !");
            addEventButton.setOnAction(event -> openAddEventForm());
        } else {
            System.out.println("⚠️ addEventButton est null !");
        }



        chargerEvenements();
    }
    private void afficherStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/statNBR.fxml")); // Mets ici le bon chemin
            AnchorPane statsPane = loader.load();
            contenuPane.getChildren().setAll(statsPane); // Remplace tout dans le mainContainer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterEvenements(String searchText) {
        ServiceEvent serviceEvent = new ServiceEvent();

        try (ResultSet rs = serviceEvent.selectAll1()) {
            int column = 0;
            int row = 0;
            gridPane.getChildren().clear();

            while (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType_e(rs.getString("type_e"));
                e.setInfo_e(rs.getString("info_e"));
                e.setDate_e(rs.getDate("date_e").toLocalDate());
                e.setPhoto_e(rs.getString("photo_e"));
                e.setPrix_s(rs.getInt("prix_s"));
                e.setPrix_vip(rs.getInt("prix_vip"));
                e.setNb_ticket(rs.getInt("nb_ticket"));

                if (e.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                        e.getType_e().toLowerCase().contains(searchText.toLowerCase())) {

                    VBox card = createEventCard(e);
                    gridPane.add(card, column, row);

                    column++;
                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createEventCard(Event e) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #dcdcdc; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        card.setPrefWidth(280);

        // Image
        ImageView imageView = new ImageView();
        try {
            String imagePath = "/imagesEvent/" + e.getPhoto_e();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception ex) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/imagesEvent/default.png"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(120);
        imageView.setFitWidth(280);
        imageView.setPreserveRatio(false);

        // Titre
        Text title = new Text(e.getNom());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Détails
        Text details = new Text("💰 " + e.getPrix_s() + " DT | VIP " + e.getPrix_vip() + " DT\n" +
                "📝 " + e.getType_e() + "\n" +
                "🎟️ " + e.getNb_ticket() + " tickets");
        details.setStyle("-fx-font-size: 14px; -fx-fill: #444444;");

        // Boutons
        HBox buttons = createActionButtons(e);
        card.getChildren().addAll(imageView, title, details, buttons);

        return card;
    }

    private HBox createActionButtons(Event e) {
        HBox buttons = new HBox(10);
        buttons.setStyle("-fx-alignment: center;");

        // Bouton Modifier
        Button edit = new Button("Modifier");
        edit.setStyle("-fx-background-color: #5a00a3; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 10;");
        edit.setPrefWidth(80);
        edit.setOnAction(event -> openEditForm(e));

        // Bouton Supprimer
        Button delete = new Button("Supprimer");
        delete.setStyle("-fx-background-color: #B399D4; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 10;");
        delete.setPrefWidth(80);
        delete.setOnAction(event -> confirmerSuppression(e));

        // Bouton Voir Réservations (en mauve)
        Button viewReservations = new Button("Réservations");
        viewReservations.setStyle("-fx-background-color: #4B0082; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-background-radius: 10; " +
                "-fx-cursor: hand;");
        viewReservations.setPrefWidth(100);

        // Effets de survol
        viewReservations.setOnMouseEntered(ev -> viewReservations.setStyle(
                "-fx-background-color: #5a00a3; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 10; " +
                        "-fx-cursor: hand;"));

        viewReservations.setOnMouseExited(ev -> viewReservations.setStyle(
                "-fx-background-color: #4B0082; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 10; " +
                        "-fx-cursor: hand;"));

        viewReservations.setOnAction(event -> openReservationsForEvent(e));

        buttons.getChildren().addAll(edit, delete, viewReservations);
        return buttons;
    }

    private void openReservationsForEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AfficherReservationA.fxml"));
            AnchorPane reservationsView = loader.load();

            // Passer l'ID de l'événement au contrôleur des réservations
            AdminAfficherReservationController controller = loader.getController();
            controller.setEventId(event.getId());

            contenuPane.getChildren().setAll(reservationsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les réservations",
                    "Une erreur est survenue lors du chargement de la page des réservations.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void openAddEventForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AjouterEvenement.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Fichier FXML introuvable");
                return;
            }
            AnchorPane ajoutView = loader.load();
            contenuPane.getChildren().setAll(ajoutView);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerEvenements() {
        ServiceEvent serviceEvent = new ServiceEvent();
        try (ResultSet rs = serviceEvent.selectAll1()) {
            int column = 0;
            int row = 0;
            gridPane.getChildren().clear();

            while (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType_e(rs.getString("type_e"));
                e.setInfo_e(rs.getString("info_e"));
                e.setDate_e(rs.getDate("date_e").toLocalDate());
                e.setPhoto_e(rs.getString("photo_e"));
                e.setPrix_s(rs.getInt("prix_s"));
                e.setPrix_vip(rs.getInt("prix_vip"));
                e.setNb_ticket(rs.getInt("nb_ticket"));

                VBox card = createEventCard(e);
                gridPane.add(card, column, row);

                column++;
                if (column == 4) {
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openEditForm(Event e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/modifierEvenements.fxml"));
            AnchorPane form = loader.load();
            modifierEvenementController controller = loader.getController();
            controller.setEvenementModifie(e);
            contenuPane.getChildren().setAll(form);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void confirmerSuppression(Event e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'événement ?");
        alert.setContentText("Voulez-vous vraiment supprimer \"" + e.getNom() + "\" ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                supprimerEvenement(e);
                chargerEvenements();
            }
        });
    }

    private void supprimerEvenement(Event e) {
        String query = "DELETE FROM event WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, e.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                evenementList.remove(e);
                System.out.println("✅ Événement supprimé avec succès !");
            } else {
                System.out.println("❌ Échec de la suppression.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}