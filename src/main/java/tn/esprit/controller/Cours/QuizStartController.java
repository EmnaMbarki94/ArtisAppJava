package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import tn.esprit.entities.*;
import tn.esprit.services.ServicePersonne;
import tn.esprit.services.ServiceQuestion;
import tn.esprit.services.ServiceQuizAttempt;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
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
    private Button ajouterq;

    @FXML
    private Button exit;

    private Question currentQuestion = null;

    private Quiz quiz;
    private List<Question> questions;
    private int currentIndex = 0;
    private final java.util.Map<Question, ToggleGroup> questionToggleGroups = new java.util.HashMap<>();
    private final java.util.Map<Question, String> selectedAnswers = new java.util.HashMap<>();


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

        List<AnswerOption> answerOptions = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            answerOptions.add(new AnswerOption(answers.get(i), i == 0));
        }
        Collections.shuffle(answerOptions);
        questionToggleGroups.put(question, toggleGroup);

        for (AnswerOption option : answerOptions) {
            RadioButton rb = new RadioButton(option.getText());
            rb.setStyle("-fx-font-size: 14px;");
            rb.setUserData(option.isCorrect());
            rb.setToggleGroup(toggleGroup);
            if (selectedAnswers.containsKey(question) && selectedAnswers.get(question).equals(option.getText())) {
                rb.setSelected(true);
            }
            box.getChildren().add(rb);
        }

        System.out.println("question box created");
        if (user.getRoles().contains("ROLE_ADMIN") || user.getRoles().contains("ROLE_ENSEIGNANT")) {
            Button editBtn = new Button("Modifier cette question");
            editBtn.setOnAction(e -> handleEditQuestion(question));
            box.getChildren().add(editBtn);

            Button deleteBtn = new Button("Supprimer cette question");
            deleteBtn.setOnAction(e -> handleDeleteQuestion(question));
            box.getChildren().add(deleteBtn);

            ajouterq.setVisible(true);
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
    private void handleAddQuestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/cours/AjoutQuestionAdmin.fxml"));
            Parent ajoutQuestionView = loader.load();

            AjoutQuestionController controller = loader.getController();
            controller.setQuiz(quiz);

            anchor.getChildren().setAll(ajoutQuestionView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        saveAnswer(currentIndex);
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        }
    }

    @FXML
    private void handlePrev(ActionEvent event) {
        saveAnswer(currentIndex);
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
        }
    }
    private void saveAnswer(int index) {
        if (index >= 0 && index < questions.size()) {
            Question question = questions.get(index);
            ToggleGroup group = questionToggleGroups.get(question);
            if (group != null && group.getSelectedToggle() != null) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                selectedAnswers.put(question, selected.getText());
            }
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        int score = 0;

        for (Question question : questions) {
            ToggleGroup group = questionToggleGroups.get(question);
            if (group != null) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                if (selected != null && Boolean.TRUE.equals(selected.getUserData())) {
                    score++;
                }
            }
        }

        Alert resultDialog = new Alert(Alert.AlertType.INFORMATION);
        resultDialog.setTitle("Résultat du Quiz");
        resultDialog.setHeaderText(null);
        if (score>questions.size()*0.7){
            resultDialog.setContentText("Félicitations, Votre score est : " + score + " / " + questions.size());
        }
        else{
            resultDialog.setContentText("Malheureusement, Votre score est : " + score + " / " + questions.size());
        }
        resultDialog.showAndWait();
        Quiz_attempt quiz_attempt = new Quiz_attempt();
        quiz_attempt.setNote(score);
        quiz_attempt.setUser(user);
        quiz_attempt.setQuiz(quiz);
        ServiceQuizAttempt serviceQuizAttempt = new ServiceQuizAttempt();
        try {
            serviceQuizAttempt.ajouter(quiz_attempt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        user.setPoint(user.getPoint()+score);
        ServicePersonne servicePersonne = new ServicePersonne();
        servicePersonne.modifierPoint(user);

        handleExit(null);
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
    private static class AnswerOption {
        private final String text;
        private final boolean isCorrect;

        public AnswerOption(String text, boolean isCorrect) {
            this.text = text;
            this.isCorrect = isCorrect;
        }

        public String getText() {
            return text;
        }

        public boolean isCorrect() {
            return isCorrect;
        }
    }
}