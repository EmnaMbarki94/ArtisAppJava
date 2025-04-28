package tn.esprit.controller.reservation;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QRCodeController {

    @FXML
    private ImageView qrCodeImageView;

    // Méthode pour définir l'image du QR code
    public void setQRCodeImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image qrImage = new Image(imageUrl, true); // Chargement asynchrone
            qrCodeImageView.setImage(qrImage);
        } else {
            System.out.println("URL du QR code invalide !");
        }
    }
}
