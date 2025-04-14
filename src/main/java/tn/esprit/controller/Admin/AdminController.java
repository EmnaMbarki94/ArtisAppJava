package tn.esprit.controller.Admin;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import tn.esprit.gui.gui;

import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    public BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal) {
                case "EnseignementAdmin" ->
                        admin_parent.setCenter(gui.getInstance().getViewFactory().getEnseignementAdminView());
                case "ArtisteAdmin" -> admin_parent.setCenter(gui.getInstance().getViewFactory().getArtisteAdminView());
                case "MagasinAdmin" -> admin_parent.setCenter(gui.getInstance().getViewFactory().getMagasinAdminView());
                case "ReclamationAdmin" ->
                        admin_parent.setCenter(gui.getInstance().getViewFactory().getReclamationAdminView());
                case "EvenementAdmin" ->
                        admin_parent.setCenter(gui.getInstance().getViewFactory().getEvenementAdminView());
                case "ProfileAdmin" -> admin_parent.setCenter(gui.getInstance().getViewFactory().getProfileView());

                //******************
                case "AddUser" ->
                        admin_parent.setCenter(gui.getInstance().getViewFactory().getAdminAddUserView());
                case "UpdateUser" ->
                        admin_parent.setCenter(gui.getInstance().getViewFactory().getUpdateUserAdminCrudWindow());
                //***********************

                default -> admin_parent.setCenter(gui.getInstance().getViewFactory().getDashboardAdminView());
            }
        });

        loadLeftView();
        loadInitialView();
    }

    // Méthode pour charger un fichier FXML dans la région Left du BorderPane
    private void loadLeftView() {
        try {
            // Charger la vue pour la région Left
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/MenuAdmin.fxml"));
            Parent leftMenuView = loader.load();
            System.out.println("Vue du menu gauche chargée avec succès");
            admin_parent.setLeft(leftMenuView); // Ajouter la vue dans la région Left du BorderPane
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger la vue initiale (Dashboard) dans le Center
    private void loadInitialView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/DashboardAdmin.fxml"));
            Parent dashboardView = loader.load();
            admin_parent.setCenter(dashboardView); // Ajouter la vue dans la région Center du BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            // Ajoutez un message d'erreur plus clair
            System.err.println("Erreur lors du chargement de la vue DashboardAdmin.");
        }
    }

}