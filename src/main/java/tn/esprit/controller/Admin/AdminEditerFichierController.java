package tn.esprit.controller.Admin;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import tn.esprit.gui.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class AdminEditerFichierController {
    @FXML private TextArea ta;
    @FXML private Slider sl;
    @FXML private DatePicker dp;
    @FXML private ColorPicker cp;
    @FXML private Button openFileBtn;
    @FXML private Button saveFileBtn;
    @FXML private Button closeFileBtn;
    private File currentFile;


    @FXML
    private void initialize() {
        sl.valueProperty().addListener((obs, oldVal, newVal) -> {
            ta.setFont(Font.font(newVal.doubleValue()));
        });

        dp.setOnAction(e -> {
            ta.setText("Date: " + dp.getValue() + "\n" + ta.getText());
        });

        cp.setOnAction(e -> {
            String hexColor = "#" + cp.getValue().toString().substring(2, 8);
            ta.setStyle("-fx-text-fill:" + hexColor);
          //  ta.setText(cp.getValue() + "\n" + ta.getText());
        });


        openFileBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Word Documents", "*.docx")
            );
            File file = fc.showOpenDialog(new Stage());

            if (file != null) {
                String fileName = file.getName().toLowerCase();

                if (fileName.endsWith(".docx")) {
                    // ➤ Lire le fichier Word avec Apache POI
                    try (FileInputStream fis = new FileInputStream(file);
                         XWPFDocument doc = new XWPFDocument(fis)) {

                        StringBuilder sb = new StringBuilder();
                        for (XWPFParagraph p : doc.getParagraphs()) {
                            sb.append(p.getText()).append("\n");
                        }
                        ta.setText(sb.toString());
                        currentFile = file;

                    } catch (Exception ex) {
                        ta.setText("Erreur de lecture du fichier Word !");
                    }

                } else {
                    // ➤ Lire un fichier CSV ou TXT avec Scanner
                    try (Scanner sc = new Scanner(new FileInputStream(file))) {
                        StringBuilder content = new StringBuilder();
                        while (sc.hasNextLine()) {
                            String line = sc.nextLine();
                            String[] values = line.split("[,;\\s]+");
                            for (String val : values) {
                                content.append(String.format("%-15s", val));
                            }
                            content.append("\n");
                        }
                        ta.setText(content.toString());
                        currentFile = file;
                    } catch (Exception ex) {
                        ta.setText("Erreur de lecture du fichier !");
                    }
                }
            }
        });




        saveFileBtn.setOnAction(e -> {
            if (currentFile != null) {
                String fileName = currentFile.getName().toLowerCase();
                if (fileName.endsWith(".docx")) {
                    try {
                        writeTextAreaToWord(currentFile);

                        // ✅ Alerte succès Word
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sauvegarde réussie");
                        alert.setHeaderText(null);
                        alert.setContentText("Le fichier Word a été sauvegardé avec succès !");
                        alert.showAndWait();

                    } catch (Exception ex) {
                        // ❌ Alerte erreur Word
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Échec de la sauvegarde Word");
                        alert.setContentText("Une erreur est survenue lors de la sauvegarde du fichier Word.");
                        alert.showAndWait();
                    }
                } else {
                    try (java.io.FileWriter fw = new java.io.FileWriter(currentFile)) {
                        fw.write(ta.getText());

                        // ✅ Alerte succès fichier texte/csv
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Sauvegarde réussie");
                        alert.setHeaderText(null);
                        alert.setContentText("Le fichier a été sauvegardé avec succès !");
                        alert.showAndWait();

                    } catch (Exception ex) {
                        // probleme au niveau lors de la sauvegarde du fichier
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Échec de la sauvegarde");
                        alert.setContentText("Une erreur est survenue lors de la sauvegarde du fichier.");
                        alert.showAndWait();
                    }
                }
            } else {
                // pas de fichier
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aucun fichier");
                alert.setHeaderText("Pas de fichier sélectionné");
                alert.setContentText("Veuillez ouvrir ou créer un fichier avant de sauvegarder.");
                alert.showAndWait();
            }
        });




        closeFileBtn.setOnAction(e -> {
            ta.clear(); // Vider le texte
            currentFile = null; // Réinitialiser le fichier courant
        });
    }

    private void writeTextAreaToWord(File file) throws Exception {
        try (XWPFDocument doc = new XWPFDocument()) {
            String[] lines = ta.getText().split("\n");
            for (String line : lines) {
                XWPFParagraph para = doc.createParagraph();
                XWPFRun run = para.createRun();
                run.setText(line);
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                doc.write(out);
            }
        }
    }

    public void handleMetier(ActionEvent actionEvent)
    {

        gui.getInstance().getViewFactory().getAdminSelectedMenuItem().set("Metiers");
    }
}


