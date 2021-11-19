package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;


public class ImageTransformation extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ImageTransformationController.launchMainView(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}