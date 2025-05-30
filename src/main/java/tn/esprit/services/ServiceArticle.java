package tn.esprit.services;

import tn.esprit.entities.Article;
import tn.esprit.entities.Magasin;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceArticle implements CRUD<Article> {
    private Connection connection;

    public ServiceArticle() {
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Article article) throws SQLException {
        String sql = "INSERT INTO article (nom_a, prix_a, desc_a, image_path, magasin_id, quantite) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, article.getNom_a());
            stmt.setDouble(2, article.getPrix_a());
            stmt.setString(3, article.getDesc_a());

            if (article.getImage_path() == null || article.getImage_path().isEmpty()) {
                stmt.setNull(4, Types.VARCHAR);
            } else {
                stmt.setString(4, article.getImage_path());
            }

//            if (article.getCommande_id() == 0) {
//                stmt.setNull(5, java.sql.Types.INTEGER);
//            } else {
//                stmt.setInt(5, article.getCommande_id());
//            }

            if (article.getMagasin().getId() == 0) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, article.getMagasin().getId());
            }

            stmt.setInt(6, article.getQuantite());

            stmt.executeUpdate();
            System.out.println("Article ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Article non ajouté");
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Article article) throws SQLException {
        String sql = "UPDATE article SET nom_a = ?, prix_a = ?, desc_a = ?, image_path = ?, magasin_id = ?, quantite = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, article.getNom_a());
            pst.setDouble(2, article.getPrix_a());
            pst.setString(3, article.getDesc_a());
            pst.setString(4, article.getImage_path());
//            pst.setInt(5, article.getCommande_id());
            pst.setInt(5, article.getMagasin().getId());
            pst.setInt(6, article.getQuantite());
            pst.setInt(7, article.getId());

            pst.executeUpdate();
            System.out.println("Article modifié avec succès !");
        }
    }


    public void supprimer1(Article article) throws SQLException {
        if (article == null || article.getId() <= 0) {
            System.err.println("Article invalide pour suppression. ID : " + (article != null ? article.getId() : "null"));
            return;
        }

        String sql = "DELETE FROM article WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, article.getId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Article supprimé avec succès !");
            } else {
                System.err.println("Aucun article trouvé avec l'ID : " + article.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'article : " + e.getMessage());
            throw e;  // Relance l'exception pour qu'elle soit capturée dans le contrôleur
        }
    }

    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM article"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

    public List<Article> getArticlesByMagasinId(int magasinId) {
        List<Article> articles = new ArrayList<>();

        try {
            String req = "SELECT * FROM article WHERE magasin_id = ?";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, magasinId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Article a = new Article();
                a.setId(rs.getInt("id"));
                a.setNom_a(rs.getString("nom_a"));
                a.setPrix_a(rs.getDouble("prix_a"));
                a.setDesc_a(rs.getString("desc_a"));
//                a.setCommande_id(rs.getInt("commande_id"));
                a.setImage_path(rs.getString("image_path"));
                Magasin magasin = new Magasin();
                magasin.setId(rs.getInt("magasin_id"));
                a.setMagasin(magasin);                a.setQuantite(rs.getInt("quantite"));

                articles.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    @Override
    public boolean supprimer(Article article) throws SQLException {
        return false;
    }

    @Override
    public List<Article> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.*, m.id as magasin_id, m.nom_m FROM article a LEFT JOIN magasin m ON a.magasin_id = m.id";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setNom_a(rs.getString("nom_a"));
                article.setPrix_a(rs.getDouble("prix_a"));
                article.setDesc_a(rs.getString("desc_a"));
                article.setImage_path(rs.getString("image_path"));
                article.setQuantite(rs.getInt("quantite"));

                // Ajout du magasin
                Magasin magasin = new Magasin();
                magasin.setId(rs.getInt("magasin_id"));
                magasin.setNom_m(rs.getString("nom_m"));
                article.setMagasin(magasin);

                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des articles: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }
    public List<Magasin> getMagasins() {
        List<Magasin> magasins = new ArrayList<>();

        String query = "SELECT DISTINCT m.* FROM magasin m JOIN article a ON m.id = a.magasin_id";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Magasin magasin = new Magasin();
                magasin.setId(rs.getInt("id"));
                magasin.setNom_m(rs.getString("nom_m")); // Corrigé: utiliser "nom_m" au lieu de "nom"
                magasins.add(magasin);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des magasins: " + e.getMessage());
            e.printStackTrace();
        }

        return magasins;
    }

    public boolean decrementerStock(int articleId, int quantite, Connection conn) throws SQLException {
        String sql = "UPDATE article SET quantite = quantite - ? WHERE id = ? AND quantite >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantite);
            ps.setInt(2, articleId);
            ps.setInt(3, quantite);
            return ps.executeUpdate() > 0;
        }
    }
    public Article getArticleById(int id, Connection conn) throws SQLException {
        String sql = "SELECT quantite FROM article WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Article a = new Article();
                a.setId(id);
                a.setQuantite(rs.getInt("quantite"));
                return a;
            }
        }
        return null;
    }
}
