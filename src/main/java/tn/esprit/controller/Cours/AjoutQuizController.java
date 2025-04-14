package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import tn.esprit.entities.Cours;
import tn.esprit.entities.Quiz;
import tn.esprit.services.ServiceCours;
import tn.esprit.services.ServiceQuiz;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjoutQuizController implements Initializable {
    private Cours cours;

    @FXML
    private Label TitreCours;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button annulerbtn;

    @FXML
    private Text err1;

    @FXML
    private TextField titreQ;

    @FXML
    private Button validerButton;

    private final ServiceQuiz serviceQuiz = new ServiceQuiz();
    private final ServiceCours serviceCours = new ServiceCours();

    @FXML
    void handleSubmit(ActionEvent event) {
        boolean isValid = true;
        err1.setText("");

        String titre = titreQ.getText();
        if (titre.length()<10) {
            err1.setText("Le titre du quiz est trop court");
            isValid = false;
        }
        if (titre.isEmpty()) {
            err1.setText("Le titre du quiz est obligatoire !");
            isValid = false;
        }


        if (isValid) {
            Quiz quiz = new Quiz(titre, cours);
            try {
                serviceQuiz.ajouter(quiz);
                cours.setQuiz(quiz);
                serviceCours.modifier(cours);
                System.out.println("Quiz ajouté avec succès");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ajout réussi");
                alert.setHeaderText(null);
                alert.setContentText("Le quiz a été ajouté avec succès !");
                alert.showAndWait();


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuestionAdmin.fxml"));
                Parent ajoutQuestionView = loader.load();

                AjoutQuestionController controller = loader.getController();
                controller.setQuiz(quiz);

                anchor.getChildren().setAll(ajoutQuestionView);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void setCours(Cours cours) {
        this.cours = cours;
        if (cours != null) {
            TitreCours.setText(cours.getNom_c());
        }
        else {
            TitreCours.setText("passed cours is null");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        err1.setText("");
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
}
