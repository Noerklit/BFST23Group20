package View;

import Model.Model;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class View {

    private Canvas canvas;
    private MapDraw mapDraw;

    public View(Model model, Stage primaryStage) {
        this.canvas = new Canvas(600, 500);

        primaryStage.setTitle("Draw Lines");
        BorderPane pane = new BorderPane(canvas);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.mapDraw = new MapDraw(model, canvas);
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
}
