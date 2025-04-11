package tn.esprit.controller;



import tn.esprit.entities.Personne;
import tn.esprit.services.ServicePersonne;
import tn.esprit.entities.Personne;
import tn.esprit.gui.gui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {



    @FXML
    public Text address_txt;

    @FXML
    public Text dob_txt;

    @FXML
    public Text email_txt;

    @FXML
    public Text last_name_txt;

    @FXML
    public Text name_txt;

    @FXML
    public Text password_txt;

    @FXML
    public Text phone_txt;

    @FXML
    public Text profile_txt;
    //public Button delete_button;

    public static Personne user = null;
    public Button update_btn;

    public TextField name_update_fld;
    public TextField lastName_update_fld;
    public TextField password_fld;
    public TextField phoneNumber_update_fld;
    public TextField adress_update_fld;
    public Text old_name_txt;
    public Text old_Lname_txt;
    public Text old_dob_txt;
    public Text old_password_txt;
    public Text old_phoneNumber_txt;
    public Text old_Adress_txt;
    public Button delete_btn;


    private ServicePersonne serviceUser = new ServicePersonne();



    public ProfileController() {
    }

    public void displayData()
    {
        profile_txt.setText(user.getFirst_Name()+" "+user.getLast_Name());
        name_txt.setText(user.getFirst_Name());
        last_name_txt.setText(user.getLast_Name());
        password_txt.setText(user.getPassword());
        email_txt.setText(user.getEmail());
        phone_txt.setText(user.getNum_tel());
        address_txt.setText(user.getAddress());

        old_password_txt.setText(user.getPassword());
        old_name_txt.setText(user.getFirst_Name());
        old_Lname_txt.setText(user.getLast_Name());
        old_Adress_txt.setText(user.getAddress());
        old_phoneNumber_txt.setText(user.getNum_tel());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        delete_btn.setOnAction(event-> onDelete());
        update_btn.setOnAction(event-> {
            try {
                onUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        // Field : false
        setUpdateFieldsVisibility(false);

    }

    public void onDelete() {
        Stage stage = (Stage) delete_btn.getScene().getWindow();


        try {
            serviceUser.supprimer(user);
            user=null;
            gui.getInstance().getViewFactory().closeStage(stage);
            gui.getInstance().getViewFactory().showLoginWindow();



        } catch (SQLException e) {
            System.out.println("Error occurred while deleting user: " + e.getMessage());
        }
    }

    public void onUpdate() throws SQLException {
        if(last_name_txt.isVisible()) {
            //Text : False
            setTextVisibility(false);
            //Field : True
            setUpdateFieldsVisibility(true);
        }
        else{
            // Text : True
            setTextVisibility(true);
            // Field : False
            setUpdateFieldsVisibility(false);
        }
        String newName = name_update_fld.getText().isEmpty() ? user.getFirst_Name() : name_update_fld.getText();
        String newLastName = lastName_update_fld.getText().isEmpty() ? user.getLast_Name() : lastName_update_fld.getText();
        String newPassword = password_fld.getText().isEmpty() ? user.getPassword() : password_fld.getText();
        String newPhoneNumber = phoneNumber_update_fld.getText().isEmpty() ? user.getNum_tel() : phoneNumber_update_fld.getText();
        String newAdress = adress_update_fld.getText().isEmpty() ? user.getAddress() : adress_update_fld.getText();

        Personne updatedUser = new Personne(user.getEmail(), user.getRoles(), newPassword, user.getCin(), newName, newLastName,
                newPhoneNumber, newAdress,  null, 0);
        serviceUser.modifier(updatedUser);
        user = updatedUser;

        displayData();
    }

    private void setTextVisibility(boolean b) {
        last_name_txt.setVisible(b);
        profile_txt.setVisible(b);
        phone_txt.setVisible(b);
        password_txt.setVisible(b);
        name_txt.setVisible(b);
        email_txt.setVisible(b);
        // dob_txt.setVisible(b);
        address_txt.setVisible(b);
    }

    private void setUpdateFieldsVisibility(boolean visible) {
        name_update_fld.setVisible(visible);
        lastName_update_fld.setVisible(visible);
        password_fld.setVisible(visible);
        phoneNumber_update_fld.setVisible(visible);
        adress_update_fld.setVisible(visible);

        old_Adress_txt.setVisible(visible);
        //  old_dob_txt.setVisible(visible);
        old_Lname_txt.setVisible(visible);
        old_phoneNumber_txt.setVisible(visible);
        old_name_txt.setVisible(visible);
        old_password_txt.setVisible(visible);
    }

}
