package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by Donovan on 02-03-2016.
 */
public class Stegosaurus extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        display(primaryStage);
    }

    public static void display(Stage window) {
        Button encodeButton = new Button();
        Button decodeButton = new Button();
        encodeButton.setStyle("-fx-background-image: url('/Images/encodeMain.png'); -fx-background-size: 200 200;");
        decodeButton.setStyle("-fx-background-image: url('/Images/decodeMain.png'); -fx-background-size: 200 200;");

        encodeButton.setMinHeight(200);
        decodeButton.setMinHeight(200);
        encodeButton.setMinWidth(200);
        decodeButton.setMinWidth(200);

        encodeButton.setOnAction(e -> EncodePage.display(window));
        decodeButton.setOnAction(e -> DecodePage.display(window));

        HBox center = new HBox(40);
        center.setPadding(new Insets(40,0,0,0)); //top, right, bottom, left
        center.setAlignment(Pos.CENTER);
        center.getChildren().addAll(encodeButton, decodeButton);

        Image img = new Image("/Images/logo.png",100,100,false,true);
        ImageView imgView = new ImageView(img);
        imgView.setOnMouseClicked(e -> AboutPage.display(window));

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0,0,5,10)); //top, right, bottom, left
        bottom.setAlignment(Pos.BASELINE_LEFT);
        bottom.getChildren().addAll(imgView);

        BorderPane bPane = new BorderPane();
        bPane.setCenter(center);
        bPane.setBottom(bottom);

        Scene scene = new Scene(bPane, 800, 400);
        window.setScene(scene);
        window.setTitle("Stegosaurus");
        window.setWidth(800);
        window.setHeight(450);
        window.setResizable(false);
        //Image(String url, double width, double height, boolean preserveRatio, boolean smooth)
        window.getIcons().add(new Image("/Images/logo.png",3000,3000,false,true));

        //Hand Cursor
        imgView.setOnMouseEntered(e -> scene.setCursor(Cursor.HAND));
        imgView.setOnMouseExited(e -> scene.setCursor(Cursor.DEFAULT));

        window.show();
    }
}



