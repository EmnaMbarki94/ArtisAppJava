package tn.esprit.entities;

import java.time.LocalDate;

public class Event {
    private int id;
    private String nom;
    private String type_e;
    private String info_e;
    private LocalDate date_e;
    private String photo_e;
    private int prix_s;
    private int prix_vip;
    private int nb_ticket;

    public Event() {
    }

    public Event(int id, String nom, String type_e, String info_e, LocalDate date_e, String photo_e, int prix_s, int prix_vip, int nb_ticket) {
        this.id = id;
        this.nom = nom;
        this.type_e = type_e;
        this.info_e = info_e;
        this.date_e = date_e;
        this.photo_e = photo_e;
        this.prix_s = prix_s;
        this.prix_vip = prix_vip;
        this.nb_ticket = nb_ticket;
    }

    public Event(String nom, String type_e, String info_e, LocalDate date_e, String photo_e, int prix_s, int prix_vip, int nb_ticket) {
        this.nom = nom;
        this.type_e = type_e;
        this.info_e = info_e;
        this.date_e = date_e;
        this.photo_e = photo_e;
        this.prix_s = prix_s;
        this.prix_vip = prix_vip;
        this.nb_ticket = nb_ticket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType_e() {
        return type_e;
    }

    public void setType_e(String type_e) {
        this.type_e = type_e;
    }

    public String getInfo_e() {
        return info_e;
    }

    public void setInfo_e(String info_e) {
        this.info_e = info_e;
    }

    public LocalDate getDate_e() {
        return date_e;
    }

    public void setDate_e(LocalDate date_e) {
        this.date_e = date_e;
    }

    public String getPhoto_e() {
        return photo_e;
    }

    public void setPhoto_e(String photo_e) {
        this.photo_e = photo_e;
    }

    public int getPrix_s() {
        return prix_s;
    }

    public void setPrix_s(int prix_s) {
        this.prix_s = prix_s;
    }

    public int getPrix_vip() {
        return prix_vip;
    }

    public void setPrix_vip(int prix_vip) {
        this.prix_vip = prix_vip;
    }

    public int getNb_ticket() {
        return nb_ticket;
    }

    public void setNb_ticket(int nb_ticket) {
        this.nb_ticket = nb_ticket;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type_e='" + type_e + '\'' +
                ", info_e='" + info_e + '\'' +
                ", date_e=" + date_e +
                ", photo_e='" + photo_e + '\'' +
                ", prix_s=" + prix_s +
                ", prix_vip=" + prix_vip +
                ", nb_ticket=" + nb_ticket +
                '}';
    }
}
