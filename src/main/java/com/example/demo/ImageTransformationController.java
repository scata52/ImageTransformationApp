package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ImageTransformationController implements Initializable{


    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextArea selectedFileText;

    @FXML
    private TextArea outputFolderText;

    @FXML
    private TextField TTypeSizeField;

    @FXML
    private TextField TTypeDepthField;

    @FXML
    private TextField EntropyField;

    @FXML
    private TextField StabilityField;

    @FXML
    private TextField RandomnessField;

    private String selectedFileName;

    private final String[] transformationTypes = {"Gabor Transformation", "Daubechies wavelet", "B-spline wavelet", "Apply All"};
    private final String[] commands = {"octave-cli"}; // "cmd.exe", "/k", "start",

    static void launchMainView(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource(
                "main_view.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Image Transformation");
        stage.setScene(scene);
        stage.show();
    }


    public void switchToMainView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("main_view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void chooseInputFiles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            selectedFileName = selectedFile.getName();
            selectedFileText.setText(selectedFileName);
        } else {
            System.out.println("File not valid!");
        }
    }

    public void chooseOutputFiles(ActionEvent event) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File defaultDirectory = new File("c:/");
        fileChooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = fileChooser.showDialog(null);
        outputFolderText.setText(selectedDirectory.getAbsolutePath());

    }

    public void setDefaultTTypeValues(ActionEvent event) {
        TTypeSizeField.setText("1");
        TTypeDepthField.setText("1");
    }

    public void setDefaultAnalysisValues(ActionEvent event) {
        EntropyField.setText("1");
        StabilityField.setText("1");
        RandomnessField.setText("1");
    }


    public void startTransform(ActionEvent event) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        builder.directory(new File("C:\\Users\\Cem Atalay\\Desktop\\Test (1)"));
        builder.redirectErrorStream(true);

        Process process;
        try {
            process = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream ()));
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));


            writer.write("pkg load ltfat");
            writer.newLine();

            writer.write("imagetransform(\""+selectedFileName+"\",\"gabor\",1)");
            writer.newLine();

//            writer.write("plot([1,2,3])");
//            writer.newLine();

            writer.write("print -djpg Transformed_"+selectedFileName);
            writer.newLine();

            writer.write("quit");
            writer.newLine();

            writer.flush();

            String line;
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceBox.getItems().addAll(transformationTypes);
    }


}