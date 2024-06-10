package org.example.javafx;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Application;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {
    private static ObservableList<Clients> clientsList = FXCollections.observableArrayList();
    private static TableView.TableViewSelectionModel<Clients> selectionModel;
    private static TableView<Clients> tv;
    private static TextField nameFileTX, nameTX, phoneTX, addressTX, birthTX;
    private static File file = null;

    private EventHandler<MouseEvent> openFile = e -> {
        var objectMapper = new ObjectMapper();
        List<Clients> clientsFile;

        try{
            clientsFile = objectMapper.readValue(file, new TypeReference<>() {});
            clientsList.clear();
            clientsList.addAll(clientsFile);

            var nameFile = file.getName();
            nameFileTX.setText(nameFile);

        }catch (IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al abrir el archivo");
            alert.showAndWait();
        }
    };

    private EventHandler<MouseEvent> saveClients = e -> {
        if(!nameFileTX.getText().isEmpty()){
            var objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            var nameFile = nameFileTX.getText();

            try{
                objectMapper.writeValue(new File(nameFile), clientsList);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Datos guardados correctamente");
                alert.showAndWait();

            }catch (IOException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar el archivo");
                alert.showAndWait();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, indica el nombre del archivo para guardar los datos");
            alert.showAndWait();

            nameFileTX.setStyle("-fx-border-color: red;");
        }
    };

    private EventHandler<MouseEvent> addClients = e -> {
        String name, phone, address, birth;
        Clients client;

        name = nameTX.getText();
        address = addressTX.getText();

        try{
            phone = phoneTX.getText();

            Pattern pattern = Pattern.compile("^[0-9]{9}$");
            Matcher matcher = pattern.matcher(phone);

            if(!matcher.matches()){
                throw new IllegalArgumentException();
            }else{
                try{
                    birth = birthTX.getText();

                    pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/((195[0-9]|19[6-9][0-9]|200[0-9]|201[0-9]|202[0-4]))$");
                    matcher = pattern.matcher(birth);

                    if(!matcher.matches()){
                        throw new IllegalArgumentException();
                    }else{
                        client = new Clients(name, phone, address, birth);
                        clientsList.add(client);
                    }

                }catch (IllegalArgumentException ex2){
                    birthTX.setStyle("-fx-text-fill: red;");
                }
            }

        }catch (IllegalArgumentException ex){
            phoneTX.setStyle("-fx-text-fill: red;");

            try{
                birth = birthTX.getText();

                Pattern pattern = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/((195[0-9]|19[6-9][0-9]|200[0-9]|201[0-9]|202[0-4]))$");
                Matcher matcher = pattern.matcher(birth);

                if(!matcher.matches()){
                    throw new IllegalArgumentException();
                }

            }catch (IllegalArgumentException ex2){
                birthTX.setStyle("-fx-text-fill: red;");
            }
        }
    };

    private EventHandler<MouseEvent> deleteClient = e -> {

        if(!clientsList.isEmpty()){
            Clients selected = selectionModel.getSelectedItem();

            clientsList.remove(selected);
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "No hay elementos que eliminar");
            alert.showAndWait();
        }

    };

    private EventHandler<KeyEvent> setColor = e -> {
        TextField selected = (TextField) e.getSource();

        if(selected.equals(phoneTX)){
            phoneTX.setStyle("-fx-text-fill: black;");
        }else{
            birthTX.setStyle("-fx-text-fill: black;");
        }

    };

    private EventHandler<MouseEvent> setBorder = e -> {

        nameFileTX.setStyle("-fx-border-color: rgba(0,0,0,0);");

    };

    @Override
    public void start(Stage stage) {
        BorderPane bp = new BorderPane();

        // Diálogo de selección de archivo
        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos JSON", "*.json"));

        //Top
        Button openFileButton = new Button("Open file");
        Button saveFile = new Button("Save file");
        saveFile.setOnMouseClicked(saveClients);
        openFileButton.setOnMouseClicked(e -> {
            file = fileChooser.showOpenDialog(stage);
            if(file != null){
                openFile.handle(e);
            }
        });

        ToolBar tb;
        tb = new ToolBar(openFileButton, saveFile);

        /*Styles*/
        tb.setPadding(new Insets(10));

        bp.setTop(tb);

        //Center
        TableColumn<Clients, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Clients, String> phoneColumn = new TableColumn<>("Phone");
        TableColumn<Clients, String> addressColumn = new TableColumn<>("Address");
        TableColumn<Clients, String> birthColumn = new TableColumn<>("Birth");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        birthColumn.setCellValueFactory(new PropertyValueFactory<>("birth"));

        tv = new TableView<>();
        tv.getColumns().addAll(nameColumn,phoneColumn,addressColumn,birthColumn);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        selectionModel = tv.getSelectionModel();

        /*Mapeamos el tableview al observableList*/
        Property<ObservableList<Clients>> clientsListProperty = new SimpleListProperty<>(clientsList);
        tv.itemsProperty().bind(clientsListProperty);

        bp.setCenter(tv);

        //Left
        Label fileLabel = new Label("File name");
        nameFileTX = new TextField();
        nameFileTX.setPromptText("example.json");
        nameFileTX.setOnMouseClicked(setBorder);

        HBox hb;
        hb = new HBox(fileLabel, nameFileTX);

        Label nameLabel = new Label("Name");
        nameTX = new TextField();
        Label phoneLabel = new Label("Phone");
        phoneTX = new TextField();
        Label addressLabel = new Label("Address");
        addressTX = new TextField();
        Label birthLabel = new Label("Birth");
        birthTX = new TextField();
        birthTX.setPromptText("dd/mm/aaaa");

        phoneTX.setOnKeyPressed(setColor);
        birthTX.setOnKeyPressed(setColor);

        GridPane gp = new GridPane();
        gp.addColumn(1, nameLabel,phoneLabel,addressLabel,birthLabel);
        gp.addColumn(2, nameTX,phoneTX,addressTX,birthTX);

        Button addClientButton = new Button("Add client");
        addClientButton.setOnMouseClicked(addClients);

        VBox vb;
        vb = new VBox(hb, gp, addClientButton);

        /*Styles*/
        gp.setPadding(new Insets(40,0,0,0));
        gp.setVgap(10);
        gp.setHgap(10);
        hb.setSpacing(10);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setPadding(new Insets(10));
        vb.setSpacing(20);

        bp.setLeft(vb);

        //Bottom
        Button deleteItemButon = new Button("Remove item");
        deleteItemButon.setOnMouseClicked(deleteClient);

        bp.setBottom(deleteItemButon);

        /*Styles*/
        BorderPane.setAlignment(deleteItemButon, Pos.TOP_RIGHT);
        BorderPane.setMargin(deleteItemButon, new Insets(10));

        Scene scene = new Scene(bp, 800, 400);
        stage.setScene(scene);
        stage.setTitle("Clients App");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}