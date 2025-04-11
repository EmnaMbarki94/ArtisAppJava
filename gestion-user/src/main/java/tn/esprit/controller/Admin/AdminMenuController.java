package tn.esprit.controller.Admin;


import javafx.fxml.FXML;
import tn.esprit.controller.ProfileController;
import tn.esprit.gui.gui;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class AdminMenuController implements Initializable {

    public Button dashboard_Admin_btn;
    public Button enseignement_Admin_btn;
    public Button artiste_Admin_btn;
    public Button evenement_Admin_btn;
    public Button magasin_Admin_btn;
    public Button reclamation_Admin_btn;
    public Button profile_Admin_btn;
    public Button logout_Admin_btn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }
    private void addListeners(){
        dashboard_Admin_btn.setOnAction(event->onDashboard());
        enseignement_Admin_btn.setOnAction(event -> onEnseignement());
        artiste_Admin_btn.setOnAction(event -> onArtiste());
        magasin_Admin_btn.setOnAction(event -> onMagazin());
        evenement_Admin_btn.setOnAction(event->onEvenement());
        profile_Admin_btn.setOnAction(event->onProfile());
        reclamation_Admin_btn.setOnAction(event-> onReclamation());
        logout_Admin_btn.setOnAction(event -> onLogout() );

    }
    @FXML
    private void onDashboard(){
        System.out.println("Sélectionner DashboardAdmin");
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("DashboardAdmin");
    }


    @FXML
    private void onEnseignement()
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("EnseignementAdmin");
    }
    @FXML
    private  void onArtiste()
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("ArtisteAdmin");
    }
    @FXML
    private void onEvenement(){
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("EvenementAdmin");
    }
    @FXML
    private void onMagazin()
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("MagasinAdmin");
    }
    @FXML    private void onReclamation()
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("ReclamationAdmin");
    }
    @FXML
    private void onProfile()
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("ProfileAdmin");
    }
    @FXML
    private void onLogout()
    {
        Stage stage = (Stage) logout_Admin_btn.getScene().getWindow();
        //ProfileController.user=null;
        gui.getInstance().getViewFactory().closeStage(stage);
        gui.getInstance().getViewFactory().showLoginWindow();
    }

}

