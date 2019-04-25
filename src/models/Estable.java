package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.sql.*;

public class Estable {

    private IntegerProperty codiEstable;
    private StringProperty nomEstable;
    private IntegerProperty quantitat;

    public Estable(int codiEstable, String nomEstable, int quantitat){
        this.codiEstable = new SimpleIntegerProperty(codiEstable);
        this.nomEstable = new SimpleStringProperty(nomEstable);
        this.quantitat = new SimpleIntegerProperty(quantitat);
    }

    public int getCodiEstable(){
        return codiEstable.get();
    }

    public void setCodiEstable(int codiEstable) {
        this.codiEstable = new SimpleIntegerProperty(codiEstable);
    }

    public IntegerProperty codiEstableProperty() {
        return codiEstable;
    }

    public String getNomEstable(){
        return nomEstable.get();
    }

    public void setNomEstable(String nomEstable){
        this.nomEstable = new SimpleStringProperty(nomEstable);
    }

    public StringProperty nomEstableProperty() {
        return nomEstable;
    }

    public int getQuantitat(){
        return quantitat.get();
    }

    public void setQuantitat(int quantitat) {
        this.quantitat = new SimpleIntegerProperty(quantitat);
    }

    public IntegerProperty quantitatProperty() {
        return quantitat;
    }

    public int guardarRegistre(Connection connection){
        try {
            PreparedStatement instruccio = connection.prepareStatement("INSERT INTO tbl_estable (codi_estable, nom_estable, quantitat) VALUES (?, ?, ?)");
            instruccio.setInt(1, codiEstable.get());
            instruccio.setString(2, nomEstable.get());
            instruccio.setInt(3, quantitat.get());
            return instruccio.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int modificarRegistre(Connection connection){
        try {
            PreparedStatement instruccio =
                    connection.prepareStatement("UPDATE tbl_estable "+
                            "SET codi_estable = ?, "+
                            "nom_estable = ?, "+
                            "quantitat = ? "+
                            "WHERE codi_estable = ?");
            instruccio.setInt(1, codiEstable.get());
            instruccio.setString(2, nomEstable.get());
            instruccio.setInt(3, quantitat.get());
            instruccio.setInt(4, codiEstable.get());
            return instruccio.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int eliminarRegistre(Connection connection){
        try {
            PreparedStatement instruccio = connection.prepareStatement(
                    "DELETE FROM tbl_estable "+
                            "WHERE codi_estable = ?"
            );
            instruccio.setInt(1, codiEstable.get());
            return instruccio.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void omplirInformacio(Connection connection, ObservableList<Estable> llistaestable){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultat = statement.executeQuery(
                    "SELECT codi_estable, "
                            + "nom_estable, "
                            + "quantitat "
                            + "FROM tbl_estable"
            );
            while (resultat.next()){
                llistaestable.add(
                        new Estable(
                                resultat.getInt("codi_estable"),
                                resultat.getString("nom_estable"),
                                resultat.getInt("quantitat")
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public String toString(){
       return nomEstable.get()+ " " +"("+ quantitat.get()+")";
    }
}
