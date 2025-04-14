package tn.esprit.entities;

public class Reponse {
    private int idreponse;
    private int reclamation_id;
    private String descr_rep;
    private String date_rep;

    public Reponse() {

    }
    public Reponse(int idreponse, String descr_rep, String date_rep) {
        this.idreponse = idreponse;
        this.descr_rep = descr_rep;
        this.date_rep = date_rep;
    }
    public int getIdreponse() {
        return idreponse;
    }
    public void setIdreponse(int idreponse) {
        this.idreponse = idreponse;
    }
    public int getReclamation_id() {
        return reclamation_id;
    }
    public void setReclamation_id(int reclamation_id) {
        this.reclamation_id = reclamation_id;
    }
    public String getDescr_rep() {
        return descr_rep;
    }
    public void setDescr_rep(String descr_rep) {
        this.descr_rep = descr_rep;
    }
    public String getDate_rep() {
        return date_rep;
    }
    public void setDate_rep(String date_rep) {
        this.date_rep = date_rep;
    }

}
