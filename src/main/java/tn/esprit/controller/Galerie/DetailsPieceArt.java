package tn.esprit.controller.Galerie;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import tn.esprit.entities.*;
import tn.esprit.services.ServiceComment;
import tn.esprit.services.ServiceGalerie;
import tn.esprit.services.ServicePieceArt;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DetailsPieceArt {
    private Personne user = Session.getUser();
    @FXML
    private ImageView photoP;
    @FXML
    private TextField nomP;
    @FXML
    private Label detailsLabel;
    @FXML
    private TextField dateP;
    @FXML
    private TextField descP;
    private int galerieId;
    private int pieceArtId;
    private ServicePieceArt servicePieceArt;
    @FXML
    private Button suppP;
    @FXML
    private Button modP;
    private ServiceGalerie serviceGalerie;
    private ServiceComment serviceComment;
    @FXML
    private Button retour;
    @FXML
    private AnchorPane users_parent;
    @FXML
    private AnchorPane formCard;
    @FXML
    private ScrollPane commentsScrollPane;
    @FXML
    private VBox commentsVBox;
    @FXML
    private TextField comment;
    @FXML
    private Button partage;
    private String publicImgurUrl;


    public DetailsPieceArt() {
        servicePieceArt = new ServicePieceArt();
        serviceGalerie = new ServiceGalerie();
        serviceComment = new ServiceComment();
    }

    public void setPieceArtId(int pieceArtId) {
        this.pieceArtId = pieceArtId;
        loadPieceArtDetails();
    }

    public void setGalerieId(int galerieId) {
        this.galerieId = galerieId;
    }

    private void loadPieceArtDetails() {
        try {
            Piece_art pieceArt = servicePieceArt.obtenirPieceParId(pieceArtId);
            if (pieceArt != null) {
               nomP.setText(pieceArt.getNom_p());
                if (pieceArt.getDate_crea() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String formattedDate = pieceArt.getDate_crea().format(formatter);
                    dateP.setText(formattedDate);
                } else {
                    dateP.setText("Date non disponible");
                }
               photoP.setImage(new Image(pieceArt.getPhoto_p()));
               descP.setText(pieceArt.getDesc_p());

                URI uri = URI.create(pieceArt.getPhoto_p());
                Path path = Paths.get(uri);
                String localPath = path.toString();

                String publicUrl = uploadImageToImgur(localPath);

                if (publicUrl != null) {
                   System.out.println("Lien public Imgur: " + publicUrl);
                   this.publicImgurUrl = publicUrl;
                }

                Integer currentUserId = user.getId();
                int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);

                System.out.println("Galerie ID: " + galerieId);

                Integer ownerUserId = null;
                Galerie galerie = serviceGalerie.obtenirGalerieParId(galerieId);

                if (galerie != null) {
                    ownerUserId = galerie.getUser();
                }
                if (currentUserId != null && currentUserId.equals(ownerUserId)) {
                    modP.setVisible(true);
                    suppP.setVisible(true);
                } else {
                    modP.setVisible(false);
                    suppP.setVisible(false);
                }
            }
            loadComments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadComments() throws SQLException {
        List<Comment> comments = serviceComment.afficherCommentaires(pieceArtId);
        System.out.println("Number of comments: " + comments.size());

        commentsVBox.getChildren().clear();
        int currentUserId = user.getId();

        for (Comment comment : comments) {
            VBox commentBox = new VBox();
            commentBox.setSpacing(10);  // Espacement vertical entre les éléments
            commentBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 15; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, #aaaaaa, 5, 0.5, 0, 2);");

            // Top row (haut de chaque commentaire) avec le nom et la date sur la même ligne
            HBox topRow = new HBox();
            topRow.setSpacing(10);
            topRow.setAlignment(Pos.CENTER_LEFT); // Aligné à gauche

            Label userLabel = new Label(comment.getUser().getFirst_Name() + " " + comment.getUser().getLast_Name());
            userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6a0dad;");

            Label dateLabel = new Label("📅 " + comment.getCreation_date().toLocalDate().toString());
            dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e44ad;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS); // Pousse la date vers la droite

            topRow.getChildren().addAll(userLabel, spacer, dateLabel);

            // Si l'utilisateur est le propriétaire du commentaire, afficher le bouton de suppression
            if (comment.getUser().getId() == currentUserId) {
                ImageView trashIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/corbeille.png"))));
                trashIcon.setFitWidth(16);
                trashIcon.setFitHeight(16);

                Button deleteButton = new Button("", trashIcon);
                deleteButton.setStyle("-fx-background-color: transparent;");

                // Effet de survol
                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #f0f8ff;"));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: transparent;"));

                // Action de suppression
                deleteButton.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Supprimer le commentaire");
                    confirm.setHeaderText("Confirmer la suppression");
                    confirm.setContentText("Voulez-vous vraiment supprimer ce commentaire ?");
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            serviceComment.supprimer(comment.getId());
                            loadComments();  // Recharge les commentaires après suppression
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                // Ajouter le bouton corbeille à la ligne du haut
                topRow.getChildren().add(deleteButton);
            }

            // Contenu du commentaire
            Label contentLabel = new Label(comment.getContent());
            contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-line-spacing: 4px;");  // Taille de police plus grande et espacement de ligne

            // Bouton de like/dislike
            ImageView unfilledHeart = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/unfilled_heart.png"))));
            ImageView filledHeart = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/photos_galeries/filled_heart.png"))));
            unfilledHeart.setFitWidth(20);
            unfilledHeart.setFitHeight(20);
            filledHeart.setFitWidth(20);
            filledHeart.setFitHeight(20);

            boolean[] isLiked = {serviceComment.hasUserLikedComment(currentUserId, comment.getId())};

            // HBox pour le bouton de like et le nombre de likes
            HBox likeBox = new HBox();
            likeBox.setSpacing(5);  // Plus d'espace entre le bouton et le nombre de likes
            likeBox.setAlignment(Pos.CENTER_LEFT);

            Button likeButton = new Button("", isLiked[0] ? filledHeart : unfilledHeart);
            likeButton.setStyle("-fx-background-color: transparent;");

            // Label pour afficher le nombre de likes
            Label likeCountLabel = new Label(" Liked by: " + comment.getLikes());
            likeCountLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8e44ad;");

            likeBox.getChildren().addAll(likeButton, likeCountLabel);

            // Action sur le bouton de like
            likeButton.setOnAction(event -> {
                try {
                    if (!isLiked[0]) {
                        serviceComment.likeComment(currentUserId, comment.getId());
                        comment.setLikes(comment.getLikes() + 1);
                        likeButton.setGraphic(filledHeart);
                        isLiked[0] = true;
                    } else {
                        serviceComment.unlikeComment(currentUserId, comment.getId());
                        comment.setLikes(comment.getLikes() - 1);
                        likeButton.setGraphic(unfilledHeart);
                        isLiked[0] = false;
                    }
                    // Mise à jour dynamique du nombre de likes
                    likeCountLabel.setText(" Liked by: " + comment.getLikes());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            // Conteneur des éléments du commentaire
            VBox contentBox = new VBox();
            contentBox.setSpacing(10);
            contentBox.getChildren().addAll(contentLabel, likeBox);

            commentBox.getChildren().addAll(topRow, contentBox);

            commentsVBox.getChildren().add(commentBox);
        }
    }



    @FXML
    public void supprimerPiece(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette pièce d'art ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ServicePieceArt servicePieceArt = new ServicePieceArt();
                int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);
                servicePieceArt.supprimer(pieceArtId);
                System.out.println("Pièce d'art supprimée avec succès!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
                Parent root = loader.load();

                DetailsGalerie detailsGalerieController = loader.getController();
                detailsGalerieController.setGalerieId(galerieId);

                users_parent.getChildren().clear();
                users_parent.getChildren().add(root);

                Stage stage = (Stage) users_parent.getScene().getWindow();
                stage.setTitle("Détails de la Galerie");
                FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Erreur lors de la suppression");
                errorAlert.setContentText("Une erreur est survenue lors de la suppression de la pièce d'art.");
                errorAlert.showAndWait();
            }
        } else {
            System.out.println("La suppression a été annulée.");
        }
    }

    @FXML
    public void modifierPiece(ActionEvent actionEvent) {
        try {
            // Charger la vue pour modifier la pièce d'art
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/ModifierPieceArt.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la nouvelle fenêtre
            ModifierPieceArt modifierPieceArtController = loader.getController();

            // Passer l'ID de la pièce d'art et éventuellement les détails de la pièce d'art
            modifierPieceArtController.loadPieceArt(servicePieceArt.obtenirPieceParId(pieceArtId));

            // Afficher la nouvelle scène
            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur lors de la modification");
            errorAlert.setContentText("Une erreur est survenue lors de l'ouverture de la page de modification.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void retourVersDetailsG(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Galerie/DetailsGalerie.fxml"));
            Parent root = loader.load();

            DetailsGalerie detailsGalerieController = loader.getController();
            int galerieId = servicePieceArt.obtenirGalerieIdParPieceArtId(pieceArtId);
            detailsGalerieController.setGalerieId(galerieId);

            users_parent.getChildren().clear();
            users_parent.getChildren().add(root);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root); // 500 ms = 0.5s
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            //Stage stage = (Stage) retour.getScene().getWindow(); // Récupérer la fenêtre actuelle
            //stage.setTitle("Liste des Galeries");
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur");
            errorAlert.setContentText("Une erreur est survenue lors de la tentative de retour.");
            errorAlert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void ajouterComment(ActionEvent actionEvent) {
        String commentContent = comment.getText();

        if (commentContent == null || commentContent.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Commentaire vide");
            alert.setContentText("Veuillez entrer un commentaire avant de soumettre.");
            alert.showAndWait();
            return;
        }

        try {
            Comment newComment = new Comment();
            Piece_art pieceArt = servicePieceArt.obtenirPieceParId(pieceArtId);
            newComment.setPieceArt(pieceArt);
            newComment.setContent(commentContent);
            newComment.setCreation_date(LocalDateTime.now());
            newComment.setUser(user);
            newComment.setLikes(0);

            serviceComment.ajouter(newComment);

            comment.clear();
            loadComments();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Erreur lors de l'ajout du commentaire");
            errorAlert.setContentText("Une erreur est survenue lors de l'ajout du commentaire.");
            errorAlert.showAndWait();
        }
    }

    private String uploadImageToImgur(String imagePath) {
        String clientId = "ecaa6e50cec2f13";
        String url = "https://api.imgur.com/3/image";
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            // Lire l'image et l'encoder en Base64
            File file = new File(imagePath);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String encodedImage = Base64.getEncoder().encodeToString(fileContent);

            // Connexion HTTP vers Imgur
            URL obj = new URL(url);
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Client-ID " + clientId);
            conn.setDoOutput(true);

            String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encodedImage, "UTF-8");

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();

            // Lire la réponse
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Extraire le lien de l'image
            JSONObject json = new JSONObject(response.toString());
            return json.getJSONObject("data").getString("link");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
            if (conn != null) conn.disconnect();
        }
    }


    @FXML
    public void Partager(ActionEvent actionEvent) {
        if (publicImgurUrl != null) {
            try {
                String facebookShareUrl = "https://www.facebook.com/sharer/sharer.php?u=" + URLEncoder.encode(publicImgurUrl, "UTF-8");
                Desktop.getDesktop().browse(new URI(facebookShareUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
