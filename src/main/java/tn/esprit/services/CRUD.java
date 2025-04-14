package tn.esprit.services;

import tn.esprit.entities.Personne;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CRUD<T>{

    void ajouter(T t) throws SQLException;
    boolean supprimer(T t) throws SQLException;
    void modifier(T t) throws SQLException;
    public List<Personne> selectAll() throws SQLException;
    //List<T> afficher() throws SQLException;
}
