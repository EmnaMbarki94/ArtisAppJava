package tn.esprit.controller.reservation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reservation;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketGenerator {

    public static void generateTicket(Reservation reservation, Personne user, Event selectedEvent) {
        try {
            // Construire les données du QR Code
            String qrData = String.format(
                    "Réservation:\n" +
                            "Événement: %s\n" +
                            "Date: %s\n" +
                            "Type: %s\n" +
                            "Nombre de places: %d\n" +
                            "Libellé: %s\n" +
                            "État: %s\n" +
                            "Utilisateur: %s %s",
                    selectedEvent.getNom(),
                    selectedEvent.getDate_e(),
                    selectedEvent.getType_e(),
                    reservation.getNb_place(),
                    reservation.getLibelle(),
                    reservation.getEtat_e(),
                    user.getFirst_Name(),
                    user.getLast_Name()
            );

            // Générer le QR Code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

            // Créer l'image du QR Code
            BufferedImage qrImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = qrImage.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, 200, 200);  // Remplir l'arrière-plan en blanc
            graphics.setColor(Color.BLACK);

            // Dessiner l'image du QR code
            for (int y = 0; y < 200; y++) {
                for (int x = 0; x < 200; x++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);  // Dessiner un pixel noir
                    }
                }
            }
            graphics.dispose();

            // Convertir l'image en format compatible pour Word (PNG)
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", byteArrayOutputStream);

            // Créer un document Word
            XWPFDocument document = new XWPFDocument();

            // Ajouter un paragraphe pour la réservation
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Réservation de " + user.getFirst_Name() + " pour l'événement: " + selectedEvent.getNom());
            run.setBold(true);
            run.setFontSize(14);

            // Ajouter le QR Code au document Word
           /* int pictureIndex = document.addPictureData(byteArrayOutputStream.toByteArray(), XWPFPictureData.PICTURE_TYPE_PNG);
            XWPFParagraph imageParagraph = document.createParagraph();
            imageParagraph.createRun().addPicture(
                    byteArrayOutputStream.toByteArray(),
                    XWPFPictureData.PICTURE_TYPE_PNG,
                    "qr_code.png",
                    200, 200  // Taille du QR Code dans le document Word
            );*/

            // Sauvegarder le document Word
            try (FileOutputStream out = new FileOutputStream("ticket.docx")) {
                document.write(out);
            }

            System.out.println("Ticket généré avec succès!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
