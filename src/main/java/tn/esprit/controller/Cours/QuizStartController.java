package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Question;
import tn.esprit.entities.Quiz;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceQuestion;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class QuizStartController implements Initializable {

    private Personne user= Session.getUser();

    @FXML
    private AnchorPane anchor;

    @FXML
    private Label quizTitleLabel;

    @FXML
    private VBox questionsContainer;

    @FXML
    private Button submitButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button exit;

    private Question currentQuestion = null;

    private Quiz quiz;
    private List<Question> questions;
    private int currentIndex = 0;

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        System.out.println("quiz : "+quiz.getTitre_c());

        quizTitleLabel.setText(quiz.getTitre_c());
        loadQuestions();

    }

    private void loadQuestions() {
        ServiceQuestion serviceQuestion = new ServiceQuestion();
        try {
            List<Question> allQuestions = serviceQuestion.selectAll();
            questions = allQuestions.stream()
                    .filter(q -> q.getQuiz().getId() == quiz.getId())
                    .collect(Collectors.toList());
            System.out.println("question loaded "+quiz.getTitre_c());
            if(!questions.isEmpty()) {
                showQuestion(currentIndex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showQuestion(int index) {
        questionsContainer.getChildren().clear();
        if (index >= 0 && index < questions.size()) {
            Question currentQuestion = questions.get(index);
            VBox questionBox = createQuestionBox(currentQuestion);
            questionsContainer.getChildren().add(questionBox);

            prevButton.setDisable(index == 0);
            nextButton.setDisable(index == questions.size() - 1);
        }
        System.out.println("showing question "+index);
    }

    private VBox createQuestionBox(Question question) {
        VBox box = new VBox(10);
        box.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9; -fx-background-radius: 8;");
        box.setPrefWidth(800);

        Label questionLabel = new Label(question.getContenu_q());
        questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        box.getChildren().add(questionLabel);

        System.out.println(question.getContenu_q());

        ToggleGroup toggleGroup = new ToggleGroup();
        List<String> answers = question.getReponses();

        System.out.println(answers);

        for (String answer : answers) {
            RadioButton rb = new RadioButton(answer);
            rb.setStyle("-fx-font-size: 14px;");
            rb.setToggleGroup(toggleGroup);
            box.getChildren().add(rb);
        }
        System.out.println("question box created");
        if (user.getRoles().contains("ROLE_USER")) {
        }
        else {
            Button editBtn = new Button("Modifier cette question");
            editBtn.setOnAction(e -> handleEditQuestion(question));
            box.getChildren().add(editBtn);

            Button deleteBtn = new Button("Supprimer cette question");
            deleteBtn.setOnAction(e -> handleDeleteQuestion(question));
            box.getChildren().add(deleteBtn);
        }
        return box;
    }

    private void handleDeleteQuestion(Question question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer cette question ?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ServiceQuestion serviceQuestion = new ServiceQuestion();
                serviceQuestion.supprimer(question.getId());

                questions.remove(question);
                if (currentIndex >= questions.size()) {
                    currentIndex = questions.size() - 1;
                }
                showQuestion(currentIndex);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Suppression");
                info.setHeaderText(null);
                info.setContentText("Question supprimée avec succès !");
                info.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors de la suppression.");
                error.showAndWait();
            }
        }
    }

    private void handleEditQuestion(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuestionAdmin.fxml"));
            Parent root = loader.load();

            AjoutQuestionController controller = loader.getController();
            controller.setQuestion(question);
            anchor.getChildren().setAll(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        }
    }

    @FXML
    private void handlePrev(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        //metier
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("quiz start initialized ");

    }
    public void handleExit(ActionEvent actionEvent) {
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
}