package GUI;

/**
 * Created by Donovan on 25-03-2016.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Code.LSBExtraction;
import Code.PasswordType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DecodePage {

    private static String carrierFile = null;
    private static TextArea dataTextArea;
    private static Label errorLabel = new Label();
    private static Button saveButton;

    public static void display(Stage window) {

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);

        errorLabel.setTextFill(Color.RED);

        //Carrier:
        Label carrierLabel = new Label("Carrier:");
        TextField carrierText = new TextField();
        carrierText.setPrefWidth(600);
        carrierText.setDisable(true);
        Button carrierChoose = new Button("...");
        carrierChoose.setOnAction(e -> {
            if((carrierFile = chooseCarrierFile(window)) != null) {
                carrierText.setText(carrierFile);
            }
            else if(!carrierText.getText().equals("")) carrierFile = carrierText.getText();
            errorLabel.setText(null);
        });
        Button viewCarrierImage = new Button("View");
        viewCarrierImage.setOnAction(e -> {
            String url = carrierText.getText();
            if(!url.equals(""))
                ImageDisplay.display(url);
        });

        // TextArea:
        Label dataLabel = new Label("Text: ");
        dataTextArea = new TextArea();
        dataTextArea.setPrefRowCount(15);
        dataTextArea.setPrefColumnCount(100);
        dataTextArea.setWrapText(true);
        dataTextArea.setPrefWidth(600);
        dataTextArea.setDisable(true);

        //Constraints
        GridPane.setConstraints(carrierLabel, 0, 0);
        GridPane.setConstraints(carrierText, 1, 0);
        GridPane.setConstraints(carrierChoose, 2, 0);
        GridPane.setConstraints(viewCarrierImage, 3, 0);
        GridPane.setConstraints(dataLabel, 0, 4);
        GridPane.setConstraints(dataTextArea, 1, 4);
        GridPane.setConstraints(errorLabel, 1, 5);

        // Add all controls to grid
        grid.getChildren().addAll(carrierText, carrierLabel, carrierChoose, viewCarrierImage,
                dataLabel, dataTextArea, errorLabel);

        //Decode
        Button decodeButton = new Button("Decode");
        Button closeButton = new Button("Close");
        Button backButton = new Button("<< Back");

        saveButton = new Button("Save");
        saveButton.setDisable(true);
        decodeButton.setOnAction(e -> {
            if(!carrierText.getText().equals("")) {
                decodeButton.setDisable(true); // ERROR: not disabling
                decode();
                decodeButton.setDisable(false);
                saveButton.setDisable(false);
            }
            else { errorLabel.setText("Carrier File Required!"); }
        });
        saveButton.setOnAction(e -> {
            decodeButton.setDisable(true);
            saveButton.setDisable(true);
            if(saveToFile(window)) {
                decodeButton.setDisable(true);
                saveButton.setDisable(true);
            }
            else {
                decodeButton.setDisable(false);
                saveButton.setDisable(false);
            }
        });
        closeButton.setOnAction(e -> window.close());
        backButton.setOnAction(e -> Stegosaurus.display(window));

        HBox base = new HBox(15);
        base.setPadding(new Insets(10,20,20,10)); // top, right, bottom, left
        base.setAlignment(Pos.BASELINE_RIGHT);
        base.getChildren().addAll(saveButton, backButton, decodeButton, closeButton);

        BorderPane bPane = new BorderPane();
        bPane.setBottom(base);
        bPane.setCenter(grid);

        Scene scene = new Scene(bPane, 800, 400);

        window.setScene(scene);
        window.show();
    }

    private static String chooseCarrierFile(Stage window) {
        File file;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File(*.png,*.bmp)", "*.png", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Select Carrier Image");
        if((file = fileChooser.showOpenDialog(window)) != null) {
            return file.getPath();
        }
        else { return null; }
    }

    private static String chooseOutputFile(Stage window) {
        File file;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
        fileChooser.getExtensionFilters().addAll(extFilter);

        fileChooser.setTitle("Save As");
        if((file = fileChooser.showSaveDialog(window)) != null)  {
            return file.getPath();
        }
        else { return null; }
    }

    private static void decode() {
        LSBExtraction.setImage(carrierFile);
        LSBExtraction.decode();
        if(LSBExtraction.isEncrypted()) {
            String password = PasswordPrompt.display(PasswordType.INPUT);
            if(password==null) { errorLabel.setText("Error: Data is Encrypted. Password required."); }
            else if(password.equals("VALID")) {
                dataTextArea.setText(LSBExtraction.getMessage());
                errorLabel.setText(null);
            }
        }
        else  { dataTextArea.setText(LSBExtraction.getMessage()); }
    }

    private static boolean saveToFile(Stage window) {
        String saveFile;
        boolean isSaved = false;
        if((saveFile = chooseOutputFile(window)) != null) {
            try {
                FileWriter fw = new FileWriter(new File(saveFile));
                fw.write(dataTextArea.getText());
                fw.close();
                isSaved = true;
            }
            catch (IOException e) { AlertBox.error("Error in saving file.", null); }
        }
        else { isSaved = false; }

        return isSaved;
    }
}
