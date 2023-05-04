package View;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.Scanner;

public class Logo {
    private String fileName;
    public Logo() {
        fileName = "./data/logo.png.png";

    }

    public ImageView getLogo() throws FileNotFoundException {
            //Creates image object
            InputStream stream = new FileInputStream(fileName);
            Image image = new Image(stream);

            //Creates the image view
            ImageView logoImageView = new ImageView();

            //Sets image to the image view
            logoImageView.setImage(image);

            logoImageView.setFitWidth(10);
            logoImageView.setFitHeight(10);
            logoImageView.setPreserveRatio(true);

            return logoImageView;

    }

}

