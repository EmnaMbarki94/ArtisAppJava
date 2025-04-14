package tn.esprit.controller.Reclamation;

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

    // Méthode pour définir les données de la réclamation et de la réponse
    public void setReclamationData(Reclamtion rec, String reponse) {
        LabelRec.setText("Réclamation: " + rec.getDesc_r());
//        LabelRep.setText("Réponse: " + reponse);
        LabelRep.setText(reponse);
    }
}

