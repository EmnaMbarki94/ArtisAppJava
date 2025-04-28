package tn.esprit.controller.Admin;



import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import tn.esprit.entities.Personne;
import tn.esprit.gui.gui;
import tn.esprit.services.ServicePersonne;
import tn.esprit.services.ControleDeSaisie;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
public class AdminDashboardController implements Initializable {

    @FXML
    public TextField name_admin_fld;
    @FXML
    public TextField lastName_admin_fld;
    @FXML
    public TextField email_admin_fldd;
    @FXML
    public TextField password_admin_fld;
    @FXML
    public TextField cin_admin_fld;
    @FXML
    public TextField phoneNumber_admin_fld;
    @FXML
    public TextField adress_admin_fld;

    @FXML
    public TextField tableView_admin_fld;
    @FXML
    public Button delete_admin_btn;
   // @FXML
    //public Button search_admin_btn;
    @FXML
    public Button order_admin_btn;
    @FXML
    public Button orderPoint_admin_btn;


    public SplitMenuButton splitMenu_admin_fld;

    @FXML
    public TableView<Personne> user_admin_tableView;

    private final ServicePersonne userService = new ServicePersonne();
    public Button register_admin_btn;
    public Button refresh_admin_btn;
    public Text email_error_txt;
    public Text name_error_txt;
    public Text lastName_error_txt;
    public Text password_error_txt;
    public Text phoneNumber_error_txt;
    public Text Adress_error_txt;
    public Text cin_error_txt;
    public Button tools_btn;

    private StringBuilder realPassword = new StringBuilder();

    ControleDeSaisie controleDeSaisie = new ControleDeSaisie();
    Personne user = new Personne();




    private List<Personne> allUsers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        loadTableData();


        delete_admin_btn.setOnAction(event -> {
            try {
                handleDeleteAction();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        //search_admin_btn.setOnAction(event -> handleSearchAction());
       /* register_admin_btn.setOnAction(event-> {
            try {
                onSignIn();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });*/
        refresh_admin_btn.setOnAction(event -> loadTableData());
        /*for (MenuItem item : splitMenu_admin_fld.getItems()) {
            item.setOnAction(event -> {
                splitMenu_admin_fld.setText(item.getText()); // Affiche le choix dans le menu
            });
        }*/
        // Masquer le mot de passe avec des "*"
       /* password_admin_fld.setOnKeyTyped(event -> {
            event.consume(); // Bloquer l'affichage du texte tapé

            String typedChar = event.getCharacter();
            if ("\b".equals(typedChar) && realPassword.length() > 0) {
                realPassword.deleteCharAt(realPassword.length() - 1);
            } else {
                realPassword.append(typedChar);
            }

            // Afficher des "*"
            password_admin_fld.setText("*".repeat(realPassword.length()));
        });*/

    //*****************
        tableView_admin_fld.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers(newValue);
        });
        //************************
    }


