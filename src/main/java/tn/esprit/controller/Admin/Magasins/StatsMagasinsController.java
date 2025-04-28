package tn.esprit.controller.Admin.Magasins;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.ArticleStatDTO;
import tn.esprit.entities.MagasinStatDTO;
import tn.esprit.services.ServiceLigneCommande;
import tn.esprit.services.ServiceMagasin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.util.StringConverter;
import javafx.scene.text.Font;
import javafx.scene.control.Tooltip;

public class StatsMagasinsController implements Initializable {

    @FXML private BarChart<String, Number> articlesChart;
    @FXML private PieChart magasinsChart;
    @FXML private Button btnRetourMagasins;


    private final ServiceLigneCommande serviceLigneCommande = new ServiceLigneCommande();
    private final ServiceMagasin serviceMagasin = new ServiceMagasin();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurerCharts();
        initialiserDonnees();
    }

    public void initialiserDonnees() {
        try {
            chargerArticlesPlusVendus();
            chargerVentesParMagasin();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur lors du chargement des données: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Une erreur inattendue est survenue");
        }
    }

    private void chargerArticlesPlusVendus() throws SQLException {
        List<ArticleStatDTO> topArticles = serviceLigneCommande.getTopArticlesVendusAvecPrix(5);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Articles les plus vendus");

        // Palette mauve pour les barres
        String[] mauveGradient = {"#E1BEE7", "#BA68C8", "#9C27B0", "#7B1FA2", "#4A148C"};

        for (int i = 0; i < topArticles.size(); i++) {
            ArticleStatDTO article = topArticles.get(i);
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    article.getNomArticle(),
                    article.getQuantiteVendue()
            );
            series.getData().add(data);

            // Appliquer le style après que le nœud est créé
            int finalI = i;
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    // Style de la barre
                    newNode.setStyle(String.format(
                            "-fx-bar-fill: %s; " +
                                    "-fx-background-radius: 3 3 0 0; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);",
                            mauveGradient[finalI % mauveGradient.length]
                    ));

                    // Tooltip d'information
                    Tooltip tooltip = new Tooltip(
                            String.format("%s\nQuantité: %d\nPrix unitaire: %.2f TND\nTotal: %.2f TND",
                                    article.getNomArticle(),
                                    article.getQuantiteVendue(),
                                    article.getPrixArticle(),
                                    article.getQuantiteVendue() * article.getPrixArticle())
                    );
                    tooltip.setStyle("-fx-font-size: 12px; -fx-font-family: 'Segoe UI';");
                    Tooltip.install(newNode, tooltip);
                }
            });
        }

        articlesChart.getData().clear();
        articlesChart.getData().add(series);

        // Configuration supplémentaire après ajout des données
        Platform.runLater(() -> {
            // Ajustement automatique de la largeur des barres
            double barWidth = 0.3; // 30% de l'espace disponible
            for (XYChart.Series<String, Number> s : articlesChart.getData()) {
                for (XYChart.Data<String, Number> d : s.getData()) {
                    if (d.getNode() != null) {
                        d.getNode().setStyle(d.getNode().getStyle() +
                                String.format("-fx-pref-width: %.2f;", barWidth));
                    }
                }
            }
        });
    }
    private void chargerVentesParMagasin() throws SQLException {
        List<MagasinStatDTO> statsMagasins = serviceMagasin.getStatsVentesParMagasin();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (MagasinStatDTO magasin : statsMagasins) {
            pieData.add(new PieChart.Data(
                    magasin.getNomMagasin(),
                    magasin.getChiffreAffaire()
            ));
        }


        magasinsChart.getData().clear();
        magasinsChart.setData(pieData);
    }

    private void configurerCharts() {
        // Palette mauve professionnelle (du clair au foncé)
        String[] mauvePalette = {
                "#F3E5F5", "#E1BEE7", "#CE93D8", "#BA68C8",
                "#AB47BC", "#9C27B0", "#8E24AA", "#7B1FA2", "#6A1B9A"
        };

        // 1. Configuration du BarChart
        articlesChart.setLegendVisible(false);
        articlesChart.setAnimated(false);
        articlesChart.setCategoryGap(20);

        // Appliquer la couleur mauve aux barres
        articlesChart.setStyle("-fx-bar-fill: #9C27B0;");

        // 2. Configuration du PieChart avec couleurs forcées
        magasinsChart.setLabelsVisible(true);
        magasinsChart.setLegendVisible(false);

        // Appliquer les couleurs manuellement
        ObservableList<PieChart.Data> pieData = magasinsChart.getData();
        for (int i = 0; i < pieData.size(); i++) {
            PieChart.Data data = pieData.get(i);
            String color = mauvePalette[i % mauvePalette.length];

            // FORCER la couleur via CSS
            data.getNode().setStyle(
                    "-fx-pie-color: " + color + "; " +
                            "-fx-border-color: white; " +
                            "-fx-border-width: 1px;"
            );

            // Tooltip amélioré
            Tooltip.install(data.getNode(), new Tooltip(
                    String.format("%s: %s TND",
                            data.getName(),
                            new DecimalFormat("#,##0.00").format(data.getPieValue()))
            ));
        }
        // Dans chargerVentesParMagasin(), après magasinsChart.setData()
        Platform.runLater(() -> {
            for (int i = 0; i < magasinsChart.getData().size(); i++) {
                PieChart.Data data = magasinsChart.getData().get(i);
                Node node = data.getNode();
                node.setStyle("-fx-pie-color: " + mauvePalette[i % mauvePalette.length] + ";");
            }
        });

        // 3. Style global CSS
        magasinsChart.lookupAll(".chart-pie-label").forEach(node ->
                node.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-fill: #4A148C;")
        );
    }
    private double getTotalVentes() {
        return magasinsChart.getData().stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();
    }
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void retournerVersListeMagasins() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml"));
            AnchorPane pageMagasins = loader.load();

            AnchorPane root = (AnchorPane) btnRetourMagasins.getScene().lookup("#contenuPane");
            root.getChildren().setAll(pageMagasins);

//            System.out.println("Retour à la liste des magasins !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}