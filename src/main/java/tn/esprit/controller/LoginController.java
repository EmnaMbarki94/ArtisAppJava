package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import tn.esprit.controller.Admin.*;
import tn.esprit.controller.users.*;
import tn.esprit.entities.Session;
import tn.esprit.gui.gui;
import tn.esprit.entities.Personne;
import tn.esprit.services.ServicePersonne;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Label email_fp_lbl;
    public Label email_login_lbl;
    public TextField email_login_field;
    public Text error_login_lbl;
    public Label password_login_lbl;
    public PasswordField password_login_field;
    public Button  login_btn;
    public Button signIn_btn;


    // Autre
    private Personne user = new Personne();

    // Ajout du su user connecte aux autre entites
    public ProfileController profileController = new ProfileController();

    private ServicePersonne serviceUser = new ServicePersonne();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event-> onLogin());
        signIn_btn.setOnAction(event-> onSignIn());
    }

    @FXML
    private void onLogin(){
        Stage stage = (Stage) login_btn.getScene().getWindow();

        // Obtention du mail et du password
        String email = email_login_field.getText();
        String password = password_login_field.getText();

        try {




            // Verfication de l'existance de l'utilidateur
            user = serviceUser.Login(email,password);
            // Generation du Code de verification



            if(user != null )
            {
                //CodeVerification.code = Integer.parseInt(CodeGenerator.generateCode(user.getNom()));
                //  System.out.println(" Le code envoye par email est : " + CodeVerification.code);
                //if(user.getRoles().equals("[]")){
                Session.initSession(user);
                if (user.getRoles().contains("ROLE_ENSEIGNANT") || user.getRoles().contains("ROLE_ARTISTE") || user.getRoles().contains("ROLE_USER")){
//["ROLE_ARTISTE"]
                    //Envoie du SMS conetenant le code de verification

                    //  String VerifString = "Votre Code de verification est : "+CodeVerification.code;
                    // this.sendSms(VerifString);
                    //Envoie du mail de Verification
                    // EmailSender.sendCodeVerif(user.getEmail(),CodeVerification.code,user.getNom() );


                    gui.getInstance().getViewFactory().closeStage(stage);
                    gui.getInstance().getViewFactory().showUsersWindow();

                }
                else if (user.getRoles().equals("[\"ROLE_ADMIN\"]")) {



                    gui.getInstance().getViewFactory().closeStage(stage);
                    gui.getInstance().getViewFactory().showAdminWindow();


                }

            }
            else {
                error_login_lbl.setText("No email password combination correspond to an account ");
            }

        }catch (Exception e) {
            // Gérer l'exception et afficher un message d'erreur
            System.err.println("Une erreur s'est produite lors de la tentative de connexion : " + e.getMessage());
            e.printStackTrace();
            error_login_lbl.setText("Erreur lors de la connexion, veuillez réessayer.");
        }
    }

    @FXML
    private void onSignIn(){
        Stage stage  = (Stage) signIn_btn.getScene().getWindow();

        gui.getInstance().getViewFactory().closeStage(stage);
        gui.getInstance().getViewFactory().showSignInWindow();

    }

}

