package tn.esprit.controller.Admin;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.concurrent.Task;
import tn.esprit.gui.gui;
import tn.esprit.utils.DBConnection;

public class AdminStatistiqueController implements Initializable {

    public MenuItem show_barChart_btn;
    public MenuItem show_pieChart_btn;
    public MenuItem close_chart_btn;
    public Button refreshButton;
    @FXML
    private BorderPane borderPane;

    @FXML private TextField enseignantField;
    @FXML private TextField artisteField;
    @FXML private TextField userField;

    @FXML
    private Label topSpecialtyLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        borderPane.setCenter(buildPieChart());



        // Initialiser les champs
        updateSpecialtyTextFields();
        updateTopSpecialty();
        // Démarrer la mise à jour automatique
        startLiveUpdate();
    }

    private ObservableList<Integer> getSpecialtyCounts() {
        ObservableList<Integer> counts = FXCollections.observableArrayList();

      //  String query = "SELECT specialite, COUNT(*) AS count FROM user GROUP BY specialite";

        String query = "SELECT specialite, COUNT(*) AS count FROM user GROUP BY specialite";

        try (PreparedStatement ps = DBConnection.getInstance().getCnx().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            int enseignementCount = 0, artisteCount = 0, userCount = 0;

            while (rs.next()) {
                String specialite = rs.getString("specialite");
                int count = rs.getInt("count");

                if ("enseignant".equalsIgnoreCase(specialite)) {
                    enseignementCount = count;
                } else if ("artiste".equalsIgnoreCase(specialite)) {
                    artisteCount = count;
                } else if ("user".equalsIgnoreCase(specialite)) {
                    userCount = count;
                }
            }

            counts.add(enseignementCount);
            counts.add(artisteCount);
            counts.add(userCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return counts;
    }

    private BarChart<String, Number> buildBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Spécialités");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre d'utilisateurs");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        ObservableList<Integer> counts = getSpecialtyCounts();

        if (counts == null || counts.isEmpty()) {
            counts = FXCollections.observableArrayList(0, 0, 0);
        }

        int enseignementCount = counts.size() > 0 ? counts.get(0) : 0;
        int artisteCount = counts.size() > 1 ? counts.get(1) : 0;
        int userCount = counts.size() > 2 ? counts.get(2) : 0;

        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Répartition des spécialités");

        dataSeries.getData().add(new XYChart.Data<>("Enseignant", enseignementCount));
        dataSeries.getData().add(new XYChart.Data<>("Artiste", artisteCount));
        dataSeries.getData().add(new XYChart.Data<>("User", userCount));

        barChart.getData().add(dataSeries);

        return barChart;
    }

    private PieChart buildPieChart() {
        ObservableList<Integer> specialtyCounts = getSpecialtyCounts();

        if (specialtyCounts.size() < 3) {
            System.err.println("Les données de spécialité sont incomplètes.");
            return new PieChart();
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Enseignant", specialtyCounts.get(0)),
                new PieChart.Data("Artiste", specialtyCounts.get(1)),
                new PieChart.Data("User", specialtyCounts.get(2))
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Répartition des utilisateurs par spécialité");

        return pieChart;
    }

    @FXML
    private void handleShowBarChart() {
        borderPane.setCenter(buildBarChart());
    }

    @FXML
    private void handleShowPieChart() {
        borderPane.setCenter(buildPieChart());
    }
    @FXML
    private void handleUpdatePieData() {
        Node node = borderPane.getCenter();

        if (node instanceof PieChart)
        {
            PieChart pc = (PieChart) node;
            double value = pc.getData().get(2).getPieValue();
            pc.getData().get(2).setPieValue(value * 1.10);
            createToolTips(pc);
        }
    }
    @FXML
    private void handleManualUpdate(ActionEvent event) {
        updateSpecialtyTextFields();
        updateTopSpecialty();
    }

    private void createToolTips(PieChart pc) {

        for (PieChart.Data data: pc.getData()) {
            String msg = Double.toString(data.getPieValue());

            Tooltip tp = new Tooltip(msg);
            tp.setShowDelay(Duration.seconds(0));

            Tooltip.install(data.getNode(), tp);

            //update tooltip data when changed
            data.pieValueProperty().addListener((observable, oldValue, newValue) ->
            {
                tp.setText(newValue.toString());
            });
        }
    }
    // Méthode appelée quand le menu item est sélectionné
    public void handleClose(ActionEvent event) {
        // Récupérer l'élément source de l'événement (le MenuItem)
        Node source = (Node) event.getSource();

        // Récupérer le Stage (la fenêtre) à partir de la scène de l'élément source
        Stage stage = (Stage) source.getScene().getWindow();

        // Fermer la fenêtre
        stage.close();
    }


    //*********stat anim +text

    private void animateTextChange(TextField field, int newValue) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), field);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            field.setText(String.valueOf(newValue));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), field);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }



    private void updateSpecialtyTextFields() {
        ObservableList<Integer> counts = getSpecialtyCounts();

        int enseignant = counts.size() > 0 ? counts.get(0) : 0;
        int artiste = counts.size() > 1 ? counts.get(1) : 0;
        int user = counts.size() > 2 ? counts.get(2) : 0;

        animateTextChange(enseignantField, enseignant);
        animateTextChange(artisteField, artiste);
        animateTextChange(userField, user);
    }



    private void startLiveUpdate() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                while (true) {
                    try {
                        Thread.sleep(10000); // 10 secondes
                    } catch (InterruptedException e) {
                        break;
                    }

                    Platform.runLater(() -> {
                        updateSpecialtyTextFields();
                        updateTopSpecialty();
                    });
                }
                return null; // ✅ Correction ici
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateTopSpecialty() {
        ObservableList<Integer> counts = getSpecialtyCounts();
        if (counts == null || counts.size() < 3) return;

        String topSpecialty;
        int max = Math.max(counts.get(0), Math.max(counts.get(1), counts.get(2)));

        if (max == counts.get(0)) {
            topSpecialty = "🏆 Spécialité la plus populaire : Enseignant (" + max + ")";
        } else if (max == counts.get(1)) {
            topSpecialty = "🏆 Spécialité la plus populaire : Artiste (" + max + ")";
        } else {
            topSpecialty = "🏆 Spécialité la plus populaire : User (" + max + ")";
        }

        topSpecialtyLabel.setText(topSpecialty);

        // petite animation de mise à jour
        FadeTransition fade = new FadeTransition(Duration.seconds(1), topSpecialtyLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void handleaddUser(ActionEvent actionEvent)
    {

        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("AddUser");
    }

    public void handelMetier(ActionEvent actionEvent)
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("Metiers");
    }
}
