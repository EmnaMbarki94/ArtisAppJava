package tn.esprit.entities;

public class Galerie {
    private int id;
    private String nom_g;
    private String photo_g;
    private String desc_g;
    private String type_g;
    private Personne user;

    public Galerie() {}

    public Galerie(String nom_g, String photo_g, String desc_g, String type_g, Personne user) {
        this.nom_g = nom_g;
        this.photo_g = photo_g;
        this.desc_g = desc_g;
        this.type_g = type_g;
        this.user = user;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom_g() {
        return nom_g;
    }
    public void setNom_g(String nom_g) {
        this.nom_g = nom_g;
    }
    public String getPhoto_g() {
        return photo_g;
    }
    public void setPhoto_g(String photo_g) {
        this.photo_g = photo_g;
    }
    public String getDesc_g() {
        return desc_g;
    }
    public void setDesc_g(String desc_g) {
        this.desc_g = desc_g;
    }
    public String getType_g() {
        return type_g;
    }
    public void setType_g(String type_g) {
        this.type_g = type_g;
    }
    public int getUser() {
        return user != null ? user.getId() : 0;
    }
    public void setUser(Personne user) {
        this.user = user;
    }
    public void setUserById(int userId) {

        Personne user = new Personne();
        user.setId(userId);
        this.user = user;
    }

    @Override
    public String toString() {
        return "Galerie{" +
                "id=" + id +
                ", nom_g='" + nom_g + '\'' +
                ", photo_g='" + photo_g + '\'' +
                ", desc_g='" + desc_g + '\'' +
                ", type_g='" + type_g + '\'' +
                ", user_id=" + user.getId() +
                '}';
    }
}
