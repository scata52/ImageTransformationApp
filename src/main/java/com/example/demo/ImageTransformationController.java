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
    private Button transformBTN;

    @FXML
    private Button pTestButton;

    @FXML
    private TextArea selectedFileText;

    @FXML
    private TextArea outputFolderText;

    @FXML
    private TextField aField;

    @FXML
    private TextField MField;

    @FXML
    private TextField LField;

    @FXML
    private TextField gaborLevelField;

    @FXML
    private TextField dbLevelField;

    @FXML
    private TextField splineLevelField;

    @FXML
    private TextField dbDegreeField;

    @FXML
    private TextField splineDegreeField;

    @FXML
    private CheckBox gaborBox;

    @FXML
    private CheckBox dbBox;

    @FXML
    private CheckBox splineBox;

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

    public String tTypeToCommand(String tType) {
        return switch (tType) {
            case "Daubechies wavelet" -> "db";
            case "B-spline wavelet" -> "spline";
            case "Apply all" -> "all";
            default -> "gabor";
        };
    }

    /*public String adaptSizeToType(String tType, String tSize) {
        return switch (tType) {
            case "B-spline wavelet" -> tSize + ":" + tSize;
            case "Daubechies wavelet" -> tSize;
            default -> "";
        };
    }*/

    void setDefaults() {
        gaborLevelField.setPromptText("1");
        dbDegreeField.setPromptText("4");
        dbLevelField.setPromptText("1");
        splineDegreeField.setPromptText("3");
        splineLevelField.setPromptText("1");
        aField.setPromptText("4");
        MField.setPromptText("128");
        LField.setPromptText("512");
    }

    static boolean isDBDegreeValid(String tSize) {
        int tSizeInt = Integer.parseInt(tSize);
        return tSizeInt % 2 == 0 && tSizeInt >= 2 && tSizeInt <= 20;
    }

    static boolean isSplineDegreeValid(String tSize) {
        int tSizeInt = Integer.parseInt(tSize.split(":")[0]);
        return tSizeInt >= 1 && tSizeInt <=9;
    }

    public boolean checkValidParameters(String tType,String tDegree,String tLevel) {
        if(Objects.equals(tType, "gabor") && !Objects.equals(tLevel, "1")) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Level");
                        alert.setHeaderText("Level of Gabor Transformation must be 1");
                        alert.showAndWait();
                    }
            );
            return true;
        }

        else if(Objects.equals(tType, "db") && !isDBDegreeValid(tDegree)) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Degree");
                        alert.setHeaderText("Daubechies wavelet transformation degree must be even, and 2<=DEGREE<=20");
                        alert.showAndWait();
                    }
            );
            return true;
        }

        else if (Objects.equals(tType, "spline") && !isSplineDegreeValid(tDegree)) {
            Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Degree");
                        alert.setHeaderText("B-spline wavelet transformation degree must be 1<=DEGREE<=9");
                        alert.showAndWait();
                    }
            );
            return true;
        }
        return false;
    }

    public void writeToCMDLine(String string) throws IOException {
        writer.write(string);
        writer.newLine();
    }

    public void imageTransform(String tTypeCommand, String tLevel) {

        String imageTransformCommand = "imagetransform(\""+selectedFileName+"\",\""+ tTypeCommand + "\","+tLevel+")";

        String preFix = tTypeCommand.split(":")[0];
        String outputFileName = preFix+"_"+selectedFileName;

        try {
            writeToCMDLine(imageTransformCommand);

            writeToCMDLine("print -djpg "+outputFileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeButtonText() {
        if(Objects.equals(transformBTN.getText(), "Processing")) {
            transformBTN.setText("Transform");
        } else {
            transformBTN.setText("Processing");
        }
    }

    public String checkEmptyField(TextField textField) {
        return textField.getText().trim().isEmpty() ? textField.getPromptText() : textField.getText();
    }

    public void pythonTest() {


        ProcessBuilder pythonBuilder = new ProcessBuilder();
        String[] pythonCommands =  {"python", "\"C:\\Users\\Cem Atalay\\Desktop\\Cem Code/gabordemonstration.py\""};

        pythonBuilder.command(pythonCommands);
        pythonBuilder.directory(new File(path));
        pythonBuilder.redirectErrorStream(true);

        try{
            Process p = pythonBuilder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runTransform() {

        builder.command(commands);
        builder.directory(new File(path));
        builder.redirectErrorStream(true);

        String gaborLevel = checkEmptyField(gaborLevelField);
        String dbLevel = checkEmptyField(dbLevelField);
        String splineLevel = checkEmptyField(splineLevelField);
        String dbDegree = checkEmptyField(dbDegreeField);
        String temp = checkEmptyField(splineDegreeField);
        String splineDegree = temp + ":" + temp;

        HashMap<String, String> transformCommands = new HashMap<>();

        if(gaborBox.isSelected()) {
            if(checkValidParameters("gabor", "", gaborLevel)) {return;}
            transformCommands.put("gabor", gaborLevel);
        }
        if(dbBox.isSelected()) {
            if(checkValidParameters("db", dbDegree, dbLevel)) {return;}
            transformCommands.put("db" + dbDegree, dbLevel);
        }
        if(splineBox.isSelected()) {
            if(checkValidParameters("spline", splineDegree, splineLevel)) {return;}
            transformCommands.put("spline" + splineDegree, splineLevel);
        }

        /*String tSize = tTypeSizeField.getText();
        String tTypeCommand = tTypeToCommand(selectedTransformationType);
        String tLevel = tTypeLevelField.getText();

        if(checkValidParameters(tTypeCommand, tSize, tLevel)) {return;}

        tSize = adaptSizeToType(selectedTransformationType,tTypeSizeField.getText());
        String tType = tTypeCommand + tSize;*/

        Platform.runLater(this::changeButtonText);

        Process process;

        try{
            process = builder.start();

            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream ()));
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            writeToCMDLine("pkg load ltfat");

            for(var entry : transformCommands.entrySet()) {
                imageTransform(entry.getKey(), entry.getValue());
            }

            writeToCMDLine("quit");

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
        setDefaults();
    }


}