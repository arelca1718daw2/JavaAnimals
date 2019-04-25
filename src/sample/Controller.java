package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Animal;
import java.sql.Date;
import models.Conexio;
import models.Estable;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    //columnes tableView
    @FXML private TableColumn<Animal, Integer> clmnCodiAnimal;
    @FXML private TableColumn<Animal, String> clmnNom;
    @FXML private TableColumn<Animal, Estable> clmnCodiEstable;
    @FXML private TableColumn<Animal, Date> clmnDataIngres;

    //Components GUI
    @FXML private TextField txtCodiAnimal;
    @FXML private TextField txtNomAnimal;
    @FXML private ComboBox<Estable> cmbEstable;
    @FXML private TableView<Animal> tblViewAnimal;
    @FXML private Button btnGuardar;
    @FXML private Button btnModificar;
    @FXML private Button btnEliminar;
    @FXML private TextField txtFilter;
    @FXML private DatePicker Data_Ingres;
    @FXML private TextField txtFilterDate;

    //Coleccions
    private ObservableList<Estable> llistatEstables;
    private ObservableList<Animal> llistatAnimal;

    private Conexio conexio;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conexio = new Conexio();
        conexio.establirConexio();

        //Inicialitzar llistes
        llistatEstables = FXCollections.observableArrayList();
        llistatAnimal = FXCollections.observableArrayList();

        //Omplir llistes
        Estable.omplirInformacio(conexio.getConnection(),llistatEstables);
        Animal.omplirInformacioAnimal(conexio.getConnection(),llistatAnimal);

        //juntar llista amb comboBox i tableView
        cmbEstable.setItems(llistatEstables);
        tblViewAnimal.setItems(llistatAnimal);

        //juntar columnes amb atributs
        clmnCodiAnimal.setCellValueFactory(new PropertyValueFactory<Animal, Integer>("codiAnimal"));
        clmnNom.setCellValueFactory(new PropertyValueFactory<Animal, String>("nomAnimal"));
        clmnCodiEstable.setCellValueFactory(new PropertyValueFactory<Animal, Estable>("estable"));
        clmnDataIngres.setCellValueFactory(new PropertyValueFactory<Animal, Date>("dataIngres"));
        gestionarEsdeveniments();
        conexio.tancarConexio();
    }

    public void gestionarEsdeveniments(){
        tblViewAnimal.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Animal>() {
            @Override
            public void changed(ObservableValue<? extends Animal> observable, Animal oldValue, Animal newValue) {
                if(newValue!=null) {
                    txtCodiAnimal.setText(String.valueOf(newValue.getCodiAnimal()));
                    txtNomAnimal.setText(newValue.getNomAnimal());
                    cmbEstable.setValue(newValue.getEstable());
                    Data_Ingres.setValue(newValue.getDataIngres().toLocalDate());
                    btnGuardar.setDisable(true);
                    btnEliminar.setDisable(false);
                    btnModificar.setDisable(false);
                }
            }
        });
    }

    @FXML
    public void guardarRegistreAnimal(){
        //Crear una nova instancia Animal
        Animal a = new Animal(Integer.valueOf(txtCodiAnimal.getText()),
                txtNomAnimal.getText(),
                Date.valueOf(Data_Ingres.getValue()),
                cmbEstable.getSelectionModel().getSelectedItem()

        );
        conexio.establirConexio();
        int resultat = a.guardarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatAnimal.add(a);
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre afegit");
            missatge.setContentText("El registre s'ha afegit a la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    public void modificarRegistreAnimal(){
        Animal a = new Animal(
                Integer.valueOf(txtCodiAnimal.getText()),
                txtNomAnimal.getText(),
                Date.valueOf(Data_Ingres.getValue()),
                cmbEstable.getSelectionModel().getSelectedItem()
        );
        conexio.establirConexio();
        int resultat = a.modificarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatAnimal.set(tblViewAnimal.getSelectionModel().getSelectedIndex(), a);
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre modificat");
            missatge.setContentText("El registre s'ha modificat a la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    public void eliminarRegistreAnimal(){
        conexio.establirConexio();
        int resultat = tblViewAnimal.getSelectionModel().getSelectedItem().eliminarRegistre(conexio.getConnection());
        conexio.tancarConexio();

        if(resultat == 1){
            llistatAnimal.remove(tblViewAnimal.getSelectionModel().getSelectedIndex());
            Alert missatge = new Alert(AlertType.INFORMATION);
            missatge.setTitle("Registre eliminat");
            missatge.setContentText("El registre s'ha eliminat de la bd correctament");
            missatge.setHeaderText("Resultat: ");
            missatge.show();
        }
    }

    @FXML
    private void filtrarParaula(KeyEvent key){
        FilteredList<Animal> filterData = new FilteredList<>(llistatAnimal, p ->true);

        txtFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(Animal -> {

                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                String typedText = newValue.toLowerCase();

                if(Animal.getEstable().getNomEstable().toLowerCase().indexOf(typedText) != -1){
                    return true;
                }

                return false;
            });
            SortedList<Animal> sortedList = new SortedList<>(filterData);
            sortedList.comparatorProperty().bind(tblViewAnimal.comparatorProperty());
            tblViewAnimal.setItems(sortedList);
        });
    }

    @FXML
    private void filtrarData(KeyEvent key){
        FilteredList<Animal> filterData = new FilteredList<>(llistatAnimal, p ->true);

        txtFilterDate.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData.setPredicate(Animal -> {

                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                String typedText = newValue.toLowerCase();

                if(Animal.getDataIngres().toLocalDate().toString().indexOf(typedText) != -1){
                    return true;
                }

                return false;
            });
            SortedList<Animal> sortedList = new SortedList<>(filterData);
            sortedList.comparatorProperty().bind(tblViewAnimal.comparatorProperty());
            tblViewAnimal.setItems(sortedList);
        });
    }

    @FXML
    public void mostrarRows(){
        int a = tblViewAnimal.getItems().size();
        int b = tblViewAnimal.getSelectionModel().getTableView().getItems().get(0).getEstable().getQuantitat();
        int resultat = b - a;
        if(resultat == 0){
            System.out.println("Esta ple l'estable: "+ tblViewAnimal.getSelectionModel().getTableView().getItems().get(0).getEstable().getNomEstable());
        } else {
            System.out.println("En l'estable "+ tblViewAnimal.getSelectionModel().getTableView().getItems().get(0).getEstable().getNomEstable() +" hi ha espai per a: " + String.valueOf(resultat)+ " animals");
        }
    }

    @FXML
    public void resetComponents(){
        txtCodiAnimal.setText(null);
        txtNomAnimal.setText(null);
        cmbEstable.setValue(null);
        Data_Ingres.setValue(null);
        btnGuardar.setDisable(false);
        btnEliminar.setDisable(true);
        btnModificar.setDisable(true);
    }



    //cambiar entre scenes
    public void cambiarScenaEstables(javafx.event.ActionEvent actionEvent)throws IOException {
        AnchorPane estables = (AnchorPane)FXMLLoader.load(getClass().getResource("Estable.fxml"));
        Scene sceneEstables = new Scene(estables);

        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(sceneEstables);
        window.show();
    }
}
