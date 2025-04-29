package tn.esprit.controller.users.magasins;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.esprit.entities.*;
import tn.esprit.services.JavaBridge;
import tn.esprit.services.ServiceCommande;
import tn.esprit.services.StripeService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class CommandeController implements Initializable, JavaBridge.NavigationHandler {

    private Personne user = Session.getUser();
    @FXML private Label nomUtilisateurLabel;
    @FXML private Label dateLabel;
    @FXML private TextField adresseField;
    @FXML private Label confirmationLabel;
    @FXML private WebView mapView;
    @FXML private VBox articlesContainer;
    @FXML private Label totalLabel;
    @FXML private AnchorPane contenuPane;

    private WebEngine webEngine;
    private TableView <Commande> commandesTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nomUtilisateurLabel.setText(user.getFirst_Name() + " " + user.getLast_Name());
        dateLabel.setText(LocalDate.now().toString());

        if (articlesContainer != null && totalLabel != null) {
            afficherArticlesPanier();
        } else {
            System.err.println("Erreur: Les composants FXML ne sont pas injectés correctement");
        }

        // Map initialization
        mapView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        mapView.setPrefWidth(Region.USE_COMPUTED_SIZE);
        mapView.prefHeightProperty().bind(((Region)mapView.getParent()).heightProperty().multiply(0.7));
        mapView.prefWidthProperty().bind(((Region)mapView.getParent()).widthProperty());
        webEngine = mapView.getEngine();
        configureWebEngine();
        webEngine.loadContent(getMapHtml());
    }

    private void configureWebEngine() {
        // Enable necessary permissions
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.7049.115 Safari/537.36");

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("Map loaded successfully!");
            } else if (newState == Worker.State.FAILED) {
                System.out.println("Failed to load the map.");
                webEngine.getLoadWorker().getException().printStackTrace();
            }
        });

        webEngine.setOnAlert(event -> {
            String data = event.getData();
            System.out.println("JS Alert: " + data);
            if (data.startsWith("adresse:")) {
                String address = data.substring(8);
                handleJavaScriptAddress(address);
            }
        });
    }

    private void afficherArticlesPanier() {
        try {
            articlesContainer.getChildren().clear();

            if (Panier.getItems() == null || Panier.getItems().isEmpty()) {
                Label emptyLabel = new Label("Votre panier est vide");
                emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
                articlesContainer.getChildren().add(emptyLabel);
                totalLabel.setText("0.00 DT");
                return;
            }

            for (PanierItem item : Panier.getItems()) {
                if (item == null || item.getArticle() == null) continue;

                HBox articleBox = new HBox();
                articleBox.getStyleClass().add("article-item");
                articleBox.setSpacing(15);
                articleBox.setAlignment(Pos.CENTER_LEFT);

                VBox detailsBox = new VBox(5);
                Label nameLabel = new Label(item.getArticle().getNom_a());
                nameLabel.getStyleClass().add("article-name");

                Label quantityLabel = new Label("Quantité: " + item.getQuantite());
                quantityLabel.getStyleClass().add("article-quantity");

                detailsBox.getChildren().addAll(nameLabel, quantityLabel);

                // Image de l'article
                ImageView imageView = new ImageView();
                Image image = loadProductImage(item.getArticle()); // Utilisation unique de la méthode de chargement

                imageView.setImage(image);
                imageView.getStyleClass().add("article-image");
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                System.out.println("Tentative de chargement de: " + item.getArticle().getImage_path());

                // Ajoutez l'image à la boîte de détails de l'article
                detailsBox.getChildren().add(imageView);

                Label priceLabel = new Label(String.format("%.2f DT",
                        item.getArticle().getPrix_a() * item.getQuantite()));
                priceLabel.getStyleClass().add("article-price");

                articleBox.getChildren().addAll(detailsBox, priceLabel);
                articlesContainer.getChildren().add(articleBox);
            }

            totalLabel.setText(String.format("%.2f DT", calculerTotalCommande()));

        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du panier: " + e.getMessage());
            e.printStackTrace();
            Label errorLabel = new Label("Erreur de chargement du panier");
            errorLabel.setStyle("-fx-text-fill: #ef4444;");
            articlesContainer.getChildren().add(errorLabel);
        }
    }

    private Image loadProductImage(Article article) {
        if (article == null || article.getImage_path() == null) {
            return getDefaultImage();
        }

        String imageName = article.getImage_path();
        String imagePath = "/image/article/" + imageName;

        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement: " + imagePath);
        }

        return getDefaultImage();
    }

    private Image getDefaultImage() {
        return new Image(getClass().getResourceAsStream("/image/magasin/imagedf.jpg"));
    }
    private String getMapHtml() {
        return """
    <!DOCTYPE html>
    <html lang="fr">
    <head>
        <meta charset="UTF-8">
        <title>Carte Tunisie</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <style>
            html, body {
                margin: 0;
                padding: 0;
                height: 100%;
                width: 100%;
                overflow: hidden;
                background: #f8f9fa;
            }

            #map {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                width: 100%;
                height: 100%;
            }

            img.leaflet-tile {
                image-rendering: pixelated !important;
                backface-visibility: hidden;
                will-change: transform;
            }

            .leaflet-tile-container {
                transform: translateZ(0); /* active accélération matérielle */
            }
        </style>
    </head>
    <body>
        <div id="map"></div>

        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <script>
            var map = L.map('map', {
                preferCanvas: true,
                zoomControl: true,
                attributionControl: true
            }).setView([34.0, 9.0], 6);

            var layer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap contributors',
                maxZoom: 18,
                tileSize: 256,
                detectRetina: false,    // <== évite zoom HD
                reuseTiles: true,       // <== empêche le flash gris
                keepBuffer: 16,         // <== précharge
                updateWhenIdle: true,
                updateWhenZooming: false
            }).addTo(map);

            var marker = null;

            function sendAddressToJava(address) {
                alert('adresse:' + address);
            }

            function reverseGeocode(lat, lon) {
                var url = 'https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=' + lat + '&lon=' + lon;
                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        var address = data.display_name || ("Lat: " + lat + ", Lng: " + lon);
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker([lat, lon]).addTo(map)
                            .bindPopup("<b>Adresse :</b><br>" + address)
                            .openPopup();
                        sendAddressToJava(address);
                    })
                    .catch(error => {
                        console.error('Erreur de géocodage inverse:', error);
                        var fallback = "Lat: " + lat.toFixed(5) + ", Lng: " + lon.toFixed(5);
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker([lat, lon]).addTo(map)
                            .bindPopup("<b>Coordonnées :</b><br>" + fallback)
                            .openPopup();
                        sendAddressToJava(fallback);
                    });
            }

            map.on('click', function(e) {
                var latlng = e.latlng;
                reverseGeocode(latlng.lat, latlng.lng);
            });
        </script>
    </body>
    </html>
    """;
    }






    private double calculerTotalCommande() {
        return Panier.getItems().stream()
                .mapToDouble(item -> item.getArticle().getPrix_a() * item.getQuantite())
                .sum();
    }

    @FXML
    private void confirmerCommande(ActionEvent event) {
        String adresse = adresseField.getText().trim();

        if (adresse.isEmpty()) {
            adresseField.setStyle("-fx-border-color: red;");
            showConfirmation("❌ L'adresse est obligatoire !", "red");
            return;
        }
        if (Panier.getItems().isEmpty()) {
            showConfirmation("❌ Le panier est vide !", "red");
            return;
        }

        try {
            Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Commande commande = new Commande(user, date, adresse, calculerTotalCommande());

            ServiceCommande cs = new ServiceCommande();
            int commandeId = cs.confirmerCommandeAvecStocks(commande, Panier.getItems());
//            Panier.viderPanier();
            afficherArticlesPanier();
            initierPaiementStripe();
            showConfirmation("✅ Votre commande est confirmée !", "green");



        } catch (SQLException e) {
            showConfirmation("❌ Erreur: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }

    private void handleJavaScriptAddress(String address) {
        if (address != null && !address.trim().isEmpty()) {
            Platform.runLater(() -> {
                adresseField.setText(address.trim());
                adresseField.setStyle("");
            });
        } else {
            System.err.println("L'adresse est vide ou invalide.");
        }
    }

    private void showConfirmation(String message, String color) {
        confirmationLabel.setText(message);
        confirmationLabel.setStyle("-fx-text-fill: " + color + ";");
        confirmationLabel.setVisible(true);
    }

    @FXML
    private void retournerAuPanier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/ListPanier.fxml"));
            AnchorPane panierView = loader.load();

            AnchorPane root = (AnchorPane) ((javafx.scene.Node) event.getSource()).getScene().lookup("#contenuPane");

            if (root != null) {
                root.getChildren().setAll(panierView);
            } else {
                ((javafx.scene.Node) event.getSource()).getScene().setRoot(panierView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour au panier.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Panier");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //Payement
    @FXML
    private void initierPaiementStripe() {
        try {
            // 1. Vérification des prérequis
            if (calculerTotalCommande() <= 0) {
                showAlert("Erreur", "Montant invalide", Alert.AlertType.ERROR);
                return;
            }

            // 2. Création du PaymentIntent
            StripeService stripeService = new StripeService();
            String clientSecret = stripeService.createPaymentIntent(calculerTotalCommande(), "usd");

            if (clientSecret == null) {
                showAlert("Erreur", "Échec de la création du paiement", Alert.AlertType.ERROR);
                return;
            }

            // 3. Création de la fenêtre de paiement
            Platform.runLater(() -> {
                Stage stripeStage = new Stage();
                WebView webView = new WebView();

                WebEngine engine = webView.getEngine();

                // Configuration essentielle
                engine.setJavaScriptEnabled(true);
                engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

                // Gestion des erreurs
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.FAILED) {
                        Throwable ex = engine.getLoadWorker().getException();
                        ex.printStackTrace();
                        showAlert("Erreur", "Échec du chargement: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
                double amount = calculerTotalCommande(); // Your existing amount calculation
                String stripePublicKey = "pk_test_51RIEHFQlg3WeFHrliDkhj0Ew1hNDgE04BmLwxeK6DF9EL2o0bzlCXpJ1NV1aPd03livDgfCfQt6o33qehX6Ct6iG00MMxNDxD3"; // Your Stripe public key
                // HTML optimisé pour Stripe
                String htmlContent = String.format(
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<head>" +
                                "    <meta charset=\"UTF-8\">" +
                                "    <title>Paiement</title>" +
                                "    <script src=\"https://js.stripe.com/v3/\"></script>" +
                                "    <style>" +
                                "        body { font-family: Arial, sans-serif; padding: 20px; margin: 0; }" +
                                "        #card-element { border: 1px solid #ccc; padding: 10px; border-radius: 4px; margin: 10px 0; }" +
                                "        button { background: #6772e5; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; font-size: 16px; }" +
                                "        #payment-status { margin-top: 15px; min-height: 20px; }" +
                                "    </style>" +
                                "</head>" +
                                "<body>" +
                                "    <h2>Paiement sécurisé</h2>" +
                                "    <div id=\"card-element\"></div>" +
                                "    <button id=\"submit-button\">Payer %.2f TND</button>" +
                                "    <div id=\"payment-status\"></div>" +
                                "    <script>" +
                                "        const stripe = Stripe('%s');" +
                                "        const elements = stripe.elements();" +
                                "        const cardElement = elements.create('card');" +
                                "        " +
                                "        cardElement.mount('#card-element');" +
                                "        " +
                                "        document.getElementById('submit-button').addEventListener('click', async () => {" +
                                "            const button = document.getElementById('submit-button');" +
                                "            button.disabled = true;" +
                                "            document.getElementById('payment-status').innerHTML = 'Traitement en cours...';" +
                                "            " +
                                "            try {" +
                                "                const {error, paymentIntent} = await stripe.confirmCardPayment('%s', {" +
                                "                    payment_method: { card: cardElement }" +
                                "                });" +
                                "                " +
                                "                if (error) {" +
                                "                    document.getElementById('payment-status').innerHTML = " +
                                "                        '<p style=\"color:red;\">Erreur: ' + error.message + '</p>';" +
                                "                    if (window.javaApp) {" +
                                "                        window.javaApp.onPaymentError(error.message);" +
                                "                    }" +
                                "                } else if (paymentIntent.status === 'succeeded') {" +
                                "                    document.getElementById('payment-status').innerHTML = " +
                                "                        '<p style=\"color:green;\">Paiement réussi!</p>';" +
                                "                    if (window.javaApp) {" +
                                "                        window.javaApp.onPaymentSuccess(paymentIntent.id);" +
                                "                    }" +
                                "                }" +
                                "            } catch (err) {" +
                                "                document.getElementById('payment-status').innerHTML = " +
                                "                    '<p style=\"color:red;\">Erreur inattendue: ' + err.message + '</p>';" +
                                "            } finally {" +
                                "                button.disabled = false;" +
                                "            }" +
                                "        });" +
                                "    </script>" +
                                "</body>" +
                                "</html>",
                        amount, stripePublicKey, clientSecret);

                // Bridge Java-JS
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        try {
                            JSObject window = (JSObject) engine.executeScript("window");
//                            window.setMember("javaApp", new JavaBridge(stripeStage));
                            window.setMember("javaApp", new JavaBridge(stripeStage, this));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                engine.loadContent(htmlContent);
                stripeStage.setScene(new Scene(webView, 500, 400));
                stripeStage.setTitle("Paiement par carte");
                stripeStage.show();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Initialisation impossible: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void showAlert(String title, String content, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    public void consulterHistorique() {
        try {
            URL fxmlUrl = getClass().getResource("/Fxml/users/Magasins/HistoriqueCommandes.fxml");
            if (fxmlUrl == null) {
                showAlert("Erreur", "Fichier FXML introuvable", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/HistoriqueCommandes.fxml"));
            AnchorPane historiqueView = loader.load();

            // Obtenez la scène actuelle à partir d'un composant qui existe certainement
            Scene currentScene = adresseField.getScene(); // ou mapView.getScene()

            // Méthode 1: Si vous utilisez un conteneur avec un ID "contenuPane"
            AnchorPane root = (AnchorPane) currentScene.lookup("#contenuPane");
            if (root != null) {
                root.getChildren().setAll(historiqueView);
            }
            // Méthode 2: Si vous voulez remplacer toute la scène
            else {
                Stage stage = (Stage) currentScene.getWindow();
                stage.setScene(new Scene(historiqueView));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'historique: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    public void retourALAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/Magasin.fxml"));
            AnchorPane accueilView = loader.load();

            // Trouve le conteneur principal dans votre scène actuelle
            AnchorPane root = (AnchorPane) mapView.getScene().lookup("#contenuPane");

            if (root != null) {
                root.getChildren().setAll(accueilView);
            } else {
                // Si pas trouvé, remplace la scène entière
                Stage stage = (Stage) mapView.getScene().getWindow();
                stage.setScene(new Scene(accueilView));
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à l'accueil");
        }
    }



}