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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tn.esprit.controller.reservation.ReservationController;
import tn.esprit.entities.Event;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Session;
import tn.esprit.utils.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EvenementController implements Initializable {

    private Personne user= Session.getUser();
    @FXML
    private GridPane gridPaneEvenements;

    @FXML
    private Button addEventButton;

    @FXML
    private AnchorPane users_parent;

    private final ObservableList<Event> evenementList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (addEventButton != null) {
            addEventButton.setOnAction(event -> openAddEventForm());
        }
        chargerEvenements();
    }

    @FXML
    private void openAddEventForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/admin/AjouterEvenement.fxml"));
            AnchorPane ajoutView = loader.load();
            users_parent.getChildren().setAll(ajoutView);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de AjouterEvenement.fxml : " + e.getMessage());
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

            int row = 0;
            int column = 0;
            int eventsPerRow = 3;

            gridPaneEvenements.setHgap(10);
            gridPaneEvenements.setVgap(10);

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

                HBox eventBox = createEventBox(e);
                gridPaneEvenements.add(eventBox, column, row);

                column++;
                if (column >= eventsPerRow) {
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createEventBox(Event e) {
        Text text = new Text("🎫 " + e.getNom() +
                "\n📅 Date: " + e.getDate_e() +
                "\n📝 Type: " + e.getType_e() +
                "\nℹ️ Infos: " + e.getInfo_e() +
                "\n🎟️ Tickets: " + e.getNb_ticket() +
                "\n💰 Prix S: " + e.getPrix_s() + " DT | VIP: " + e.getPrix_vip() + " DT");
        text.setWrappingWidth(300);
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

        // Bouton Réserver
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #7f56d9; -fx-text-fill: white;");
        reserverButton.setOnAction(evt -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Events/user/reservation.fxml"));
                AnchorPane reserverPane = loader.load();

                ReservationController controller = loader.getController();
                controller.setEventDetails(e); // Passe l’événement sélectionné
                //ReservationController.user = user; // ✅ Injecte l’utilisateur statique

                users_parent.getChildren().setAll(reserverPane);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        VBox textAndButton = new VBox(10, text, reserverButton);
        HBox container = new HBox(20, imageView, textAndButton);
        container.setStyle("-fx-padding: 10; -fx-background-color: #eae6fa; -fx-background-radius: 10;");

        return container;
    }


    /*private void supprimerEvenement(Event e) {
        String query = "DELETE FROM event WHERE id = ?";

        try (Connection conn = DBConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, e.getId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                evenementList.remove(e);
                System.out.println("✅ Événement supprimé avec succès !");
                gridPaneEvenements.getChildren().clear(); // Recharger les événements
                chargerEvenements();
            } else {
                System.out.println("❌ Échec de la suppression.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }*/

}
