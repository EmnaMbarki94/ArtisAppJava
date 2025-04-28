package tn.esprit.entities;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Comment {
    private int id;
    private Piece_art piece_art;
    private String content;
    private LocalDateTime creation_date;
    private Personne user;
    private int likes;

    public Comment() {}

    public Comment(Piece_art piece_art, String content, LocalDateTime creation_date, Personne user, int likes) {
        this.piece_art = piece_art;
        this.content = content;
        this.creation_date = creation_date;
        this.user = user;
        this.likes = likes;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Piece_art getPieceArt() {
        return piece_art;
    }

    public void setPieceArt(Piece_art piece_art) {
        this.piece_art = piece_art; // Setter pour l'objet Piece_art
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getCreation_date() {
        return creation_date;
    }
    public void setCreation_date(LocalDateTime creation_date) {
        this.creation_date = creation_date;
    }
    public Personne getUser() {
        return user;
    }
    public void setUser(Personne user) {
        this.user = user;
    }
    public int getLikes() {
        return likes;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", piece_art_id=" + piece_art.getId() +
                ", content='" + content + '\'' +
                ", creation_date=" + creation_date +
                ", user_id=" + user.getId() +
                ", likes=" + likes +
                '}';
    }
}
