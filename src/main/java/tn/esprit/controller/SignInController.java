package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.gui.gui;
import tn.esprit.entities.Personne;
import tn.esprit.services.ControleDeSaisie;
import tn.esprit.services.ServicePersonne;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    public TextField name_signIn_field, lastName_signIn_field, email_signIn_field, cin_signIn_field, adress_signIn_field, phoneNumber_signIn_field;
    @FXML
    public DatePicker dob_singIn_field;
    @FXML
    public Button register_signIn_btn;
    @FXML
    public Text email_error_txt, phoneNumber_error_txt, name_error_txt, lastName_error_txt, password_error_txt, cin_error_txt;
    public Button retour_btn;
    @FXML
    private SplitMenuButton splitmenu_signin_fld;
    @FXML
    private TextField password_signIn_field;

    private final ServicePersonne serviceUser = new ServicePersonne();
    private final ControleDeSaisie controleDeSaisie = new ControleDeSaisie();
    private Personne user = new Personne();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        register_signIn_btn.setOnAction(event -> {
            try {
                onSignIn();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        for (MenuItem item : splitmenu_signin_fld.getItems()) {
            item.setOnAction(event -> splitmenu_signin_fld.setText(item.getText()));
        }

        // Masquer le mot de passe avec des "*"

    }

    /*public void onSignIn() throws SQLException {
        Stage stage = (Stage) register_signIn_btn.getScene().getWindow();

        // Effacer les messages d'erreur
        email_error_txt.setText("");
        phoneNumber_error_txt.setText("");
        name_error_txt.setText("");
        lastName_error_txt.setText("");
        password_error_txt.setText("");
        cin_error_txt.setText("");

        // Récupération du rôle choisi
        String selectedRole = splitmenu_signin_fld.getText();

        if (selectedRole.equals("enseignant")) {
            user.setRoles("[\"ROLE_ENSEIGNANT\"]");
        } else if (selectedRole.equals("artiste")) {
            user.setRoles("[\"ROLE_ARTISTE\"]");
        } else {
            user.setRoles("[\"ROLE_USER\"]");
        }

        // Vérification des champs
        if (controleDeSaisie.isValidEmail(email_signIn_field.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_signIn_field.getText()) &&
                controleDeSaisie.checkPasswordStrength(realPassword.toString()) &&
                controleDeSaisie.checkText(name_signIn_field.getText()) &&
                controleDeSaisie.checkText(lastName_signIn_field.getText()) &&
                controleDeSaisie.chekNumero(cin_signIn_field.getText()) &&
                !serviceUser.emailExists(email_signIn_field.getText()) &&
                !serviceUser.cinExists(cin_signIn_field.getText())) {

            user.setLast_Name(lastName_signIn_field.getText());
            user.setFirst_Name(name_signIn_field.getText());
            user.setCin(cin_signIn_field.getText());
            user.setNum_tel(phoneNumber_signIn_field.getText());
            user.setAddress(adress_signIn_field.getText());
            user.setEmail(email_signIn_field.getText());
            //user.setPassword(realPassword.toString()); // Utilisation du vrai mot de passe
            String hashedPassword = BCrypt.hashpw(password_signIn_field.getText(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            serviceUser.add(user);

            // Redirection
            gui.getInstance().getViewFactory().closeStage(stage);
            gui.getInstance().getViewFactory().showLoginWindow();
        } else {
            // Gestion des erreurs
            if (!controleDeSaisie.isValidEmail(email_signIn_field.getText())) {
                email_error_txt.setText("Invalid e-mail");
            } else if (serviceUser.emailExists(email_signIn_field.getText())) {
                email_error_txt.setText("This email already exists");
            } else if (serviceUser.cinExists(cin_signIn_field.getText())) {
                cin_error_txt.setText("This CIN already exists");
            } else if (!controleDeSaisie.checkPasswordStrength(realPassword.toString())) {
                password_error_txt.setText("Weak password");
            } else if (!controleDeSaisie.checkText(name_signIn_field.getText())) {
                name_error_txt.setText("Name must be at least 2 letters");
            } else if (!controleDeSaisie.checkText(lastName_signIn_field.getText())) {
                lastName_error_txt.setText("Last Name must be at least 2 letters");
            } else if (!controleDeSaisie.chekNumero(cin_signIn_field.getText())) {
                cin_error_txt.setText("CIN must be 8 numbers");
            } else if (!controleDeSaisie.chekNumero(phoneNumber_signIn_field.getText())) {
                phoneNumber_error_txt.setText("Phone number must be 8 numbers");
            }
        }*/
    public void onSignIn() throws SQLException {
        Stage stage = (Stage) register_signIn_btn.getScene().getWindow();

        // Effacer les messages d'erreur
        email_error_txt.setText("");
        phoneNumber_error_txt.setText("");
        name_error_txt.setText("");
        lastName_error_txt.setText("");
        password_error_txt.setText("");
        cin_error_txt.setText("");

        // Récupération de la spécialité choisie
        String selectedSpeciality = splitmenu_signin_fld.getText(); // Récupère la spécialité sélectionnée

        // Définir le rôle en fonction de la spécialité
        String selectedRole = "";
        if (selectedSpeciality.equalsIgnoreCase("enseignant")) {
            selectedRole = "[\"ROLE_ENSEIGNANT\"]";  // Exemple : le rôle pour la spécialité Informatique
        } else if (selectedSpeciality.equalsIgnoreCase("artiste")) {
            selectedRole = "[\"ROLE_ARTISTE\"]";  // Exemple : le rôle pour la spécialité Artiste
        } else {
            selectedRole = "[\"ROLE_USER\"]";  // Par défaut, pour une autre spécialité ou une spécialité inconnue
        }

        // Affecter le rôle à l'utilisateur
        user.setRoles(selectedRole);

        // Vérification des champs
        if (controleDeSaisie.isValidEmail(email_signIn_field.getText()) &&
                controleDeSaisie.chekNumero(phoneNumber_signIn_field.getText()) &&
                controleDeSaisie.checkPasswordStrength(password_signIn_field.getText()) &&
                controleDeSaisie.checkText(name_signIn_field.getText()) &&
                controleDeSaisie.checkText(lastName_signIn_field.getText()) &&
                controleDeSaisie.chekNumero(cin_signIn_field.getText()) &&
                !serviceUser.emailExists(email_signIn_field.getText()) &&
                !serviceUser.cinExists(cin_signIn_field.getText())) {

            // Initialisation des informations de l'utilisateur
            user.setLast_Name(lastName_signIn_field.getText());
            user.setFirst_Name(name_signIn_field.getText());
            user.setCin(cin_signIn_field.getText());
            user.setNum_tel(phoneNumber_signIn_field.getText());
            user.setAddress(adress_signIn_field.getText());
            user.setEmail(email_signIn_field.getText());
            user.setSpecialite(selectedSpeciality);
            // Hasher le mot de passe
            System.out.println(password_signIn_field.getText());
            String hashedPassword = BCrypt.hashpw(password_signIn_field.getText(), BCrypt.gensalt(10));
            user.setPassword(hashedPassword);

            // Ajouter l'utilisateur dans le service
            serviceUser.ajouter(user);

            // Redirection vers la fenêtre de connexion
            gui.getInstance().getViewFactory().closeStage(stage);
            gui.getInstance().getViewFactory().showLoginWindow();
        } else {
            // Gestion des erreurs
            if (!controleDeSaisie.isValidEmail(email_signIn_field.getText())) {
                email_error_txt.setText("Invalid e-mail");
            } else if (serviceUser.emailExists(email_signIn_field.getText())) {
                email_error_txt.setText("This email already exists");
            } else if (serviceUser.cinExists(cin_signIn_field.getText())) {
                cin_error_txt.setText("This CIN already exists");
            } else if (!controleDeSaisie.checkPasswordStrength(password_signIn_field.getText())) {
                password_error_txt.setText("Weak password");
            } else if (!controleDeSaisie.checkText(name_signIn_field.getText())) {
                name_error_txt.setText("Name must be at least 2 letters");
            } else if (!controleDeSaisie.checkText(lastName_signIn_field.getText())) {
                lastName_error_txt.setText("Last Name must be at least 2 letters");
            } else if (!controleDeSaisie.chekNumero(cin_signIn_field.getText())) {
                cin_error_txt.setText("CIN must be 8 numbers");
            } else if (!controleDeSaisie.chekNumero(phoneNumber_signIn_field.getText())) {
                phoneNumber_error_txt.setText("Phone number must be 8 numbers");
            }
        }
    }

    public void handleReturnToLogin(ActionEvent actionEvent)
    {
        Stage stage = (Stage) retour_btn.getScene().getWindow();
        gui.getInstance().getViewFactory().closeStage(stage);
        gui.getInstance().getViewFactory().showLoginWindow();
    }
}

