package tn.esprit.controller.Reclamation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReclamation;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AfficherReController {
    private final Personne user = Session.getUser();

    @FXML
    private ListView<Reclamtion> listViewRec;

    private final ObservableList<Reclamtion> list = FXCollections.observableArrayList();
    private final ServiceReclamation service = new ServiceReclamation();

    @FXML
    public void initialize() {
        loadReclamations();
        listViewRec.setItems(list);
        listViewRec.setCellFactory(param -> new ListCell<>() {
            private final Label description = new Label();
            private final Label date = new Label();
            private final Label type = new Label();
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final Button btnVoir = new Button("Voir");
            private final HBox buttonBox = new HBox(10, btnModifier, btnSupprimer);
            private final VBox vbox = new VBox(description, date, type, buttonBox);

            {
                vbox.setSpacing(5);
                vbox.setStyle("-fx-padding: 10; -fx-background-color: #f7f1ff; -fx-background-radius: 10;");

                btnModifier.setOnAction(event -> {
                    Reclamtion rec = getItem();
                    openModifierRecWindow(rec);
                });

                btnSupprimer.setOnAction(event -> {
                    Reclamtion rec = getItem();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
                    alert.setContentText("Cette action est irréversible.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                service.supprimer(rec);
                                list.remove(rec);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                });

                btnVoir.setOnAction(event -> {
                    Reclamtion rec = getItem();
                    openRecRepWindow(rec);
                });
            }

            @Override
            protected void updateItem(Reclamtion rec, boolean empty) {
                super.updateItem(rec, empty);
                if (empty || rec == null) {
                    setGraphic(null);
                } else {
                    description.setText("📄 Description : " + rec.getDesc_r());
                    date.setText("📅 Date : " + rec.getDate_r());
                    type.setText("📂 Type : " + rec.getType_r());

                    if (service.hasResponseForReclamation(rec.getId())) {
                        buttonBox.getChildren().setAll(btnVoir);
                    } else {
                        buttonBox.getChildren().setAll(btnModifier, btnSupprimer);
                    }

                    setGraphic(vbox);
                }
            }
        });
    }

    @FXML
    private void openAjouterRecWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/AjouterRec.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Réclamation");
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> {
                list.clear();
                loadReclamations();
                listViewRec.refresh();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadReclamations() {
        try {
            ResultSet rs = service.selectAll1();
            while (rs.next()) {
                int usr = rs.getInt("user_id");
                if (user.getId() == usr) {
                    Personne u = new Personne();
                    u.setId(usr);
                    Reclamtion r = new Reclamtion(
                            rs.getString("desc_r"),
                            rs.getString("date_r"),
                            rs.getString("type_r"),
                            u
                    );
                    r.setId(rs.getInt("id"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openModifierRecWindow(Reclamtion rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/ModifierRec.fxml"));
            Parent root = loader.load();

            ModifierReController controller = loader.getController();
            controller.setReclamationData(rec);

            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));

            stage.setOnHidden(e -> {
                list.clear();
                loadReclamations();
                listViewRec.refresh();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openRecRepWindow(Reclamtion rec) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/RecRep.fxml"));
            Parent root = loader.load();

            RecRepController controller = loader.getController();
            String reponse = service.getReponseForReclamation(rec.getId());
            controller.setReclamationData(rec, reponse);

            Stage stage = new Stage();
            stage.setTitle("Voir Réponse");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
