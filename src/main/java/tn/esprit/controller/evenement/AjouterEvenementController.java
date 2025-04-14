package tn.esprit.controller.evenement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import tn.esprit.entities.Event;
import tn.esprit.services.ServiceEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterEvenementController {

    @FXML private TextField nomField;
    @FXML private TextField typeField;
    @FXML private TextArea infoField;
    @FXML private DatePicker datePicker;
    @FXML private TextField prixStandardField;
    @FXML private TextField prixVIPField;
    @FXML private TextField nbTicketField;
    @FXML private TextField photoField;
    @FXML private ImageView imageView;
    @FXML private AnchorPane contenuPane;

    // Labels pour erreurs
    @FXML private Label errorNom;
    @FXML private Label errorType;
    @FXML private Label errorInfo;
    @FXML private Label errorDate;
    @FXML private Label errorPrixStandard;
    @FXML private Label errorPrixVIP;
    @FXML private Label errorNbTicket;

    private ServiceEvent evenementService;

    public void initialize() {
        evenementService = new ServiceEvent();
    }

    public void ajouterEvenement(ActionEvent actionEvent) {
        clearErrors();

        String eventName = nomField.getText().trim();
        String eventType = typeField.getText().trim();
        String eventInfo = infoField.getText().trim();
        LocalDate eventDate = datePicker.getValue();
        String photoPath = photoField.getText().trim();

        int prixStandard = 0, prixVIP = 0, nbTickets = 0;

        boolean valid = true;

        if (eventName.isEmpty()) {
            errorNom.setText("Le nom est requis.");
            valid = false;
        } else if (!eventName.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            errorNom.setText("Le nom ne doit contenir que des lettres.");
            valid = false;
        }

        if (eventType.isEmpty()) {
            errorType.setText("Le type est requis.");
            valid = false;
        } else if (!eventType.matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            errorType.setText("Le type ne doit contenir que des lettres.");
            valid = false;
        }

        if (eventInfo.isEmpty()) {
            errorInfo.setText("Les informations sont requises.");
            valid = false;
        }

        if (eventDate == null) {
            errorDate.setText("La date est requise.");
            valid = false;
        }

        if (photoPath.isEmpty()) {
            // on peut ignorer cette erreur si image non obligatoire
        }

        try {
            prixStandard = Integer.parseInt(prixStandardField.getText().trim());
            if (prixStandard < 0) {
                errorPrixStandard.setText("Le prix doit être positif.");
                valid = false;
            }
        } catch (NumberFormatException e) {
            errorPrixStandard.setText("Prix standard invalide.");
            valid = false;
        }

        try {
            prixVIP = Integer.parseInt(prixVIPField.getText().trim());
            if (prixVIP < 0) {
                errorPrixVIP.setText("Le prix doit être positif.");
                valid = false;
            }
        } catch (NumberFormatException e) {
            errorPrixVIP.setText("Prix VIP invalide.");
            valid = false;
        }

        try {
            nbTickets = Integer.parseInt(nbTicketField.getText().trim());
            if (nbTickets < 0) {
                errorNbTicket.setText("Le nombre doit être positif.");
                valid = false;
            }
        } catch (NumberFormatException e) {
            errorNbTicket.setText("Nombre de tickets invalide.");
            valid = false;
        }

        if (!valid) return;

        // Création de l'événement
        Event event = new Event(
                eventName,
                eventType,
                eventInfo,
                eventDate,
                photoPath,
                prixStandard,
                prixVIP,
                nbTickets
        );

        try {
            evenementService.ajouter(event);
            clearFields();
            handleRetour(); // Redirection
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String destinationFolder = "src/main/resources/imagesEvent";
                File dir = new File(destinationFolder);
                if (!dir.exists()) dir.mkdirs();

                String fileName = selectedFile.getName();
                File destFile = new File(dir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                photoField.setText(fileName);
                imageView.setImage(new Image(destFile.toURI().toString()));
            } catch (IOException e) {
                System.out.println("Erreur image : " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        nomField.clear();
        typeField.clear();
        infoField.clear();
        datePicker.setValue(null);
        prixStandardField.clear();
        prixVIPField.clear();
        nbTicketField.clear();
        photoField.clear();
        imageView.setImage(null);
        clearErrors();
    }

    private void clearErrors() {
        errorNom.setText("");
        errorType.setText("");
        errorInfo.setText("");
        errorDate.setText("");
        errorPrixStandard.setText("");
        errorPrixVIP.setText("");
        errorNbTicket.setText("");
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
