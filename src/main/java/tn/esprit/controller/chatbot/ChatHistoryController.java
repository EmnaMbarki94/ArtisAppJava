package tn.esprit.controller.chatbot;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.util.List;

public class ChatHistoryController {
    @FXML private ListView<String> historyListView;
    @FXML private Button closeButton;

    public void setHistory(List<String> history) {
        historyListView.getItems().addAll(history);
    }

    @FXML
    private void handleClose(ActionEvent event) {
        ((Stage) closeButton.getScene().getWindow()).close();
    }
}