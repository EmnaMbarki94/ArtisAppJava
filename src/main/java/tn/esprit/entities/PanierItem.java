package tn.esprit.entities;

public class PanierItem {
    private Article article;
    private int quantite;

    public PanierItem(Article article, int quantite) {
        this.article = article;
        this.quantite = quantite;
    }

    public Article getArticle() {
        return article;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getSousTotal() {
        return article.getPrix_a() * quantite;
    }
}
