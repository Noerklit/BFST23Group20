package View;

import Benchmark.FrameRateTimer;
import Model.Model;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class View {

    private Canvas canvas;
    private MapDraw mapDraw;
    private MenuBar menuBar;
    private Scene scene;


    public View(Model model, Stage primaryStage) {
        this.canvas = new Canvas(600, 500);
        
        this.mapDraw = new MapDraw(model, canvas);

        this.scene = createGeneralUi();

        primaryStage.setTitle("Draw Lines");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Scene createGeneralUi(){
        Text fpsText = new Text();
        FrameRateTimer.setFpsText(fpsText);
        Text mapDrawDebugText = new Text();
        mapDraw.setDebugText(mapDrawDebugText);
//        TextField textField = new TextField("");
        this.menuBar = createMenuBar();
        var hBoxMenu = new HBox(menuBar);
        hBoxMenu.setAlignment(Pos.CENTER);
        hBoxMenu.setStyle("-fx-background-color: #5a9fb0");

        HBox hBoxCanvas = new HBox(canvas);
        hBoxCanvas.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(hBoxMenu, fpsText, mapDrawDebugText, hBoxCanvas);
        return new Scene(vBox);
    }

    private MenuBar createMenuBar(){
        // Darkmode
        MenuItem on = new MenuItem("on");
        MenuItem off = new MenuItem("off");
        Menu chooseDarkMode = new Menu("Toggle Dark Mode");
        chooseDarkMode.getItems().add(on);
        chooseDarkMode.getItems().add(off);

        //Choose File
        File path = new File("../app/data/");
        File[] files = path.listFiles();
        Menu chooseFile = new Menu("Choose File");
        for (File file : files){
            var curr = file.getName();
            if (curr.endsWith(".obj")){
                chooseFile.getItems().add(new MenuItem(curr));
            }
        }

        var mb = new MenuBar(chooseDarkMode, chooseFile);
        mb.setStyle("-fx-background-color: #5a9fb0");
        return mb;
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

    public void setDarkMode(String toggle){
        switch (toggle) {
            case "on":
                mapDraw.setDarkMode(true);
                return;
            case "off":
                mapDraw.setDarkMode(false);
                return;
        }
    }
    public void toggleDebug(){
        mapDraw.toggleDebug();
    }

    public void increaseMapDebugZone() {
        mapDraw.increaseDebugZone();
    }

    public void decreaseMapDebugZone() {
        mapDraw.decreaseDebugZone();
    }

    public Scene getScene() {
        return scene;
    }
}
