package tn.esprit.services;

import tn.esprit.entities.Galerie;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGalerie implements CRUD <Galerie> {
    private Connection connection;

    @Override
    public boolean supprimer(Galerie galerie) throws SQLException {
        return false;
    }

    public ServiceGalerie(){
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Galerie galerie) throws SQLException {
        String sql = "INSERT INTO `galerie`(`nom_g`, `photo_g`, `desc_g`, `type_g`, `user_id`) VALUES ('"+galerie.getNom_g()+"','"+galerie.getPhoto_g()+"','"+galerie.getDesc_g()+"','"+galerie.getType_g()+"',"+galerie.getUser()+")";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sqlPiecesArt = "SELECT id FROM piece_art WHERE galerie_id = ?";
        try (PreparedStatement psPiecesArtSelect = connection.prepareStatement(sqlPiecesArt)) {
            psPiecesArtSelect.setInt(1, id);
            ResultSet piecesArt = psPiecesArtSelect.executeQuery();

            // Parcourir les pièces d'art et supprimer les commentaires
            while (piecesArt.next()) {
                int pieceArtId = piecesArt.getInt("id");
                String sqlCommentDelete = "DELETE FROM comment WHERE piece_art_id = ?";
                try (PreparedStatement psCommentDelete = connection.prepareStatement(sqlCommentDelete)) {
                    psCommentDelete.setInt(1, pieceArtId);
                    psCommentDelete.executeUpdate();
                }
            }
        }
        String sqlPiecesArtDelete = "DELETE FROM piece_art WHERE galerie_id = ?";
        try (PreparedStatement psPiecesArtDelete = connection.prepareStatement(sqlPiecesArtDelete)) {
            psPiecesArtDelete.setInt(1, id);
            psPiecesArtDelete.executeUpdate();
        }
        String sqlGalerieDelete = "DELETE FROM galerie WHERE id = ?";
        try (PreparedStatement psGalerieDelete = connection.prepareStatement(sqlGalerieDelete)) {
            psGalerieDelete.setInt(1, id);
            psGalerieDelete.executeUpdate();
        }
    }

    @Override
    public void modifier(Galerie galerie) throws SQLException {
        String sql = "UPDATE `galerie` SET `nom_g`=? ,`photo_g`=?,`desc_g`=? , `type_g`= ? WHERE id=?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, galerie.getNom_g());
        pst.setString(2, galerie.getPhoto_g());
        pst.setString(3, galerie.getDesc_g());
        pst.setString(4, galerie.getType_g());
        //pst.setInt(5, galerie.getUser());
        pst.setInt(5, galerie.getId());
        pst.executeUpdate();

    }

    @Override
    public List<Galerie> selectAll() throws SQLException {
        return null;
    }

    public List<Galerie> afficher() throws SQLException {
        List<Galerie> galeries = new ArrayList<>();
        String sql = "Select * from `galerie`";
        Statement statement = connection.createStatement();
        ResultSet rs =statement.executeQuery(sql);
        while (rs.next()){
            Galerie g = new Galerie();
            g.setId(rs.getInt("id"));
            g.setNom_g(rs.getString(2));
            g.setPhoto_g(rs.getString(3));
            g.setDesc_g(rs.getString(4));
            g.setType_g(rs.getString(5));
            Personne user = new Personne();
            user.setId(rs.getInt("user_id"));
            g.setUser(user);
            galeries.add(g);
        }
        return galeries;
    }

    public Galerie obtenirGalerieParId(int id) throws SQLException {
        String sql = "SELECT * FROM `galerie` WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int idGalerie = rs.getInt("id");
                String nomGalerie = rs.getString("nom_g");
                String photoGalerie = rs.getString("photo_g");
                String descGalerie = rs.getString("desc_g");
                String typeGalerie = rs.getString("type_g");
                int userId = rs.getInt("user_id");
                Personne user =new Personne();
                user.setId(userId);
                Galerie galerie = new Galerie(nomGalerie, photoGalerie, descGalerie, typeGalerie, user);
                galerie.setId(idGalerie);
                return galerie;
            }
        }
        return null;
    }

    public boolean userHasGallery(Integer userId) {
        String req = "SELECT COUNT(*) FROM galerie WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
