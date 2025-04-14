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

public class AdminUpdateCrudController implements Initializable {

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
    public TableView<Personne> user_adminCrud_tableView;
    public Text name_error_txt;
    public Text lastName_error_txt;
    public Text email_error_txt;
    public Text password_error_txt;
    public Text phoneNumber_error_txt;
    public Text Adress_error_txt;
    public Text cin_error_txt;
    private final ServicePersonne userService = new ServicePersonne();
    public Button addCrud_btn;
    private StringBuilder realPassword = new StringBuilder();
    ControleDeSaisie controleDeSaisie = new ControleDeSaisie();
    Personne user = new Personne();

    private Personne selectedUser;

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
        password_adminCrud_fld.setOnKeyTyped(event -> {
            event.consume(); // Bloquer l'affichage du texte tapé

            String typedChar = event.getCharacter();
            if ("\b".equals(typedChar) && realPassword.length() > 0) {
                realPassword.deleteCharAt(realPassword.length() - 1);
            } else {
                realPassword.append(typedChar);
            }

            // Afficher des "*"
            password_adminCrud_fld.setText("*".repeat(realPassword.length()));
        });

        // Ajouter un écouteur de sélection à la TableView
        user_adminCrud_tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedUser = newValue;
                        populateFieldsWithUserData(newValue);
                    }
                });
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
        List<Personne> users = userService.selectAll();

        if (users != null && !users.isEmpty()) {
            ObservableList<Personne> userList = FXCollections.observableArrayList(users);
            user_adminCrud_tableView.setItems(userList);
        } else {
            showAlert("Aucune donnée disponible", Alert.AlertType.INFORMATION);
        }
    }

    // Méthode pour remplir les champs avec les données de l'utilisateur sélectionné
    private void populateFieldsWithUserData(Personne user) {
        name_adminCrud_fld.setText(user.getFirst_Name());
        lastName_adminCrud_fld.setText(user.getLast_Name());
        email_adminCrud_fldd.setText(user.getEmail());
        // Note: Pour des raisons de sécurité, on ne remplit pas le mot de passe
        // password_adminCrud_fld.setText("");
        cin_adminCrud_fld.setText(user.getCin());
        phoneNumber_adminCrud_fld.setText(user.getNum_tel());
        adress_adminCrud_fld.setText(user.getAddress());

        // Définir la spécialité dans le SplitMenuButton
        if (user.getSpecialite() != null) {
            splitMenu_adminCrud_fld.setText(user.getSpecialite());
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
    public void onUpdate() throws SQLException {
        if (selectedUser == null) {
            System.out.println("user up1");
            showAlert("Aucun utilisateur sélectionné", Alert.AlertType.ERROR);
            return;
        }

        // Récupération de la spécialité choisie
        String selectedSpeciality = splitMenu_adminCrud_fld.getText();
        //System.out.println("Specialité sélectionnée: [" + selectedSpeciality + "]"); // Debug

        // Définition des rôles en fonction de la spécialité
        System.out.println(selectedSpeciality);
        String selectedRole = "";
        System.out.println(selectedRole);
        if (selectedSpeciality.equals("enseignant")) {
            System.out.println("user up2");
            System.out.println(selectedRole);
            selectedRole = "[\"ROLE_ENSEIGNANT\"]";
        } else if (selectedSpeciality.equals("artiste")) {
            System.out.println("user up3");

            selectedRole = "[\"ROLE_ARTISTE\"]";
        } else if (selectedSpeciality.equals("admin")) {
            System.out.println("user up4");

            selectedRole = "[\"ROLE_ADMIN\"]";
        } else {
            System.out.println("user up5");
            System.out.println(selectedRole);
            selectedRole = "[\"ROLE_USER\"]";
            System.out.println(selectedRole);
        }
        System.out.println("Specialité sélectionnée: [" +  selectedRole + "]"); // Debug
        // Validation des champs
        if (controleDeSaisie.isValidEmail(email_adminCrud_fldd.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_adminCrud_fld.getText()) &&
                controleDeSaisie.checkPasswordStrength(password_adminCrud_fld.getText()) &&
                controleDeSaisie.checkText(name_adminCrud_fld.getText())
                && controleDeSaisie.checkText(lastName_adminCrud_fld.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_adminCrud_fld.getText()) &&
                controleDeSaisie.chekNumero(cin_adminCrud_fld.getText())) {

            // Mise à jour de l'utilisateur sélectionné
            System.out.println("user up6");

            selectedUser.setLast_Name(lastName_adminCrud_fld.getText());
            selectedUser.setFirst_Name(name_adminCrud_fld.getText());
            selectedUser.setCin(cin_adminCrud_fld.getText());
            selectedUser.setNum_tel(phoneNumber_adminCrud_fld.getText());
            selectedUser.setAddress(adress_adminCrud_fld.getText());
            selectedUser.setEmail(email_adminCrud_fldd.getText());
            selectedUser.setRoles(selectedRole);
            selectedUser.setSpecialite(selectedSpeciality);

            // Si le mot de passe a été modifié
            if (!password_adminCrud_fld.getText().isEmpty() &&
                    !password_adminCrud_fld.getText().equals("*".repeat(realPassword.length()))) {
                System.out.println("user up1");

                if (controleDeSaisie.checkPasswordStrength(password_adminCrud_fld.getText())) {
                    System.out.println("user up1");

                    String hashedPassword = BCrypt.hashpw(realPassword.toString(), BCrypt.gensalt());
                    selectedUser.setPassword(hashedPassword);
                } else {
                    System.out.println("user up1");

                    password_error_txt.setText("password is not strong enough");
                    return;
                }
            }

            // Mise à jour dans la base de données
            System.out.println("user up9");

            userService.modifier(selectedUser);
            System.out.println("user up9s");
            loadTableData();
            clearFields();
            showAlert("Utilisateur mis à jour avec succès", Alert.AlertType.INFORMATION);

        } else {
            System.out.println("user up10");

            // Gestion des erreurs (votre code existant)
            if (!controleDeSaisie.isValidEmail(email_adminCrud_fldd.getText())) {
                System.out.println("user up11");

                email_error_txt.setText("Invalid e-mail ");
            }
            // ... (autres vérifications d'erreur)
        }
    }



    // Méthode pour vider les champs
    private void clearFields() {
        name_adminCrud_fld.clear();
        lastName_adminCrud_fld.clear();
        email_adminCrud_fldd.clear();
        password_adminCrud_fld.clear();
        cin_adminCrud_fld.clear();
        phoneNumber_adminCrud_fld.clear();
        adress_adminCrud_fld.clear();
        splitMenu_adminCrud_fld.setText("SplitMenuButton");
        realPassword.setLength(0);

        // Effacer les messages d'erreur
        name_error_txt.setText("");
        lastName_error_txt.setText("");
        email_error_txt.setText("");
        password_error_txt.setText("");
        phoneNumber_error_txt.setText("");
        Adress_error_txt.setText("");
        cin_error_txt.setText("");
    }

    //@FXML

   /* public void onSignIn() throws SQLException {


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

            user.setLast_Name(lastName_adminCrud_fld.getText());
            user.setFirst_Name(name_adminCrud_fld.getText());
            user.setCin(cin_adminCrud_fld.getText());
            user.setNum_tel(phoneNumber_adminCrud_fld.getText());
            user.setAddress(adress_adminCrud_fld.getText());
            user.setEmail(email_adminCrud_fldd.getText());
            //user.setPassword(password_admin_fld.getText());
            String hashedPassword = BCrypt.hashpw(password_adminCrud_fld.getText(), BCrypt.gensalt());
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

    }*/

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


    public void handleAddUser(ActionEvent actionEvent) {
        /*BorderPane adminParent = (BorderPane) addCrud_btn.getScene().lookup("#admin_parent");
        if (adminParent != null)
        {
            adminParent.setCenter(gui.getInstance().getViewFactory().getAdminAddUserView());
        } else
        {
            System.err.println("admin_parent non trouvé !");
        }*/
        // Utilisez le système de navigation
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("AddUser");
    }


    // Méthode d'action liée au bouton Statistique dans le FXML
    @FXML
    public void handleOpenStatistique() {
        openStatistique();
    }




}
