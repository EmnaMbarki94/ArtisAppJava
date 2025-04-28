package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.gui.gui;
import tn.esprit.utils.DBConnection;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.util.Properties;

public class ForgotPassword {

    public Button retour_btn;
    @FXML private TextField cinField;
    @FXML private TextField emailField;
    @FXML private Button sendButton;
    @FXML private Label messageLabel;

    private String cinGlobal;

    @FXML
    public void handleVerifyCin() {
        String cin = cinField.getText().trim();
        System.out.println("CIN saisi : " + cin);
        if (cin.isEmpty()) {
            messageLabel.setText("CIN requis.");
            return;
        }

        String query = "SELECT * FROM user WHERE cin = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getCnx().prepareStatement(query)) {
            stmt.setString(1, cin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cinGlobal = cin;
                emailField.setVisible(true);
                sendButton.setVisible(true);
                messageLabel.setText("CIN trouvé. Entrez votre email.");
            } else {
                messageLabel.setText("CIN introuvable.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur base de données.");
        }
    }

    @FXML
    public void handleSendPassword() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            messageLabel.setText("Email requis.");
            return;
        }

        String newPassword = generateFixedPassword(); // ici "0000"
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        System.out.println("Hashed password: " + hashedPassword);


        System.out.println("CIN global: " + cinGlobal);

        String updateQuery = "UPDATE user SET password = ? WHERE cin = ?";

        try (PreparedStatement stmt = DBConnection.getInstance().getCnx().prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, cinGlobal);
            int rows = stmt.executeUpdate();

            if (rows > 0 && sendPasswordResetEmail(email, newPassword)) {
                messageLabel.setText("Mot de passe envoyé à votre email.");
            } else {
                messageLabel.setText("Échec de l’envoi ou de la mise à jour.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur base de données.");
        }
    }


    private String generateFixedPassword() {
        return "0000";
    }

    private boolean sendPasswordResetEmail(String to, String newPassword) {
        String from = "bob2985476@gmail.com";
        String pass = "mtdf bcaa sqxg zwbj "; // Ton mot de passe ou un mot de passe d'application gmail
        String subject = "Réinitialisation de votre mot de passe";
        String body = "Votre nouveau mot de passe est : " + newPassword;

        // Configuration SMTP pour Outlook
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Créer la session pour l'envoi d'email
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        try {
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Envoyer l'email
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void handleReturnToLogin(ActionEvent actionEvent)
    {
        Stage stage = (Stage) retour_btn.getScene().getWindow();
        gui.getInstance().getViewFactory().closeStage(stage);
        gui.getInstance().getViewFactory().showLoginWindow();
    }
}

