package tn.esprit.entities;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Commande {

    private int id;
    private double total;
    private String adresse_c;
    private Date date_c;
    private Personne user;
    private List<LigneCommande> lignesCommande; // facultatif mais utile

    // Corrected constructor
    public Commande(Personne user, Date date, String adresse, double total) {
        this.user = user;
        this.date_c = date != null ? date : new Date();
        this.adresse_c = adresse; // THIS WAS MISSING
        this.total = total;
    }

    // Second constructor (also corrected)
    public Commande(double total, String adresse_c, Personne user) {
        this.total = total;
        this.adresse_c = adresse_c;
        this.date_c = new Date();
        this.user = user;
    }

    public Commande() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getAdresse_c() {
        return adresse_c;
    }

    public void setAdresse_c(String adresse_c) {
        this.adresse_c = adresse_c;
    }

    public Date getDate_c() {
        return date_c;
    }

    // Supprimer le setter de la date pour empêcher de la modifier manuellement
    public void setDate_c(Date date_c) { this.date_c = date_c; }

    public Personne getUser() {
        return user;
    }

    public void setUser(Personne user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", total=" + total +
                ", adresse_c='" + adresse_c + '\'' +
                ", date_c=" + date_c +
                "user=" + (user != null ? user.getId() : "null")+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commande commande = (Commande) o;
        return id == commande.id && Double.compare(total, commande.total) == 0 && Objects.equals(adresse_c, commande.adresse_c) && Objects.equals(date_c, commande.date_c) && Objects.equals(user, commande.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, total, adresse_c, date_c, user);
    }

}
