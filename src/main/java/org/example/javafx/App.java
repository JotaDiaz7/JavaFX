package org.example.javafx;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App extends Application {
    private static ObservableList<Clients> clientsList = FXCollections.observableArrayList();
    private static TableView<Clients> tv;
    private static TextField nameTX, phoneTX, addressTX, birthTX;
    private EventHandler<MouseEvent> saveClients = e -> {
        var objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try{
            objectMapper.writeValue(new File("clients.json"), clientsList);

        }catch (IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar el archivo");
            alert.showAndWait();
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

                    pattern = Pattern.compile("\\d{0,2}/\\d{0,2}/\\d{0,4}");
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

                Pattern pattern = Pattern.compile("\\d{0,2}/\\d{0,2}/\\d{0,4}");
                Matcher matcher = pattern.matcher(birth);

                if(!matcher.matches()){
                    throw new IllegalArgumentException();
                }

            }catch (IllegalArgumentException ex2){
                birthTX.setStyle("-fx-text-fill: red;");
            }

        }

    };

    @Override
    public void start(Stage stage) {
        BorderPane bp = new BorderPane();

        //Top
        Button openFile = new Button("Open file");
        Button saveFile = new Button("Save file");
        saveFile.setOnMouseClicked(saveClients);

        ToolBar tb;
        tb = new ToolBar(openFile, saveFile);

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

        /*Mapeamos el tableview al observableList*/
        Property<ObservableList<Clients>> clientsListProperty = new SimpleListProperty<>(clientsList);
        tv.itemsProperty().bind(clientsListProperty);

        bp.setCenter(tv);

        //Left
        Label nameLabel = new Label("Name");
        nameTX = new TextField();
        Label phoneLabel = new Label("Phone");
        phoneTX = new TextField();
        Label addressLabel = new Label("Address");
        addressTX = new TextField();
        Label birthLabel = new Label("Birth");
        birthTX = new TextField();

        GridPane gp = new GridPane();
        gp.addColumn(1, nameLabel,phoneLabel,addressLabel,birthLabel);
        gp.addColumn(2, nameTX,phoneTX,addressTX,birthTX);

        Button addClientButton = new Button("Add client");
        addClientButton.setOnMouseClicked(addClients);

        VBox vb;
        vb = new VBox(gp, addClientButton);

        /*Styles*/
        gp.setVgap(10);
        gp.setHgap(10);
        vb.setAlignment(Pos.CENTER);
        vb.setPadding(new Insets(10));
        vb.setSpacing(20);

        bp.setLeft(vb);

        //Bottom

        Scene scene = new Scene(bp, 800, 400);
        stage.setScene(scene);
        stage.setTitle("Clients App");
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}