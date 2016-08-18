package GUI;

import Code.LSBExtraction;
import Code.PasswordType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Donovan on 01-03-2016.
 */

public class PasswordPrompt {

    private static String errorLabelText;
    private static String password;

    public static String display(PasswordType p) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter Password");
        window.setMinWidth(400);
        window.setMinHeight(150);
        window.setOnCloseRequest(e -> password = null);

        // Password
        Label passLabel = new Label("Password:");
        GridPane.setConstraints(passLabel, 0, 0);
        PasswordField passText = new PasswordField();
        passText.setPrefWidth(230);
        GridPane.setConstraints(passText, 1, 0);

        // Confirm Password
        Label confirmPassLabel = new Label("Confirm Password:");
        GridPane.setConstraints(confirmPassLabel, 0, 1);
        PasswordField confirmPassText = new PasswordField();
        confirmPassText.setPrefWidth(230);
        GridPane.setConstraints(confirmPassText, 1, 1);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setConstraints(errorLabel, 1, 2);

        // Buttons:
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");

        // Button ActionEvents
        okButton.setOnAction(e -> {
            if(p == PasswordType.VALIDATE) {
                if (validatePassword(passText.getText(), confirmPassText.getText())) {
                    password = padPassword(passText.getText());
                    window.close();
                }
                else {
                    password = null;
                    errorLabel.setText(errorLabelText);
                    passText.clear();
                    confirmPassText.clear();
                }
            }
            else if(p == PasswordType.INPUT) {
                if (!padPassword(passText.getText()).equals(LSBExtraction.getPassword())) {
                    errorLabel.setText("Invalid Password!");
                    password = null;
                    passText.clear();
                } else {
                    password = "VALID";
                    window.close();
                }
            }
        });

        cancelButton.setOnAction(e -> {
            password = null;
            window.close();
        });

        // GridPane with 10px padding around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        if(p == PasswordType.VALIDATE) {
            grid.getChildren().addAll(passLabel, passText, confirmPassLabel, confirmPassText, errorLabel);
        }
        else if(p == PasswordType.INPUT) {
            grid.getChildren().addAll(passLabel, passText, errorLabel);
        }


        // HBox
        HBox base = new HBox(5);
        base.setPadding(new Insets(10));
        base.setAlignment(Pos.BASELINE_RIGHT);
        base.getChildren().addAll(okButton, cancelButton);

        // BorderPane
        BorderPane bPane = new BorderPane();
        bPane.setCenter(grid);
        bPane.setBottom(base);

        Scene scene = new Scene(bPane);
        window.setScene(scene);
        window.showAndWait();
        return password;
    }

    private static String padPassword(String password) {
        StringBuilder pass = new StringBuilder(password);

        if(pass.length() < 16)
            while(pass.length() != 16)
                pass.append("$");

        return pass.toString();
    }

    private static boolean validatePassword(String pass,String confirmPass) {
        boolean isValid;

        if(pass.equals("") || confirmPass.equals("")) {
            errorLabelText = "Both fields are required."; isValid = false;
        }
        else {
            if (pass.equals(confirmPass)) {
                if(!pass.trim().isEmpty()) {
                    if(pass.length() >= 6 && pass.length() <= 16) {
                        if(pass.matches(".*\\d+.*")) {
                            isValid = true;
                        }
                        else {
                            errorLabelText = "Password must contain a number.";
                            isValid = false;
                        }
                    }
                    else {
                        errorLabelText = "Password must be between 6 to 16 characters";
                        isValid = false;
                    }
                }
                else {
                    errorLabelText = "Only white spaces not allowed.";
                    isValid = false;
                }
            }
            else {
                errorLabelText = "Passwords don't match!";
                isValid = false;
            }
        }
        return isValid;
    }
}
