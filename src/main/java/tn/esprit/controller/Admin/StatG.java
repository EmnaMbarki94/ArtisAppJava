package tn.esprit.controller.Admin;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tn.esprit.services.ServiceGalerie;
import tn.esprit.services.ServicePieceArt;
import tn.esprit.entities.Piece_art;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StatG {
    @javafx.fxml.FXML
    private Label nombreGaleries;
    @javafx.fxml.FXML
    private Label nombrePieces;
    @javafx.fxml.FXML
    private BarChart barChart;
    @javafx.fxml.FXML
    private Label nombreGaleriesActives;

    private final ServiceGalerie serviceGalerie = new ServiceGalerie();
    private final ServicePieceArt servicePieceArt = new ServicePieceArt();
    @FXML
    private VBox stat_parent;
    @FXML
    private PieChart pieChart;
    @FXML
    private StackPane cardPieces;
    @FXML
    private StackPane cardGaleries;
    @FXML
    private StackPane cardGaleriesActives;
    @FXML
    private Button btnRetour;
    private Timeline timeline;


    @FXML
    public void initialize() {
        try {
            int totalGaleries = serviceGalerie.afficher().size();
            int totalPieces = servicePieceArt.afficher().size();
            int galeriesActives = calculerGaleriesActives();

            // Affichage sur les cartes
            nombreGaleries.setText(totalGaleries + " Galeries");
            nombrePieces.setText(totalPieces + " Pièces");
            nombreGaleriesActives.setText(galeriesActives + " Actives");


            // Remplir le graphique
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Statistiques");

            XYChart.Data<String, Number> dataGaleries = new XYChart.Data<>("Galeries", 0);
            XYChart.Data<String, Number> dataPieces = new XYChart.Data<>("Pièces d'Art", 0);
            XYChart.Data<String, Number> dataGaleriesActives = new XYChart.Data<>("Galeries Actives", 0);

            series.getData().addAll(dataGaleries, dataPieces, dataGaleriesActives);

            barChart.getData().clear();
            barChart.getData().add(series);

            timeline = new Timeline();

            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(1),
                            new KeyValue(dataGaleries.YValueProperty(), totalGaleries),
                            new KeyValue(dataPieces.YValueProperty(), totalPieces),
                            new KeyValue(dataGaleriesActives.YValueProperty(), galeriesActives)
                    )
            );
            timeline.play();

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Galeries", totalGaleries),
                    new PieChart.Data("Pièces d'Art", totalPieces),
                    new PieChart.Data("Galeries Actives", galeriesActives)
            );

            pieChart.setData(pieData);

            barChart.setLegendVisible(false);
            pieChart.setLegendVisible(true);
            pieChart.setLabelsVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int calculerGaleriesActives() throws SQLException {
        List<Piece_art> pieces = servicePieceArt.afficher();
        Set<Integer> galeriesActives = new HashSet<>();

        LocalDate troisJoursAvant = LocalDate.now().minusDays(3);

        for (Piece_art piece : pieces) {
            if (piece.getDate_crea() != null && !piece.getDate_crea().isBefore(troisJoursAvant)) {
                galeriesActives.add(piece.getGalerie().getId());
            }
        }

        return galeriesActives.size();
    }

    @FXML
    public void retour(ActionEvent actionEvent) {
        try {

            if (timeline != null) {
                timeline.stop();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ArtisteAdmin.fxml"));
            Parent root = loader.load();

            stat_parent.getChildren().clear();
            stat_parent.getChildren().add(root);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), root);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();

        } catch (IOException e) {
            // Gérer l'erreur en affichant un message d'erreur à l'utilisateur
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }
}

