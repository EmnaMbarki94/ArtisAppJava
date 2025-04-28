package tn.esprit.services;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Piece_art;
import tn.esprit.entities.Comment;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceComment implements CRUD <Comment> {
    private Connection connection;

    @Override
    public boolean supprimer(Comment comment) throws SQLException {
        return false;
    }

    @Override
    public List<Comment> selectAll() throws SQLException {
        return null;
    }

    public ServiceComment() {
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Comment comment) throws SQLException {
        String sql = "INSERT INTO comment (content, user_id, piece_art_id, creation_date,likes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, comment.getContent());
            pst.setInt(2, comment.getUser().getId());
            pst.setInt(3, comment.getPieceArt().getId());
            pst.setTimestamp(4, Timestamp.valueOf(comment.getCreation_date()));
            pst.setInt(5, 0);
            pst.executeUpdate();
        }
    }


    @Override
    public void modifier(Comment comment) throws SQLException {
        String sql = "UPDATE `comment` SET `content`=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, comment.getContent());
            pst.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM `comment` WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }

    public List<Comment> afficher() throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.first_name, u.last_name FROM `comment` c JOIN `user` u ON c.user_id = u.id";
        Statement statement = connection.createStatement();
        ResultSet rs =statement.executeQuery(sql);
        ServicePieceArt servicePieceArt = new ServicePieceArt();
        while (rs.next()){
            Comment c = new Comment();
            c.setId(rs.getInt("id"));
            int pieceArtId = rs.getInt("piece_art_id");
            Piece_art pieceArt = servicePieceArt.obtenirPieceParId(pieceArtId);
            c.setPieceArt(pieceArt);
            c.setContent(rs.getString("content"));
            c.setCreation_date(rs.getTimestamp("creation_date").toLocalDateTime());
            Personne user = new Personne();
            user.setId(rs.getInt("user_id"));
            user.setFirst_Name(rs.getString("first_name"));
            user.setLast_Name(rs.getString("last_name"));
            c.setUser(user);
            c.setLikes(rs.getInt("likes"));
            comments.add(c);
        }
        return comments;
    }

    public List<Comment> afficherCommentaires(int pieceArtId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, p.first_name, p.last_name " +
                "FROM comment c " +
                "JOIN user p ON c.user_id = p.id " +
                "WHERE c.piece_art_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, pieceArtId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setContent(rs.getString("content"));
                comment.setCreation_date(rs.getTimestamp("creation_date").toLocalDateTime());
                comment.setLikes(rs.getInt("likes"));

                // Create and populate the User object
                Personne user = new Personne();
                user.setId(rs.getInt("user_id"));
                user.setFirst_Name(rs.getString("first_name")); // Fetch first name
                user.setLast_Name(rs.getString("last_name"));   // Fetch last name
                comment.setUser(user); // Set the user for the comment

                comments.add(comment);
            }
        }
        return comments;
    }

    public boolean hasUserLikedComment(int userId, int commentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM comment_likes WHERE user_id = ? AND comment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, commentId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void likeComment(int userId, int commentId) throws SQLException {
        String insertLike = "INSERT INTO comment_likes (user_id, comment_id) VALUES (?, ?)";
        String updateLikes = "UPDATE comment SET likes = likes + 1 WHERE id = ?";

        try (
                PreparedStatement psLike = connection.prepareStatement(insertLike);
                PreparedStatement psUpdate = connection.prepareStatement(updateLikes)
        ) {
            psLike.setInt(1, userId);
            psLike.setInt(2, commentId);
            psLike.executeUpdate();

            psUpdate.setInt(1, commentId);
            psUpdate.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors du like : " + e.getMessage());
        }
    }



    public void unlikeComment(int userId, int commentId) throws SQLException {
        String delete = "DELETE FROM comment_likes WHERE user_id = ? AND comment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(delete)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, commentId);
            stmt.executeUpdate();
        }

        String update = "UPDATE comment SET likes = likes - 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(update)) {
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        }
    }


}
