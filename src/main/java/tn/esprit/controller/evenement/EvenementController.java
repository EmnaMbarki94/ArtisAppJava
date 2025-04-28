package tn.esprit.controller.evenement;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ServiceReservation;
import tn.esprit.entities.Session;
import tn.esprit.utils.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.Notifications;
public class EvenementController implements Initializable {

    private final Personne user = Session.getUser();

    @FXML
    private GridPane gridPaneEvenements;

    @FXML
    private Button addEventButton;

    @FXML
    private AnchorPane users_parent;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private Button calendarButton;

    private final ObservableList<Event> observableEvenements = FXCollections.observableArrayList();
    private final List<Event> evenementsOriginaux = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortCombo.setItems(FXCollections.observableArrayList("Nom (A-Z)", "Date (récent)", "Type"));
        sortCombo.getSelectionModel().selectFirst();

        searchField.textProperty().addListener((obs, oldText, newText) -> filtrerEtTrier());
        sortCombo.setOnAction(event -> filtrerEtTrier());

        if (addEventButton != null) {
            addEventButton.setOnAction(event -> openAddEventForm());
        }

        gridPaneEvenements.setHgap(20);
        gridPaneEvenements.setVgap(20);

        chargerEvenements();  // Charge la liste de tous les événements
        afficherEvenements(observableEvenements);// Affiche initialement tous les événements
        checkAndNotifyUpcomingEvents();
        calendarButton.setOnAction(event -> navigateToCalendar());
    }


    public void checkAndNotifyUpcomingEvents() {
        List<Reservation> reservations = ServiceReservation.getReservationsByUser2(Session.getUser().getId());

        for (Reservation reservation : reservations) {
            Event event = reservation.getEvent();
            LocalDate eventDate = event.getDate_e();
            long daysUntilEvent = ChronoUnit.DAYS.between(LocalDate.now(), eventDate);

            if (daysUntilEvent <= 2 && daysUntilEvent >= 0) {
                showModernEventNotification(event, daysUntilEvent);
            }
        }
    }

    private void showModernEventNotification(Event event, long daysUntilEvent) {
        Platform.runLater(() -> {
            String title, message, graphicStyle;

            if (daysUntilEvent == 0) {
                title = "ÇA SE PASSE AUJOURD'HUI !";
                message = "Ne ratez pas '" + event.getNom() + "' qui commence bientôt!";
                graphicStyle = "-fx-background-color: linear-gradient(to right, #FF416C, #FF4B2B);";
            } else if (daysUntilEvent == 1) {
                title = "DEMAIN !";
                message = "Préparez-vous pour '" + event.getNom() + "'";
                graphicStyle = "-fx-background-color: linear-gradient(to right, #4776E6, #8E54E9);";
            } else {
                title = "DANS " + daysUntilEvent + " JOURS";
                message = "L'événement '" + event.getNom() + "' approche";
                graphicStyle = "-fx-background-color: linear-gradient(to right, #1D976C, #93F9B9);";
            }

            StackPane graphic = new StackPane();
            graphic.setStyle(graphicStyle +
                    " -fx-min-width: 20px; -fx-min-height: 20px; " +
                    " -fx-max-width: 20px; -fx-max-height: 20px; " +
                    " -fx-background-radius: 10;");

            Notifications notificationBuilder = Notifications.create()
                    .title(title)
                    .text(message)
                    .graphic(graphic)
                    .position(Pos.TOP_RIGHT) // Modifier ici pour afficher en haut à droite
                    .hideAfter(javafx.util.Duration.seconds(7))
                    .owner(Stage.getWindows().stream().findFirst().orElse(null));

            // Style CSS moderne
            notificationBuilder.show();
        });
    }

    @FXML
    private void navigateToCalendar() {
        try {
            // Assure-toi que le chemin du fichier FXML est correct
            URL fxmlUrl = getClass().getResource("/Fxml/Events/user/calendar.fxml");
            System.out.println("URL du FXML : " + fxmlUrl);

            // Si le fichier FXML n'est pas trouvé, afficher un message d'erreur
            if (fxmlUrl == null) {
                System.out.println("Le fichier FXML n'a pas été trouvé !");
                return;
            }

            // Charger l'FXML et insérer la vue dans le parent
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent calendarView = loader.load(); // Utilise Parent pour être plus générique

            // Vérifie si 'users_parent' est bien initialisé
            if (users_parent != null) {
                users_parent.getChildren().setAll(calendarView); // Remplace le contenu de users_parent par la vue du calendrier
            } else {
                System.out.println("users_parent n'est pas initialisé !");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de calendar.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void openAddEventForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AjouterEvenement.fxml"));
            AnchorPane ajoutView = loader.load();
            users_parent.getChildren().setAll(ajoutView);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de AjouterEvenement.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerEvenements() {
        evenementsOriginaux.clear();
        String query = "SELECT * FROM event";

        try (
                Connection conn = DBConnection.getInstance().getCnx();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
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
                evenementsOriginaux.add(e);
            }
            observableEvenements.setAll(evenementsOriginaux);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrerEtTrier() {
        String filtre = searchField.getText().toLowerCase().trim();
        String critere = sortCombo.getValue();

        List<Event> filtrés = evenementsOriginaux.stream()
                .filter(e -> e.getNom().toLowerCase().contains(filtre)
                        || e.getType_e().toLowerCase().contains(filtre)
                        || e.getInfo_e().toLowerCase().contains(filtre))
                .sorted((e1, e2) -> {
                    if (critere == null) return 0;
                    return switch (critere) {
                        case "Nom (A-Z)" -> e1.getNom().compareToIgnoreCase(e2.getNom());
                        case "Date (récent)" -> e2.getDate_e().compareTo(e1.getDate_e());
                        case "Type" -> e1.getType_e().compareToIgnoreCase(e2.getType_e());
                        default -> 0;
                    };
                })
                .collect(Collectors.toList());

        observableEvenements.setAll(filtrés);
        afficherEvenements(observableEvenements);
    }

    private void afficherEvenements(ObservableList<Event> events) {
        gridPaneEvenements.getChildren().clear();

        int row = 0;
        int column = 0;
        int eventsPerRow = 4;

        for (Event e : events) {
            VBox eventBox = createEventBox(e);
            gridPaneEvenements.add(eventBox, column, row);
            column++;
            if (column >= eventsPerRow) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createEventBox(Event e) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.getStyleClass().add("card");
        card.setCursor(Cursor.HAND);

        ImageView imageView = new ImageView();
        try {
            String imagePath = "/imagesEvent/" + e.getPhoto_e();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception ex) {
            Image defaultImage = new Image(getClass().getResourceAsStream("/imagesEvent/default.png"));
            imageView.setImage(defaultImage);
        }
        imageView.setFitHeight(140);
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(false);

        Label nom = new Label(e.getNom());
        nom.getStyleClass().add("event-title");
        Label date = new Label("📅 " + e.getDate_e());
        Label type = new Label("📝 " + e.getType_e());
        Label prix = new Label("💰 S: " + e.getPrix_s() + " DT | VIP: " + e.getPrix_vip() + " DT");

        VBox infoBox = new VBox(5, nom, date, type, prix);

        if (e.getNb_ticket() == 0) {
            Label complet = new Label("Complet");
            complet.getStyleClass().add("label-complet");
            card.getChildren().addAll(imageView, infoBox, complet);
        } else {
            card.getChildren().addAll(imageView, infoBox);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(600), card);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        card.setOnMouseClicked(evt -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/detailsEvenement.fxml"));
                AnchorPane detailPane = loader.load();

                DetailEvenementController controller = loader.getController();
                controller.setEventDetails(e);
                users_parent.getChildren().setAll(detailPane);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }
}
