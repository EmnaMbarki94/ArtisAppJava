package tn.esprit.entities;

import java.util.Objects;

public class LigneCommande {

    private int id;
    private Commande commande; // Utilisation d'un objet Commande
    private Article article;   // Utilisation d'un objet Article
    private int quantite;

    public LigneCommande() {
        this.article = new Article();
    }

    public LigneCommande(Commande commande, Article article, int quantite) {
        this.commande = commande;
        this.article = article;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id=" + id +
                ", commande=" + commande +
                ", article=" + article +
                ", quantite=" + quantite +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneCommande that = (LigneCommande) o;
        return id == that.id && quantite == that.quantite && Objects.equals(commande, that.commande) && Objects.equals(article, that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, commande, article, quantite);
    }
}
