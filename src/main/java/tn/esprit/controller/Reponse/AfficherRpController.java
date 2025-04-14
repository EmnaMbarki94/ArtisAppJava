package tn.esprit.controller.Reponse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Reponse;
import tn.esprit.services.ServiceReponse;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AfficherRpController {

    @FXML
    private TableColumn<Reponse, Void> ActionRep;

    @FXML
    private Button AjouterBtRep;

    @FXML
    private TableColumn<Reponse, LocalDate> DateRep;

    @FXML
    private TableColumn<Reponse, String> DescRep;

    @FXML
    private TableView<Reponse> TabelViewRec;


    private final ServiceReponse serviceReponse = new ServiceReponse();

    @FXML
    public void initialize() {
        DescRep.setCellValueFactory(new PropertyValueFactory<>("descr_rep"));
        DateRep.setCellValueFactory(new PropertyValueFactory<>("date_rep"));

        // Charger les données
        ObservableList<Reponse> reponseList = FXCollections.observableArrayList();

        try {
            ResultSet rs = serviceReponse.selectAll1();
            while (rs.next()) {
                Reponse r = new Reponse(
                        rs.getInt("id"),
                        rs.getString("desc_rep"),
                        rs.getString("date_rep")
                );

                reponseList.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TabelViewRec.setItems(reponseList);
        addButtonToTable();
    }

    private void addButtonToTable() {
        ActionRep.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox hBox = new HBox(10, btnModifier, btnSupprimer);

            {
                btnModifier.setOnAction(event -> {
                    Reponse reponse = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Reponse/ModifierRep.fxml"));
                        Parent root = loader.load();

                        // Passer l'objet Reponse au contrôleur
                        ModifierRpController controller = loader.getController();
                        controller.initData(reponse); // ⚠️ On passe l'objet à modifier

                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.setTitle("Modifier Réponse");
                        stage.setScene(new Scene(root));

                        // Fermer la fenêtre et rafraîchir la table après fermeture
                        stage.setOnHidden(e -> refreshTable());  // Rafraîchit la table lorsque la fenêtre se ferme
                        stage.showAndWait();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


                btnSupprimer.setOnAction(event -> {
                    Reponse reponse = getTableView().getItems().get(getIndex());
                    System.out.println("Supprimer : " + reponse);

                    // Appel à la suppression dans le service
                    try {
                        serviceReponse.supprimer(reponse);
                        refreshTable();  // Rafraîchit la table après suppression
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hBox);
                }
            }
        });
    }

//    private void refreshTable() {
//        ObservableList<Reponse> reponseList = FXCollections.observableArrayList();
//
//        try {
//            ResultSet rs = serviceReponse.selectAll();
//            while (rs.next()) {
//                Reponse r = new Reponse();
//                r.setIdreponse(rs.getInt("id"));
//                r.setReclamation_id(rs.getInt("reclamation_id")); // ✅ IMPORTANT
//                r.setDescr_rep(rs.getString("desc_rep"));
//                r.setDate_rep(rs.getString("date_rep"));
//
//                reponseList.add(r);
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        TabelViewRec.setItems(reponseList);
//        addButtonToTable(); // ⚠️ Nécessaire si tu veux garder les boutons à jour
//    }
private void refreshTable() {
    ObservableList<Reponse> reponseList = FXCollections.observableArrayList();

    try {
        ResultSet rs = serviceReponse.selectAll1();
        while (rs.next()) {
            Reponse r = new Reponse();
            r.setIdreponse(rs.getInt("id"));
            r.setReclamation_id(rs.getInt("reclamation_id"));
            r.setDescr_rep(rs.getString("desc_rep"));
            r.setDate_rep(rs.getString("date_rep"));

            reponseList.add(r);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    TabelViewRec.setItems(reponseList);
    addButtonToTable(); // Reajouter les boutons pour chaque ligne de la table
}





}
