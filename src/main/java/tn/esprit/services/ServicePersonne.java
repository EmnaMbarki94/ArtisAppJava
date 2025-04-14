package tn.esprit.services;

import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicePersonne implements CRUD<Personne> {
    private final Connection cnx;

    public ServicePersonne() {
        this.cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Personne user) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, num_tel, address, verification_code, is_verified, user_agent, specialite, point, cin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getRoles());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFirst_Name());
            ps.setString(5, user.getLast_Name());
            ps.setString(6, user.getNum_tel());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getVerificationCode());
            ps.setInt(9, user.getIs_verified());
            ps.setString(10, user.getUserAgent());
            ps.setString(11, user.getSpecialite());
            ps.setInt(12, user.getPoint() != null ? user.getPoint() : 0);
            ps.setString(13, user.getCin());

            ps.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public boolean supprimer(Personne user) {
        String req = "DELETE FROM user WHERE email = ?";

        try (/*Connection cnx = DBConnection.getInstance().getCnx();*/
             PreparedStatement ps = cnx.prepareStatement(req)) {

            ps.setString(1, user.getEmail());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch(SQLException e) {
            System.err.println("Erreur SQL suppression: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void modifier(Personne user) {
        String req = "UPDATE user SET roles=?, password=?, cin=?, last_name=?, first_name=?, num_tel=?, address=?, verification_code=?, is_verified=? WHERE email=?";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getRoles());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.getCin());
            ps.setString(4, user.getLast_Name());
            ps.setString(5, user.getFirst_Name());
            ps.setString(6, user.getNum_tel());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getVerificationCode());
            ps.setInt(9, user.getIs_verified());
            ps.setString(10, user.getEmail());

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Mise à jour réussie" : "Échec de la mise à jour");
        } catch (SQLException e) {
            System.err.println("Erreur de modification : " + e.getMessage());
        }
    }

    @Override
    public List<Personne> selectAll() {
        List<Personne> personnes = new ArrayList<>();

        try (/*Connection cnx = DBConnection.getInstance().getCnx();*/
             PreparedStatement ps = cnx.prepareStatement("SELECT * FROM user");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    personnes.add(mapResultSetToPersonne(rs));
                } catch (SQLException e) {
                    System.err.println("Erreur de mapping sur la ligne " + rs.getRow());
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL globale [selectAll()]: " + e.getMessage());
        }

        return Collections.unmodifiableList(personnes);
    }

    @Override
    public void supprimer(int id) throws SQLException {

    }

    private Personne mapResultSetToPersonne(ResultSet rs) throws SQLException {
        Personne p = new Personne();
        p.setId(rs.getInt("id"));
        p.setEmail(rs.getString("email"));
        p.setRoles(rs.getString("roles"));
        p.setFirst_Name(rs.getString("first_name"));
        p.setLast_Name(rs.getString("last_name"));
        p.setPassword(rs.getString("password"));
        p.setNum_tel(rs.getString("num_tel"));
        p.setCin(rs.getString("cin"));
        p.setAddress(rs.getString("address"));
        p.setIs_verified(rs.getInt("is_verified"));
        p.setSpecialite(rs.getString("specialite"));
        p.setPoint(rs.getInt("point"));
        return p;
    }

    public Personne Login(String email, String password) {
        String req = "SELECT * FROM user WHERE email=? ";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Personne user = new Personne();
                    System.out.println(password);
                    mapResultSetToPersonne(rs, user);
                    System.out.println(user.getPassword());

                    boolean test=BCrypt.checkpw(password, user.getPassword());
                     System.out.println(test);
                     System.out.println(user);
                     return test? user : null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de login : " + e.getMessage());
        }
        return null;
    }

    public Personne searchByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";

        try (/*Connection cnx = DBConnection.getInstance().getCnx();*/
             PreparedStatement ps = cnx.prepareStatement(query)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    Personne user = new Personne();
                    // ... remplir les champs ...
                    return user;
                }
            }
        } catch(SQLException e) {
            System.err.println("Erreur de recherche: " + e.getMessage());
            // Optionnel: logger l'erreur
        }
        return null;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur emailExists : " + e.getMessage());
        }
        return false;
    }

    public boolean cinExists(String cin) {
        String query = "SELECT COUNT(*) FROM user WHERE cin = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur cinExists : " + e.getMessage());
        }
        return false;
    }

    private void mapResultSetToPersonne(ResultSet rs, Personne p) throws SQLException {
        p.setId(rs.getInt("id"));
        p.setEmail(rs.getString("email"));
        p.setRoles(rs.getString("roles"));
        p.setLast_Name(rs.getString("last_name"));
        p.setFirst_Name(rs.getString("first_name"));
        p.setPassword(rs.getString("password"));
        p.setNum_tel(rs.getString("num_tel"));
        p.setCin(rs.getString("cin"));
        p.setAddress(rs.getString("address"));
        p.setIs_verified(rs.getInt("is_verified"));
        p.setSpecialite(rs.getString("specialite"));
        p.setPoint(rs.getInt("point"));
    }
}