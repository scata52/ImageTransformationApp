package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ImageTransformationController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToStartView(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("start_view.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void switchToBasicView(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("basic_view.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAdvancedView(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("advanced_view.fxml")));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}