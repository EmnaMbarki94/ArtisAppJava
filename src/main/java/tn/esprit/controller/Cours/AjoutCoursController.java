package tn.esprit.controller.Cours;

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
import javafx.stage.Window;
import java.io.File;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceCours;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;



public class AjoutCoursController implements Initializable {

    private Personne user= Session.getUser();

    private Cours coursModif=null;

    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField titreC;

    @FXML
    private TextField categC;

    @FXML
    private TextArea contenuC;

    @FXML
    private Text err1;

    @FXML
    private Text err2;

    @FXML
    private Text err3;

    @FXML
    private Button validerButton;

    @FXML
    private Button uploadImageButton;

    @FXML
    private Label imagePathLabel;

    @FXML
    private ImageView previewImage;

    @FXML
    private Text errImage;

    private final ServiceCours serviceCours = new ServiceCours();

    private String existingImagePath = null;
    private String selectedImagePath = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        validerButton.setOnAction(event -> handleSubmit());
        err1.setText("");
        err2.setText("");
        err3.setText("");
    }

    @FXML
    private void handleSubmit() {
        boolean isValid = true;

        err1.setText("");
        err2.setText("");
        err3.setText("");
        errImage.setText("");

        String titre = titreC.getText().trim();
        String categ = categC.getText().trim();
        String contenu = contenuC.getText().trim();

        if (titre.length()<10) {
            err1.setText("Titre est trop court");
            isValid = false;
        }
        if (titre.isEmpty()) {
            err1.setText("Titre est requis");
            isValid = false;
        }


        if (categ.isEmpty()) {
            err2.setText("Catégorie est requise");
            isValid = false;
        }

        if (contenu.isEmpty()) {
            err3.setText("Contenu est requis");
            isValid = false;
        }
        if (selectedImagePath == null || selectedImagePath.isEmpty()) {
            if(existingImagePath==null){
                errImage.setText("Image est requise");
                isValid = false;
            }
        }

        if (isValid) {
            try{
                if (coursModif == null) {
                    Cours cours = new Cours();
                    cours.setNom_c(titre);
                    cours.setCateg_c(categ);
                    cours.setImage(selectedImagePath);
                    cours.setContenu_c(contenu);
                    cours.setDate_c(LocalDate.now());
                    cours.setHeure_c(LocalTime.now());
                    cours.setUser(user);

                    serviceCours.ajouterssq(cours);
                    System.out.println("Cours ajouté avec succès");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ajout réussi");
                    alert.setHeaderText(null);
                    alert.setContentText("Le cours a été ajouté avec succès !");
                    alert.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuizAdmin.fxml"));
                    Parent ajoutQuizView = loader.load();

                    AjoutQuizController controller = loader.getController();
                    controller.setCours(cours);

                    anchor.getChildren().setAll(ajoutQuizView);
                }
                else{
                    coursModif.setNom_c(titre);
                    coursModif.setCateg_c(categ);
                    coursModif.setContenu_c(contenu);
                    if (selectedImagePath != null) {
                        coursModif.setImage(selectedImagePath);
                    } else {
                        coursModif.setImage(existingImagePath);
                    }
                    serviceCours.modifier(coursModif);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Modification réussie");
                    alert.setHeaderText(null);
                    alert.setContentText("Le cours a été modifié avec succès !");
                    alert.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/CoursDetails.fxml"));
                    Parent root = loader.load();

                    CoursDetailsController controller = loader.getController();
                    controller.setCours(coursModif);

                    anchor.getChildren().setAll(root);

                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout du cours: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void handleUploadImage() {
        String projectRoot = System.getProperty("user.dir");
        String targetDir = projectRoot + File.separator + "uploads" + File.separator + "cours";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Window stage = anchor.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(targetDir, fileName);

                java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                selectedImagePath = fileName;
                imagePathLabel.setText(fileName);

                if (destFile.exists()) {
                    Image image = new Image(destFile.toURI().toString(), false);
                    previewImage.setImage(image);
                    errImage.setText("");
                } else {
                    errImage.setText("Erreur: image non trouvée après la copie");
                    System.err.println("Image non trouvée après la copie: " + destFile.getAbsolutePath());
                }

            } catch (IOException e) {
                e.printStackTrace();
                errImage.setText("Erreur lors de la copie de l'image");
            }
        }
    }

    public void handleAnnuler(ActionEvent actionEvent) {
        try {
            System.out.println("back to cours menu!");

            Parent EnscoursView = FXMLLoader.load(getClass().getResource("/Fxml/Admin/EnseignementAdmin.fxml"));
            anchor.getChildren().setAll(EnscoursView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCoursModif(Cours cours) {
        this.coursModif = cours;
        remplirChampsPourModification();
    }
    private void remplirChampsPourModification() {
        if (coursModif != null) {
            titreC.setText(coursModif.getNom_c());
            categC.setText(coursModif.getCateg_c());
            contenuC.setText(coursModif.getContenu_c());
            existingImagePath = coursModif.getImage();
            String imagePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "cours" + File.separator + existingImagePath;
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString(), false);
                previewImage.setImage(image);
                imagePathLabel.setText(existingImagePath);
            } else {
                System.out.println("Image file not found: " + imagePath);
                Image fallbackImage = new Image(getClass().getResource("/image/art.jpg").toExternalForm(), false);
                previewImage.setImage(fallbackImage);
            }

        }
    }
}
