package tn.esprit.controller.Reclamation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Reclamtion;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceReclamation;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class AfficherReController {
    private final Personne user = Session.getUser();

    @FXML
    private ListView<Reclamtion> listViewRec;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> typeFilterComboBox;

    private final ObservableList<Reclamtion> list = FXCollections.observableArrayList();
    private final FilteredList<Reclamtion> filteredData = new FilteredList<>(list);
    private final ServiceReclamation service = new ServiceReclamation();

    @FXML
    public void initialize() {
        //refreshUserSession();
        loadReclamations();
        setupListView();
        setupSearchAndFilter();
        populateTypeFilter();
    }

    //private void refreshUserSession() {
//       user = Session.getUser();
//        if (user == null) {
//            list.clear();
//            showAlert("Erreur", "Aucun utilisateur connecté", "Veuillez vous reconnecter.");
//        }
//    }

//    private void setupListView() {
//        listViewRec.setItems(filteredData);
//        listViewRec.setCellFactory(param -> new ListCell<Reclamtion>() {
//            private final Label description = new Label();
//            private final Label date = new Label();
//            private final Label type = new Label();
//            private final Button btnModifier = new Button("Modifier");
//            private final Button btnSupprimer = new Button("Supprimer");
//            private final Button btnVoir = new Button("Voir");
//            private final HBox buttonBox = new HBox(10, btnModifier, btnSupprimer);
//            private final VBox vbox = new VBox(description, date, type, buttonBox);
//
//            {
//                vbox.setSpacing(5);
//                vbox.setStyle("-fx-padding: 10; -fx-background-color: #f7f1ff; -fx-background-radius: 10;");
//
//                btnModifier.setOnAction(event -> {
//                    Reclamtion rec = getItem();
//                    if (rec != null) {
//                        openModifierRecWindow(rec);
//                    }
//                });
//
//                btnSupprimer.setOnAction(event -> {
//                    Reclamtion rec = getItem();
//                    if (rec != null) {
//                        showDeleteConfirmation(rec);
//                    }
//                });
//
//                btnVoir.setOnAction(event -> {
//                    Reclamtion rec = getItem();
//                    if (rec != null) {
//                        openRecRepWindow(rec);
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Reclamtion rec, boolean empty) {
//                super.updateItem(rec, empty);
//                if (empty || rec == null) {
//                    setGraphic(null);
//                } else {
//                    description.setText("📄 Description : " + rec.getDesc_r());
//                    date.setText("📅 Date : " + rec.getDate_r());
//                    type.setText("📂 Type : " + rec.getType_r());
//
//                    if (service.hasResponseForReclamation(rec.getId())) {
//                        buttonBox.getChildren().setAll(btnVoir);
//                    } else {
//                        buttonBox.getChildren().setAll(btnModifier, btnSupprimer);
//                    }
//
//                    setGraphic(vbox);
//                }
//            }
//        });
//    }
private void setupListView() {
    listViewRec.setItems(filteredData);
    listViewRec.setCellFactory(param -> new ListCell<Reclamtion>() {
        private final VBox container = new VBox();
        private final HBox headerBox = new HBox();
        private final HBox footerBox = new HBox();
        private final Label titleLabel = new Label();
        private final Label dateLabel = new Label();
        private final Label typeLabel = new Label();
        private final Label descLabel = new Label();
        private final Button btnModifier = new Button("Modifier");
        private final Button btnSupprimer = new Button("Supprimer");
        private final Button btnVoir = new Button("Voir la réponse");

        {
            // Configuration des styles
            container.getStyleClass().add("reclamation-cell");
            titleLabel.getStyleClass().add("reclamation-title");
            dateLabel.getStyleClass().add("reclamation-date");
            typeLabel.getStyleClass().add("reclamation-type");
            descLabel.getStyleClass().add("reclamation-desc");
            btnModifier.getStyleClass().add("action-button");
            btnModifier.getStyleClass().add("edit-button");
            btnSupprimer.getStyleClass().add("action-button");
            btnSupprimer.getStyleClass().add("delete-button");
            btnVoir.getStyleClass().add("action-button");
            btnVoir.getStyleClass().add("view-button");

            // Configuration de la mise en page
            headerBox.setSpacing(10);
            headerBox.getChildren().addAll(titleLabel, dateLabel);

            footerBox.setSpacing(10);
            footerBox.setAlignment(Pos.CENTER_RIGHT);

            VBox contentBox = new VBox(5, headerBox, typeLabel, descLabel, footerBox);
            container.getChildren().add(contentBox);
            VBox.setVgrow(contentBox, Priority.ALWAYS);
            container.setPadding(new Insets(15));
        }

        @Override
        protected void updateItem(Reclamtion rec, boolean empty) {
            super.updateItem(rec, empty);

            if (empty || rec == null) {
                setGraphic(null);
            } else {
                titleLabel.setText("Réclamation #" + rec.getId());
                dateLabel.setText("Date: " + rec.getDate_r());
                typeLabel.setText("Type: " + rec.getType_r());
                descLabel.setText(rec.getDesc_r());

                // Gestion des boutons en fonction de la présence de réponse
                footerBox.getChildren().clear();
                if (service.hasResponseForReclamation(rec.getId())) {
                    footerBox.getChildren().add(btnVoir);
                    btnVoir.setOnAction(event -> openRecRepWindow(rec));
                } else {
                    footerBox.getChildren().addAll(btnModifier, btnSupprimer);
                    btnModifier.setOnAction(event -> openModifierRecWindow(rec));
                    btnSupprimer.setOnAction(event -> showDeleteConfirmation(rec));
                }

                setGraphic(container);
            }
        }
    });
}


    private void loadReclamations() {
        list.clear();
        if (user != null) {
            try {
                System.out.println(user.getId());
                ResultSet rs = service.selectByUserId(user.getId());
                while (rs.next()) {
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
                showAlert("Erreur", "Chargement des réclamations", "Impossible de charger les réclamations.");
            }
        }
    }

    private void setupSearchAndFilter() {
        // 1. Écouteur pour le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilters();
        });

        // 2. Écouteur pour le ComboBox de filtrage par type
        typeFilterComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            updateFilters();
        });

        // Configuration initiale du filtre
        updateFilters();
    }

    private void updateFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(reclamation -> {
            // Filtre par texte de recherche
            boolean matchesSearch = searchText.isEmpty() ||
                    reclamation.getDesc_r().toLowerCase().contains(searchText) ||
                    reclamation.getType_r().toLowerCase().contains(searchText) ||
                    reclamation.getDate_r().toLowerCase().contains(searchText);

            // Filtre par type
            boolean matchesType = selectedType == null ||
                    selectedType.equals("Tous les types") ||
                    reclamation.getType_r().equals(selectedType);

            return matchesSearch && matchesType;
        });
    }

    private void populateTypeFilter() {
        Set<String> types = new HashSet<>();
        types.add("Tous les types"); // Option par défaut

        // Récupérer tous les types distincts des réclamations
        for (Reclamtion rec : list) {
            types.add(rec.getType_r());
        }

        typeFilterComboBox.setItems(FXCollections.observableArrayList(types));
        typeFilterComboBox.getSelectionModel().selectFirst(); // Sélectionne "Tous les types" par défaut
    }

    @FXML
    private void openChatbotWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/chatbot/ChatbotUI.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Assistant Virtuel");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le chatbot", "Une erreur est survenue lors de l'ouverture du chatbot.");
        }
    }


    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showDeleteConfirmation(Reclamtion rec) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cette réclamation ?");
        alert.setContentText("Cette action est irréversible.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (service.supprimer(rec)) {
                        list.remove(rec);
                        populateTypeFilter(); // Mettre à jour les filtres après suppression
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Échec de la suppression", "Une erreur est survenue lors de la suppression.");
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
                refreshList();
                populateTypeFilter(); // Mettre à jour les filtres après ajout
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre", "Une erreur est survenue lors de l'ouverture.");
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
                refreshList();
                populateTypeFilter(); // Mettre à jour les filtres après modification
            });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre", "Une erreur est survenue lors de l'ouverture.");
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
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre", "Une erreur est survenue lors de l'ouverture.");
        }
    }

    public void refreshList() {
        //refreshUserSession();
        loadReclamations();
        listViewRec.refresh();
    }
}