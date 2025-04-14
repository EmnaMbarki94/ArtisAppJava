package tn.esprit.entities;

public class Reservation {
    private int id;
    private Event event;
    private int nb_place;
    private String libelle;
    private String etat_e;
    private int user_id_id;

    public Reservation() {
    }

    public Reservation(int id, Event event, int nb_place, String libelle, String etat_e, int user_id_id) {
        this.id = id;
        this.event = event;
        this.nb_place = nb_place;
        this.libelle = libelle;
        this.etat_e = etat_e;
        this.user_id_id = user_id_id;
    }

    public Reservation(Event event, int nb_place, String libelle, String etat_e, int user_id_id) {
        this.event= event;
        this.nb_place = nb_place;
        this.libelle = libelle;
        this.etat_e = etat_e;
        this.user_id_id = user_id_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getNb_place() {
        return nb_place;
    }

    public void setNb_place(int nb_place) {
        this.nb_place = nb_place;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getEtat_e() {
        return etat_e;
    }

    public void setEtat_e(String etat_e) {
        this.etat_e = etat_e;
    }

    public int getUser_id_id() {
        return user_id_id;
    }

    public void setUser_id_id(int user_id_id) {
        this.user_id_id = user_id_id;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", event=" + event +
                ", nb_place=" + nb_place +
                ", libelle='" + libelle + '\'' +
                ", etat_e='" + etat_e + '\'' +
                ", user_id_id=" + user_id_id +
                '}';
    }
}
