package View;

import Benchmark.FrameRateTimer;
import Model.Model;

import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class View {

    private Canvas canvas;
    private MapDraw mapDraw;
    private MenuBar menuBar;


    public View(Model model, Stage primaryStage) {
        this.canvas = new Canvas(600, 500);

        primaryStage.setTitle("Draw Lines");

        primaryStage.setScene(createGeneralUi());
        primaryStage.show();

        this.mapDraw = new MapDraw(model, canvas);
    }

    private Scene createGeneralUi(){
        Text fps = new Text();
        FrameRateTimer.setFps(fps);
        TextField textField = new TextField("");
        this.menuBar = createMenuBar();
        var hBox = new HBox(menuBar, textField);

        VBox vBox = new VBox(hBox, fps, canvas);
        return new Scene(vBox);
    }

    private MenuBar createMenuBar(){
        // Darkmode
        MenuItem on = new MenuItem("on");
        MenuItem off = new MenuItem("off");
        Menu chooseDarkMode = new Menu("Dark Mode");
        chooseDarkMode.getItems().add(on);
        chooseDarkMode.getItems().add(off);

        //Choose File
        File path = new File("../app/data/");
        File[] files = path.listFiles();
        Menu chooseFile = new Menu("Choose File");
        for (File file : files){
            var item = new MenuItem(file.getName());
//            item.setOnAction(event -> {
//                System.out.println(file.getName());
//            });
            chooseFile.getItems().add(item);
        }


        return new MenuBar(chooseDarkMode, chooseFile);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void panMap(double dx, double dy) {
        mapDraw.pan(dx, dy);
    }
    public void zoomMap(double dx, double dy, double factor) {
        mapDraw.zoom(dx, dy, factor);
    }
    public void redrawMap() {
        mapDraw.redraw();
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
