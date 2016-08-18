package GUI;

import Code.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Donovan on 12-03-2016.
 */
public class DetailsPage {

    private static CheckBox redCheck;
    private static CheckBox greenCheck;
    private static CheckBox blueCheck;

    private static Label l1 = new Label();
    private static Label l2 = new Label();
    private static Label l3 = new Label();
    private static Label l4 = new Label();
    private static Label l5 = new Label();

    private static ColorChannel colorChannel = ColorChannel.B;
    private static int LSBs = 1;

    private static String carrierFile;
    private static Label errorLabel = new Label();
    private static Label encodeStatusLabel = new Label();

    public static void display(Stage window) {
        //GridPane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 0, 20)); //top, right, bottom, left
        grid.setVgap(20);
        grid.setHgap(40);

        //Advanced Settings:
        //CheckBoxes:
        Label colorChannelLabel = new Label("Pixel Color: ");
        redCheck = new CheckBox("Red");
        greenCheck = new CheckBox("Green");
        blueCheck = new CheckBox("Blue");
        blueCheck.setSelected(true);
        encodeStatusLabel.setTextFill(Color.GREEN);

        Label noOfLSBLabel = new Label("No. of LSB bits: ");
        Label sliderValueLabel = new Label();
        Slider LSBSlider = new Slider();
        LSBSlider.valueProperty().addListener((obs,ov,nv) -> {
            sliderValueLabel.textProperty().setValue("Value: " + String.valueOf((int)LSBSlider.getValue()));
            LSBs = (int)LSBSlider.getValue();
            refreshDetails();
        });
        LSBSlider.setMax(8);
        LSBSlider.setMin(1);
        LSBSlider.setMinSize(50, 20);

        //CheckBox ActionEvents
        redCheck.setOnAction(e -> setColorChannel());
        greenCheck.setOnAction(e -> setColorChannel());
        blueCheck.setOnAction(e -> setColorChannel());

        //Data Information:
        Label originalTextSizeLabel = new Label("Text Size: ");
        Label compressedTextSizeLabel = new Label("Compressed Text Size: ");
        Label requiredPixelsLabel = new Label("Carrier Pixels Required: ");
        Label carrierPixelsLabel = new Label("Total Carrier Pixels: ");
        Label maxCarrierDataLabel = new Label("Maximum Data: ");
        errorLabel.setTextFill(Color.RED);

        //Constraints: //NOTE: grid.add(Node, colIndex, rowIndex, colSpan, rowSpan):
        GridPane.setConstraints(colorChannelLabel, 0, 0);
        GridPane.setConstraints(redCheck, 1, 0);
        GridPane.setConstraints(greenCheck, 2, 0);
        GridPane.setConstraints(blueCheck, 4, 0);
        GridPane.setConstraints(noOfLSBLabel, 0, 1);
        GridPane.setConstraints(LSBSlider, 1, 1, 3, 1);
        GridPane.setConstraints(sliderValueLabel, 1, 2);

        GridPane.setConstraints(originalTextSizeLabel, 0, 4);
        GridPane.setConstraints(compressedTextSizeLabel, 0, 5);
        GridPane.setConstraints(requiredPixelsLabel, 0, 6);
        GridPane.setConstraints(carrierPixelsLabel, 0, 7);
        GridPane.setConstraints(maxCarrierDataLabel, 0, 8);
        GridPane.setConstraints(errorLabel, 0, 9);

        GridPane.setConstraints(l1, 1, 4);
        GridPane.setConstraints(l2, 1, 5);
        GridPane.setConstraints(l3, 1, 6);
        GridPane.setConstraints(l4, 1, 7);
        GridPane.setConstraints(l5, 1, 8);

        grid.getChildren().addAll(colorChannelLabel, redCheck, greenCheck, blueCheck, noOfLSBLabel, LSBSlider, sliderValueLabel);
        grid.getChildren().addAll(originalTextSizeLabel, compressedTextSizeLabel, requiredPixelsLabel, carrierPixelsLabel, maxCarrierDataLabel, errorLabel);
        grid.getChildren().addAll(l1, l2, l3, l4, l5);

        //Base Buttons:
        Button backButton = new Button("<< Back");
        Button encodeButton = new Button("Encode");
        Button closeButton = new Button("Close");

        //Base ActionEvents
        backButton.setOnAction(e -> EncodePage.display(window));
        encodeButton.setOnAction(e -> {
            if(!isError()) {
                encodeButton.setDisable(true);
                backButton.setDisable(true);
                closeButton.setDisable(true);
                encode();
                encodeStatusLabel.setText("Encoding Successful!");
                AlertBox.information("Encoding Successful!", null);
                closeButton.setDisable(false);
            }
        });
        closeButton.setOnAction(e -> window.close());

        //HBox:
        HBox hBox = new HBox(15);
        hBox.setPadding(new Insets(10,20,20,10));// top, right, bottom, left
        hBox.setAlignment(Pos.BASELINE_RIGHT);
        hBox.getChildren().addAll(encodeStatusLabel, backButton, encodeButton, closeButton);

        //BorderPane
        BorderPane bPane = new BorderPane();
        bPane.setCenter(grid);
        bPane.setBottom(hBox);
        Scene scene= new Scene(bPane, 800, 400);

        //Initialize
        initDisplay();

        window.setTitle("Stegosaurus [Encode] - Details");
        window.setScene(scene);
        window.show();
    }

