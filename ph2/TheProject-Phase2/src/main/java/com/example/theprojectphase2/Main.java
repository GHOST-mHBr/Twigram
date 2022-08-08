package com.example.theprojectphase2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 550);

        stage.setTitle("Twitter");
        stage.getIcons().add(new Image("D:\\University\\semester 2\\ProjDir\\TheProject-Phase2\\src\\main\\resources\\com\\example\\theprojectphase2\\twitter-logo.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}