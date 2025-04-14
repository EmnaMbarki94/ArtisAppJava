package tn.esprit.controller.users;

import javafx.fxml.FXML;
import tn.esprit.gui.gui;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable{

    @FXML
    public BorderPane users_parent;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("I M @ USER CONTROLLER ");

      gui.getInstance().getViewFactory().getUserSelectedMenuItem().addListener((observableValue, oldVal, newVal) ->{
            switch (newVal){
                case "Enseignement" -> users_parent.setCenter(gui.getInstance().getViewFactory().getEnseignementView());
                case "Artiste" -> users_parent.setCenter(gui.getInstance().getViewFactory().getArtisteView());
                case "Magasin" -> users_parent.setCenter(gui.getInstance().getViewFactory().getEvenementView());
                case "Evenement" -> users_parent.setCenter(gui.getInstance().getViewFactory().getMagasinView());
                case "reclamation" -> users_parent.setCenter(gui.getInstance().getViewFactory().getReclamationView());
                case "Profile" -> users_parent.setCenter(gui.getInstance().getViewFactory().getProfileView());

                default ->users_parent.setCenter(gui.getInstance().getViewFactory().getDashboardView());
            }
        } );

    }
}

