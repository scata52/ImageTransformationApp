package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ImageTransformationController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    /*@FXML
    private Button chooseInputFileBTN;

    @FXML
    private Button chooseOutputFileBTN;

    @FXML
    private Button setParametersBTN;*/

   /* @FXML
    private ChoiceBox<String> choiceBox;

    private final String[] transformationTypes = {"Gabor Transformation"};*/

    @FXML
    private ListView<String> inputFilePath;

    @FXML
    private ListView<String> outputFilePath;

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

    public void chooseInputFiles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            inputFilePath.getItems().add(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File not valid!");
        }
    }

    public void chooseOutputFiles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            outputFilePath.getItems().add(selectedFile.getAbsolutePath());
        } else {
            System.out.println("File not valid!");
        }
    }

}