    private static void setColorChannel() {
        if(redCheck.isSelected())
            if(greenCheck.isSelected())
                if(blueCheck.isSelected()) colorChannel = ColorChannel.RGB;
                else colorChannel = ColorChannel.RG;
            else if(blueCheck.isSelected()) colorChannel = ColorChannel.RB;
            else colorChannel = ColorChannel.R;
        else if(greenCheck.isSelected())
            if(blueCheck.isSelected()) colorChannel = ColorChannel.GB;
            else colorChannel = ColorChannel.G;
        else if(blueCheck.isSelected()) colorChannel = ColorChannel.B;
        else colorChannel = ColorChannel.RGB; // nothing selected
        refreshDetails();
    }

    private static int originalTextSize;
    private static int compressedTextSize;
    private static int requiredPixels;
    private static int carrierPixels;

    private static void initDisplay() {
        try {
            carrierFile = EncodePage.carrierFile;
            carrierPixels = getTotalCarrierPixels();
            originalTextSize = EncodePage.message.length();
            compressedTextSize = TextCompression.compress(EncodePage.message).length;
            errorLabel.setText(null);
            refreshDetails();
        }
        catch(IOException e) { AlertBox.error("Error in Compression of file.", null); }
    }

    private static void refreshDetails() {
        String cc = colorChannel.toString();
        requiredPixels = (((originalTextSize * 8) + 250) / (cc.length() * LSBs)); //LSBs 1->8, cc 1, 2 or 3
        int maxCarrierData = (cc.length() * LSBs * carrierPixels)/8 - (250/8); //max of 250 bits for header

        l1.setText(String.valueOf(originalTextSize) + "\tBytes");
        l2.setText(String.valueOf(compressedTextSize) + "\tBytes");
        l3.setText(String.valueOf(requiredPixels) + "\tPixels");
        l4.setText(String.valueOf(carrierPixels) + "\tPixels");
        l5.setText(String.valueOf(maxCarrierData) + "\tBytes");

    }

    private static int getTotalCarrierPixels() {
        int noOfPixels = 0;
        try {
            BufferedImage img = ImageIO.read(new File(carrierFile));
            noOfPixels = img.getHeight() * img.getWidth();
        }
        catch(IOException e) { AlertBox.error("Error in reading Carrier file.", null); }
        return noOfPixels;
    }

    private static boolean isError() {
        boolean isError = false;
        if(!redCheck.isSelected() && !greenCheck.isSelected() && !blueCheck.isSelected()) {
            isError = true;
            errorLabel.setText("Error: Select Pixel color!");
        }
        else if(requiredPixels > carrierPixels) {
            errorLabel.setText("Error: Carrier too small!");
        }
        return isError;
    }

    private static void encode() {
        try {
            LSBInsertion.setImage(EncodePage.carrierFile, EncodePage.outputFile);
            if(EncodePage.isEncrypted) {
                LSBInsertion.setHeader(colorChannel, LSBs, EncodePage.isCompressed, EncodePage.isEncrypted, EncodePage.password);
                LSBInsertion.encode(EncodeType.HEADER, ColorChannel.RGB, 2);

                if(EncodePage.isCompressed)
                    LSBInsertion.setMessage(TextEncryption.encrypt(TextCompression.compress(EncodePage.message), EncodePage.password));
                else if(!EncodePage.isCompressed)
                    LSBInsertion.setMessage(TextEncryption.encrypt(EncodePage.message.getBytes(), EncodePage.password));

                LSBInsertion.encode(EncodeType.MESSAGE, colorChannel, LSBs);
            }
            else if(!EncodePage.isEncrypted) {
                LSBInsertion.setHeader(colorChannel, LSBs, EncodePage.isCompressed, EncodePage.isEncrypted);
                LSBInsertion.encode(EncodeType.HEADER, ColorChannel.RGB, 2);

                if(EncodePage.isCompressed)
                    LSBInsertion.setMessage(TextCompression.compress(EncodePage.message));
                else if(!EncodePage.isCompressed)
                    LSBInsertion.setMessage(EncodePage.message.getBytes());

                LSBInsertion.encode(EncodeType.MESSAGE, colorChannel, LSBs);
            }
        }
        catch(Exception e) { AlertBox.error("Error in Compression and/or Encryption.", null); }
    }
}

