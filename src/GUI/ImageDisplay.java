package GUI;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by Donovan on 06-03-2016.
 */
public class ImageDisplay {

    public static void display(String carrierFile) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);

        File file = new File(carrierFile);
        Image img = null;

        try {
            img = new Image(file.toURI().toURL().toString());
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }

        ImageView imgView = new ImageView(img);

        VBox vbox = new VBox();
        vbox.getChildren().add(imgView);

        Scene scene = new Scene(new Group());
        scene.setRoot(vbox);
        window.setScene(scene);
        window.setTitle("Image");
        window.showAndWait();
    }
}
