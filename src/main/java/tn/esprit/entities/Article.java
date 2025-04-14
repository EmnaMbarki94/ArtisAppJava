package tn.esprit.entities;

public class Article {

    private int id;
    private String nom_a;
    private double prix_a;
    private String desc_a;
    private String image_path;
//    private int commande_id;
    private Magasin magasin;
    private int quantite;

    public Article() {
    }

    public Article(String nom_a, double prix_a, String desc_a, String image_path,  Magasin magasin, int quantite) {
        this.nom_a = nom_a;
        this.prix_a = prix_a;
        this.desc_a = desc_a;
        this.image_path = image_path;
//        this.commande_id = commande_id;
        this.magasin = magasin;
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_a() {
        return nom_a;
    }

    public void setNom_a(String nom_a) {
        this.nom_a = nom_a;
    }

    public double getPrix_a() {
        return prix_a;
    }

    public void setPrix_a(double prix_a) {
        this.prix_a = prix_a;
    }

    public String getDesc_a() {
        return desc_a;
    }

    public void setDesc_a(String desc_a) {
        this.desc_a = desc_a;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

//    public int getCommande_id() {
//        return commande_id;
//    }
//
//    public void setCommande_id(int commande_id) {
//        this.commande_id = commande_id;
//    }

    public Magasin getMagasin() {
        return magasin;
    }

    public void setMagasin(Magasin magasin) {
        this.magasin = magasin;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
