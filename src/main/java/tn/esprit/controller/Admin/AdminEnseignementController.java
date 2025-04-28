package tn.esprit.controller.Admin;

import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import tn.esprit.controller.Cours.CoursDetailsController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Cours;
import tn.esprit.services.ServiceCours;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AdminEnseignementController implements Initializable {
    private Personne user=Session.getUser();

    List<Cours> coursList=null;
    @FXML
    private AnchorPane anchor;

    @FXML
    private TextField searchField;

    @FXML
    private FlowPane coursesContainer;

    private final ServiceCours serviceCours = new ServiceCours();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            System.out.println("session.userid :"+user.getEmail());
            System.out.println("user :"+user);

            coursList = serviceCours.selectAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(coursList.size());
        if(coursList==null || coursList.size()==0){
            coursesContainer.setVisible(false);
        }
        coursesContainer.getChildren().clear();
        for (Cours cours : coursList) {
            VBox card = createCourseCard(cours);
            //System.out.println(cours.getId());

            coursesContainer.getChildren().add(card);
        }

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCourses(newValue,coursList);
        });
    }
    private void filterCourses(String query, List<Cours> coursList) {
        coursesContainer.getChildren().clear();

        if (query.isEmpty()) {
            for (Cours cours : coursList) {
                VBox card = createCourseCard(cours);
                coursesContainer.getChildren().add(card);
            }
        } else {
            for (Cours cours : coursList) {
                if (cours.getNom_c().toLowerCase().contains(query.toLowerCase()) ||
                        cours.getCateg_c().toLowerCase().contains(query.toLowerCase())) {
                    VBox card = createCourseCard(cours);
                    coursesContainer.getChildren().add(card);
                }
            }
        }
    }
    private VBox createCourseCard(Cours cours) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        ImageView imageView = new ImageView();
        try {
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
        } catch (Exception e) {
            System.out.println("Image not found for course: " + cours.getNom_c());
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);

        Label titre = new Label(cours.getNom_c());
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        titre.setOnMouseClicked(event -> {
            handleVoirDetails(cours);
        });

        Label categorie = new Label(cours.getCateg_c());
        categorie.setStyle("-fx-text-fill: #666;");

        card.getChildren().addAll(imageView, titre, categorie);
        return card;
    }

    @FXML
    private void goToAjoutCours(ActionEvent event) {
        try {
            Parent ajoutCoursView = FXMLLoader.load(getClass().getResource("/Fxml/cours/AjoutCoursAdmin.fxml"));

            anchor.getChildren().setAll(ajoutCoursView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoirDetails(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/CoursDetails.fxml"));
            Parent root = loader.load();

            CoursDetailsController controller = loader.getController();
            controller.setCours(cours);

            anchor.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToStats(ActionEvent event) {
        try {
            Parent CoursStatsView = FXMLLoader.load(getClass().getResource("/Fxml/cours/coursStats.fxml"));

            anchor.getChildren().setAll(CoursStatsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

