package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Quiz;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceCours;
import tn.esprit.services.ServiceQuiz;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class CoursDetailsController implements Initializable {

    private Personne user= Session.getUser();

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

    private Cours cours;

    public void setCours(Cours cours) {
        this.cours = cours;
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

        if (user.getRoles().contains("ROLE_USER")) {
            deleteQuizBtn.setVisible(false);
            addQuizBtn.setVisible(false);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
        }

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
}