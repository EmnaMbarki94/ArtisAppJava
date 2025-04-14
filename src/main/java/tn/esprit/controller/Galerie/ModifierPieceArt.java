package tn.esprit.controller.Galerie;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Piece_art;
import tn.esprit.services.ServicePieceArt;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ModifierPieceArt {
    @FXML
    private TextField nompTF;
    @FXML
    private Label titreG;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField descpTF;
    @FXML
    private TextField datepTF;

    private ServicePieceArt servicePieceArt;
    private Piece_art pieceArt;
    private String imagePath;

    public ModifierPieceArt() {
        servicePieceArt = new ServicePieceArt();
    }

    public void loadPieceArt(Piece_art pieceArt) {
        this.pieceArt = pieceArt;
        nompTF.setText(pieceArt.getNom_p());

        // Formatage de la date
        if (pieceArt.getDate_crea() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = pieceArt.getDate_crea().format(formatter);
            datepTF.setText(formattedDate);
        }

        imageView.setImage(new Image(pieceArt.getPhoto_p())); // Afficher l'image actuelle
        imagePath = pieceArt.getPhoto_p(); // Stocke le chemin actuel de l'image
        descpTF.setText(pieceArt.getDesc_p());
        titreG.setText("Modifier la Pièce d'Art : " + pieceArt.getNom_p());
    }

    @FXML
    public void importerImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) nompTF.getScene().getWindow(); // Get the stage from the TextField
        File file = fileChooser.showOpenDialog(stage); // Show file chooser dialog

        if (file != null) {
            imagePath = file.toURI().toString(); // Stocker le chemin de l'image
            imageView.setImage(new Image(imagePath)); // Afficher l'image sélectionnée
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
    }

    @FXML
    public void modifierP(ActionEvent actionEvent) {
        // Récupération des nouvelles valeurs
        String nom = nompTF.getText();
        String desc = descpTF.getText();
        String dateStr = datepTF.getText(); // Date sous forme de chaîne

        try {
            // Récupérer la date sous forme de LocalDate
            LocalDate dateCrea = null;
            if (!dateStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dateCrea = LocalDate.parse(dateStr, formatter); // Parsing de la date
            }

            // Mettre à jour la pièce d'art
            pieceArt.setNom_p(nom);
            pieceArt.setDate_crea(Date.valueOf(dateCrea).toLocalDate()); // Conversion en java.sql.Date
            pieceArt.setPhoto_p(imagePath); // Le chemin de l'image
            pieceArt.setDesc_p(desc);
            servicePieceArt.modifier(pieceArt);
            System.out.println("La pièce d'art a été modifiée avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}