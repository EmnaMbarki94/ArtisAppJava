package tn.esprit.entities;

public class Magasin {

    private int id;
    private String nom_m,type_m;
    private String photo_m;
    public Magasin() {

    }
    public Magasin(String nom_m, String type_m, String photo_m) {
        this.nom_m = nom_m;
        this.type_m = type_m;
        this.photo_m = photo_m;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_m() {
        return nom_m;
    }

    public void setNom_m(String nom_m) {
        this.nom_m = nom_m;
    }

    public String getType_m() {
        return type_m;
    }

    public void setType_m(String type_m) {
        this.type_m = type_m;
    }

    public String getPhoto_m() {
        return photo_m;
    }

    public void setPhoto_m(String photo_m) {
        this.photo_m = photo_m;
    }
}
