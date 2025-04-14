package tn.esprit.entities;

public class Reclamtion {
    private int id;
    private String desc_r;
    private String date_r;
    private String type_r;
    private Personne user;

    public Reclamtion() {

    }
    public Reclamtion( String desc_r, String date_r, String type_r, Personne user) {
        this.desc_r = desc_r;
        this.date_r = date_r;
        this.type_r = type_r;
        this.user = user;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDesc_r() {
        return desc_r;
    }
    public void setDesc_r(String desc_r) {
        this.desc_r = desc_r;
    }
    public String getDate_r() {
        return date_r;
    }
    public void setDate_r(String date_r) {
        this.date_r = date_r;
    }

    public String getType_r() {
        return type_r;
    }
    public void setType_r(String type_r) {
        this.type_r = type_r;
    }
    public Personne getUser() {
        return user;
    }
    public void setUser(Personne user) {
        this.user = user;
    }



}
