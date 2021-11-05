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
    private ListView<String> inputFilePath;

    @FXML
    private ListView<String> outputFilePath;

    private final String[] transformationTypes = {"Gabor Transformation", "Wavelet Transformation", "Apply All"};
    private final String[] commands = {"octave-cli"};

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

    public void startParameterView(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("parameters_view.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Set Parameters");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void startTransform(ActionEvent event) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        builder.redirectErrorStream(true);

        Process process;
        try {
            process = builder.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream ()));
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Check that Octave is really running and streams are read from / written to
            writer.write("1+1");
            writer.newLine();

            // Try to plot
            writer.write("plot([1,2,3,4])");
            writer.newLine();

            writer.write("2+2");
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

    /*private static class ProcessReadTask implements Callable<List<String>> {

        private InputStream inputStream;

        public ProcessReadTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public List<String> call() {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList());
        }
    }*/
}