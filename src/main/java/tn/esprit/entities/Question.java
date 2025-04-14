package tn.esprit.entities;

import java.util.List;
import java.util.Objects;

public class Question {
    private int id;
    private String contenu_q;
    private List<String> reponses;
    private Quiz quiz;

    public Question() {
    }

    public Question(int id, String contenu_q, List<String> reponses, Quiz quiz) {
        this.id = id;
        this.contenu_q = contenu_q;
        this.reponses = reponses;
        this.quiz = quiz;
    }
    public Question(String contenu_q, List<String> reponses, Quiz quiz) {
        this.contenu_q = contenu_q;
        this.reponses = reponses;
        this.quiz = quiz;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenu_q() {
        return contenu_q;
    }

    public void setContenu_q(String contenu_q) {
        this.contenu_q = contenu_q;
    }

    public List<String> getReponses() {
        return reponses;
    }

    public void setReponses(List<String> reponses) {
        this.reponses = reponses;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return getId() == question.getId() && Objects.equals(getContenu_q(), question.getContenu_q()) && Objects.equals(getReponses(), question.getReponses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getContenu_q(), getReponses());
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", contenu_q='" + contenu_q + '\'' +
                ", reponses=" + reponses +
                ", quiz=" + quiz +
                '}';
    }
}
