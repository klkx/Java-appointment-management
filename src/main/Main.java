package main;

import db.DBConnection;
import utility.UtFuns;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("../views/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Appointment Management");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        System.out.println(Locale.getDefault());

        DBConnection.cnntion();
        launch();
    }
}