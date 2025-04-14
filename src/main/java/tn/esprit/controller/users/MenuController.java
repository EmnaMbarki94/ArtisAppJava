package tn.esprit.controller.users;

import tn.esprit.controller.ProfileController;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.gui.gui;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    public Button dashboard_user_btn;

    public Button enseignement_user_btn;
    public Button artiste_user_btn;
    public Button evenement_user_btn;
    public Button magasin_user_btn;
    public Button reclamation_user_btn;
    public Button profile_user_btn;
    public Button logout_user_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Personne user=Session.getUser();
        if (user.getRoles().contains("ROLE_ENSEIGNANT")) {
            artiste_user_btn.setDisable(true);
        }else if(user.getRoles().contains("ROLE_ARTISTE")){
            enseignement_user_btn.setDisable(true);
        }

            addListeners();
    }
    private void addListeners(){
        dashboard_user_btn.setOnAction(event->onDashboard());
        enseignement_user_btn.setOnAction(event -> onEnseignement());
        artiste_user_btn.setOnAction(event -> onArtiste());
        evenement_user_btn.setOnAction(event -> onEvenement());
        magasin_user_btn.setOnAction(event->onMagasin());
        reclamation_user_btn.setOnAction(event->onReclamation());
        profile_user_btn.setOnAction(event-> onProfile());
        logout_user_btn.setOnAction(event -> onLogout() );

    }

    private void onDashboard(){
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Dashboard");
    }

    private void onEnseignement()
    {
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Enseignement");
    }

    private  void onArtiste()
    {
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Artiste");
    }

    private void onEvenement(){
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Evenement");
    }

    private void onMagasin()
    {
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Magasin");
    }

    private void onReclamation()
    {
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Reclamation");
    }
    private void onProfile()
    {
        gui.getInstance().getViewFactory().getUserSelectedMenuItem().set("Profile");
    }

    private void onLogout()
    {
        Stage stage = (Stage) logout_user_btn.getScene().getWindow();
        //ProfileController.user=null;
        Session.destroySession();
        gui.getInstance().getViewFactory().closeStage(stage);
        gui.getInstance().getViewFactory().showLoginWindow();
    }
}

