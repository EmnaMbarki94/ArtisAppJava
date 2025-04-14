package tn.esprit.services;
import tn.esprit.entities.Galerie;
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
        String sql = "INSERT INTO `comment`(`piece_art_id`, `content`, `creation_date`, `user_id`, `likes`) VALUES ("+comment.getPieceArt().getId()+",'"+comment.getContent()+"','"+comment.getCreation_date()+"',"+comment.getUser_id()+","+comment.getLikes()+")";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    public void ajouterLike(int commentId) throws SQLException {
        String sql = "UPDATE `comment` SET likes = likes + 1 WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, commentId);
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
        String sql = "Select * from `comment`";
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
            c.setUser_id(rs.getInt("user_id"));
            c.setLikes(rs.getInt("likes"));
            comments.add(c);
        }
        return comments;
    }

    public List<Comment> afficherCommentaires(int pieceArtId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * FROM comment WHERE piece_art_id = ?";
        ServicePieceArt servicePieceArt = new ServicePieceArt();
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, pieceArtId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                Piece_art pieceArt = servicePieceArt.obtenirPieceParId(pieceArtId);
                comment.setPieceArt(pieceArt);
                comment.setContent(rs.getString("content"));
                comment.setCreation_date(rs.getTimestamp("creation_date").toLocalDateTime());
                comment.setUser_id(rs.getInt("user_id"));
                comment.setLikes(rs.getInt("likes"));
                comments.add(comment);
            }
        }
        return comments;
    }
}
