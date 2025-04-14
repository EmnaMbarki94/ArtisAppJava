package tn.esprit.entities;
import java.time.LocalDate;

public class Piece_art {
    private int id;
    private String nom_p;
    private LocalDate date_crea;
    private String photo_p;
    private String desc_p;
    private Galerie galerie;

    public Piece_art() {}

    public Piece_art(String nom_p, LocalDate date_crea, String photo_p, String desc_p, Galerie galerie) {
        this.nom_p = nom_p;
        this.date_crea = date_crea;
        this.photo_p = photo_p;
        this.desc_p = desc_p;
        this.galerie= galerie;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_p() {
        return nom_p;
    }

    public void setNom_p(String nom_p) {
        this.nom_p = nom_p;
    }

    public LocalDate getDate_crea() {
        return date_crea;
    }

    public void setDate_crea(LocalDate date_crea) {
        this.date_crea = date_crea;
    }

    public String getPhoto_p() {
        return photo_p;
    }

    public void setPhoto_p(String photo_p) {
        this.photo_p = photo_p;
    }

    public String getDesc_p() {
        return desc_p;
    }

    public void setDesc_p(String desc_p) {
        this.desc_p = desc_p;
    }

    public Galerie getGalerie() {
        return galerie;
    }

    public void setGalerie(Galerie galerie) {
        this.galerie = galerie;
    }

    @Override
    public String toString() {
        return "Piece_art{" +
                "id=" + id +
                ", nom_p='" + nom_p + '\'' +
                ", date_crea=" + date_crea +
                ", photo_p='" + photo_p + '\'' +
                ", desc_p='" + desc_p + '\'' +
                ", galerie=" + galerie.getNom_g()+
                '}';
    }
}
