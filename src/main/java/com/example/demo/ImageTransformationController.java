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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageTransformationController implements Initializable{

    @FXML
    private Button transformBTN;

    @FXML
    private Button likeUnlikeTestButton;

    @FXML
    private Button chooseInputFileBTN;

    @FXML
    private Button chooseInputFolderBTN;

    @FXML
    private Button series1BTN;

    @FXML
    private Button series2BTN;

    @FXML
    private TextArea selectedFileText;

    @FXML
    private TextArea series1Text;

    @FXML
    private TextArea series2Text;

    @FXML
    private TextArea outputFolderText;

    @FXML
    private TextField gaborWLField;

    @FXML
    private TextField gaborOrientField;

    @FXML
    private TextField dbLevelField;

    @FXML
    private TextField haarLevelField;

    @FXML
    private TextField splineLevelField;

    @FXML
    private TextField dbDegreeField;

    @FXML
    private TextField splineDegreeField;

    @FXML
    private CheckBox gaborBox;

    @FXML
    private CheckBox haarBox;

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

    private String selectedImageName;

    private BufferedWriter writer;
    private final ProcessBuilder builder = new ProcessBuilder();

    private String defaultPath = "C:\\Users\\Cem Atalay\\Desktop\\Cem Code";

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
        File selectedPath = fileChooser.showOpenDialog(null);

        if (selectedPath != null) {
            String selectedFileName = selectedPath.getAbsolutePath();
            selectedImageName = selectedPath.getName();
            selectedFileText.setText(selectedFileName);
        } else {
            System.out.println("File not valid!");
        }
    }

    public void chooseInputFolder() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File defaultDirectory = new File("c:/");
        fileChooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = fileChooser.showDialog(null);
        selectedFileText.setText(selectedDirectory.getAbsolutePath());
    }

    public void chooseSeries1() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File defaultDirectory = new File("c:/");
        fileChooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = fileChooser.showDialog(null);
        series1Text.setText(selectedDirectory.getAbsolutePath());
    }

    public void chooseSeries2() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File defaultDirectory = new File("c:/");
        fileChooser.setInitialDirectory(defaultDirectory);

        File selectedDirectory = fileChooser.showDialog(null);
        series2Text.setText(selectedDirectory.getAbsolutePath());
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

    void setDefaults() {
        gaborWLField.setPromptText("6");
        gaborOrientField.setPromptText("45");
        dbDegreeField.setPromptText("4");
        dbLevelField.setPromptText("1");
        splineDegreeField.setPromptText("3");
        splineLevelField.setPromptText("1");
        haarLevelField.setPromptText("1");
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

        String imageTransformCommand = "imagetransform(\""+selectedImageName+"\",\""+ tTypeCommand + "\","+tLevel+")";

        String preFix = tTypeCommand.split(":")[0];
        String outputFileName = preFix+"_"+selectedImageName;

        try {
            writeToCMDLine(imageTransformCommand);

            writeToCMDLine("print -djpg "+outputFileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeButtonText() {
        if(Objects.equals(transformBTN.getText(), "Processing")) {
            transformBTN.setText("Apply Transformations");
        } else {
            transformBTN.setText("Processing");
        }
    }

    public String checkEmptyField(TextField textField) {
        return textField.getText().trim().isEmpty() ? textField.getPromptText() : textField.getText();
    }


    public void addToTransformCommands(String pathToImage, ArrayList<String[]> transformCommands, Boolean display) {

        String boolString;

        if(display){
            boolString = "True";
        } else {
            boolString = "False";
        }


        if(dbBox.isSelected()) {
            String dbLvl = checkEmptyField(dbLevelField);
            String dbDeg = checkEmptyField(dbDegreeField);

            String[] transformCommand = {"python", "\"C:\\Users\\Cem Atalay\\Desktop\\Cem Code\\real_wavelet.py\"",
                    "db" + dbDeg, dbLvl, pathToImage, boolString};

            transformCommands.add(transformCommand);
        }

        if(haarBox.isSelected()) {
            String haarLvl = checkEmptyField(haarLevelField);

            String[] transformcommand = {"python", "\"C:\\Users\\Cem Atalay\\Desktop\\Cem Code\\real_wavelet.py\"",
                    "haar", haarLvl, pathToImage, boolString};

            transformCommands.add(transformcommand);
        }

        if(gaborBox.isSelected()) {

            String gaborWL = checkEmptyField(gaborWLField);
            String gaborOrient = checkEmptyField(gaborOrientField);

            String[] transformcommand = {"python",
                    "\"C:\\Users\\Cem Atalay\\Desktop\\Cem Code\\real_gabor.py\"", pathToImage, gaborWL, gaborOrient, boolString};

            transformCommands.add(transformcommand);
        }
    }

    public void analysisTest() {

        String series1 = series1Text.getText();
        String series2 = series2Text.getText();

        ProcessBuilder pythonBuilder = new ProcessBuilder();
        String[] pythonCommands =  {"python", "\"C:\\Users\\Cem Atalay\\Desktop\\Cem Code\\like_unlike.py\"", series1, series2};

        pythonBuilder.command(pythonCommands);
        pythonBuilder.directory(new File(defaultPath));
        pythonBuilder.redirectErrorStream(true);

        try{
            Process p = pythonBuilder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runPythonTransform() throws IOException {

        Platform.runLater(this::changeButtonText);

        ProcessBuilder pythonBuilder = new ProcessBuilder();

        ArrayList<String[]> transformCommands = new ArrayList<>();

        String pathToImage = selectedFileText.getText();
        File file = new File(pathToImage);

        if(file.isDirectory()) {
            List<Path> paths;
            try (Stream<Path> walk = Files.walk(Path.of(pathToImage))) {
                paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            }
            Path firstImage = paths.get(0);
            addToTransformCommands(firstImage.toString(), transformCommands, true); // WIP
            paths.forEach(x -> addToTransformCommands(x.toString(), transformCommands, false));
        } else {
            addToTransformCommands(pathToImage, transformCommands, true);
        }

        for (String[] command : transformCommands) {
            pythonBuilder.command(command);
            pythonBuilder.directory(new File(defaultPath));
            pythonBuilder.redirectErrorStream(true);

            try{
                Process p = pythonBuilder.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Platform.runLater(this::changeButtonText);

    }

    public void runOctaveTransform() {

        Platform.runLater(this::changeButtonText);

        builder.command(commands);
        builder.directory(new File(defaultPath));
        builder.redirectErrorStream(true);

        String splineLevel = checkEmptyField(splineLevelField);
        String temp = checkEmptyField(splineDegreeField);
        String splineDegree = temp + ":" + temp;

        HashMap<String, String> transformCommands = new HashMap<>();

        if(splineBox.isSelected()) {
            if(checkValidParameters("spline", splineDegree, splineLevel)) {return;}
            transformCommands.put("spline" + splineDegree, splineLevel);
        }

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



        if(splineBox.isSelected()) {

            Runnable transformation = this::runOctaveTransform;

            Thread backgroundThread = new Thread(transformation);
            backgroundThread.setDaemon(true);
            backgroundThread.start();

        }

        if(gaborBox.isSelected() || dbBox.isSelected() || haarBox.isSelected()) {
            runPythonTransform(); // WIP runnable
        }


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDefaults();
    }


}