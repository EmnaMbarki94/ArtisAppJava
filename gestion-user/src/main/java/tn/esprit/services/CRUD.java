package tn.esprit.services;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CRUD<T>{

    void ajouter(T t) throws SQLException;
    void supprimer(T t) throws SQLException;
    void modifier(T t) throws SQLException;
    ResultSet selectAll() throws SQLException;
    //List<T> afficher() throws SQLException;
}
