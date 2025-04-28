package tn.esprit.controller.reservation;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import tn.esprit.services.ServiceReservation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StatsController {

    // Couleurs du thème mauve
    private static final String MAIN_PURPLE = "#7F56D9";  // Mauve plus clair
    private static final String DARK_PURPLE = "#483D8B";
    private static final String LIGHT_PURPLE = "#9B7CC3";  // Mauve plus clair
    private static final String ACCENT_COLOR = "#8A4FAD";  // Pas de jaune

    @FXML
    private AnchorPane contenuPane;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ComboBox<String> comboStatType;

    @FXML
    private VBox topEventBox;

    @FXML
    private HBox headerBox;

    private ServiceReservation serviceReservation = new ServiceReservation();

    @FXML
    public void initialize() {
        configureChartAppearance();
        initComboBox();
        comboStatType.setOnAction(event -> loadSelectedStats());
        loadReservationStats();
        displayTopReservedEvent();
    }



    private void displayTopReservedEvent() {
        List<Map<String, Object>> stats = serviceReservation.getReservationStatsByEvent();

        if (!stats.isEmpty()) {
            Map<String, Object> topEvent = stats.stream()
                    .max((e1, e2) -> Long.compare((long)e1.get("reservation_count"), (long)e2.get("reservation_count")))
                    .orElse(stats.get(0));

            String eventName = (String) topEvent.get("event_name");
            long reservationCount = (long) topEvent.get("reservation_count");
            topEventBox.setTranslateX(400);  // Déplacer horizontalement

            // Conteneur principal
            VBox container = new VBox(10);
            container.setAlignment(Pos.BOTTOM_CENTER);
            container.setStyle("-fx-background-color: " + DARK_PURPLE + "; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 20; " +
                    "-fx-border-color: " + ACCENT_COLOR + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 15;");



            // Légende réservations
            Label reservationsLabel = new Label("Nombre total de réservations:");
            reservationsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            reservationsLabel.setTextFill(Color.WHITE);

            Label countLabel = new Label(String.valueOf(reservationCount));
            countLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            countLabel.setTextFill(Color.web(ACCENT_COLOR));

            // Titre événement star
            Label starTitle = new Label("ÉVÉNEMENT STAR");
            starTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            starTitle.setTextFill(Color.web(ACCENT_COLOR));

            // Nom de l'événement
            Label eventLabel = new Label(eventName);
            eventLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            eventLabel.setTextFill(Color.WHITE);
            eventLabel.setWrapText(true);
            eventLabel.setMaxWidth(200);
            eventLabel.setAlignment(Pos.CENTER);

            // Icône
            Label trophyIcon = new Label("🏆");
            trophyIcon.setFont(Font.font(24));

            container.getChildren().addAll(
                    new Separator(),
                    reservationsLabel,
                    countLabel,
                    new Separator(),
                    starTitle,
                    trophyIcon,
                    eventLabel
            );

            // Définir la position X de l'élément conteneur à 500px
            container.setLayoutX(1200);  // Positionner horizontalement à 500px

            topEventBox.getChildren().clear();
            topEventBox.getChildren().add(container);

            // Alignement vertical centré dans topEventBox
            topEventBox.setAlignment(Pos.CENTER);
        }
    }


    private void configureChartAppearance() {
        // ... (configuration existante)

        // Ajout de légendes aux axes
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setLabel("Événements");
        xAxis.setStyle("-fx-label-fill: " + ACCENT_COLOR + ";");

        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Nombre de réservations");
        yAxis.setStyle("-fx-label-fill: " + ACCENT_COLOR + ";");
    }

    private String formatEventName(String originalName) {
        if (originalName == null) {
            return "";
        }
        if (originalName.length() > 15) {
            return originalName.substring(0, 12) + "...";
        }
        return originalName;
    }

    private void loadReservationStats() {
        barChart.getData().clear();
        barChart.setTitle("RÉSERVATIONS PAR ÉVÉNEMENT");
        barChart.setStyle("-fx-title-fill: " + ACCENT_COLOR + ";");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réservations");

        List<Map<String, Object>> stats = serviceReservation.getReservationStatsByEvent();
        stats.sort((e1, e2) -> Long.compare((long)e2.get("reservation_count"), (long)e1.get("reservation_count")));

        for (Map<String, Object> stat : stats) {
            String eventName = formatEventName((String) stat.get("event_name"));
            long reservationCount = (long) stat.get("reservation_count");

            XYChart.Data<String, Number> data = new XYChart.Data<>(eventName, reservationCount);
            series.getData().add(data);
            setupBarTooltip(data, "Événement: " + stat.get("event_name") + "\nRéservations: " + reservationCount);
        }

        barChart.getData().add(series);

        // Apply style after the series has been added to the chart
        Platform.runLater(() -> {
            if (series.getNode() != null) {
                series.getNode().setStyle("-fx-legend-visible: true;");
            }
        });

        applyAnimation();
        colorBars(series, LIGHT_PURPLE);  // Mise à jour de la couleur du bar
    }

    private void initComboBox() {
        comboStatType.setItems(FXCollections.observableArrayList(
                "Réservations par événement",
                "Tickets VIP vendus",
                "Revenus générés"
        ));
        comboStatType.setValue("Réservations par événement");
        comboStatType.setStyle("-fx-font: 14px 'Segoe UI'; " +
                "-fx-background-color: white; " +
                "-fx-text-fill: " + DARK_PURPLE + "; " +
                "-fx-pref-width: 250;");
    }

    private void loadSelectedStats() {
        String selected = comboStatType.getValue();
        switch (selected) {
            case "Réservations par événement":
                loadReservationStats();
                break;
            case "Tickets VIP vendus":
                loadTicketsVIPStats();
                break;

        }
    }

    private void loadTicketsVIPStats() {
        barChart.getData().clear();
        barChart.setTitle("Statistiques des Tickets VIP");
        barChart.setStyle("-fx-title-fill: " + LIGHT_PURPLE + ";");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tickets VIP vendus");

        List<Map<String, Object>> stats = serviceReservation.getVIPTicketsStatsByEvent();

        for (Map<String, Object> stat : stats) {
            String eventName = (String) stat.get("event_name");
            long vipCount = (long) stat.get("vip_count");

            XYChart.Data<String, Number> data = new XYChart.Data<>(eventName, vipCount);
            series.getData().add(data);

            setupBarTooltip(data, "VIP: " + vipCount);
        }

        barChart.getData().add(series);
        applyAnimation();
        colorBars(series, MAIN_PURPLE);  // Couleur du bar modifiée
    }

    /*private void loadRevenueStats() {
        barChart.getData().clear();
        barChart.setTitle("Revenus générés par événement");
        barChart.setStyle("-fx-title-fill: " + LIGHT_PURPLE + ";");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenus (€)");

        List<Map<String, Object>> stats = serviceReservation.getRevenueStatsByEvent();

        for (Map<String, Object> stat : stats) {
            String eventName = (String) stat.get("event_name");
            double revenue = (double) stat.get("revenue");

            XYChart.Data<String, Number> data = new XYChart.Data<>(eventName, revenue);
            series.getData().add(data);

            setupBarTooltip(data, String.format("Revenus: %.2f DT", revenue));
        }

        barChart.getData().add(series);
        applyAnimation();
        colorBars(series, "#9B7CC3");  // Couleur des bar modifiée
    }*/

    private void setupBarTooltip(XYChart.Data<String, Number> data, String text) {
        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                Tooltip tooltip = new Tooltip(text);
                tooltip.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI'; -fx-background-color: " + DARK_PURPLE + ";");
                Tooltip.install(newNode, tooltip);

                newNode.setOnMouseEntered(event -> {
                    newNode.setStyle("-fx-effect: dropshadow(three-pass-box, " + ACCENT_COLOR + ", 15, 0.5, 0, 0);");
                    newNode.setScaleX(1.05);
                    newNode.setScaleY(1.05);
                });

                newNode.setOnMouseExited(event -> {
                    newNode.setStyle("-fx-bar-fill: " + getCurrentColor() + ";");
                    newNode.setScaleX(1.0);
                    newNode.setScaleY(1.0);
                });
            }
        });
    }

    private void colorBars(XYChart.Series<String, Number> series, String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
        }
    }

    private String getCurrentColor() {
        String selected = comboStatType.getValue();
        switch (selected) {
            case "Réservations par événement": return LIGHT_PURPLE;
            case "Tickets VIP vendus": return MAIN_PURPLE;
            case "Revenus générés": return "#9B7CC3";
            default: return LIGHT_PURPLE;
        }
    }

    private void applyAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(800), barChart);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/EvenementAdmin.fxml"));
            AnchorPane retourView = loader.load();
            contenuPane.getChildren().setAll(retourView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
