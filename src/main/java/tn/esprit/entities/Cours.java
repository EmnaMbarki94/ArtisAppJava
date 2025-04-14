package tn.esprit.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Cours {

    private int id;
    private String nom_c;
    private String categ_c;
    private String contenu_c;
    private LocalDate date_c;
    private LocalTime heure_c;
    private Personne user;
    private String image;
    private Quiz quiz;

    public Cours(int id, String nom_c, String categ_c, String contenu_c, LocalDate date_c, LocalTime heure_c, String image, Quiz quiz) {
        this.id = id;
        this.nom_c = nom_c;
        this.categ_c = categ_c;
        this.contenu_c = contenu_c;
        this.date_c = date_c;
        this.heure_c = heure_c;
        this.image = image;
        this.quiz = quiz;
    }
    public Cours(String nom_c, String categ_c, String contenu_c, LocalDate date_c, LocalTime heure_c, String image, Quiz quiz) {
        this.nom_c = nom_c;
        this.categ_c = categ_c;
        this.contenu_c = contenu_c;
        this.date_c = date_c;
        this.heure_c = heure_c;
        this.image = image;
        this.quiz = quiz;
    }
    public Cours( String nom_c, String categ_c, String contenu_c, LocalDate date_c, LocalTime heure_c, String image) {
        this.nom_c = nom_c;
        this.categ_c = categ_c;
        this.contenu_c = contenu_c;
        this.date_c = date_c;
        this.heure_c = heure_c;
        this.image = image;
        this.quiz = null;
    }

    public Cours(Quiz quiz, String image, Personne user, LocalTime heure_c, LocalDate date_c, String contenu_c, String nom_c, String categ_c) {
        this.quiz = quiz;
        this.image = image;
        this.user = user;
        this.heure_c = heure_c;
        this.date_c = date_c;
        this.contenu_c = contenu_c;
        this.nom_c = nom_c;
        this.categ_c = categ_c;
    }

    public Cours() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_c() {
        return nom_c;
    }

    public void setNom_c(String nom_c) {
        this.nom_c = nom_c;
    }

    public String getCateg_c() {
        return categ_c;
    }

    public void setCateg_c(String categ_c) {
        this.categ_c = categ_c;
    }

    public String getContenu_c() {
        return contenu_c;
    }

    public void setContenu_c(String contenu_c) {
        this.contenu_c = contenu_c;
    }

    public LocalDate getDate_c() {
        return date_c;
    }

    public void setDate_c(LocalDate date_c) {
        this.date_c = date_c;
    }

    public LocalTime getHeure_c() {
        return heure_c;
    }

    public void setHeure_c(LocalTime heure_c) {
        this.heure_c = heure_c;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Personne getUser() {
        return user;
    }

    public void setUser(Personne user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cours cours)) return false;
        return getId() == cours.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Cours{" +
                "id=" + id +
                ", nom_c='" + nom_c + '\'' +
                ", categ_c='" + categ_c + '\'' +
                ", contenu_c='" + contenu_c + '\'' +
                ", date_c=" + date_c +
                ", heure_c=" + heure_c +
                ", image='" + image + '\'' +
                ", quiz=" + quiz +
                '}';
    }
}
