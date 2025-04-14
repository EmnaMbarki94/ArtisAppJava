package tn.esprit.controller.Reponse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.services.ServiceReclamation;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListReController {

    @FXML
    private TableView<Reclamtion> TabelViewRec;

    @FXML
    private Button RepButton;


    @FXML
    private TableColumn<Reclamtion, String> Desc;

    @FXML
    private TableColumn<Reclamtion, String> Type;

    @FXML
    private TableColumn<Reclamtion, String> Date;

    @FXML
    private TableColumn<Reclamtion, Void> ActionRec;

    private final ObservableList<Reclamtion> list = FXCollections.observableArrayList();
    private final ServiceReclamation service = new ServiceReclamation();

    @FXML
    public void initialize() {
        loadReclamations();
        Desc.setCellValueFactory(new PropertyValueFactory<>("desc_r"));
        Date.setCellValueFactory(new PropertyValueFactory<>("date_r"));
        Type.setCellValueFactory(new PropertyValueFactory<>("type_r"));
        addButtonToTable();
        TabelViewRec.setItems(list);
    }
    @FXML
    private void onAfficherListeReponse() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Reponse/AfficherRep.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Liste des réponses");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReclamations() {
        list.clear(); // pour éviter les doublons
        try {
            ResultSet rs = service.selectAll1();
            while (rs.next()) {
                int userid=rs.getInt("user_id");
                Personne user= new Personne();
                user.setId(userid);
                Reclamtion r = new Reclamtion(
                        rs.getString("desc_r"),
                        rs.getString("date_r"),
                        rs.getString("type_r"),
                        user
                );
                r.setId(rs.getInt("id"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<Reclamtion, Void>, TableCell<Reclamtion, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button();

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Reclamtion reclamation = getTableView().getItems().get(getIndex());
                boolean aDejaRepondu = service.existeReponsePourReclamation(reclamation.getId());

                btn.setText(aDejaRepondu ? "Voir" : "Répondre");

                btn.setOnAction(event -> {
                    if (aDejaRepondu) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Reponse/RecRep.fxml"));
                            Parent root = loader.load();

                            // 🔍 On récupère la réclamation et la réponse
                            RecRepController controller = loader.getController();
                            String descReclamation = reclamation.getDesc_r();
                            String contenuReponse = service.getReponsePourReclamation(reclamation.getId());

                            controller.setReclamationEtReponse(descReclamation, contenuReponse);

                            Stage stage = new Stage();
                            stage.setTitle("Détail Réclamation et Réponse");
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reponse/AjouterRep.fxml"));
                            Parent root = loader.load();

                            AjouterRpController controller = loader.getController();
                            controller.setReclamationId(reclamation.getId());
                            controller.setOnReponseAjoute(() -> {
                                list.clear();
                                loadReclamations();
                                TabelViewRec.refresh();
                            });

                            Stage stage = new Stage();
                            stage.setTitle("Ajouter Réponse"); // tu peux personnaliser le titre
                            stage.setScene(new Scene(root));
                            stage.setOnHidden(eventClose -> {
                                list.clear();
                                loadReclamations();
                                TabelViewRec.refresh();
                            });
                            stage.show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });

                setGraphic(btn);
            }
        };

        ActionRec.setCellFactory(cellFactory);
    }
}
