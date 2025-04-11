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
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/User/Dashboard.fxml")).load();
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

    public void showUsersWindow()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Users/Users.fxml"));
        // UserController userController = new UserController();
        //loader.setController(userController);
        createStage(loader);
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

    public AnchorPane getEnseignementView() {
        if(enseignementView == null)
        {
            try{
                enseignementView = new FXMLLoader(getClass().getResource("/Fxml/Users/Enseignement.fxml")).load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return enseignementView;
    }

    public AnchorPane getArtisteView(){
        if(artistView == null){
            try{
                artistView = new FXMLLoader(getClass().getResource("/Fxml/Users/Artiste.fxml")).load();
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
                magasinView = new FXMLLoader(getClass().getResource("/Fxml/Users/Magasin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return magasinView;
    }

    public AnchorPane getReclamationView()
    {
        if(reclamationView == null)
        {
            try{
                reclamationView = new FXMLLoader(getClass().getResource("/Fxml/Users/Reclamation.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return reclamationView;
    }

    public AnchorPane getEvenementView()
    {
        if(evenementView == null)
            try{
                evenementView = new FXMLLoader(getClass().getResource("/Fxml/Users/Evenement.fxml")).load();
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
        if(artistAdminView == null){
            try{
                artistAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/ArtisteAdmin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return artistAdminView;
    }

    public AnchorPane getMagasinAdminView()
    {
        if(magasinAdminView == null)
            try{
                magasinAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/MagasinAdmin.fxml")).load();
            }catch (Exception e )
            {
                e.printStackTrace();
            }
        return magasinAdminView;

    }

    public AnchorPane getReclamationAdminView()
    {
        if(reclamationAdminView == null)
        {
            try{
                reclamationAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/ReclamationAdmin.fxml")).load();
            }catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return reclamationAdminView;
    }

    public AnchorPane getEvenementAdminView()
    {
        if(evenementAdminView == null)
        {
            try{
                evenementAdminView = new FXMLLoader(getClass().getResource("/Fxml/Admin/EvenementAdmin.fxml")).load();
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


