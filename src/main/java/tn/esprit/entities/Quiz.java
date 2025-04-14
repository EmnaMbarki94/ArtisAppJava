package tn.esprit.entities;

import java.util.Objects;

public class Quiz {
    private int id;
    private String titre_c;
    private Cours cours;

    public Quiz(String titre_c, Cours cours) {
        this.titre_c = titre_c;
        this.cours = cours;
    }

    public Quiz() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre_c() {
        return titre_c;
    }

    public void setTitre_c(String titre_c) {
        this.titre_c = titre_c;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz quiz)) return false;
        return getId() == quiz.getId() && Objects.equals(getTitre_c(), quiz.getTitre_c()) && Objects.equals(getCours(), quiz.getCours());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitre_c(), getCours());
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "id=" + id +
                ", titre_c='" + titre_c + '\'' +
                ", cours=" + cours +
                '}';
    }
}
