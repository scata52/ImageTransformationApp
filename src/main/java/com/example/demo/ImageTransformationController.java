package com.example.demo;

import javafx.application.Platform;
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
import java.util.*;

public class ImageTransformationController implements Initializable{


    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button transformBTN;

    @FXML
    private TextArea selectedFileText;

    @FXML
    private TextArea outputFolderText;

    @FXML
    private TextField tTypeSizeField;

    @FXML
    private TextField tTypeLevelField;

    @FXML
    private TextField entropyField;

    @FXML
    private TextField stabilityField;

    @FXML
    private TextField randomnessField;

    private String selectedFileName;

    private BufferedWriter writer;
    private final ProcessBuilder builder = new ProcessBuilder();

    private String path = "C:\\Users\\Cem Atalay\\Desktop\\Cem Code";

    private String selectedTransformationType = "Gabor Transformation";

    private final String[] transformationTypes = {"Gabor Transformation", "Daubechies wavelet", "B-spline wavelet", "Apply All"};
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
        tTypeSizeField.setText("1");
        tTypeLevelField.setText("1");
    }

    public void setDefaultAnalysisValues(ActionEvent event) {
        entropyField.setText("1");
        stabilityField.setText("1");
        randomnessField.setText("1");
    }

    public void getTransformationType(ActionEvent event) {
        selectedTransformationType = choiceBox.getValue();
    }

    public String tTypeToCommand(String tType) {
        return switch (tType) {
            case "Daubechies wavelet" -> "db";
            case "B-spline wavelet" -> "spline";
            case "Apply all" -> "all";
            default -> "gabor";
        };
    }

    public String adaptSizeToType(String tType, String tSize) {
        return switch (tType) {
            case "B-spline wavelet" -> tSize + ":" + tSize;
            case "Daubechies wavelet" -> tSize;
            default -> "";
        };
    }

    static boolean isDBSizeValid(String tSize) {
        int tSizeInt = Integer.parseInt(tSize);
        return tSizeInt % 2 == 0 && tSizeInt >= 2 && tSizeInt <= 20;
    }

    static boolean isSplineSizeValid(String tSize) {
        int tSizeInt = Integer.parseInt(tSize);
        return tSizeInt >= 1 && tSizeInt <=9;
    }

    public boolean checkValidParameters(String tType,String tSize,String tLevel) {
        if(Objects.equals(tType, "gabor") && !Objects.equals(tLevel, "1")) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Level");
                        alert.setHeaderText("Level of Gabor Transformation must be \"1\"");
                        alert.showAndWait();
                    }
            );
            return true;
        }

        else if(Objects.equals(tType, "db") && !isDBSizeValid(tSize)) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Size");
                        alert.setHeaderText("Daubechies wavelet transformation number must be even, and 2<=NUMBER<=20");
                        alert.showAndWait();
                    }
            );
            return true;
        }

        else if (Objects.equals(tType, "spline") && !isSplineSizeValid(tSize)) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Size");
                        alert.setHeaderText("B-spline wavelet transformation number must be 1<=NUMBER<=9");
                        alert.showAndWait();
                    }
            );
            return true;
        }
        return false;
    }

    public void writeToOctave(String string) throws IOException {
        writer.write(string);
        writer.newLine();
    }

    public String imageTransform(String tTypeCommand, String tLevel) {

        String imageTransformCommand = "imagetransform(\""+selectedFileName+"\",\""+ tTypeCommand + "\","+tLevel+")";

        String preFix = tTypeCommand.split(":")[0];
        String outputFileName = preFix+"_"+selectedFileName;

        try {
            writeToOctave(imageTransformCommand);

            writeToOctave("print -djpg "+outputFileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFileName;
    }

    public static void wait(int ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public void applyAll() {

        String[] defaultCommands = {"gabor", "db2", "spline1:1"};
        String defaultLevel = "1";

        for(String command : defaultCommands) {
            selectedFileName = imageTransform(command, defaultLevel);
            wait(4000);
        }

    }

    public void changeButtonText() {
        if(Objects.equals(transformBTN.getText(), "Processing")) {
            transformBTN.setText("Transform");
        } else {
            transformBTN.setText("Processing");
        }
    }

    public void runTransform() {

        builder.command(commands);
        builder.directory(new File(path));
        builder.redirectErrorStream(true);

        String tSize = tTypeSizeField.getText();
        String tTypeCommand = tTypeToCommand(selectedTransformationType);
        String tLevel = tTypeLevelField.getText();

        if(checkValidParameters(tTypeCommand, tSize, tLevel)) {return;}

        tSize = adaptSizeToType(selectedTransformationType,tTypeSizeField.getText());
        String tType = tTypeCommand + tSize;

        Platform.runLater(this::changeButtonText);

        Process process;

        try{
            process = builder.start();

            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream ()));
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            writeToOctave("pkg load ltfat");

            if(Objects.equals(tTypeCommand, "all")) {
                applyAll();
            } else {
                imageTransform(tType, tLevel);
            }

            writeToOctave("quit");

            writer.flush();

            String line;
            while ((line = br.readLine()) != null)
            {
                System.out.println(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(this::changeButtonText);
    }

    public void startTransform(ActionEvent event) throws IOException {

        Runnable transformation = this::runTransform;

        Thread backgroundThread = new Thread(transformation);
        backgroundThread.setDaemon(true);
        backgroundThread.start();

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        choiceBox.getItems().addAll(transformationTypes);
        choiceBox.setOnAction(this::getTransformationType);

    }


}