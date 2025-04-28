package tn.esprit.entities;

public class Quiz_attempt {
    private int id;
    private Quiz quiz;
    private int note;
    private Personne user;
    public Quiz_attempt() {
    }
    public Quiz_attempt(Quiz quiz, int note, Personne user) {
        this.quiz = quiz;
        this.note = note;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Personne getUser() {
        return user;
    }

    public void setUser(Personne user) {
        this.user = user;
    }
}
