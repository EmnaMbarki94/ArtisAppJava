package tn.esprit.controller.chatbot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import tn.esprit.entities.Session;
import tn.esprit.services.GeminiService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatbotController implements Initializable {
    @FXML private TextField messageField;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messagesContainer;
    @FXML private Button sendButton;
    @FXML private Button historyButton;
    @FXML private Button clearButton;

    private GeminiService geminiService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        geminiService = new GeminiService();

        sendButton.setOnAction(event -> sendMessage());
        messageField.setOnAction(event -> sendMessage());
        historyButton.setOnAction(event -> showChatHistory());
        clearButton.setOnAction(event -> clearChat());

        showWelcomeMessage();
    }

//    private void showWelcomeMessage() {
//        addMessage("AI", "Bonjour! Je suis ArtisBot, votre assistant polyvalent.\n\n"
//                + "Je peux vous aider avec:\n"
//                + "- ArtisApp (galerie/cours/evenement/...)\n"
//                + "- Questions générales\n"
//                + "- Aide technique\n\n"
//                + "Comment puis-je vous aider?");
//    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            addMessage("User", message);
            messageField.clear();

            showTypingIndicator();

            new Thread(() -> {
                String response = geminiService.sendMessage(message);

                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addMessage("AI", response);
                });
            }).start();
        }
    }

//    private void showTypingIndicator() {
//        HBox typingBox = new HBox(10);
//        typingBox.setAlignment(Pos.CENTER_LEFT);
//        typingBox.setPadding(new Insets(5, 10, 5, 10));
//        typingBox.setId("typing-indicator");
//
//        ProgressIndicator progress = new ProgressIndicator();
//        progress.setPrefSize(20, 20);
//
//        TextFlow textFlow = new TextFlow(new Text("ArtisBot réfléchit..."));
//        textFlow.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");
//        textFlow.setPadding(new Insets(5, 10, 5, 10));
//
//        typingBox.getChildren().addAll(progress, textFlow);
//        messagesContainer.getChildren().add(typingBox);
//        scrollToBottom();
//    }

    private void removeTypingIndicator() {
        messagesContainer.getChildren().removeIf(node ->
                node.getId() != null && node.getId().equals("typing-indicator"));
    }

//    private void addMessage(String sender, String message) {
//        HBox messageContainer = new HBox();
//        messageContainer.setPadding(new Insets(5, 10, 5, 10));
//
//        Text text = new Text(message);
//        TextFlow textFlow = new TextFlow(text);
//        textFlow.setPadding(new Insets(5, 10, 5, 10));
//
//        if (sender.equals("User")) {
//            messageContainer.setAlignment(Pos.CENTER_RIGHT);
//            textFlow.setStyle("-fx-background-color: #dbb3f5; -fx-background-radius: 10;");
//            text.setFill(Color.BLACK);
//        } else {
//            messageContainer.setAlignment(Pos.CENTER_LEFT);
//            textFlow.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;");
//            text.setFill(Color.BLACK);
//        }
//
//        messageContainer.getChildren().add(textFlow);
//        messagesContainer.getChildren().add(messageContainer);
//        scrollToBottom();
//    }

    private void scrollToBottom() {
        scrollPane.setVvalue(1.0);
    }

    private void showChatHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/chatbot/ChatHistory.fxml"));
            Parent root = loader.load();

            ChatHistoryController controller = loader.getController();
            controller.setHistory(geminiService.getChatHistory());

            Stage stage = new Stage();
            stage.setTitle("Historique - " + Session.getUser().getFirst_Name());
            stage.setScene(new Scene(root, 500, 700));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir l'historique");
        }
    }

    private void clearChat() {
        geminiService.clearChatHistory();
        messagesContainer.getChildren().clear();
        showWelcomeMessage();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void addMessage(String sender, String message) {
        HBox messageContainer = new HBox();
        messageContainer.setPadding(new Insets(5, 10, 5, 10));
        messageContainer.setAlignment(sender.equals("User") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPadding(new Insets(8, 12, 8, 12));
        textFlow.setMaxWidth(300); // Limite la largeur des bulles

        if (sender.equals("User")) {
            textFlow.setStyle("-fx-background-color: #dbb3f5; -fx-background-radius: 15 15 0 15;");
            text.setFill(Color.BLACK);
        } else {
            textFlow.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15 15 15 0;");
            text.setFill(Color.BLACK);
        }

        // Ajout d'un label pour le nom de l'expéditeur et l'heure
        VBox messageWithMeta = new VBox();
        Label senderLabel = new Label(sender.equals("User") ? "Vous" : "ArtisBot");
        senderLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        Label timeLabel = new Label(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        HBox metaBox = new HBox(5, senderLabel, timeLabel);
        metaBox.setAlignment(Pos.CENTER_LEFT);

        messageWithMeta.getChildren().addAll(metaBox, textFlow);
        messageWithMeta.setSpacing(2);

        messageContainer.getChildren().add(messageWithMeta);
        messagesContainer.getChildren().add(messageContainer);
        scrollToBottom();
    }

    private void showWelcomeMessage() {
        VBox welcomeBox = new VBox(10);
        welcomeBox.setPadding(new Insets(10));
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        welcomeBox.setStyle("-fx-background-color: #f8f8f8; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-border-width: 1;");

        Label title = new Label("ArtisBot");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Text welcomeText = new Text("Bonjour! Je suis ArtisBot, votre assistant polyvalent.\n\n"
                + "Je peux vous aider avec:\n"
                + "- ArtisApp (galerie/cours/evenement/...)\n"
                + "- Questions générales\n"
                + "- Aide technique");

        Label timeLabel = new Label(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        welcomeBox.getChildren().addAll(title, welcomeText, timeLabel);

        HBox container = new HBox(welcomeBox);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));

        messagesContainer.getChildren().add(container);
    }

    private void showTypingIndicator() {
        HBox typingBox = new HBox(10);
        typingBox.setAlignment(Pos.CENTER_LEFT);
        typingBox.setPadding(new Insets(5, 10, 5, 10));
        typingBox.setId("typing-indicator");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(15, 15);
        progress.setStyle("-fx-progress-color: #666;");

        TextFlow textFlow = new TextFlow(new Text("ArtisBot réfléchit..."));
        textFlow.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15 15 15 0;");
        textFlow.setPadding(new Insets(8, 12, 8, 12));

        typingBox.getChildren().addAll(progress, textFlow);
        messagesContainer.getChildren().add(typingBox);
        scrollToBottom();
    }

}