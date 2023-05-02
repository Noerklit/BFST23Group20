package View;

import Benchmark.FrameRateTimer;
import Model.Model;
import Model.Way;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;

public class View {

    private Canvas canvas;
    private MapDraw mapDraw;
    private MenuBar menuBar;
    private TextField searchbar;
    private Scene scene;

    public View(Model model, Stage primaryStage) {
        this.canvas = new Canvas(600, 500);
        
        this.mapDraw = new MapDraw(model, canvas);

        this.scene = createGeneralUi();

        primaryStage.setTitle("Draw Lines");

        primaryStage.setScene(scene);
        primaryStage.show();

        setCanvasResize();
    }

    private Scene createGeneralUi(){
        Text fpsText = new Text();
        FrameRateTimer.setFpsText(fpsText);
        Text mapDrawDebugText = new Text();
        mapDraw.setDebugText(mapDrawDebugText);
//        TextField textField = new TextField("");
        this.menuBar = createMenuBar();
        this.searchbar = createSearchBar();
        var hBoxMenu = new HBox(menuBar,searchbar);
        hBoxMenu.setAlignment(Pos.CENTER);

        hBoxMenu.setStyle("-fx-background-color: #5a9fb0");

        HBox hBoxCanvas = new HBox(canvas);
        hBoxCanvas.setAlignment(Pos.CENTER);


        VBox vBox = new VBox(hBoxMenu, fpsText, mapDrawDebugText, hBoxCanvas);
        return new Scene(vBox);
    }


    private Menu chooseViewWidget(){
        MenuItem showRoads = new MenuItem("Show roads only");
        MenuItem showCompleteMap = new MenuItem("Show complete map");
        Menu chooseView = new Menu("Choose view");
        chooseView.getItems().add(showRoads);
        chooseView.getItems().add(showCompleteMap);

        return chooseView;
    }

    private Menu chooseDarkWidget(){
        MenuItem on = new MenuItem("on");
        MenuItem off = new MenuItem("off");
        Menu chooseDarkMode = new Menu("Toggle Dark Mode");
        chooseDarkMode.getItems().add(on);
        chooseDarkMode.getItems().add(off);

        return chooseDarkMode;
    }

    private Menu chooseFileWidget(){
        File path = new File("../app/data/");
        File[] files = path.listFiles();
        Menu chooseFile = new Menu("Choose File");
        for (File file : files){
            var curr = file.getName();
            if (curr.endsWith(".obj")){
                chooseFile.getItems().add(new MenuItem(curr));
            }
        }

        return chooseFile;
    }

    public TextField createSearchBar(){
        TextField searchbar = new TextField("Search...");
        searchbar.setAlignment(Pos.CENTER);

        return searchbar;
    }

    private MenuBar createMenuBar(){
        Menu chooseDarkMode = chooseDarkWidget();
        Menu chooseFile = chooseFileWidget();
        Menu chooseView = chooseViewWidget();

        var mb = new MenuBar(chooseDarkMode, chooseFile, chooseView);

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

    public double convertMouseLon(double mouseCoord) {
        return mapDraw.convertMouseLon(mouseCoord);
    }
    public double convertMouseLat(double mouseCoord) {
        return mapDraw.convertMouseLat(mouseCoord);
    }
    
    public MenuBar getMenuBar() {
        return menuBar;
    }

    public TextField getSearchbar() {
        return searchbar;
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

    public void setViewRoads(String toggle){
        switch (toggle){
            case "Show roads only":
                mapDraw.setViewMode(true);
                return;
            case "Show complete map":
                mapDraw.setViewMode(false);
                return;
        }

    }

    public void toggleDebug(){
        mapDraw.toggleDebug();
        //updateCanvasSize();
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

    private void setCanvasResize() {
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            double deltaWidth = newVal.doubleValue() - oldVal.doubleValue();
            mapDraw.resizeCanvasWidth(deltaWidth);
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            double deltaHeight = newVal.doubleValue() - oldVal.doubleValue();
            mapDraw.resizeCanvasHeight(deltaHeight);
        });
    }

    public MapDraw getMapDraw(){
        return mapDraw;
    }

    public void runDijstra(long from, long to){
        mapDraw.runDijsktra(from, to);
    }
}
