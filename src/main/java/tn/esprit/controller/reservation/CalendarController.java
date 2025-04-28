package tn.esprit.controller.reservation;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Reservation;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CalendarController {

    private ServiceReservation serviceReservation;
    @FXML private AnchorPane contenuPane;

    @FXML
    private CalendarView calendarView;

    @FXML
    private Button btnAnnulerReservation;

    // Conserver une référence à l'entrée sélectionnée (si besoin)
    private Entry<?> selectedEntry;

    public CalendarController() {
        this.serviceReservation = new ServiceReservation();
    }

    public void initialize() {
        //calendarView.setShowEntryDetailsPopOver(true);

        int userId = Session.getUser().getId();
        List<Reservation> reservations = serviceReservation.getReservationsByUser(userId);

        Calendar monCalendrier = new Calendar("Mes Événements");
        monCalendrier.setStyle(Calendar.Style.STYLE1);

        CalendarSource source = new CalendarSource("Source Événements");
        source.getCalendars().add(monCalendrier);

        for (Reservation reservation : reservations) {
            LocalDate eventDate = reservation.getEvent().getDate_e();
            LocalDateTime eventStartDateTime = eventDate.atStartOfDay();
            LocalDateTime eventEndDateTime = eventStartDateTime.plusHours(2);

            Entry<String> entry = new Entry<>(" :"
                    + reservation.getId()
                    + " - "
                    + reservation.getEvent().getNom());
            entry.setInterval(eventStartDateTime, eventEndDateTime);
            monCalendrier.addEntry(entry);
        }

        calendarView.setPrefSize(1300, 700);
        calendarView.getCalendarSources().add(source);
        calendarView.setEntryFactory(param -> null);
        // Optionnel : callback pour le popover
        calendarView.setEntryDetailsPopOverContentCallback(param -> {
            Entry<?> entry = param.getEntry();
            this.selectedEntry = entry; // mémoriser l'entrée sélectionnée
            VBox content = new VBox();
            content.setSpacing(10);
            Label label = new Label("Détails de la réservation:\n" + entry.getTitle());
            content.getChildren().add(label);
            return content;
        });

        // Action du bouton visible dans le bas de l'écran
        btnAnnulerReservation.setOnAction(e -> {
            if (selectedEntry != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmer l'annulation");
                alert.setHeaderText("Voulez-vous vraiment annuler cette réservation ?");
                alert.setContentText(selectedEntry.getTitle());

                ButtonType ouiButton = new ButtonType("Oui");
                ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(ouiButton, nonButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ouiButton) {
                    supprimerReservation(selectedEntry);
                    selectedEntry = null;
                }
            } else {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Aucune sélection");
                info.setHeaderText(null);
                info.setContentText("Veuillez sélectionner une réservation dans le calendrier.");
                info.showAndWait();
            }
        });

    }

    private void supprimerReservation(Entry<?> entry) {
        String titre = entry.getTitle();
        int reservationId = extraireIdDeTitre(titre);
        if (reservationId != -1) {
            serviceReservation.supprimerReservation(reservationId);
            entry.getCalendar().removeEntry(entry);
        }
    }

    private int extraireIdDeTitre(String titre) {
        try {
            String[] parties = titre.split(":");
            return Integer.parseInt(parties[1].trim().split(" ")[0]);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'extraction de l'ID : " + e.getMessage());
            return -1;
        }
    }
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/Evenement.fxml"));
            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}