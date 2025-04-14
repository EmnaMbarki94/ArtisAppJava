package tn.esprit.controller.Reponse;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import tn.esprit.entities.Reclamtion;

public class RecRepController {

    @FXML
    private Button AfficherBtRp;

    @FXML
    private Label LabelRec;

    @FXML
    private Label LabelRep;

    public void setReclamationEtReponse(String reclamation, String reponse) {
        LabelRec.setText(reclamation);
        LabelRep.setText(reponse);
    }

    public void setReclamationData(Reclamtion rec, String reponse) {
    }
}
