package tn.esprit.services;

import tn.esprit.entities.Personne;
import tn.esprit.utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServicePersonne implements CRUD<Personne> {
    private Connection cnx;

    public ServicePersonne(){
        cnx = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Personne user) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, num_tel, address, verification_code, is_verified, user_agent, specialite, point, cin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cnx = DBConnection.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(req)) {

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

            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected > 0 ? "User added successfully" : "Failed to add user");

        } catch (SQLException e) {
            System.out.println("Error occurred while adding user: " + e.getMessage());
        }
    }


    @Override
    public void supprimer(Personne user) throws SQLException {
        String req = "DELETE FROM user WHERE email = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getEmail());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully");
            } else {
                System.out.println("User not found or failed to delete");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while deleting user: " + e.getMessage());
        }
    }




    @Override
    public void modifier(Personne user) throws SQLException {
        String req = "UPDATE user SET roles = ?, password = ?, cin = ?, last_name = ?, first_name = ?, " +
                "num_tel = ?, address = ?, verification_code = ?, is_verified = ? WHERE email = ?";

        try (Connection cnx = DBConnection.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(req)) {

            ps.setString(1, user.getRoles());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getCin());
            ps.setString(4, user.getLast_Name());
            ps.setString(5, user.getFirst_Name());
            ps.setString(6, user.getNum_tel());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getVerificationCode());
            ps.setInt(9, user.getIs_verified());
            ps.setString(10, user.getEmail());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update successful");
            } else {
                System.out.println("User not found or update failed");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while updating user: " + e.getMessage());
        }

    }

    @Override
    public ResultSet selectAll() throws SQLException {
        ResultSet rs = null;
        String query = "SELECT * FROM user"; // Assuming your table name is 'user'

        try {
            PreparedStatement ps = cnx.prepareStatement(query);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred while selecting all users: " + e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        }

        return rs;
    }

    public Personne Login(String email, String password)
    {
        Personne user = new Personne();

        String req = "SELECT * FROM user WHERE email = ? AND  password = ?";
        try(PreparedStatement ps = cnx.prepareStatement(req)){;
            ps.setString(1,email);
            ps.setString(2,password);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                user = new Personne();
                user.setEmail(resultSet.getString("email"));
                user.setRoles(resultSet.getString("roles"));
                user.setLast_Name(resultSet.getString("last_name"));
                user.setFirst_Name(resultSet.getString("first_name"));
                user.setPassword(resultSet.getString("password"));
                user.setNum_tel(resultSet.getString("num_tel"));
                user.setCin(resultSet.getString("cin"));
                user.setAddress(resultSet.getString("address"));
                user.setIs_verified(resultSet.getInt("is_verified"));
            }
            else {
                user = null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);

        }
        return user ;

    }
    public Personne searchByEmail(String email) throws SQLException {
        Personne user = null;
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                user = new Personne();
                user.setEmail(resultSet.getString("email"));
                user.setRoles(resultSet.getString("roles"));
                user.setLast_Name(resultSet.getString("last_name"));
                user.setFirst_Name(resultSet.getString("first_name"));
                user.setPassword(resultSet.getString("password"));
                user.setNum_tel(resultSet.getString("num_tel"));
                user.setCin(resultSet.getString("cin"));
                user.setAddress(resultSet.getString("address"));
                user.setIs_verified(resultSet.getInt("is_verified"));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while searching for user by email: " + e.getMessage());
            throw e;
        }

        return user;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking email existence: " + e.getMessage());
        }
        return false;
    }
    public boolean cinExists(String cin) {
        String query = "SELECT COUNT(*) FROM user WHERE cin = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, cin);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking cin existence: " + e.getMessage());
        }
        return false;
    }


}

