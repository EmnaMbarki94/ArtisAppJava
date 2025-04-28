package tn.esprit.controller.Reponse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import tn.esprit.services.ServiceReclamation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatsController {

    @FXML
    private PieChart pieChart;  // Le graphique circulaire pour afficher les statistiques

    private final ServiceReclamation service = new ServiceReclamation();  // Service pour récupérer les réclamations

    @FXML
    public void initialize() {
        // Récupérer les statistiques et les afficher dans le graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        loadStatistics(pieChartData);
        pieChart.setData(pieChartData);
    }

    private void loadStatistics(ObservableList<PieChart.Data> pieChartData) {
        try {
            // Récupérer les réclamations depuis la base de données
            ResultSet rs = service.selectAll1();
            Map<String, Integer> typeCounts = new HashMap<>();  // Carte pour compter les réclamations par type
            int totalReclamations = 0;  // Compteur du nombre total de réclamations

            // Parcourir les résultats et compter le nombre de réclamations par type
            while (rs.next()) {
                String type = rs.getString("type_r");
                typeCounts.put(type, typeCounts.getOrDefault(type, 0) + 1);
                totalReclamations++;  // Incrémenter le nombre total de réclamations
            }

            // Ajouter les données dans le graphique avec les pourcentages
            for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
                String type = entry.getKey();
                int count = entry.getValue();
                double percentage = (count / (double) totalReclamations) * 100;  // Calculer le pourcentage

                // Créer une étiquette qui inclut le type et le pourcentage
                String label = type + " (" + String.format("%.2f", percentage) + "%)";
                pieChartData.add(new PieChart.Data(label, count));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
