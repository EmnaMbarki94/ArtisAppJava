package tn.esprit.controller.reservation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import tn.esprit.entities.Reservation;
import tn.esprit.services.ServiceReservation;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReservationsParEvenementController implements Initializable {

    @FXML private Label nomEventLabel;
    @FXML private Label dateEventLabel;
    @FXML private ImageView photoEventView;
    @FXML private ListView<Reservation> reservationsListView;
    @FXML private Button btnAjouter;
    @FXML private Button btnSupprimer;
    @FXML private AnchorPane reservationPane;

    private final ObservableList<Reservation> reservationList = FXCollections.observableArrayList();
    private final ServiceReservation serviceReservation = new ServiceReservation();
    private int eventId;

    // Pour compatibilité avec les appels depuis d'autres contrôleurs
    public void setEventId(int eventId) {
        this.eventId = eventId;
        chargerReservationsAssociees();
    }

    public void setEventDetails(String nom, String date, String photoPath, int eventId) {
        nomEventLabel.setText("🎉 " + nom);
        dateEventLabel.setText("📅 " + date);
        this.eventId = eventId;

        File file = new File(photoPath);
        if (file.exists()) {
            Image img = new Image(file.toURI().toString());
            photoEventView.setImage(img);
        } else {
            System.out.println("⚠️ Image introuvable : " + photoPath);
        }

        chargerReservationsAssociees();
    }

    private void chargerReservationsAssociees() {
        List<Reservation> reservations = serviceReservation.getReservationsParEvenement(eventId);
        reservationList.setAll(reservations);

        reservationsListView.setItems(reservationList);
        reservationsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text details = new Text(
                            "🧾 Libellé: " + r.getLibelle() +
                                    "\n👤 Utilisateur ID: " + r.getUser_id_id() +
                                    "\n🎫 Nb places: " + r.getNb_place() +
                                    "\n📌 État: " + r.getEtat_e()
                    );
                    details.setWrappingWidth(500);
                    details.setStyle("-fx-font-size: 13px; -fx-fill: #4B0082;");

                    HBox box = new HBox(details);
                    box.setStyle("-fx-background-color: #f3e8ff; -fx-padding: 10; -fx-background-radius: 10;");
                    setGraphic(box);
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAjouter.setStyle("-fx-background-color: #c084fc; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        btnSupprimer.setStyle("-fx-background-color: #c084fc; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");
    }
}
