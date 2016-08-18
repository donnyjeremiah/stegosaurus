package GUI;

import Code.PasswordType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Donovan on 01-03-2016.
 */
public class EncodePage {

    public static boolean isCompressed;
    public static boolean isEncrypted;
    public static String password;

    public static String carrierFile = null;
    private static String carrierFileExt = null;
    public static String outputFile = null;
    public static String message = null;

    public static void display(Stage window) {
        //GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 20)); //top, right, bottom, left
        grid.setVgap(8);
        grid.setHgap(10);

        //ErrorLabel
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        //Carrier:
        Label carrierLabel = new Label("Carrier: ");
        TextField carrierText = new TextField();
        carrierText.setPrefWidth(600);
        carrierText.setDisable(true);
        Button carrierChoose = new Button("...");
        Button viewCarrierImage = new Button("View");

        //Output:
        Label outputLabel = new Label("Output: ");
        TextField outputText = new TextField();
        outputText.setPrefWidth(600);
        outputText.setDisable(true);
        Button outputChoose = new Button("...");

        //Button ActionEvents
        carrierChoose.setOnAction(e -> {
            if((carrierFile = chooseCarrierFile(window)) != null) {
                carrierText.setText(carrierFile);
            }
            else if(carrierText.getText() != null) carrierFile = carrierText.getText();
        });

        viewCarrierImage.setOnAction(e -> ImageDisplay.display(carrierText.getText()));

        outputChoose.setOnAction(e -> {
            if ((outputFile = chooseOutputFile(window)) != null) {
                outputText.setText(outputFile);
            }
            else if(carrierText.getText() != null) carrierFile = carrierText.getText();
        });

        //CheckBoxes:
        CheckBox compressCheck = new CheckBox("Compress Text");
        CheckBox encryptCheck = new CheckBox("Encrypt Text");

        //CheckBox ActionEvents
        compressCheck.setOnAction(e -> {
            isCompressed = (compressCheck.isSelected());
        });
        encryptCheck.setOnAction(e -> {
            if(encryptCheck.isSelected()) {
                if((password=PasswordPrompt.display(PasswordType.VALIDATE)) == null) {
                    encryptCheck.setSelected(false);
                    isEncrypted = false;
                }
                else { isEncrypted = true; }
            }
            else { password = null; isEncrypted = false; }
        });

        //TextArea:
        Label dataLabel = new Label("Text: ");
        TextArea dataTextArea = new TextArea();
        dataTextArea.setPrefRowCount(10);
        dataTextArea.setPrefColumnCount(100);
        dataTextArea.setWrapText(true);
        dataTextArea.setPrefWidth(600);

        grid.getChildren().addAll(carrierText, carrierLabel, carrierChoose,
                viewCarrierImage, outputLabel, outputText, outputChoose, compressCheck,
                encryptCheck, dataLabel, dataTextArea, errorLabel);

        //Base Buttons:
        Button nextButton = new Button("Next >>");
        Button backButton = new Button("<< Back");
        Button resetButton = new Button("Reset");

        //Button ActionEvents
        backButton.setOnAction(e -> {
            if(dataTextArea.getText() != null)
                message = (dataTextArea.getText().trim().length()>0)? dataTextArea.getText() : null;
            Stegosaurus.display(window);
        });
        nextButton.setOnAction(e -> {
            if(dataTextArea.getText() != null)
                message = (dataTextArea.getText().trim().length()>0)? dataTextArea.getText() : null;
            if(!isError())
                DetailsPage.display(window);
            else errorLabel.setText("All fields required!");
        });
        resetButton.setOnAction(e -> {
            carrierFile = null;
            outputFile = null;
            message = null;
            password = null;
            carrierFileExt = null;
            isCompressed = false;
            isEncrypted = false;

            carrierText.setText(null);
            outputText.setText(null);
            dataTextArea.setText(null);
            errorLabel.setText(null);
            if(!isCompressed) compressCheck.setSelected(false);
            if(!isEncrypted) encryptCheck.setSelected(false);
        });

        //HBox:
        HBox hBox = new HBox(15);
        hBox.setPadding(new Insets(10,20,20,10)); // top, right, bottom, left
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(backButton, nextButton, resetButton);

        //BorderPane
        BorderPane bPane = new BorderPane();
        bPane.setCenter(grid);
        bPane.setBottom(hBox);
        Scene scene = new Scene(bPane, 800, 400);

        //Constraints:
        GridPane.setConstraints(carrierLabel, 0, 0);
        GridPane.setConstraints(carrierText, 1, 0);
        GridPane.setConstraints(carrierChoose, 2, 0);
        GridPane.setConstraints(viewCarrierImage, 3, 0);
        GridPane.setConstraints(outputLabel, 0, 1);
        GridPane.setConstraints(outputText, 1, 1);
        GridPane.setConstraints(outputChoose, 2, 1);
        GridPane.setConstraints(compressCheck, 1, 2);
        GridPane.setConstraints(encryptCheck, 1, 3);
        GridPane.setConstraints(dataLabel, 0, 4);
        GridPane.setConstraints(dataTextArea, 1, 4);
        GridPane.setConstraints(carrierLabel, 0, 0);
        GridPane.setConstraints(errorLabel, 1, 5);

        //Initialize: Sets values eg: when '<<Back' button is clicked on DetailsPage
        carrierText.setText(carrierFile);
        outputText.setText(outputFile);
        dataTextArea.setText(message);
        errorLabel.setText(null);
        if(isCompressed) compressCheck.setSelected(true);
        if(isEncrypted) encryptCheck.setSelected(true);

        window.setTitle("Stegosaurus [Encode]");
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
            String path = file.getPath();
            carrierFileExt = path.substring(path.lastIndexOf("."));
            return file.getPath();
        }
        else { return null; }
    }

    private static String chooseOutputFile(Stage window) {
        File file;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File (*"+carrierFileExt+")", "*"+carrierFileExt);
        fileChooser.getExtensionFilters().addAll(extFilter);

        fileChooser.setTitle("Save As");
        if((file = fileChooser.showSaveDialog(window)) != null)  {
            String path = file.getPath();
            String outputFileExt = path.substring(path.lastIndexOf("."));
            path = path.replace(outputFileExt, carrierFileExt);
            return path;
        }
        else { return null; }
    }

    private static boolean isError() {
        boolean isError = false;
        if(carrierFile == null || outputFile == null || message == null)
            isError = true;
        return isError;
    }
}