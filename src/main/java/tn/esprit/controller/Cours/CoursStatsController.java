package tn.esprit.controller.Cours;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Quiz_attempt;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceCours;
import tn.esprit.services.ServiceQuiz;
import tn.esprit.services.ServiceQuizAttempt;

public class CoursStatsController implements Initializable {
    private final Personne user= Session.getUser();

    @FXML
    private AnchorPane anchor;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label coursesWithQuizzesLabel;

    @FXML
    private Label coursesWithoutQuizzesLabel;

    @FXML
    private PieChart coursePieChart;

    @FXML
    private BarChart<String, Number> topQuizzesBarChart;

    @FXML
    private TableView<UserAttempts> leaderboardTable;

    @FXML
    private TableColumn<UserAttempts, String> userColumn;

    @FXML
    private TableColumn<UserAttempts, Integer> attemptsColumn;

    private ServiceCours courseService = new ServiceCours();
    private ServiceQuizAttempt quizAttemptService = new ServiceQuizAttempt();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCourseStatistics();
        loadPieChart();
        loadTopQuizzesBarChart();
        loadLeaderboardTable();
    }
    private void loadCourseStatistics() {
        int totalCourses = courseService.countTotalCourses();
        int coursesWithQuizzes = courseService.countCoursesWithQuizzes();
        int coursesWithoutQuizzes = totalCourses - coursesWithQuizzes;

        totalCoursesLabel.setText(String.valueOf(totalCourses));
        coursesWithQuizzesLabel.setText(String.valueOf(coursesWithQuizzes));
        coursesWithoutQuizzesLabel.setText(String.valueOf(coursesWithoutQuizzes));
    }

    private void loadPieChart() {
        PieChart.Data withQuiz = new PieChart.Data("Avec Quiz", courseService.countCoursesWithQuizzes());
        PieChart.Data withoutQuiz = new PieChart.Data("Sans Quiz", courseService.countTotalCourses() - courseService.countCoursesWithQuizzes());

        coursePieChart.getData().addAll(withQuiz, withoutQuiz);
    }

    private void loadTopQuizzesBarChart() {
        topQuizzesBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        quizAttemptService.getTop5QuizzesByAttempts().forEach((quizTitle, attempts) -> {
            series.getData().add(new XYChart.Data<>(quizTitle, attempts));
        });
        topQuizzesBarChart.getData().add(series);
    }

    private void loadLeaderboardTable() {
        userColumn.setCellValueFactory(cellData->new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUser().getEmail()));
        attemptsColumn.setCellValueFactory(new PropertyValueFactory<>("attempts"));
        leaderboardTable.getItems().addAll(quizAttemptService.getTop5UsersByAttempts());
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

    public static class UserAttempts {
        private Personne user;
        private int attempts;

        public UserAttempts(Personne user, int attempts) {
            this.user = user;
            this.attempts = attempts;
        }

        public Personne getUser() {
            return user;
        }

        public int getAttempts() {
            return attempts;
        }
    }
}