    private void initTable() {
        TableColumn<Personne, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Personne, String> rolesColumn = new TableColumn<>("Roles");
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));

        TableColumn<Personne, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("last_Name"));

        TableColumn<Personne, String> prenomColumn = new TableColumn<>("Prenom");
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("first_Name"));

        TableColumn<Personne, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<Personne, String> numTelColumn = new TableColumn<>("Phone Number");
        numTelColumn.setCellValueFactory(new PropertyValueFactory<>("num_tel"));

        TableColumn<Personne, String> cinColumn = new TableColumn<>("CIN");
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));

        TableColumn<Personne, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Personne, String> verifiedColumn = new TableColumn<>("Verification Status");
        verifiedColumn.setCellValueFactory(new PropertyValueFactory<>("is_verified"));

        TableColumn<Personne, String> specialiteColumn = new TableColumn<>("Specialite");
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));

        TableColumn<Personne, String> pointColumn = new TableColumn<>("point");
        pointColumn.setCellValueFactory(new PropertyValueFactory<>("point"));

        user_admin_tableView.getColumns().addAll(emailColumn, rolesColumn, nomColumn, prenomColumn,
                passwordColumn, numTelColumn, cinColumn, addressColumn, verifiedColumn, specialiteColumn, pointColumn);
    }

    private void loadTableData() {

        List<Personne> users = userService.selectAll();

        if(users != null && !users.isEmpty()) {
            ObservableList<Personne> userList = FXCollections.observableArrayList(users);
            user_admin_tableView.setItems(userList);
            System.out.println("p19");
        } else {
            showAlert("Aucune donnée disponible", Alert.AlertType.INFORMATION);
            user_admin_tableView.getItems().clear();
        }
    }

    @FXML  // Ensure this annotation is present
    public void handleDeleteAction() throws SQLException {
        Personne selectedUser = user_admin_tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.supprimer(selectedUser);
            loadTableData();
        } else {
            showAlert("No user selected", Alert.AlertType.ERROR);
        }
        ;
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            loadTableData(); // pour réafficher tous les utilisateurs
            return;
        }

        List<Personne> allUsers = userService.selectAll();
        List<Personne> filteredList = allUsers.stream()
                .filter(user -> user.getFirst_Name().toLowerCase().contains(query.toLowerCase())
                        || user.getLast_Name().toLowerCase().contains(query.toLowerCase())
                        || user.getEmail().toLowerCase().contains(query.toLowerCase()))
                .toList();

        ObservableList<Personne> observableList = FXCollections.observableArrayList(filteredList);
        user_admin_tableView.setItems(observableList);
    }
    @FXML
    private void handleSearchAction() {
        /*String email = tableView_admin_fld.getText().trim();

        if(email.isEmpty()) {
            showAlert("Veuillez entrer un email", Alert.AlertType.WARNING);
            return;
        }

        Personne user = userService.searchByEmail(email);

        if(user != null) {
            ObservableList<Personne> userList = FXCollections.observableArrayList(user);
            user_admin_tableView.setItems(userList);
        } else {
            showAlert("Aucun utilisateur trouvé avec l'email: " + email, Alert.AlertType.INFORMATION);
        }*/

    }




    @FXML
    private void handleSortAction() {
        ObservableList<Personne> currentUsers = user_admin_tableView.getItems();

        if (currentUsers != null && !currentUsers.isEmpty()) {
            FXCollections.sort(currentUsers, Comparator.comparing(Personne::getFirst_Name));
            user_admin_tableView.setItems(currentUsers);
        } else {
            showAlert("Aucun utilisateur à trier", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleSortByPoints() {
        ObservableList<Personne> currentUsers = user_admin_tableView.getItems();

        if (currentUsers != null && !currentUsers.isEmpty()) {
            FXCollections.sort(currentUsers, Comparator.comparingInt(Personne::getPoint));
            user_admin_tableView.setItems(currentUsers);
        } else {
            showAlert("Aucun utilisateur à trier", Alert.AlertType.INFORMATION);
        }
    }


    @FXML

    public void onSignIn() throws SQLException {


        // Récupération de la spécialité choisie
        String selectedSpeciality = splitMenu_admin_fld.getText();


        // Définition des rôles en fonction de la spécialité
        String selectedRole = "";
        if (selectedSpeciality.equals("enseignant")) {
            selectedRole = "[\"ROLE_ENSEIGNANT\"]"; // Exemple : le rôle pour la spécialité Informatique
        } else if (selectedSpeciality.equals("Artiste")) {
            selectedRole = "[\"ROLE_ARTISTE\"]"; // Exemple : le rôle pour la spécialité Artiste
        } else if (selectedSpeciality.equals("Admin")) {
            selectedRole = "[\"ROLE_ADMIN\"]"; // Exemple : le rôle pour la spécialité Admin
        } else {
            selectedRole = "[\"ROLE_USER\"]"; // Rôle par défaut pour d'autres spécialités
        }

        // apres ici j'affecte le rôle et la spécialité à l'utilisateur
        user.setRoles(selectedRole); //  le rôle de l'utilisateur
        user.setSpecialite(selectedSpeciality); //  la spécialité à l'utilisateur

        if (controleDeSaisie.isValidEmail(email_admin_fldd.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_admin_fld.getText()) &&
                controleDeSaisie.checkPasswordStrength(password_admin_fld.getText()) &&
                controleDeSaisie.checkText(name_admin_fld.getText())
                && controleDeSaisie.checkText(lastName_admin_fld.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_admin_fld.getText()) &&
                controleDeSaisie.chekNumero(cin_admin_fld.getText())) {

            user.setLast_Name(lastName_admin_fld.getText());
            user.setFirst_Name(name_admin_fld.getText());
            user.setCin(cin_admin_fld.getText());
            user.setNum_tel(phoneNumber_admin_fld.getText());
            user.setAddress(adress_admin_fld.getText());
            user.setEmail(email_admin_fldd.getText());
            //user.setPassword(password_admin_fld.getText());
            String hashedPassword = BCrypt.hashpw(password_admin_fld.getText(), BCrypt.gensalt());
            user.setPassword(hashedPassword);


            // Ajout dans la base de donnees
            ServicePersonne serviceUser = new ServicePersonne();
            serviceUser.ajouter(user);
            // Mise a jour du table View apres  ajout
            loadTableData();


        } else if (!controleDeSaisie.isValidEmail(email_admin_fldd.getText())) {
            email_error_txt.setText("Invalid e-mail ");


        } else if (!controleDeSaisie.checkPasswordStrength(password_admin_fld.getText())) {
            password_error_txt.setText("password is not strong enough");
        } else if (!controleDeSaisie.checkText(name_admin_fld.getText())) {
            name_error_txt.setText("Name Must be at least 2 letters");
        } else if (!controleDeSaisie.checkText(lastName_admin_fld.getText())) {
            lastName_error_txt.setText("Last Name Must be at least 2 letters");
        } else if (!controleDeSaisie.chekNumero(cin_admin_fld.getText())) {
            cin_error_txt.setText("cin must be 8 numbers");
        } else if (!controleDeSaisie.chekNumero(phoneNumber_admin_fld.getText())) {
            phoneNumber_error_txt.setText("Phone number must be 8 numbers");
        }

    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Méthode pour ouvrir la page de statistiques dans une nouvelle fenêtre
    public void openStatistique() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/pidev3a46geeksmesmerise/Fxml/Admin/adminStatistique.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Statistique");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Méthode d'action liée au bouton Statistique dans le FXML
    @FXML
    public void handleOpenStatistique() {
        openStatistique();
    }

    public void handleAddUser(ActionEvent actionEvent)
    {
        /*BorderPane adminParent = (BorderPane) tools_btn.getScene().lookup("#admin_parent");
        if (adminParent != null)
        {
            adminParent.setCenter(gui.getInstance().getViewFactory().getAdminAddUserView());
        } else
        {
            System.err.println("admin_parent non trouvé !");
        }*/

        // Au lieu de manipuler directement le BorderPane, utilisez le système de navigation
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("AddUser");
    }



}

