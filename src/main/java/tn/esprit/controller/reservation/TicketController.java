package tn.esprit.controller.reservation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TicketController {
        @FXML
        private ImageView eventImage;  // ImageView pour afficher l'image de l'événement
        @FXML
        private Label eventName;
        @FXML
        private Label eventDate;
        @FXML
        private Label eventType;
        @FXML
        private Label reservationInfo;
        @FXML
        private ImageView qrCodeImage;

        public void setTicketData(String imagePath, String name, String date, String type, String reservationDetails, String qrCodeUrl) {
            // Charger l'image depuis le chemin passé en paramètre
            Image eventImageLoaded = new Image(imagePath);
            eventImage.setImage(eventImageLoaded);  // Afficher l'image dans l'ImageView

            // Mettre à jour les autres informations du ticket
            eventName.setText(name);
            eventDate.setText(date);
            eventType.setText(type);
            reservationInfo.setText(reservationDetails);

            // Charger le QR Code
            qrCodeImage.setImage(new Image(qrCodeUrl));  // Afficher le QR code dans l'ImageView
        }
    }
