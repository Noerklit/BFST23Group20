package View;

import Benchmark.FrameRateTimer;
import Model.Model;
import Model.Way;

import com.sun.scenario.effect.Flood;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;

public class View {

    private Canvas canvas;
    private MapDraw mapDraw;

    private Logo logo;
    private MenuBar menuBar;

    private VBox helpVBox;
    private Scene helpScene;
    private Stage helpStage;
    private Button helpButton;
    private TextField searchbar;
    private Scene scene;
    private boolean showHelp = false;

    public View(Model model, Stage primaryStage) throws FileNotFoundException {
        this.canvas = new Canvas(600, 500);
        
        this.mapDraw = new MapDraw(model, canvas);

        this.logo = new Logo();
        this.scene = createGeneralUi();


        primaryStage.setTitle("Draw Lines");

        primaryStage.setScene(scene);
        primaryStage.show();

        //helpStage.setX(primaryStage.getX() + primaryStage.getWidth() / 2 );
        //helpStage.setY(primaryStage.getY() + primaryStage.getHeight() / 2);


        setCanvasResize();
    }

    private Scene createGeneralUi() throws FileNotFoundException {
        Text fpsText = new Text();
        FrameRateTimer.setFpsText(fpsText);
        Text mapDrawDebugText = new Text();
        mapDraw.setDebugText(mapDrawDebugText);
//        TextField textField = new TextField("");
        this.menuBar = createMenuBar();
        this.searchbar = createSearchBar();
        this.helpButton = createHelpButton();

        helpStage = new Stage(StageStyle.DECORATED);
        helpStage.setTitle("Help");
        helpStage.setWidth(500);
        helpStage.setHeight(300);


        FlowPane rootNode = new FlowPane();
        rootNode.setColumnHalignment(HPos.CENTER);
        rootNode.setPrefWrapLength(200);


        ImageView aImage = new ImageView(logo.getLogo().getImage());

        Label annLabel = new Label("""
                  Help to use map:
                  \n
                  Find route: Press "shift" down and click on the app to
                  choose a staring point. Do the same thing again to
                  choose an ending point. Now your route will be calculated.
                  \n
                  Debug mode: Press "tap" to create debuglines.
                  Change the size of the debuglines by pressing 'A' to
                  make the debuglines smaller.
                  Press 'D' to make the debuglines bigger.
                  \n
                """);

        rootNode.getChildren().add(annLabel);
        rootNode.getChildren().add(aImage);



        helpScene = new Scene(rootNode, 300, 300, Color.WHITE);
       rootNode.setStyle("-fx-background-color: #FDFEFE");


        helpStage.setScene(helpScene);


        var hBoxMenu = new HBox(helpButton,menuBar,searchbar);
        hBoxMenu.setAlignment(Pos.CENTER);

        hBoxMenu.setStyle("-fx-background-color: #5a9fb0");

        HBox hBoxCanvas = new HBox(canvas);
        hBoxCanvas.setAlignment(Pos.CENTER);


        VBox vBox = new VBox(hBoxMenu, fpsText, mapDrawDebugText, hBoxCanvas);
        return new Scene(vBox);
    }

    public void showHelpStage(){

        if (showHelp)
            helpStage.show();
        else{
            helpStage.hide();
        }
    }

    public void toggleHelp(){
        showHelp = !showHelp;
    }

    private Button createHelpButton(){
        Button showHelp = new Button("Help");
        showHelp.setStyle("-fx-background-color: #5a9fb0");


        return showHelp;
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

    public Button getHelpButton(){
        return helpButton;
    }

    public Stage getHelpStage(){
        return helpStage;
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

   // public void setStartCoord(float startX, float startY){
     //   mapDraw.setStartCoord(startX, startY);
    //}
    //public void setEndCoord(float endX, float endY){
     //   mapDraw.setEndCoord(endX, endY);
    //}

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
