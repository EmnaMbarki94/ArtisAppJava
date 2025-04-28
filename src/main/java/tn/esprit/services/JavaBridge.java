package tn.esprit.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import tn.esprit.controller.users.magasins.HistoriqueCommandesController;

public class JavaBridge {
    private static final Logger LOGGER = Logger.getLogger(JavaBridge.class.getName());

    private final Stage stage;
    private final NavigationHandler navigationHandler;

    public interface NavigationHandler {
        void retourALAccueil();
        void consulterHistorique();
    }

    public JavaBridge(Stage stage, NavigationHandler handler) {
        this.stage = stage;
        this.navigationHandler = handler;
    }

    public void onPaymentSuccess(String paymentId) {
        Platform.runLater(() -> {
            try {
                if (stage != null) {
                    stage.close();
                }
                showPaymentSuccessAlert(paymentId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error handling payment success", e);
                showAlert("Erreur", "Une erreur est survenue lors du traitement du paiement.", Alert.AlertType.ERROR);
            }
        });
    }

    public void onPaymentError(String error) {
        Platform.runLater(() -> {
            try {
                if (stage != null) {
                    stage.close();
                }
                showAlert("Erreur de Paiement", error, Alert.AlertType.ERROR);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error handling payment error", e);
            }
        });
    }

    private void showPaymentSuccessAlert(String paymentId) {
        try {
            if (!Platform.isFxApplicationThread()) {
                Platform.runLater(() -> showPaymentSuccessAlert(paymentId));
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paiement réussi");
            alert.setHeaderText(null);
            alert.setContentText("Votre paiement a été traité avec succès !");

            ButtonType accueilButton = new ButtonType("Retour à l'accueil");
            ButtonType historiqueButton = new ButtonType("Voir mes commandes");

            alert.getButtonTypes().setAll(accueilButton, historiqueButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (navigationHandler == null) {
                    LOGGER.warning("NavigationHandler is null, cannot handle button click");
                    return;
                }

                if (result.get() == accueilButton) {
                    navigationHandler.retourALAccueil();
                } else if (result.get() == historiqueButton) {
                    navigationHandler.consulterHistorique();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing payment success alert: " + e.getMessage(), e);
            showSimpleAlert("Paiement réussi", "Votre paiement a été traité avec succès !");
        }
    }

    private void showSimpleAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.show();
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        try {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error showing alert", e);
        }
    }

}