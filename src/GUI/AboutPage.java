package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by Donovan on 13-04-2016.
 */
public class AboutPage {
    public static void display(Stage window) {

        ImageView imgView = new ImageView(new Image("/Images/logo.png",250,250,false,true));
        HBox logo = new HBox();
        logo.setAlignment(Pos.CENTER);
        logo.getChildren().addAll(imgView);

        Label l1 = new Label();
        l1.setText("");
        Label l2 = new Label();
        l2.setText("");
        Label l3 = new Label();
        l3.setText("");
        Label l4 = new Label();
        l4.setText("");
        Label l5 = new Label();
        l5.setText("Copyright © 2016 Donovan Jeremiah");

        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER);
        info.getChildren().addAll(l1,l2,l3,l4,l5);

        Button backButton = new Button("<< Back");
        backButton.setOnAction(e -> Stegosaurus.display(window));
        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0,0,20,20)); // top, right, bottom, left
        bottom.setAlignment(Pos.BASELINE_LEFT);
        bottom.getChildren().addAll(backButton);

        BorderPane bPane = new BorderPane();
        //bPane.setPadding(new Insets(0,10,10,10));  //top, right, bottom, left
        bPane.setTop(logo);
        bPane.setCenter(info);
        bPane.setBottom(bottom);

        Scene scene = new Scene(bPane, 800, 400);
        window.setScene(scene);
        window.setTitle("Stegosaurus [About]");
    }
}
