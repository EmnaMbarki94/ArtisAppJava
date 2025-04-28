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
public class AdminCrudController implements Initializable {


    public Button updateCrud_btn;
    public Button deleteCrud_btn;
    public Button statsCrud_btn;
    public Button metierCrud_btn;
    public TextField name_adminCrud_fld;
    public TextField lastName_adminCrud_fld;
    public TextField email_adminCrud_fldd;
    public TextField password_adminCrud_fld;
    public TextField cin_adminCrud_fld;
    public TextField phoneNumber_adminCrud_fld;
    public TextField adress_adminCrud_fld;
    public SplitMenuButton splitMenu_adminCrud_fld;
    public Button register_adminCrud_btn;
    public TableView<Personne>  user_adminCrud_tableView;
    private final ServicePersonne userService = new ServicePersonne();
    public Text name_error_txt;
    public Text lastName_error_txt;
    public Text email_error_txt;
    public Text password_error_txt;
    public Text phoneNumber_error_txt;
    public Text Adress_error_txt;
    public Text cin_error_txt;
    private StringBuilder realPassword = new StringBuilder();
    ControleDeSaisie controleDeSaisie = new ControleDeSaisie();
    Personne user = new Personne();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        loadTableData();
        deleteCrud_btn.setOnAction(event-> {
            try {
                handleDeleteAction();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
       /* search_admin_btn.setOnAction(event->handleSearchAction());
        register_adminCrud_btn.setOnAction(event-> {
            try {
                onSignIn();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });*/
       /* refresh_admin_btn.setOnAction(event->loadTableData());
        for (MenuItem item : splitMenu_adminCrud_fld.getItems()) {
            item.setOnAction(event -> {
                splitMenu_adminCrud_fld.setText(item.getText()); // Affiche le choix dans le menu
            });
        }*/

        // Masquer le mot de passe avec des "*"
      /*  password_adminCrud_fld.setOnKeyTyped(event -> {
            event.consume(); // Bloquer l'affichage du texte tapé

            String typedChar = event.getCharacter();
            if ("\b".equals(typedChar) && realPassword.length() > 0) {
                realPassword.deleteCharAt(realPassword.length() - 1);
            } else {
                realPassword.append(typedChar);
            }

            // Afficher des "*"
            password_adminCrud_fld.setText("*".repeat(realPassword.length()));
        });*/

        setupSplitMenuButton();
    }

    private void setupSplitMenuButton() {
        // ici j'ai créé les options
        MenuItem enseignantItem = new MenuItem("enseignant");
        MenuItem artisteItem = new MenuItem("Artiste");
        MenuItem adminItem = new MenuItem("Admin");
        MenuItem userItem = new MenuItem("user");

        // quand on clique sur une option celle ci s'afiche sur le splitmenu
        enseignantItem.setOnAction(e -> splitMenu_adminCrud_fld.setText("enseignant"));
        artisteItem.setOnAction(e -> splitMenu_adminCrud_fld.setText("Artiste"));
        adminItem.setOnAction(e -> splitMenu_adminCrud_fld.setText("Admin"));
        userItem.setOnAction(e -> splitMenu_adminCrud_fld.setText("user"));

        // je fini par integrer les option au splitmenu
        splitMenu_adminCrud_fld.getItems().addAll(enseignantItem, artisteItem, adminItem, userItem);
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

        user_adminCrud_tableView.getColumns().addAll(emailColumn, rolesColumn, nomColumn, prenomColumn,
                passwordColumn, numTelColumn, cinColumn, addressColumn, verifiedColumn, specialiteColumn,pointColumn);
    }

    private void loadTableData() {
        try {
            List<Personne> users = userService.selectAll();
            ObservableList<Personne> userList = FXCollections.observableArrayList(users);
            user_adminCrud_tableView.setItems(userList);
        } catch (Exception e) { // Changement ici
            e.printStackTrace();
            showAlert("Erreur de chargement: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML  // Ensure this annotation is present
    public void handleDeleteAction() throws SQLException {
        Personne selectedUser = user_adminCrud_tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.supprimer(selectedUser);
            loadTableData();
        } else {
            showAlert("No user selected", Alert.AlertType.ERROR);
        };
    }

    /*@FXML
    private void handleSearchAction() {
        String email = user_adminCrud_tableView.getText();
        try {
            Personne user = userService.searchByEmail(email);
            if (user != null) {
                ObservableList<Personne> userList = FXCollections.observableArrayList();
                userList.add(user);
                user_adminCrud_tableView.setItems(userList);
            } else {
                showAlert("User not found", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    @FXML
    private void handleSortAction() {
        ObservableList<Personne> currentUsers = user_adminCrud_tableView.getItems();

        if (currentUsers != null && !currentUsers.isEmpty()) {
            FXCollections.sort(currentUsers, Comparator.comparing(Personne::getFirst_Name));
            user_adminCrud_tableView.setItems(currentUsers);
        } else {
            showAlert("Aucun utilisateur à trier", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleSortByPoints() {
        ObservableList<Personne> currentUsers = user_adminCrud_tableView.getItems();

        if (currentUsers != null && !currentUsers.isEmpty()) {
            FXCollections.sort(currentUsers, Comparator.comparingInt(Personne::getPoint));
            user_adminCrud_tableView.setItems(currentUsers);
        } else {
            showAlert("Aucun utilisateur à trier", Alert.AlertType.INFORMATION);
        }
    }


    @FXML

    public void onSignIn() throws SQLException {


        // Récupération de la spécialité choisie
        String selectedSpeciality = splitMenu_adminCrud_fld.getText();


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

        if (controleDeSaisie.isValidEmail(email_adminCrud_fldd.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_adminCrud_fld.getText()) &&
                controleDeSaisie.checkPasswordStrength(password_adminCrud_fld.getText()) &&
                controleDeSaisie.checkText(name_adminCrud_fld.getText())
                && controleDeSaisie.checkText(lastName_adminCrud_fld.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_adminCrud_fld.getText()) &&
                controleDeSaisie.chekNumero(cin_adminCrud_fld.getText())) {

            // Vérification si l'email existe déjà en utilisant la méthode searchByEmail
            String email = email_adminCrud_fldd.getText();
            Personne existingPerson = userService.searchByEmail(email);
            if (existingPerson != null) {  // Si l'utilisateur avec cet email existe
                email_error_txt.setText("Email already exists!");
                return; // Arrêter le processus si l'email existe déjà
            }

            user.setLast_Name(lastName_adminCrud_fld.getText());
            user.setFirst_Name(name_adminCrud_fld.getText());
            user.setCin(cin_adminCrud_fld.getText());
            user.setNum_tel(phoneNumber_adminCrud_fld.getText());
            user.setAddress(adress_adminCrud_fld.getText());
            user.setEmail(email_adminCrud_fldd.getText());
            //user.setPassword(password_admin_fld.getText());
           // String hashedPassword = BCrypt.hashpw(password_adminCrud_fld.getText(), BCrypt.gensalt());
            String hashedPassword = BCrypt.hashpw(password_adminCrud_fld.getText(), BCrypt.gensalt(10));
            user.setPassword(hashedPassword);


            // Ajout dans la base de donnees
            ServicePersonne serviceUser = new ServicePersonne();
            serviceUser.ajouter(user);
            // Mise a jour du table View apres  ajout
            loadTableData();


        } else if (!controleDeSaisie.isValidEmail(email_adminCrud_fldd.getText())) {
            email_error_txt.setText("Invalid e-mail ");


        }
        else if(!controleDeSaisie.checkPasswordStrength(password_adminCrud_fld.getText()))
        {
            password_error_txt.setText("password is not strong enough");
        }
        else if(!controleDeSaisie.checkText(name_adminCrud_fld.getText()))
        {
            name_error_txt.setText("Name Must be at least 2 letters");
        }
        else if(!controleDeSaisie.checkText(lastName_adminCrud_fld.getText()))
        {
            lastName_error_txt.setText("Last Name Must be at least 2 letters");
        } else if (!controleDeSaisie.chekNumero(cin_adminCrud_fld.getText())) {
            cin_error_txt.setText("cin must be 8 numbers");
        }
        else if(!controleDeSaisie.chekNumero(phoneNumber_adminCrud_fld.getText()))
        {
            phoneNumber_error_txt.setText("Phone number must be 8 numbers");
        }

        //clear********
        clearFields();

    }

    //*********
    private void clearFields() {
        name_adminCrud_fld.clear();
        lastName_adminCrud_fld.clear();
        email_adminCrud_fldd.clear();
        password_adminCrud_fld.clear();
        cin_adminCrud_fld.clear();
        phoneNumber_adminCrud_fld.clear();
        adress_adminCrud_fld.clear();
        splitMenu_adminCrud_fld.setText("Choisir spécialité"); // ou vide si tu veux: ""
    }
    //*****

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

    public void handleUpdateUser(ActionEvent actionEvent) {
      /* BorderPane adminParent = (BorderPane) updateCrud_btn.getScene().lookup("#admin_parent");
        if (adminParent != null)
        {
            adminParent.setCenter(gui.getInstance().getViewFactory().getUpdateUserAdminCrudWindow());
        } else
        {
            System.err.println("admin_parent non trouvé !");
        }*/
        // Utilisez le système de navigation au lieu de manipuler directement le BorderPane
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("UpdateUser");
    }


    public void handelMetier(ActionEvent actionEvent)
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("Metiers");
    }

    public void handelstat(ActionEvent actionEvent)
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("stat");
    }

}


