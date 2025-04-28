package tn.esprit.services;

import tn.esprit.entities.Commande;
import tn.esprit.entities.Panier;
import tn.esprit.entities.PanierItem;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande implements CRUD<Commande> {

    private Connection connection;

    public ServiceCommande() {
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Commande commande) throws SQLException {
        String sql = "INSERT INTO commande (total, adresse_c, date_c, user_id) VALUES (?, ?, NOW(), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, commande.getTotal());
            stmt.setString(2, commande.getAdresse_c());
            stmt.setInt(3, commande.getUser().getId());
            stmt.executeUpdate();

            // Récupérer l'ID généré
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                commande.setId(rs.getInt(1));
            }

            System.out.println("✅ Commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Échec de l'ajout de la commande.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean supprimer(Commande commande) throws SQLException {
        return false;
    }

    @Override
    public void modifier(Commande commande) throws SQLException {
        String sql = "UPDATE commande SET total = ?, adresse_c = ?, date_c = NOW(), user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, commande.getTotal());
            stmt.setString(2, commande.getAdresse_c());
            stmt.setInt(3, commande.getUser().getId());
            stmt.setInt(4, commande.getId());
            stmt.executeUpdate();
            System.out.println("✅ Commande mise à jour !");
        }
    }

    @Override
    public List<Commande> selectAll() throws SQLException {
        return List.of();
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    public void supprimer1(Commande commande) throws SQLException {
        String sql = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commande.getId());
            stmt.executeUpdate();
            System.out.println("✅ Commande supprimée !");
        }
    }
    public ResultSet selectAll1() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM commande"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

    public int enregistrerCommande(Commande commande) {
        int generatedId = -1;

        // Validate all required fields first
        if (commande.getUser() == null) {
            System.out.println("❌ L'utilisateur est null, impossible d'enregistrer la commande.");
            return generatedId;
        }

        if (commande.getAdresse_c() == null || commande.getAdresse_c().trim().isEmpty()) {
            System.out.println("❌ L'adresse est null ou vide, impossible d'enregistrer la commande.");
            return generatedId;
        }

        String sql = "INSERT INTO commande (total, adresse_c, date_c, user_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set total (required)
            ps.setDouble(1, commande.getTotal());

            // Set address (now validated)
            ps.setString(2, commande.getAdresse_c().trim());

            // Set date (nullable)
            if (commande.getDate_c() != null) {
                ps.setDate(3, new java.sql.Date(commande.getDate_c().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            // Set user ID (validated)
            ps.setInt(4, commande.getUser().getId());

            // Execute the insert
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating command failed, no rows affected.");
            }

            // Get generated ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'enregistrement de la commande: " + e.getMessage());
            e.printStackTrace();
        }

        return generatedId;
    }

    public int confirmerCommandeAvecStocks(Commande commande, List<PanierItem> items) throws SQLException {
        Connection conn = null;
        int commandeId = -1;

        try {
            conn = DBConnection.getInstance().getCnx();
            boolean originalAutoCommit = conn.getAutoCommit();

            try {
                conn.setAutoCommit(false);

                // 1. Enregistrer la commande (avec la connexion passée)
                commandeId = enregistrerCommande(commande, conn);
                if (commandeId == -1) {
                    throw new SQLException("Échec de l'enregistrement de la commande");
                }

                // 2. Traiter chaque article
                ServiceArticle serviceArticle = new ServiceArticle();
                for (PanierItem item : items) {
                    // Vérifier et mettre à jour le stock
                    if (!serviceArticle.decrementerStock(item.getArticle().getId(), item.getQuantite(), conn)) {
                        throw new SQLException("Stock insuffisant pour l'article: " + item.getArticle().getId());
                    }

                    // Enregistrer la ligne de commande
                    enregistrerLigneCommande(commandeId, item, conn);
                }

                conn.commit();
                return commandeId;

            } catch (SQLException e) {
                if (conn != null) conn.rollback();
                throw e;
            } finally {
                if (conn != null) conn.setAutoCommit(originalAutoCommit);
            }
        } finally {
            // Ne fermez pas la connexion ici - laissez le pool de connexions gérer cela
        }
    }

    private int enregistrerCommande(Commande commande, Connection conn) throws SQLException {
        String sql = "INSERT INTO commande (total, adresse_c, date_c, user_id) VALUES (?, ?, NOW(), ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, commande.getTotal());
            ps.setString(2, commande.getAdresse_c());
            ps.setInt(3, commande.getUser().getId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("Échec de la récupération de l'ID de commande");
        }
    }

    private void enregistrerLigneCommande(int commandeId, PanierItem item, Connection conn) throws SQLException {
        String sql = "INSERT INTO ligne_commande (commande_id, article_id, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commandeId);
            ps.setInt(2, item.getArticle().getId());
            ps.setInt(3, item.getQuantite());
            ps.executeUpdate();
        }
    }

    public List<Commande> getCommandesByUser(int userId) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT c.*, u.first_name, u.last_name FROM commande c " +
                "JOIN user u ON c.user_id = u.id " +
                "WHERE c.user_id = ? ORDER BY c.date_c DESC";

        try (Connection conn = DBConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setId(rs.getInt("id"));
                commande.setDate_c(rs.getDate("date_c"));
                commande.setAdresse_c(rs.getString("adresse_c"));
                commande.setTotal(rs.getDouble("total"));

                // Création de l'utilisateur directement
                Personne user = new Personne();
                user.setId(userId);
                user.setFirst_Name(rs.getString("first_name"));
                user.setLast_Name(rs.getString("last_name"));
                commande.setUser(user);

                commandes.add(commande);
            }
        }

        return commandes;
    }



}
