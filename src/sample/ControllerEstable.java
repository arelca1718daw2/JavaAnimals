package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Animal;
import models.Conexio;
import models.Estable;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEstable implements Initializable {

    //columnes tableView
    @FXML private TableColumn<Estable, Integer> clmnCodiEstable;
    @FXML private TableColumn<Estable, String> clmnNomEstable;
    @FXML private TableColumn<Estable, Integer> clmnQuantitat;

    //Components GUI
    @FXML private TextField txtCodiEstable;
    @FXML private TextField txtNomEstable;
    @FXML private TextField txtQuantitat;
    @FXML private TableView<Estable> tblViewEstable;
    @FXML private Button btnGuardar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNou;

    //Coleccions
    private ObservableList<Estable> llistatEstables;


    private Conexio conexio;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        conexio = new Conexio();
        conexio.establirConexio();

        //Inicialitzar llistes
        llistatEstables = FXCollections.observableArrayList();

        //Omplir llistes
        Estable.omplirInformacio(conexio.getConnection(),llistatEstables);

        //juntar llista amb tableView
        tblViewEstable.setItems(llistatEstables);

        //juntar columnes amb atributs
        clmnCodiEstable.setCellValueFactory(new PropertyValueFactory<Estable, Integer>("codiEstable"));;
        clmnNomEstable.setCellValueFactory(new PropertyValueFactory<Estable, String>("nomEstable"));;
        clmnQuantitat.setCellValueFactory(new PropertyValueFactory<Estable, Integer>("quantitat"));
        gestionarEsdeveniments();
        conexio.tancarConexio();

    }

    public void gestionarEsdeveniments(){
        tblViewEstable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Estable>() {
            @Override
            public void changed(ObservableValue<? extends Estable> observable, Estable oldValue, Estable newValue) {
                if(newValue!=null) {
                    txtCodiEstable.setText(String.valueOf(newValue.getCodiEstable()));
                    txtNomEstable.setText(newValue.getNomEstable());
                    txtQuantitat.setText(String.valueOf(newValue.getQuantitat()));
                    btnGuardar.setDisable(true);
                    btnEliminar.setDisable(false);
                    btnModificar.setDisable(false);
                }
            }
        });
    }

    @FXML
    public void guardarRegistreEstable(){
        //Crear una nova instancia Animal
        Estable a = new Estable(Integer.valueOf(txtCodiEstable.getText()),
                txtNomEstable.getText(),
                Integer.valueOf(txtQuantitat.getText())
        );
        conexio.establirConexio();
        int resultat = a.guardarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatEstables.add(a);
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre afegit");
            missatge.setContentText("El registre s'ha afegit a la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    public void modificarRegistreEstable(){
        Estable a = new Estable(Integer.valueOf(txtCodiEstable.getText()),
                txtNomEstable.getText(),
                Integer.valueOf(txtQuantitat.getText())
        );
        conexio.establirConexio();
        int resultat = a.modificarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatEstables.set(tblViewEstable.getSelectionModel().getSelectedIndex(), a);
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre modificat");
            missatge.setContentText("El registre s'ha modificat a la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    public void eliminarRegistreEstable(){
        conexio.establirConexio();
        int resultat = tblViewEstable.getSelectionModel().getSelectedItem().eliminarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatEstables.remove(tblViewEstable.getSelectionModel().getSelectedIndex());
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre eliminat");
            missatge.setContentText("El registre s'ha eliminat de la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    public void resetComponents(){
        txtCodiEstable.setText(null);
        txtNomEstable.setText(null);
        txtQuantitat.setText(null);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        btnModificar.setDisable(true);
    }

    //cambiar entre scenes
    public void cambiarScenaAnimals(javafx.event.ActionEvent actionEvent)throws IOException {
        AnchorPane animals = (AnchorPane) FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene sceneAnimals = new Scene(animals);

        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(sceneAnimals);
        window.show();
    }
}
