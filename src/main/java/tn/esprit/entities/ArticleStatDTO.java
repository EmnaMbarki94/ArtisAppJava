package tn.esprit.entities;

public class ArticleStatDTO {
    private int articleId;
    private String nomArticle;
    private double prixArticle; // Optionnel
    private int quantiteVendue;

    // Constructeur avec prix
    public ArticleStatDTO(int articleId, String nomArticle, double prixArticle, int quantiteVendue) {
        this.articleId = articleId;
        this.nomArticle = nomArticle;
        this.prixArticle = prixArticle;
        this.quantiteVendue = quantiteVendue;
    }

    // Nouveau constructeur sans prix
    public ArticleStatDTO(int articleId, String nomArticle, int quantiteVendue) {
        this(articleId, nomArticle, 0.0, quantiteVendue); // Prix par défaut à 0
    }

    // Getters
    public int getArticleId() { return articleId; }
    public String getNomArticle() { return nomArticle; }
    public double getPrixArticle() { return prixArticle; }
    public int getQuantiteVendue() { return quantiteVendue; }
}