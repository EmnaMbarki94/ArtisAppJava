package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import tn.esprit.entities.Question;
import tn.esprit.entities.Quiz;
import tn.esprit.services.ServiceQuestion;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AjoutQuestionController implements Initializable {
    private final ServiceQuestion serviceQuestion = new ServiceQuestion();
    private Quiz quiz;

    private Question currentQuestion;

    @FXML
    private AnchorPane anchor;

    @FXML
    private Button annulerbtn;

    @FXML
    private TextField contenu_q;

    @FXML
    private Text err1;

    @FXML
    private Text err2;

    @FXML
    private Text err3;

    @FXML
    private Text err4;

    @FXML
    private Text err5;

    @FXML
    private TextField rep1;

    @FXML
    private TextField rep2;

    @FXML
    private TextField rep3;

    @FXML
    private TextField rep4;

    @FXML
    private Button validerButton;

    public void handleSubmit(ActionEvent actionEvent) {
        err1.setText("");
        err2.setText("");
        err3.setText("");
        err4.setText("");
        err5.setText("");
        boolean isValid = true;
        String question = contenu_q.getText().trim();
        String r1 = rep1.getText().trim();
        String r2 = rep2.getText().trim();
        String r3 = rep3.getText().trim();
        String r4 = rep4.getText().trim();

        if (!question.endsWith("?")) {
            err1.setText("question devrait se terminer par ?");
            isValid = false;
        }
        if (question.length() < 10) {
            err1.setText("question est trop courte");
            isValid = false;
        }
        if (question.isEmpty()) {
            err1.setText("question est requise");
            isValid = false;
        }
        if (r1.isEmpty()) {
            err2.setText("reponse 1 est requise");
            isValid = false;
        }
        if (r2.isEmpty()) {
            err3.setText("reponse 2 est requise");
            isValid = false;
        }
        if (r3.isEmpty()) {
            err4.setText("reponse 3 est requise");
            isValid = false;
        }
        if (r4.isEmpty()) {
            err5.setText("reponse 4 est requise");
            isValid = false;
        }
        if (isValid) {
            List<String> l=new ArrayList<>();
            l.add(r1);
            l.add(r2);
            l.add(r3);
            l.add(r4);
            try {
                if (currentQuestion==null){
                    Question question1=new Question(question,l,quiz);
                    serviceQuestion.ajouter(question1);
                    System.out.println("question ajouté");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ajout réussi");
                    alert.setHeaderText(null);
                    alert.setContentText("Le question a été ajouté avec succès !");
                    alert.showAndWait();

                    contenu_q.clear();
                    rep1.clear();
                    rep2.clear();
                    rep3.clear();
                    rep4.clear();
                }else{
                    currentQuestion.setContenu_q(question);
                    currentQuestion.setReponses(l);

                    System.out.println(currentQuestion.getQuiz());
                    System.out.println(currentQuestion.getContenu_q());
                    System.out.println(currentQuestion.getReponses());
                    serviceQuestion.modifier(currentQuestion);
                    System.out.println("Question modifiée");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Modification réussie");
                    alert.setHeaderText(null);
                    alert.setContentText("La question a été modifiée avec succès !");
                    alert.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuestionAdmin.fxml"));
                    Parent ajoutQuestionView = null;
                    try {
                        ajoutQuestionView = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    AjoutQuestionController controller = loader.getController();
                    controller.setQuiz(currentQuestion.getQuiz());

                    anchor.getChildren().setAll(ajoutQuestionView);

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        err1.setText("");
        err2.setText("");
        err3.setText("");
        err4.setText("");
        err5.setText("");
    }
    public void setQuestion(Question question) {
        this.currentQuestion = question;
        contenu_q.setText(question.getContenu_q());

        List<String> reponses = question.getReponses();
        if (reponses.size() >= 4) {
            rep1.setText(reponses.get(0));
            rep2.setText(reponses.get(1));
            rep3.setText(reponses.get(2));
            rep4.setText(reponses.get(3));
        }
    }
}
