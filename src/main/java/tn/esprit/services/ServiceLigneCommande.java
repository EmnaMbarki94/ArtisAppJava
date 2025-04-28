package tn.esprit.services;

import tn.esprit.entities.Article;
import tn.esprit.entities.ArticleStatDTO;
import tn.esprit.entities.Commande;
import tn.esprit.entities.LigneCommande;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLigneCommande implements CRUD<LigneCommande> {

    private Connection connection;

    public ServiceLigneCommande() {
        connection = DBConnection.getInstance().getCnx();
    }

    public void ajouter(LigneCommande ligneCommande) throws SQLException {
        String sql = "INSERT INTO ligne_commande (commande_id, article_id, quantite) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligneCommande.getCommande().getId());
            stmt.setInt(2, ligneCommande.getArticle().getId());
            stmt.setInt(3, ligneCommande.getQuantite());
            stmt.executeUpdate();
            System.out.println("✅ Ligne de commande ajoutée !");
        }
    }

    @Override
    public boolean supprimer(LigneCommande ligneCommande) throws SQLException {
        return false;
    }

    public void modifier(LigneCommande ligneCommande) throws SQLException {
        String sql = "UPDATE ligne_commande SET quantite = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligneCommande.getQuantite());
            stmt.setInt(2, ligneCommande.getId());
            stmt.executeUpdate();
            System.out.println("✅ Ligne de commande mise à jour !");
        }
    }

    @Override
    public List<LigneCommande> selectAll() throws SQLException {
        return List.of();
    }

    public void supprimer1(LigneCommande ligneCommande) throws SQLException {
        String sql = "DELETE FROM ligne_commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ligneCommande.getId());
            stmt.executeUpdate();
            System.out.println("✅ Ligne de commande supprimée !");
        }
    }

    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM ligne_commande"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

    @Override
    public void supprimer(int id) throws SQLException {
        return;
    }

    public List<LigneCommande> afficher() throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM ligne_commande";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            LigneCommande lc = new LigneCommande();

            lc.setId(rs.getInt("id"));
            lc.getCommande().setId(rs.getInt("commande_id"));
            lc.getArticle().setId(rs.getInt("article_id"));
            lc.setQuantite(rs.getInt("quantite"));
            lignes.add(lc);
        }
        return lignes;
    }

//
//public List<LigneCommande> afficherParCommande(int commandeId) throws SQLException {
//    List<LigneCommande> lignes = new ArrayList<>();
//    String sql = "SELECT * FROM ligne_commande WHERE commande_id = ?";
//
//    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//        stmt.setInt(1, commandeId);
//        ResultSet rs = stmt.executeQuery();
//
//        while (rs.next()) {
//            LigneCommande lc = new LigneCommande();
//
//            // Initialisation de l'objet Commande avant de lui attribuer l'id
//            if (lc.getCommande() == null) {
//                lc.setCommande(new Commande());  // Assure-toi que Commande est bien initialisé
//            }
//
//            lc.setId(rs.getInt("id"));
//            lc.getCommande().setId(rs.getInt("commande_id"));  // Maintenant tu peux lui assigner l'id
//            lc.getArticle().setId(rs.getInt("article_id"));
//            lc.setQuantite(rs.getInt("quantite"));
//            lignes.add(lc);
//        }
//    }
//    return lignes;
//}
public List<LigneCommande> afficherParCommande(int commandeId) throws SQLException {
    List<LigneCommande> lignes = new ArrayList<>();
    String sql = "SELECT lc.*, a.nom_a, a.prix_a FROM ligne_commande lc " +
            "INNER JOIN article a ON lc.article_id = a.id " +
            "WHERE lc.commande_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, commandeId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            LigneCommande lc = new LigneCommande();
            lc.setId(rs.getInt("id"));

            // Initialisez complètement l'article
            Article article = new Article();
            article.setId(rs.getInt("article_id"));
            article.setNom_a(rs.getString("nom_a"));
            article.setPrix_a(rs.getDouble("prix_a"));

            lc.setArticle(article);
            lc.setQuantite(rs.getInt("quantite"));
            lignes.add(lc);
        }
    }
    return lignes;
}


    //Stat
    public List<ArticleStatDTO> getTopArticlesVendusAvecPrix(int limit) throws SQLException {
        List<ArticleStatDTO> topArticles = new ArrayList<>();
        String sql = "SELECT a.id, a.nom_a, a.prix_a, SUM(lc.quantite) as total_vendu " +
                "FROM ligne_commande lc " +
                "INNER JOIN article a ON lc.article_id = a.id " +
                "GROUP BY a.id, a.nom_a, a.prix_a " +
                "ORDER BY total_vendu DESC " +
                "LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                topArticles.add(new ArticleStatDTO(
                        rs.getInt("id"),
                        rs.getString("nom_a"),
                        rs.getDouble("prix_a"),
                        rs.getInt("total_vendu") // Utilise le constructeur avec prix
                ));
            }
        }
        return topArticles;
    }

}
