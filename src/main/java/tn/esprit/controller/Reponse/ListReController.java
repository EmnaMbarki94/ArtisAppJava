package tn.esprit.controller.Reponse;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private TextField searchField;

    @FXML
    private ComboBox<String> typeFilterComboBox;

    @FXML
    private Button RepButton;
    @FXML
    private TableColumn<Reclamtion, String> EmailUser;
//    @FXML
//    private TableColumn<Reclamtion, String> NameUser;
//    @FXML
//    private TableColumn<Reclamtion, String> LastNameUser;



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
        EmailUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getEmail()));
//        NameUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getFirst_Name()));
//        LastNameUser.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getLast_Name()));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterData());

        // Configuration de la ComboBox pour filtrer par type
        typeFilterComboBox.setItems(FXCollections.observableArrayList("Technique", "Service", "Produit","autre","Tous les types")); // Remplacer par les types réels
        typeFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterData());


    }
    private void filterData() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedType = typeFilterComboBox.getValue();

        // Filtrer les réclamations selon le texte et le type sélectionné
        ObservableList<Reclamtion> filteredList = FXCollections.observableArrayList();

        for (Reclamtion reclamation : list) {
            boolean matchesSearch = reclamation.getDesc_r().toLowerCase().contains(searchText);
            boolean matchesType = selectedType == null || selectedType.equals("Tous les types") || reclamation.getType_r().equals(selectedType);

            if (matchesSearch && matchesType) {
                filteredList.add(reclamation);
            }
        }

        TabelViewRec.setItems(filteredList);
    }


    @FXML
    private void onAfficherStatistiques() {
        try {
            // Charger le fichier FXML pour l'écran des statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Reponse/stats.fxml"));
            Parent root = loader.load();

            // Créer et afficher une nouvelle fenêtre pour les statistiques
            Stage stage = new Stage();
            stage.setTitle("Statistiques des Réclamations");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                String email = rs.getString("email"); // 🆕 On récupère l'email
//                String first_Name = rs.getString("first_Name");
//                String last_Name = rs.getString("last_Name");

                Personne user= new Personne();
                user.setId(userid);
                user.setEmail(email); // 🆕 On injecte l'email
//                user.setLast_Name(last_Name);
//                user.setFirst_Name(first_Name);

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
