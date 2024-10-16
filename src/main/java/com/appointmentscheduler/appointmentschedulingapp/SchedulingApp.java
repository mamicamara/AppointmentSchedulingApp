package com.appointmentscheduler.appointmentschedulingapp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Locale;

public class SchedulingApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.titleProperty().bind(Translator.getStringBinding(Translator.STAGE_TITLE));

        // WindowEvent listen to prevent other Windows (Scenes) from terminating the app
        //when the top-right Exit (X) button is clicked.
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Stage stage =  ((Stage) e.getSource());
                if (scene != stage.getScene()){
                    e.consume();
                }
                stage.setScene(scene);
            }
        });

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}