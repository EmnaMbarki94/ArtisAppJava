package tn.esprit.controller.users.magasins;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Commande;
import tn.esprit.entities.LigneCommande;
import tn.esprit.entities.Session;
import tn.esprit.services.ServiceCommande;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
//PDF
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import tn.esprit.services.ServiceLigneCommande;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoriqueCommandesController implements Initializable {

    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, Integer> idColumn;
    @FXML private TableColumn<Commande, String> dateColumn;
    @FXML private TableColumn<Commande, String> adresseColumn;
    @FXML private TableColumn<Commande, Double> montantColumn;
    @FXML private TableColumn<Commande, Void> actionsColumn;

    private final ServiceCommande serviceCommande = new ServiceCommande();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Chargement des commandes pour user ID: " + Session.getUser().getId());
            List<Commande> commandes = serviceCommande.getCommandesByUser(Session.getUser().getId());
            System.out.println("Nombre de commandes récupérées: " + commandes.size());

            setupTableColumns();
            commandesTable.getItems().setAll(commandes);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'historique des commandes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        // Lier les colonnes simples
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_c"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse_c"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Formater la colonne Montant
        montantColumn.setCellFactory(col -> new TableCell<Commande, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item) + " TND");
                    setStyle("-fx-alignment: CENTER-RIGHT; -fx-text-fill: #4a148c; -fx-font-weight: bold;");
                }
            }
        });

        actionsColumn.setCellFactory(col -> new TableCell<Commande, Void>() {
            private final Button btn = new Button("Facture");

            {
                btn.setStyle("-fx-background-color: #9e6ca6; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/image/facture.png"), 16, 16, true, true)));

                // Agrandir le bouton
                btn.setPrefWidth(250);  // Ajustez la largeur selon votre besoin
                btn.setPrefHeight(20);  // Ajustez la hauteur selon votre besoin

                btn.setOnAction(event -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    imprimerFacture(commande);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

    }

    private void imprimerFacture(Commande commande) {
        try {
            ServiceLigneCommande serviceLigneCommande = new ServiceLigneCommande();
            List<LigneCommande> lignesCommande = serviceLigneCommande.afficherParCommande(commande.getId());

            // Définir le chemin où le PDF sera sauvegardé
            String fileName = "Facture_Commande_" + commande.getId() + "_" + System.currentTimeMillis() + ".pdf";
            String filePath = System.getProperty("user.home") + "/Downloads/" + fileName;

            // Créer un document avec des marges plus petites
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Définir la couleur mauve
            BaseColor mauveColor = new BaseColor(156, 39, 176); // #9c27b0
            BaseColor whiteColor = new BaseColor(255, 255, 255);

            // Police personnalisée avec couleur mauve
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, whiteColor);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, mauveColor);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);

            // Logo et en-tête de la facture
            Paragraph companyHeader = new Paragraph();
            companyHeader.add(new Chunk("ARTIS\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, mauveColor)));
            companyHeader.add(new Chunk("www.artis.com\n", smallFont));
            companyHeader.add(new Chunk(Session.getUser().getEmail() + "\n", smallFont));
            companyHeader.add(new Chunk("123-456-7890\n\n", smallFont));
            companyHeader.add(new Chunk("108 N Platinum Ave, Tunis, Tunisia\n\n", smallFont));
            companyHeader.setAlignment(Element.ALIGN_LEFT);
            document.add(companyHeader);

            // Ligne séparatrice
            document.add(new Chunk("\n"));
            LineSeparator line = new LineSeparator();
            line.setLineWidth(1f);
            document.add(line);
            document.add(new Chunk("\n"));

            // Section "Facturé à"
            Paragraph clientInfo = new Paragraph();
            clientInfo.add(new Chunk("Facturé à:\n", headerFont));
            clientInfo.add(new Chunk(Session.getUser().getFirst_Name() + " " + Session.getUser().getLast_Name() + "\n", boldFont));
            clientInfo.add(new Chunk(commande.getAdresse_c() + "\n\n", normalFont));
            clientInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(clientInfo);

            // Détails de la facture
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            PdfPCell leftCell = new PdfPCell(new Phrase("Facture #" + commande.getId(), normalFont));
            PdfPCell rightCell = new PdfPCell(new Phrase("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));

            leftCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setBorder(Rectangle.NO_BORDER);

            infoTable.addCell(leftCell);
            infoTable.addCell(rightCell);
            document.add(infoTable);

            document.add(new Chunk("\n"));

            // Tableau des articles
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{4, 2, 2, 2});

            // En-têtes du tableau avec couleur mauve
            String[] headers = {"Produits", "Prix unitaire", "Quantité", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, titleFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(mauveColor); // Fond mauve
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Remplir le tableau avec les articles
            double sousTotal = 0;
            for (LigneCommande ligne : lignesCommande) {
                String description = ligne.getArticle() != null ? ligne.getArticle().getNom_a() : "Inconnu";
                double prix = ligne.getArticle() != null ? ligne.getArticle().getPrix_a() : 0.0;
                int quantite = ligne.getQuantite();
                double totalLigne = prix * quantite;
                sousTotal += totalLigne;

                addProductToTable(table, description, prix, quantite, normalFont);
            }

            document.add(table);

            // Totaux avec couleur mauve
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10f);
            totalTable.setWidths(new float[]{3, 2});

            double fraisLivraison = 8.0;
            double total = sousTotal + fraisLivraison;

            addTotalRow(totalTable, "Sous total:", sousTotal, normalFont);
            addTotalRow(totalTable, "Frais de livraison:", fraisLivraison, normalFont);
            addTotalRow(totalTable, "TOTAL:", total, new Font(FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, mauveColor)));

            document.add(totalTable);

            document.add(new Chunk("\n"));

            // Informations de paiement
            Paragraph paymentInfo = new Paragraph();
            paymentInfo.add(new Chunk("Informations de paiement\n", headerFont));
            paymentInfo.add(new Chunk("Banque: My Bank\n", normalFont));
            paymentInfo.add(new Chunk("Titulaire: " + Session.getUser().getFirst_Name() + " " + Session.getUser().getLast_Name() + "\n", normalFont));
            paymentInfo.add(new Chunk("IBAN: TN59 0123 4567 8901 2345 6789\n\n", normalFont));
            paymentInfo.add(new Chunk("Merci pour votre confiance!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK)));
            document.add(paymentInfo);

            // Signature
            Paragraph signature = new Paragraph();
            signature.add(new Chunk("\n\n"));
            signature.add(new Chunk("Signature\n", smallFont));
            signature.add(new Chunk("_________________________\n", normalFont));
            signature.add(new Chunk(Session.getUser().getFirst_Name() + " " + Session.getUser().getLast_Name(), smallFont));
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            document.close();

            showAlert("Succès", "Facture téléchargée dans votre dossier Téléchargements.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération du PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Méthode utilitaire pour ajouter une ligne de total
    private void addTotalRow(PdfPTable table, String label, double value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("%,.2f DT", value), font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    // Méthode utilitaire pour ajouter un produit au tableau
    private void addProductToTable(PdfPTable table, String description, double prix, int quantite, Font font) {
        PdfPCell descCell = new PdfPCell(new Phrase(description, font));
        PdfPCell prixCell = new PdfPCell(new Phrase(String.format("%,.2f DT", prix), font));
        PdfPCell qteCell = new PdfPCell(new Phrase(String.valueOf(quantite), font));
        PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%,.2f DT", prix * quantite), font));

        descCell.setPadding(5);
        prixCell.setPadding(5);
        qteCell.setPadding(5);
        totalCell.setPadding(5);

        descCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        prixCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        qteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(descCell);
        table.addCell(prixCell);
        table.addCell(qteCell);
        table.addCell(totalCell);
    }


    @FXML
    private void retournerALAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/users/Magasins/Magasin.fxml"));
            AnchorPane magasinView = loader.load();

            AnchorPane root = (AnchorPane) ((javafx.scene.Node) event.getSource()).getScene().lookup("#contenuPane");

            if (root != null) {
                root.getChildren().setAll(magasinView);
            } else {
                ((javafx.scene.Node) event.getSource()).getScene().setRoot(magasinView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à l'accueil.");
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        showAlert("Information", message, Alert.AlertType.INFORMATION);
    }
}
