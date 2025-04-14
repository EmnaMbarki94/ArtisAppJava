package tn.esprit.controller.Reclamation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.esprit.controller.Reclamation.RecRepController;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReclamation;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AfficherReController {
    private Personne user= Session.getUser();

    @FXML
    private TableColumn<Reclamtion, Void> ActionRec;

    @FXML
    private Button AjouterBt;

    @FXML
    private TableColumn<Reclamtion, String> Date;

    @FXML
    private TableColumn<Reclamtion, String> Desc;

    @FXML
    private TableView<Reclamtion> TabelViewRec;

    @FXML
    private TableColumn<Reclamtion, String> Type;

    private final ObservableList<Reclamtion> list = FXCollections.observableArrayList();
    private final ServiceReclamation service = new ServiceReclamation();

    @FXML
    public void initialize() {
        loadReclamations();
        Desc.setCellValueFactory(new PropertyValueFactory<>("desc_r"));
        Date.setCellValueFactory(new PropertyValueFactory<>("date_r"));
        Type.setCellValueFactory(new PropertyValueFactory<>("type_r"));
        addActionButtons();
        TabelViewRec.setItems(list);
    }
@FXML
private void openAjouterRecWindow() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/AjouterRec.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Ajouter Réclamation");
        stage.setScene(new Scene(root));

        // Quand la fenêtre se ferme, recharge la table
        stage.setOnHidden(e -> {
            list.clear(); // vider l'ancienne liste
            loadReclamations(); // recharger depuis la BD
            TabelViewRec.refresh(); // rafraîchir la vue
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
                int usr=rs.getInt("user_id");

                if(user.getId()==usr){
                    Personne user=new Personne();
                    user.setId(usr);
                    Reclamtion r = new Reclamtion(
                            rs.getString("desc_r"),
                            rs.getString("date_r"),
                            rs.getString("type_r"),
                            user
                );
                r.setId(rs.getInt("id"));
                list.add(r);}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addActionButtons() {
        ActionRec.setCellFactory(param -> new TableCell<Reclamtion, Void>() {
            private final Button btnAction = new Button();
            private final Button btnDelete = new Button("Supprimer");
            private final Button btnEdit = new Button("Modifier");
            private final HBox box = new HBox(10, btnEdit, btnDelete);

            {
                // Action pour le bouton "Voir"
                btnAction.setOnAction(event -> {
                    Reclamtion rec = getTableView().getItems().get(getIndex());
                    if (hasResponse(rec)) {
                        openRecRepWindow(rec); // Si la réclamation a une réponse, on ouvre RecRep
                    } else {
                        openModifierRecWindow(rec); // Sinon, ouvrir la fenêtre de modification
                    }
                });

                // Action pour le bouton "Modifier"
                btnEdit.setOnAction(event -> {
                    Reclamtion rec = getTableView().getItems().get(getIndex());
                    openModifierRecWindow(rec);
                });

                // Action pour le bouton "Supprimer"
                btnDelete.setOnAction(event -> {
                    Reclamtion rec = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
                    alert.setContentText("Cette action est irréversible.");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                service.supprimer(rec);
                                list.remove(rec);
                                System.out.println("🗑️ Réclamation supprimée !");
                            } catch (SQLException e) {
                                e.printStackTrace();
                                System.out.println("❌ Erreur lors de la suppression !");
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    Reclamtion rec = getTableView().getItems().get(getIndex());
                    if (hasResponse(rec)) {
                        // Si une réponse est présente, on remplace les boutons Modifier/Supprimer par un bouton "Voir"
                        btnEdit.setVisible(false);
                        btnDelete.setVisible(false);
                        btnAction.setText("Voir");
                        box.getChildren().setAll(btnAction);
                    } else {
                        // Sinon, on affiche les boutons Modifier et Supprimer
                        btnEdit.setVisible(true);
                        btnDelete.setVisible(true);
                        box.getChildren().setAll(btnEdit, btnDelete);
                    }
                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }

            // Méthode pour vérifier si la réclamation a une réponse
            private boolean hasResponse(Reclamtion rec) {
                // Ajoutez ici la logique pour vérifier si la réclamation a une réponse
                return service.hasResponseForReclamation(rec.getId()); // Cette méthode à ajouter dans votre service
            }
        });
    }

    private void openRecRepWindow(Reclamtion rec) {
        try {
            // Charger le fichier FXML avec le bon contrôleur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/RecRep.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de RecRep
            RecRepController controller = loader.getController();

            // Récupérer la réponse pour la réclamation
            String reponse = service.getReponseForReclamation(rec.getId());

            // Passer la réclamation et la réponse au contrôleur
            controller.setReclamationData(rec, reponse);

            // Ouvrir la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Voir Réponse");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    //    private void openModifierRecWindow(Reclamtion reclamation) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/ModifierRec.fxml"));
//            Parent root = loader.load();
//
//            // Accéder au controller de ModifierRec.fxml
//            ModifierReController controller = loader.getController();
//
//            // Envoyer la réclamation sélectionnée
//            controller.setReclamationData(reclamation);
//
//            // Afficher la nouvelle fenêtre
//            Stage stage = new Stage();
//            stage.setTitle("Modifier Réclamation");
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//private void openModifierRecWindow(Reclamtion reclamation) {
//    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/ModifierRec.fxml"));
//        Parent root = loader.load();
//
//        // Accéder au controller de ModifierRec.fxml
//        ModifierReController controller = loader.getController();
//
//        // Envoyer la réclamation sélectionnée
//        controller.setReclamationData(reclamation);
//
//        // Afficher la nouvelle fenêtre
//        Stage stage = new Stage();
//        stage.setTitle("Modifier Réclamation");
//        stage.setScene(new Scene(root));
//
//        // Quand la fenêtre se ferme, rafraîchir la table
//        stage.setOnHidden(e -> {
//            list.clear(); // vider l'ancienne liste
//            loadReclamations(); // recharger depuis la BD
//            TabelViewRec.refresh(); // rafraîchir la vue
//        });
//
//        stage.show();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
    private void openModifierRecWindow(Reclamtion reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Reclamation/ModifierRec.fxml"));
            Parent root = loader.load();

            // Accéder au controller de ModifierRec.fxml
            ModifierReController controller = loader.getController();

            // Envoyer la réclamation sélectionnée
            controller.setReclamationData(reclamation);

            // Afficher la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Modifier Réclamation");
            stage.setScene(new Scene(root));

            // Lorsqu'on ferme, on recharge la liste
            stage.setOnHidden(e -> {
                list.clear();
                loadReclamations();
                TabelViewRec.refresh();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
