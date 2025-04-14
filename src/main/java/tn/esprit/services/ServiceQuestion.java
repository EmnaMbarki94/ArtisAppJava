package tn.esprit.services;

import tn.esprit.entities.Question;
import tn.esprit.entities.Quiz;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceQuestion implements CRUD<Question> {

    private Connection connection;

    public ServiceQuestion(){
        connection = DBConnection.getInstance().getCnx();
    }

    @Override
    public void ajouter(Question question) throws SQLException {
        String sql = "INSERT INTO question (quiz_id, contenu_q, reponses) VALUES (?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        pst.setLong(1, question.getQuiz().getId());
        pst.setString(2, question.getContenu_q());

        String serializedReponses = phpSerializeStringList(question.getReponses());
        pst.setString(3, serializedReponses);

        pst.executeUpdate();

        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            int generatedId = rs.getInt(1);
            question.setId(generatedId);
        } else {
            throw new SQLException("Impossible de récupérer l'ID généré pour la question.");
        }
    }

    @Override
    public boolean supprimer(Question question) throws SQLException {
        return false;
    }


    @Override
    public void modifier(Question question) throws SQLException {

        String sql = "UPDATE `question` SET `quiz_id`=? ,`contenu_q`=?,`reponses`=? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(sql);

        pst.setInt(1, question.getQuiz().getId());
        pst.setString(2, question.getContenu_q());
        String serializedReponses = phpSerializeStringList(question.getReponses());
        pst.setString(3, serializedReponses);
        pst.setInt(4,question.getId());
        pst.executeUpdate();

    }

    @Override
    public void supprimer(int id) throws SQLException {

        String sql = "DELETE FROM `question` WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,id);
        ps.executeUpdate();

    }

    @Override
    public List<Question> selectAll() throws SQLException {
        List<Question> lquestion = new ArrayList<>();
        String sql = "SELECT q.id, q.quiz_id, q.contenu_q, q.reponses, " +
                "quiz.titre_c, quiz.cours_id " +
                "FROM question q " +
                "LEFT JOIN quiz quiz ON q.quiz_id = quiz.id";
        Statement statement = connection.createStatement();
        ResultSet rs =statement.executeQuery(sql);

        while (rs.next()){
            Question q = new Question();
            q.setId(rs.getInt("id"));
            q.setContenu_q(rs.getString("contenu_q"));

            Quiz qq = new Quiz();
            qq.setId(rs.getInt("quiz_id"));
            qq.setTitre_c(rs.getString("titre_c"));
            q.setQuiz(qq);


            String serializedAnswers = rs.getString("reponses");
            List<String> answers = phpDeserializeStringList(serializedAnswers);
            q.setReponses(answers);

            lquestion.add(q);
        }
        return lquestion;
    }
    public static String phpSerializeStringList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("a:").append(list.size()).append(":{");
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);
            sb.append("i:").append(i).append(";")
                    .append("s:").append(item.length()).append(":\"")
                    .append(item).append("\";");
        }
        sb.append("}");
        return sb.toString();
    }
    public static List<String> phpDeserializeStringList(String serialized) {
        List<String> result = new ArrayList<>();
        if (serialized == null || !serialized.startsWith("a:")) {
            return result;
        }
        try {
            // Very basic PHP array parsing logic
            String[] parts = serialized.split(";");
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].startsWith("s:")) {
                    int firstQuote = parts[i].indexOf("\"");
                    int lastQuote = parts[i].lastIndexOf("\"");
                    if (firstQuote != -1 && lastQuote != -1 && lastQuote > firstQuote) {
                        String value = parts[i].substring(firstQuote + 1, lastQuote);
                        result.add(value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to deserialize PHP array: " + serialized);
            e.printStackTrace();
        }
        return result;
    }

}
