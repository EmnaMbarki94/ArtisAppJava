package tn.esprit.test;


import javafx.application.Application;
import javafx.stage.Stage;
import tn.esprit.gui.gui;

public class Main extends Application {
    @Override
    public void start(Stage stage)   {
        //gui.getInstance().getViewFactory().showAdminWindow();
          //gui.getInstance().getViewFactory().showUsersWindow();
          gui.getInstance().getViewFactory().showLoginWindow();

    }
}








/*public class Main {
    public static void main(String[] args) {
        // ServicePersonne sp = new ServicePersonne();

        // sp.ajouter(new Personne("Gaith", "bb", 22));
        System.out.println("Ajouter !!");
        // sp.modifier(new Personne(1, "Koussay", "bb", 7));
        // System.out.println("Modifier !!");
        // sp.supprimer(2);
        // System.out.println(sp.afficher());
    }
}*/
