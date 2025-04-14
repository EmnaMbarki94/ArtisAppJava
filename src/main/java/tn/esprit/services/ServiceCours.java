package tn.esprit.services;

import tn.esprit.entities.Cours;
import tn.esprit.entities.Personne;
import tn.esprit.entities.Quiz;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCours implements CRUD<Cours> {

    private Connection connection;

    public ServiceCours(){
        connection = DBConnection.getInstance().getCnx();
    }
    @Override
    public void ajouter(Cours cours) throws SQLException {
        String sql = "INSERT INTO `cours`(`nom_c`, `categ_c`, `contenu_c`, `date_c`, `heure_c`, `user_id`, `image`,`quiz_id`) VALUES ('"+cours.getNom_c()+"','"+cours.getCateg_c()+"','"+cours.getContenu_c()+"','"+cours.getDate_c()+"','"+cours.getHeure_c()+"','"+cours.getUser().getId()+"','"+cours.getImage()+"','"+cours.getQuiz().getId()+"')";
        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    @Override
    public boolean supprimer(Cours cours) throws SQLException {
        return false;
    }

    public int ajouterssq(Cours cours) throws SQLException {
        String sql = "INSERT INTO cours (nom_c, categ_c, contenu_c, date_c, heure_c, user_id, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pst.setString(1, cours.getNom_c());
        pst.setString(2, cours.getCateg_c());
        pst.setString(3, cours.getContenu_c());
        pst.setDate(4, Date.valueOf(cours.getDate_c()));
        pst.setTime(5, Time.valueOf(cours.getHeure_c()));
        pst.setInt(6, cours.getUser().getId());
        pst.setString(7, cours.getImage());
        pst.executeUpdate();


        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            cours.setId(id);
            return id;
        } else {
            throw new SQLException("Failed to retrieve ID after inserting Cours.");
        }
    }

    @Override
    public void modifier(Cours cours) throws SQLException {

        String sql = "UPDATE `cours` SET `nom_c`=? ,`categ_c`=?,`contenu_c`=?,`date_c`=?,`heure_c`=?,`user_id`=?,`image`=?,`quiz_id`=? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setString(1, cours.getNom_c());
        pst.setString(2, cours.getCateg_c());
        pst.setString(3,cours.getContenu_c());
        pst.setDate(4, Date.valueOf(cours.getDate_c()));
        pst.setTime(5, Time.valueOf(cours.getHeure_c()));
        pst.setInt(6, cours.getUser().getId());
        pst.setString(7, cours.getImage());

        if(cours.getQuiz()==null || cours.getQuiz().getTitre_c()==null){
            pst.setNull(8, Types.INTEGER);
        }
        else{
        pst.setInt(8, cours.getQuiz().getId());
        }

        pst.setInt(9,cours.getId());
        pst.executeUpdate();

    }



    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM `cours` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,id);
        ps.executeUpdate();

    }

    @Override
    public List<Cours> selectAll() throws SQLException {
        List<Cours> lcours = new ArrayList<>();
        String sql = "SELECT c.id, c.nom_c, c.categ_c, c.contenu_c, c.date_c, c.heure_c, c.image, " +
                "c.user_id, " +
                "u.first_name, u.last_name, u.email, " +
                "q.id AS quiz_id, q.titre_c " +
                "FROM cours c " +
                "LEFT JOIN quiz q ON c.quiz_id = q.id " +
                "LEFT JOIN user u ON c.user_id = u.id";
        Statement statement = connection.createStatement();
        ResultSet rs =statement.executeQuery(sql);
        while (rs.next()){
            Cours c = new Cours();
            c.setId(rs.getInt("id"));
            c.setNom_c(rs.getString("nom_c"));
            c.setCateg_c(rs.getString("categ_c"));
            c.setContenu_c(rs.getString("contenu_c"));
            c.setDate_c(rs.getDate("date_c").toLocalDate());
            c.setHeure_c(rs.getTime("heure_c").toLocalTime());
            c.setImage(rs.getString("image"));

            Personne user=new Personne();
            user.setId(rs.getInt("user_id"));
            user.setFirst_Name(rs.getString("first_name"));
            user.setLast_Name(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            c.setUser(user);


            Quiz quiz = new Quiz();
            quiz.setId(rs.getInt("quiz_id"));
            quiz.setTitre_c(rs.getString("titre_c"));

            c.setQuiz(quiz);

            lcours.add(c);
        }
        return lcours;
    }
}
