package tn.esprit.controller.Admin;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tn.esprit.entities.Comment;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceComment;
import tn.esprit.services.ServicePieceArt;
import tn.esprit.entities.Piece_art;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class CommentsA {

    @FXML
    private VBox commentsBox;  // Ce VBox est dans ton FXML ScrollPane

    private final ServiceComment serviceComment = new ServiceComment();
    private final ServicePieceArt servicePieceArt = new ServicePieceArt();
    @FXML
    private AnchorPane comments_parent;
    @FXML
    private Button btnRetour;

    @FXML
    public void initialize() {
        try {
            List<Comment> commentaires = serviceComment.afficher();
            populateCommentaires(commentaires);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateCommentaires(List<Comment> commentaires) {
        commentsBox.getChildren().clear();

        for (Comment comment : commentaires) {
            HBox card = new HBox();
            card.setSpacing(10);
            card.setPadding(new Insets(20));
            card.setBackground(new Background(new BackgroundFill(Color.web("#f8f4fc"), new CornerRadii(15), Insets.EMPTY)));
            card.setStyle("-fx-effect: dropshadow(gaussian, rgba(155,89,182,0.2), 12, 0, 0, 6); -fx-cursor: hand;");
            card.setPrefWidth(1050);

            VBox textContainer = new VBox();
            textContainer.setSpacing(8);

            // Nom de l'utilisateur
            Label userLabel = new Label(comment.getUser().getFirst_Name() + " " + comment.getUser().getLast_Name());
            userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a0dad;");

            // Contenu du commentaire
            Label contentLabel = new Label("\"" + comment.getContent() + "\"");
            contentLabel.setWrapText(true);
            contentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #5e5e5e;");

            // Infos supplémentaires en bas
            HBox bottomInfo = new HBox();
            bottomInfo.setSpacing(30);

            Label dateLabel = new Label("📅 " + comment.getCreation_date().toLocalDate().toString());
            dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e44ad;");

            Label likesLabel = new Label("💜 " + comment.getLikes() + " likes");
            likesLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e44ad;");

            bottomInfo.getChildren().addAll(dateLabel, likesLabel);

            textContainer.getChildren().addAll(userLabel, contentLabel, bottomInfo);

            // Bouton supprimer (avec icône)
            Button deleteButton = new Button();
            deleteButton.setStyle("-fx-background-color: transparent;");
            deleteButton.setPrefSize(30, 30);

            ImageView trashIcon =new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/corbeille.png"))));
            trashIcon.setFitWidth(24);
            trashIcon.setFitHeight(24);
            deleteButton.setGraphic(trashIcon);

            // Supprimer le commentaire au clic
            deleteButton.setOnAction(event -> {
                try {
                    serviceComment.supprimer(comment.getId());
                    commentsBox.getChildren().remove(card);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            card.getChildren().addAll(textContainer, spacer, deleteButton);
            commentsBox.getChildren().add(card);

            // Animation hover
            card.setOnMouseEntered(event -> card.setStyle("-fx-background-color: #ede0f5; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(155,89,182,0.4), 14, 0, 0, 8); -fx-cursor: hand;"));
            card.setOnMouseExited(event -> card.setStyle("-fx-background-color: #f8f4fc; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(155,89,182,0.2), 12, 0, 0, 6); -fx-cursor: hand;"));
        }
    }


    @FXML
    public void retour(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ArtisteAdmin.fxml"));
            Parent root = loader.load();

            comments_parent.getChildren().clear();
            comments_parent.getChildren().add(root);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), root);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.play();

        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }
}
