package tn.esprit.controller.users;

import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private Personne user = Session.getUser();
    @javafx.fxml.FXML
    private Region spacer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
