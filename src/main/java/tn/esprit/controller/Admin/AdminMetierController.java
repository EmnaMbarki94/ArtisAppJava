package tn.esprit.controller.Admin;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import tn.esprit.gui.gui;

public class AdminMetierController {
    public Button addCrud_btn;
    public Button statsCrud_btn;

    public void handleEditfile(ActionEvent actionEvent)
    {

        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("editfile");
    }

    public void handleExportfile(ActionEvent actionEvent)
    {

        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("exportfile");
    }

    public void handleAddUser(ActionEvent actionEvent) {

        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("AddUser");
    }

    public void handleStats(ActionEvent actionEvent) {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("stat");
    }
}
