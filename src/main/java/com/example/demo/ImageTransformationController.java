package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private ListView<String> inputFilePath;

    @FXML
    private ListView<String> outputFilePath;

    private final String[] transformationTypes = {"Gabor Transformation", "k-Wavelet Transformation"};
    private final String[] commands = {"octave-cli"};


    public void switchToMainView(ActionEvent event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(ImageTransformation.class.getResource("main_view.fxml")));
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
            /*writer.write("plot(sin(0:0.1:2*pi))");
            writer.newLine();*/

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