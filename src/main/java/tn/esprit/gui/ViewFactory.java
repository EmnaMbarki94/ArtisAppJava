package tn.esprit.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.controller.ProfileController;

import java.io.IOException;


public class ViewFactory
{
    // user View
    private final StringProperty UserSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane enseignementView;
    private AnchorPane artistView;
    private AnchorPane magasinView;
    private AnchorPane reclamationView;
    private AnchorPane evenementView;
    private AnchorPane profileView;


//Admin

    private final StringProperty adminSelectedMenuItem;
    private AnchorPane dashboardAdminView;
    private AnchorPane enseignementAdminView;
    private AnchorPane artistAdminView;
    private AnchorPane magasinAdminView;
    private AnchorPane reclamationAdminView;
    private AnchorPane evenementAdminView;
    private AnchorPane profileAdminView;
    private AnchorPane AdminAddUserView;
    private AnchorPane UpdateUserAdminCrudWindow;
    private AnchorPane AdminMetiersWindow;
    private AnchorPane AdminEditFileWindow;
    private AnchorPane AdminStatWindow;
    private AnchorPane AdminExportFileWindow;
    public ViewFactory() {
        this.UserSelectedMenuItem = new SimpleStringProperty("");
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }

    public StringProperty getUserSelectedMenuItem() {
        return UserSelectedMenuItem;
    }

    public AnchorPane getDashboardView() {
        if (dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/users/Dashboard.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dashboardView;
    }

    public void showLoginWindow() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader);
    }

    public void showDashboardWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/DashboardAdmin.fxml"));
        createStage(loader);
    }

    public void showSignInWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/SignIn.fxml"));
        createStage(loader);
    }

    public void showForgotPasswordnWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ForgotPassword.fxml"));
        createStage(loader);
    }

    public void showUsersWindow()
    {
        System.out.println("im @shwo users ");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Users.fxml"));
            // UserController userController = new UserController();
            // loader.setController(userController);
            createStage(loader);
        } catch (Exception e) {
            System.err.println("Une erreur inattendue est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showVerificationCodeWindow()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/CodeVerification.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try{
            scene = new Scene(loader.load());
        }catch(Exception e){
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("mesmerise");
        stage.show();
    }

    public void closeStage(Stage stage){
        stage.close();
    }

    public AnchorPane getAdminAddUserView() {
        if (AdminAddUserView == null) {
            try {
                AdminAddUserView = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminCrud.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdminAddUserView;
    }

    public AnchorPane getUpdateUserAdminCrudWindow() {
        if (UpdateUserAdminCrudWindow == null) {
            try {
                UpdateUserAdminCrudWindow = new FXMLLoader(getClass().getResource("/Fxml/Admin/updateCrudeAdmin.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return UpdateUserAdminCrudWindow;
    }

    //******* admin metier ************

    public AnchorPane getAdminMetiersdWindow() {
        if (AdminMetiersWindow == null) {
            try {
                AdminMetiersWindow = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminMetier.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdminMetiersWindow;
    }
    //*********************************

    //admin metier editer file *********
    public AnchorPane getAdminEditFileWindow() {
        if (AdminEditFileWindow == null) {
            try {
                AdminEditFileWindow = new FXMLLoader(getClass().getResource("/Fxml/Admin/editfile.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdminEditFileWindow;
    }
    //****************************

    //admin export file ********
    public AnchorPane getAdminExportFileWindow() {
        if (AdminExportFileWindow == null) {
            try {
                AdminExportFileWindow = new FXMLLoader(getClass().getResource("/Fxml/Admin/AdminexportFiles.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdminExportFileWindow;
    }
    //*************

    //admin static *********
    public AnchorPane getAdminStatistcWindow() {
        if (AdminStatWindow == null) {
            try {
                AdminStatWindow = new FXMLLoader(getClass().getResource("/Fxml/Admin/adminStatistique.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return AdminStatWindow;
    }
    //****************************


    //************************


    public AnchorPane getEnseignementView() {
        if(enseignementView == null)
        {
            try{
                enseignementView = new FXMLLoader(getClass().getResource("/Fxml/users/Enseignement.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enseignementView;
    }

    public AnchorPane getArtisteView(){
        if(artistView == null){
            try{
                artistView = new FXMLLoader(getClass().getResource("/Fxml/Galerie/AfficherGalerie.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return artistView;
    }

    public AnchorPane getMagasinView()
    {
        if(magasinView == null)
        {
            try{
                magasinView = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/Magasin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return magasinView;
    }


    public AnchorPane getEvenementView()
    {
        if(evenementView == null)
            try{
                evenementView = new FXMLLoader(getClass().getResource("/Fxml/Events/user/Evenement.fxml")).load();
            }catch (Exception e )
            {
                e.printStackTrace();
            }
        return evenementView;

    }
    public AnchorPane getProfileView() {
        if(profileView == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Profile.fxml"));
                Parent root = loader.load();
                ProfileController profileController = loader.getController();
                // Now that the controller is fully initialized, you can call methods on it
                profileController.displayData(); // Pass user data to display on the profile page
                profileView = (AnchorPane) root;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profileView;
    }

    //admin*************************************************************************
    //     *************************************************************************

    public AnchorPane getEnseignementAdminView() {
        if(enseignementAdminView == null)
        {
            try{
                enseignementAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/EnseignementAdmin.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enseignementAdminView;
    }

    public AnchorPane getArtisteAdminView(){
        //if(artistAdminView == null){
            try{
                artistAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/ArtisteAdmin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        //}
        return artistAdminView;
    }

    public AnchorPane getMagasinAdminView()
    {
        if(magasinAdminView == null)
            try{
                magasinAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/GestionMagasins/MagasinAdmin.fxml")).load();
            }catch (Exception e )
            {
                e.printStackTrace();
            }
        return magasinAdminView;

    }

    public AnchorPane getReclamationView()
    {
        if(reclamationView == null)
        {
            try{
                reclamationView = new FXMLLoader(getClass().getResource("/Fxml/Reclamation/AfficherRecl.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return reclamationView;
    }

    //    public AnchorPane getReclamationView()
//    {
//        if(reclamationView == null)
//        {
//            try {
//                reclamationView = new FXMLLoader(getClass().getResource("/Fxml/Reclamation/AfficherRecl.fxml")).load();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.err.println("❌ Erreur de chargement de AjouterRec.fxml pour admin.");
//            }
//        }
//        return reclamationView;
//    }
    public AnchorPane getReclamationAdminView()

    {
        if(reclamationAdminView == null)
        {
            try {
                reclamationAdminView = new FXMLLoader(getClass().getResource("/Fxml/Reponse/ListReclamation.fxml")).load();

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Erreur de chargement de AjouterRec.fxml pour admin.");
            }
        }
        return reclamationAdminView;
    }

    public AnchorPane getEvenementAdminView()
    {
        if(evenementAdminView == null)
        {
            try{
                evenementAdminView = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/EvenementAdmin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return evenementAdminView;
    }

    public AnchorPane getDashboardAdminView()
    {
        if( dashboardAdminView == null)
        {
            try{
                dashboardAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/DashboardAdmin.fxml")).load();
            }catch( Exception e)
            {
                e.printStackTrace();
            }
        }
        return dashboardAdminView;
    }

    public StringProperty getAdminSelectedMenuItem(){

        return adminSelectedMenuItem;
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        createStage(loader); // Let FXML's fx:controller handle instantiation
    }



}


