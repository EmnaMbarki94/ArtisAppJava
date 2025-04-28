package tn.esprit.entities;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    private static List<PanierItem> items = new ArrayList<>();

    public static void ajouterArticle(Article article, int quantite) {
        for (PanierItem item : items) {
            if (item.getArticle().getId() == article.getId()) {
                item.setQuantite(item.getQuantite() + quantite);
                return;
            }
        }
        items.add(new PanierItem(article, quantite));
    }

    public static void supprimerArticle(Article article) {
        items.removeIf(item -> item.getArticle().getId() == article.getId());
    }

    public static void viderPanier() {
        items.clear();
    }

    public static List<PanierItem> getItems() {
        return items;
    }

    public static double getTotal() {
        return items.stream().mapToDouble(PanierItem::getSousTotal).sum();
    }

    public static boolean estVide() {
        return items.isEmpty();
    }
}
