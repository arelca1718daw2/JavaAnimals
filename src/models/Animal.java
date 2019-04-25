package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.sql.*;

public class Animal {
    private IntegerProperty codiAnimal;
    private StringProperty nomAnimal;
    private Date dataIngres;
    private Estable estable;

    public Animal(int codiAnimal, String nomAnimal, Date dataIngres, Estable estable){
        this.codiAnimal = new SimpleIntegerProperty(codiAnimal);
        this.nomAnimal = new SimpleStringProperty(nomAnimal);
        this.dataIngres = dataIngres;
        this.estable = estable;
    }

    public int getCodiAnimal(){
        return codiAnimal.get();
    }

    public void setCodiAnimal(int codiAnimal) {
        this.codiAnimal = new SimpleIntegerProperty(codiAnimal);
    }

    public IntegerProperty codiAnimalProperty() {
        return codiAnimal;
    }

    public String getNomAnimal(){
        return nomAnimal.get();
    }

    public void setNomAnimal(String nomAnimal){
        this.nomAnimal = new SimpleStringProperty(nomAnimal);
    }

    public StringProperty nomAnimalProperty() {
        return nomAnimal;
    }

    public Estable getEstable(){
        return estable;
    }

    public void setEstable(Estable estable) {
        this.estable = estable;
    }

    public Date getDataIngres(){
        return dataIngres;
    }

    public void setDataIngresIngres(Date dataIngres){
        this.dataIngres = dataIngres;
    }

    public int guardarRegistre(Connection connection){
        try {
            PreparedStatement instruccio = connection.prepareStatement("INSERT INTO tbl_animal (codi_animal, nom_animal, codi_estable, data_ingres) VALUES (?, ?, ?, ?)");
            instruccio.setInt(1, codiAnimal.get());
            instruccio.setString(2, nomAnimal.get());
            instruccio.setInt(3, estable.getCodiEstable());
            instruccio.setDate(4, dataIngres);
            return instruccio.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int modificarRegistre(Connection connection){
        try {
            PreparedStatement instruccio =
                    connection.prepareStatement("UPDATE tbl_animal SET codi_animal = ?, nom_animal = ?, codi_estable = ?, data_ingres = ? WHERE codi_animal = ?");
            instruccio.setInt(1, codiAnimal.get());
            instruccio.setString(2, nomAnimal.get());
            instruccio.setInt(3, estable.getCodiEstable());
            instruccio.setDate(4, dataIngres);
            instruccio.setInt(5, codiAnimal.get());
            return instruccio.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int eliminarRegistre(Connection connection){
        try {
            PreparedStatement instruccio = connection.prepareStatement(
                    "DELETE FROM tbl_animal "+
                            "WHERE codi_animal = ?"
            );
            instruccio.setInt(1, codiAnimal.get());
            return instruccio.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void omplirInformacioAnimal(Connection connection, ObservableList<Animal> llistanimal){
        try {
            Statement instruccio = connection.createStatement();
            ResultSet resultat = instruccio.executeQuery(
                    "SELECT  a.codi_animal, " +
                            "a.nom_animal, " +
                            "a.codi_estable, " +
                            "a.data_ingres, " +
                            "b.nom_estable, " +
                            "b.quantitat " +
                            "FROM tbl_animal a " +
                            "INNER JOIN tbl_estable b " +
                            "ON (a.codi_estable = b.codi_estable)"
            );
            while (resultat.next()){
                llistanimal.add(
                        new Animal(
                                resultat.getInt("codi_animal"),
                                resultat.getString("nom_animal"),
                                resultat.getDate("data_ingres"),
                                new Estable(resultat.getInt("codi_estable"),resultat.getString("nom_estable"),resultat.getInt("quantitat"))
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
