package tn.esprit.controller.Cours;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import tn.esprit.entities.*;
import tn.esprit.services.ServiceCours;
import tn.esprit.services.ServiceQuiz;
import tn.esprit.utils.TextToSpeech;
import tn.esprit.utils.Translator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoursDetailsController implements Initializable {

    private final Personne user= Session.getUser();

    @FXML
    private AnchorPane anchor;

    @FXML
    private Label categorieLabel;

    @FXML
    private Label datec;

    @FXML
    private Text descriptionText;

    @FXML
    private Label heurec;

    @FXML
    private Button retour;

    @FXML
    private Label titreLabel;

    @FXML
    private Button quizButton;

    @FXML
    private Button deleteQuizBtn;

    @FXML
    private Button addQuizBtn;

    @FXML
    private Text user_flname;

    @FXML
    private Text user_email;

    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private ImageView imageView;
    @FXML
    private Button exportPdfButton;
    @FXML
    private ToggleButton langToggleButton;
    @FXML
    private ToggleButton ttsButton;




    private boolean isFrench = true;
    private String originalTitle;
    private String originalCategory;
    private String originalContent;

    private Cours cours;

    public void setCours(Cours cours) {
        this.cours = cours;
        originalTitle = cours.getNom_c();
        originalCategory = cours.getCateg_c();
        originalContent = cours.getContenu_c();

        titreLabel.setText(cours.getNom_c());
        categorieLabel.setText("Catégorie : " + cours.getCateg_c());
        descriptionText.setText(cours.getContenu_c());
        datec.setText(cours.getDate_c().toString());
        heurec.setText(cours.getHeure_c().toString());

        System.out.println(cours.getUser().getFirst_Name());
        if (cours.getUser() != null) {
            user_flname.setText(this.cours.getUser().getFirst_Name()+" "+this.cours.getUser().getLast_Name());
            user_email.setText(this.cours.getUser().getEmail());
        } else {
            user_flname.setText("Ajouté par : Inconnu");
        }

        String imagePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "cours" + File.separator + cours.getImage();
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            Image image = new Image(imageFile.toURI().toString(), false);
            imageView.setImage(image);
        } else {
            System.out.println("Image file not found: " + imagePath);
            Image fallbackImage = new Image(getClass().getResource("/image/art.jpg").toExternalForm(), false);
            imageView.setImage(fallbackImage);
        }

        ServiceQuiz serviceQuiz = new ServiceQuiz();
        try {
            boolean hasQuiz = serviceQuiz.quizExistsForCours(cours.getId());
            quizButton.setVisible(hasQuiz);
            deleteQuizBtn.setVisible(hasQuiz);
            addQuizBtn.setVisible(!hasQuiz);
        } catch (SQLException e) {
            e.printStackTrace();
            quizButton.setVisible(false);
        }

        if (!user.getRoles().contains("ROLE_ADMIN") && !user.getRoles().contains("ROLE_ENSEIGNANT")) {
            deleteQuizBtn.setVisible(false);
            addQuizBtn.setVisible(false);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        Image icon=new Image(getClass().getResourceAsStream("/icons/translate.png"));
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(40);
        iconView.setFitWidth(40);
        langToggleButton.setGraphic(iconView);
        langToggleButton.setContentDisplay(ContentDisplay.LEFT);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void retour(ActionEvent actionEvent) {
        try {
            System.out.println("back to cours menu!");

            if(user.getRoles().contains("ROLE_ADMIN")) {
                Parent EnscoursView = FXMLLoader.load(getClass().getResource("/Fxml/Admin/EnseignementAdmin.fxml"));
                anchor.getChildren().setAll(EnscoursView);
            }
            else{
                Parent EnscoursView = FXMLLoader.load(getClass().getResource("/Fxml/users/Enseignement.fxml"));
                anchor.getChildren().setAll(EnscoursView);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleQuizStart(ActionEvent event) {
        try {
            ServiceQuiz serviceQuiz = new ServiceQuiz();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/QuizStart.fxml"));
            Parent root = loader.load();

            QuizStartController controller = loader.getController();
            Quiz q = cours.getQuiz();
            System.out.println("nom c : "+cours.getNom_c());
            System.out.println("quiz title :"+q.getTitre_c());
            controller.setQuiz(q);

            anchor.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEdit(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutCoursAdmin.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AjoutCoursController controller = loader.getController();
        controller.setCoursModif(cours);

        anchor.getChildren().setAll(root);
    }

    public void handleDelete(ActionEvent actionEvent) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Voulez-vous vraiment supprimer ce cours?");
        confirmationAlert.setContentText("Cette action est irréversible.");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                deleteCourse();
            }
        });
    }

    private void deleteCourse() {
        ServiceCours serviceCours = new ServiceCours();
        try {
            serviceCours.supprimer(cours.getId());
            try {
                System.out.println("back to cours menu!");

                Parent EnscoursView = FXMLLoader.load(getClass().getResource("/Fxml/Admin/EnseignementAdmin.fxml"));
                anchor.getChildren().setAll(EnscoursView);
            } catch (IOException e) {
                e.printStackTrace();
            }
            showDeletionSuccessAlert();
        } catch (Exception e) {
            showDeletionErrorAlert();
        }
    }
    private void showDeletionSuccessAlert() {
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Suppression réussie");
        successAlert.setHeaderText("Le cours a été supprimé avec succès.");
        successAlert.showAndWait();
    }
    private void showDeletionErrorAlert() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Erreur de suppression");
        errorAlert.setHeaderText("Une erreur est survenue lors de la suppression.");
        errorAlert.showAndWait();
    }

    @FXML
    private void handleDeleteQuiz(ActionEvent event) {
        if (cours.getQuiz() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pas de quiz");
            alert.setHeaderText(null);
            alert.setContentText("Ce cours n’a pas encore de quiz.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer le quiz associé à ce cours ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ServiceQuiz serviceQuiz = new ServiceQuiz();
                serviceQuiz.supprimer(cours.getQuiz().getId());

                cours.setQuiz(null);
                ServiceCours serviceCours = new ServiceCours();
                serviceCours.modifier(cours);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Suppression réussie");
                success.setHeaderText(null);
                success.setContentText("Le quiz a été supprimé avec succès.");
                success.showAndWait();

                deleteQuizBtn.setVisible(false);
                quizButton.setVisible(false);


            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la suppression du quiz.");
                error.showAndWait();
            }
        }
    }
    @FXML
    private void handleAddQuiz(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuizAdmin.fxml"));
            Parent root = loader.load();

            AjoutQuizController ajoutQuizController = loader.getController();
            ajoutQuizController.setCours(cours);

            anchor.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPdf(ActionEvent event) {
        Document document = new Document();
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.setInitialFileName(cours.getNom_c() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());

            if (file != null) {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
                Paragraph title = new Paragraph(titreLabel.getText(), titleFont);
                document.add(title);
                document.add(new Paragraph("\n"));
                if (cours != null && cours.getImage() != null) {
                    String imagePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "cours" + File.separator + cours.getImage();
                    System.out.println("img path "+imagePath);
                    File imageFile = new File(imagePath);

                    if (imageFile.exists()) {
                        com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageFile.getAbsolutePath());
                        pdfImage.scaleToFit(400f, 300f);
                        document.add(pdfImage);
                    } else {
                        String fallbackImagePath = getClass().getResource("/image/art.jpg").getPath();
                        com.itextpdf.text.Image fallbackPdfImage = com.itextpdf.text.Image.getInstance(fallbackImagePath);
                        fallbackPdfImage.scaleToFit(400f, 300f);
                        document.add(fallbackPdfImage);
                    }
                }
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(categorieLabel.getText(), normalFont));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(descriptionText.getText(), normalFont));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Date de creation: " + datec.getText() + "  "+heurec.getText(), normalFont));
                document.add(new Paragraph("Cours Ajouté par: " + user_flname.getText() + "  " + user_email.getText(), normalFont));
                document.add(new Paragraph("\n"));


                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Exporté");
                alert.setHeaderText(null);
                alert.setContentText("Le cours a été exporté avec succès !");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur PDF");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'exportation.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLanguageToggle(ActionEvent event) {
        new Thread(() -> {
            try {
                if (isFrench) {
                    String translatedTitle = Translator.translate(originalTitle, "auto", "en");
                    String translatedCategory = Translator.translate(originalCategory, "auto", "en");
                    String translatedContent = Translator.translate(originalContent, "auto", "en");

                    Platform.runLater(() -> {
                        titreLabel.setText(translatedTitle);
                        categorieLabel.setText("Category: " + translatedCategory);
                        descriptionText.setText(translatedContent);
                        langToggleButton.setText("FR");
                    });
                } else {
                    Platform.runLater(() -> {
                        titreLabel.setText(originalTitle);
                        categorieLabel.setText("Catégorie : " + originalCategory);
                        descriptionText.setText(originalContent);
                        langToggleButton.setText("EN");
                    });
                }
                isFrench = !isFrench;
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    descriptionText.setText("Erreur de traduction !");
                });
            }
        }).start();
    }
    @FXML
    private void handleSpeak() {
        if (ttsButton.isSelected()) {
            String content = descriptionText.getText();
            String language;
            if(isFrench){
                language="fr-fr";
            }
            else{
                language="en-us";
            }
            TextToSpeech.speak(content, language);

        }
        else{
            TextToSpeech.pause();
        }
    }
}

