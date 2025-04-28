package tn.esprit.controller.Admin;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Personne;
import tn.esprit.gui.gui;
import tn.esprit.services.ServicePersonne;
import java.io.*;
import java.util.List;
public class ExportFileController {

    @FXML
    private TableView<Personne> user_admin_tableView;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button exportCsvButton;

    private final ServicePersonne userService = new ServicePersonne(); // à adapter selon ton projet

    @FXML
    private void initialize() {
        initTable();
        loadTableData();
    }

    private void initTable() {
        TableColumn<Personne, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Personne, String> rolesColumn = new TableColumn<>("Roles");
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));

        TableColumn<Personne, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("last_Name"));

        TableColumn<Personne, String> prenomColumn = new TableColumn<>("Prenom");
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("first_Name"));

        TableColumn<Personne, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<Personne, String> numTelColumn = new TableColumn<>("Phone Number");
        numTelColumn.setCellValueFactory(new PropertyValueFactory<>("num_tel"));

        TableColumn<Personne, String> cinColumn = new TableColumn<>("CIN");
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));

        TableColumn<Personne, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Personne, String> verifiedColumn = new TableColumn<>("Verification Status");
        verifiedColumn.setCellValueFactory(new PropertyValueFactory<>("is_verified"));

        TableColumn<Personne, String> specialiteColumn = new TableColumn<>("Specialite");
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));

        TableColumn<Personne, String> pointColumn = new TableColumn<>("Point");
        pointColumn.setCellValueFactory(new PropertyValueFactory<>("point"));

        user_admin_tableView.getColumns().addAll(
                emailColumn, rolesColumn, nomColumn, prenomColumn,
                passwordColumn, numTelColumn, cinColumn,
                addressColumn, verifiedColumn, specialiteColumn, pointColumn
        );
    }

    private void loadTableData() {
        List<Personne> users = userService.selectAll();

        if (users != null && !users.isEmpty()) {
            ObservableList<Personne> userList = FXCollections.observableArrayList(users);
            user_admin_tableView.setItems(userList);
        } else {
            showAlert("Aucune donnée disponible", Alert.AlertType.INFORMATION);
            user_admin_tableView.getItems().clear();
        }
    }

    @FXML
    private void exportToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.setInitialFileName("utilisateurs.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));

        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());

        if (file != null) {
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Paragraph title = new Paragraph("Liste des Utilisateurs", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" "));

                PdfPTable pdfTable = new PdfPTable(10); // Sans le mot de passe
                pdfTable.setWidthPercentage(100);

                String[] headers = {"Email", "Roles", "Nom", "Prenom", "Phone", "CIN", "Adresse", "Vérifié", "Spécialité", "Points"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    pdfTable.addCell(cell);
                }

                for (Personne user : user_admin_tableView.getItems()) {
                    pdfTable.addCell(user.getEmail());
                    pdfTable.addCell(user.getRoles());
                    pdfTable.addCell(user.getLast_Name());
                    pdfTable.addCell(user.getFirst_Name());
                    pdfTable.addCell(user.getNum_tel());
                    pdfTable.addCell(user.getCin());
                    pdfTable.addCell(user.getAddress());
                    pdfTable.addCell(String.valueOf(user.getIs_verified()));
                    pdfTable.addCell(user.getSpecialite());
                    pdfTable.addCell(String.valueOf(user.getPoint()));
                }

                document.add(pdfTable);
                showAlert("PDF exporté avec succès : " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                showAlert("Erreur lors de l'export PDF : " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            } finally {
                document.close();
            }
        }
    }



    @FXML
    private void exportToCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier CSV");
        fileChooser.setInitialFileName("utilisateurs.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(exportCsvButton.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                // En-têtes CSV (séparés par des ; avec guillemets)
                String header = "\"Email\";\"Roles\";\"Nom\";\"Prenom\";\"Password\";\"Phone\";\"CIN\";\"Adresse\";\"Vérifié\";\"Spécialité\";\"Points\"";
                writer.write(header);
                writer.newLine();

                for (Personne user : user_admin_tableView.getItems()) {
                    String row = String.join(";",
                            quote(user.getEmail()),
                            quote(user.getRoles()),
                            quote(user.getLast_Name()),
                            quote(user.getFirst_Name()),
                            quote(user.getPassword()),
                            quote(user.getNum_tel()),
                            quote(user.getCin()),
                            quote(user.getAddress()),
                            quote(String.valueOf(user.getIs_verified())),
                            quote(user.getSpecialite()),
                            quote(String.valueOf(user.getPoint()))
                    );
                    writer.write(row);
                    writer.newLine();
                }

                showAlert("CSV exporté avec succès : " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("Erreur lors de l'export CSV : " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private String quote(String value) {
        if (value == null) value = "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }


    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }



    public void handelReturnToMetier(ActionEvent actionEvent)
    {
        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("Metiers");
    }
}
