package tn.esprit.controller.evenement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminEvenementController implements Initializable {

    public static Personne user;
    @FXML
    private ListView<Event> listViewEvenements;

    private final ObservableList<Event> evenementList = FXCollections.observableArrayList();
    @FXML
    private Button addEventButton;
    @FXML
    private AnchorPane contenuPane;
    @FXML
    private Button showGridViewButton;
    @FXML
    private Button showReservationsButton;
    @FXML
    private StackPane mainContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialize() appelé");

        if (addEventButton != null) {
            System.out.println("addEventButton n'est pas null !");
            addEventButton.setOnAction(event -> openAddEventForm());
        } else {
            System.out.println("⚠️ addEventButton est null !");
        }
        //showGridViewButton.setOnAction(event -> openGridView());
        showReservationsButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AfficherReservationA.fxml"));
                AnchorPane reservationView = loader.load();
                mainContainer.getChildren().setAll(reservationView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        chargerEvenements();
    }
    /*
    private void openGridView() {
        try {
            // Charger la vue de la grille des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/Evenement.fxml"));
            AnchorPane gridView = loader.load();

            // Injecter le contenu dans le contenuPane
            contenuPane.getChildren().setAll(gridView);

            // Si tu veux, tu peux aussi passer des données au contrôleur de la grille


        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    @FXML
    private void openAddEventForm() {
        System.out.println("Tentative d'ouverture de la page AjouterEvenement");
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/AjouterEvenement.fxml"));

        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AjouterEvenement.fxml"));

            // Vérifier si la ressource FXML est introuvable
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Fichier FXML introuvable à l'emplacement spécifié.");
                return;  // Sortir de la méthode si le fichier est introuvable
            }

            AnchorPane ajoutView = loader.load();
            contenuPane.getChildren().setAll(ajoutView);



        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la fenêtre AjouterEvenement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void chargerEvenements() {
        String query = "SELECT * FROM event";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Récupère la connexion (ne la ferme pas ici)
            conn = DBConnection.getInstance().getCnx();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            // Charger les événements depuis la base de données
            while (rs.next()) {
                Event e = new Event();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType_e(rs.getString("type_e"));
                e.setInfo_e(rs.getString("info_e"));
                e.setDate_e(rs.getDate("date_e").toLocalDate());
                e.setPhoto_e(rs.getString("photo_e"));
                e.setPrix_s(rs.getInt("prix_s"));
                e.setPrix_vip(rs.getInt("prix_vip"));
                e.setNb_ticket(rs.getInt("nb_ticket"));

                evenementList.add(e);
            }

            listViewEvenements.setItems(evenementList);

            listViewEvenements.setCellFactory(param -> new ListCell<Event>() {
                @Override
                protected void updateItem(Event e, boolean empty) {
                    super.updateItem(e, empty);
                    if (empty || e == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Text text = new Text("🎫 " + e.getNom() +
                                "\n📅 Date: " + e.getDate_e() +
                                "\n📝 Type: " + e.getType_e() +
                                "\nℹ️ Infos: " + e.getInfo_e() +
                                "\n🎟️ Tickets: " + e.getNb_ticket() +
                                "\n💰 Prix S: " + e.getPrix_s() + " DT | VIP: " + e.getPrix_vip() + " DT");
                        text.setWrappingWidth(600);
                        text.setStyle("-fx-font-size: 14px; -fx-fill: #4B0082;");
                        ImageView imageView = new ImageView();
                        try {
                            String imagePath = "/imagesEvent/" + e.getPhoto_e();
                            System.out.println("🖼️ Chargement image : " + imagePath);
                            Image image = new Image(getClass().getResourceAsStream(imagePath));
                            imageView.setImage(image);
                        } catch (Exception ex) {
                            System.out.println("⚠️ Image non trouvée, chargement image par défaut");
                            Image defaultImage = new Image(getClass().getResourceAsStream("/imagesEvent/default.png"));
                            imageView.setImage(defaultImage);
                        }

                        imageView.setFitWidth(120);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);

                        Button editButton = new Button("✏️");
                        editButton.setStyle("""
                        -fx-background-color: transparent;
                        -fx-cursor: hand;
                        -fx-font-size: 25px;
                        -fx-text-fill: #800080;
                    """);
                        editButton.setOnAction(event -> {
                            try {
                                System.out.println("🔧 Ouverture du formulaire de modification pour: " + e.getNom());

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/modifierEvenements.fxml"));
                                AnchorPane form = loader.load();

                                System.out.println("✅ FXML chargé avec succès");

                                modifierEvenementController controller = loader.getController();
                                controller.setEvenementModifie(e);

                                contenuPane.getChildren().setAll(form);
                                System.out.println("✅ Formulaire injecté dans contenuPane");

                            } catch (IOException ex) {
                                System.out.println("❌ Erreur de chargement du formulaire:");
                                ex.printStackTrace();
                            }
                        });



                        Button deleteButton = new Button("🗑️");
                        deleteButton.setStyle("""
                        -fx-background-color: transparent;
                        -fx-cursor: hand;
                        -fx-font-size: 25px;
                        -fx-text-fill: #800080;
                    """);
                        deleteButton.setOnAction(event -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation de suppression");
                            alert.setHeaderText("Supprimer l'événement ?");
                            alert.setContentText("Voulez-vous vraiment supprimer \"" + e.getNom() + "\" ?");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    supprimerEvenement(e);
                                }
                            });
                        });


                        HBox actionBox = new HBox(5, editButton, deleteButton);
                        actionBox.setStyle("-fx-alignment: top-right;");
                        HBox container = new HBox(20, text, actionBox);
                        container.setStyle("-fx-padding: 10; -fx-background-color: #eae6fa; -fx-background-radius: 10;");
                        setGraphic(container);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermer uniquement ResultSet et Statement si besoin
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void supprimerEvenement(Event e) {
        String query = "DELETE FROM event WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, e.getId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                evenementList.remove(e); // Supprimer de la liste observable
                System.out.println("✅ Événement supprimé avec succès !");
            } else {
                System.out.println("❌ Échec de la suppression.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
