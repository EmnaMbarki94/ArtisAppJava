package tn.esprit.services;
import tn.esprit.entities.Galerie;
import tn.esprit.entities.Piece_art;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePieceArt implements CRUD <Piece_art> {
    private Connection connection;

    @Override
    public boolean supprimer(Piece_art pieceArt) throws SQLException {
        return false;
    }

    @Override
    public List<Piece_art> selectAll() throws SQLException {
        return null;
    }

    public ServicePieceArt() {
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Piece_art pieceArt) throws SQLException {
        String sql = "INSERT INTO `piece_art`(`nom_p`, `date_crea`, `photo_p`, `desc_p`, `galerie_id`) VALUES ('"+pieceArt.getNom_p()+"','"+pieceArt.getDate_crea()+"','"+pieceArt.getPhoto_p()+"','"+pieceArt.getDesc_p()+"',"+pieceArt.getGalerie().getId()+")";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    @Override
    public void modifier(Piece_art pieceArt) throws SQLException {
        String sql = "UPDATE `piece_art` SET `nom_p`=?, `date_crea`=?, `photo_p`=?, `desc_p`=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, pieceArt.getNom_p());
            pst.setDate(2, Date.valueOf(pieceArt.getDate_crea()));  // Convert LocalDate to SQL Date
            pst.setString(3, pieceArt.getPhoto_p());
            pst.setString(4, pieceArt.getDesc_p());
            // pst.setInt(5, pieceArt.getGalerie().getId());
            pst.setInt(5, pieceArt.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        // Suppression des commentaires associés à la pièce
        String sqlComment = "DELETE FROM `comment` WHERE piece_art_id = ?";
        try (PreparedStatement psComment = connection.prepareStatement(sqlComment)) {
            psComment.setInt(1, id);
            psComment.executeUpdate();
        }
        String sql = "DELETE FROM `piece_art` WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setInt(1, id);
        pst.executeUpdate();
    }


    public List<Piece_art> afficher() throws SQLException {
        List<Piece_art> piecesArt = new ArrayList<>();
        String sql = "SELECT * FROM `piece_art`";
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Piece_art piece = new Piece_art();
                piece.setId(rs.getInt("id"));
                piece.setNom_p(rs.getString("nom_p"));
                piece.setDate_crea(rs.getDate("date_crea").toLocalDate()); // Convert SQL Date to LocalDate
                piece.setPhoto_p(rs.getString("photo_p"));
                piece.setDesc_p(rs.getString("desc_p"));
                int galerieId = rs.getInt("galerie_id");
                // Obtenir la galerie correspondante
                ServiceGalerie serviceGalerie = new ServiceGalerie();
                Galerie galerie = serviceGalerie.obtenirGalerieParId(galerieId);
                if (galerie != null) {
                    piece.setGalerie(galerie); // Assurez-vous que setGalerie() est disponible
                } else {
                    System.out.println("Galerie non trouvée pour l'ID: " + galerieId);
                }
                piecesArt.add(piece);
            }
        }
        return piecesArt;
    }

    public List<Piece_art> afficherByGalerieId(int galerieId) throws SQLException {
        List<Piece_art> piecesArt = new ArrayList<>();
        String sql = "SELECT * FROM piece_art WHERE galerie_id = ?"; // Récupère les pièces d'art selon l'ID de galerie

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, galerieId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                Piece_art piece = new Piece_art();
                piece.setId(rs.getInt("id"));
                piece.setNom_p(rs.getString("nom_p"));
                piece.setDate_crea(rs.getDate("date_crea").toLocalDate());
                piece.setPhoto_p(rs.getString("photo_p"));
                piece.setDesc_p(rs.getString("desc_p"));
                int galerie_id = rs.getInt("galerie_id");
                // Obtenir la galerie correspondante
                ServiceGalerie serviceGalerie = new ServiceGalerie();
                Galerie galerie = serviceGalerie.obtenirGalerieParId(galerie_id);
                if (galerie != null) {
                    piece.setGalerie(galerie); // Assurez-vous que setGalerie() est disponible
                } else {
                    System.out.println("Galerie non trouvée pour l'ID: " + galerieId);
                }
                piecesArt.add(piece);
            }
        }
        return piecesArt;
    }

    public Piece_art obtenirPieceParId(int id) throws SQLException {
        Piece_art pieceArt = null;
        String sql = "SELECT * FROM piece_art WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                pieceArt = new Piece_art();
                pieceArt.setId(rs.getInt("id"));
                pieceArt.setNom_p(rs.getString("nom_p"));
                pieceArt.setDate_crea(rs.getDate("date_crea").toLocalDate());
                pieceArt.setPhoto_p(rs.getString("photo_p"));
                pieceArt.setDesc_p(rs.getString("desc_p"));
            }
        }
        return pieceArt;
    }

    public int obtenirGalerieIdParPieceArtId(int pieceArtId) throws SQLException {
        String sql = "SELECT galerie_id FROM piece_art WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, pieceArtId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("galerie_id");
            }
        }
        return -1;
    }
}
