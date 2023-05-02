package Controller;

import Model.Model;
import Pathfinding.Dijkstra;
import View.View;
//import View.MapDraw;
import javafx.geometry.Point2D;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import App.App;
import javafx.scene.input.KeyEvent;
import Parser.AddressParser;
import javafx.scene.paint.Color;

public class Controller {
    private String search;
    private double lastX;
    private double lastY;
    private long startId, endId;
    AddressParser parser;

    public Controller(Model model, View view) {
    
    parser = new AddressParser();

        final boolean[] isStart = {true};
    view.getCanvas().setOnMousePressed(e -> {
        if(e.isPrimaryButtonDown() && e.isShiftDown()){
            try {
                var target = new Point2D( view.convertMouseLon(e.getX()), view.convertMouseLat(e.getY()));
                var temp  = model.wayKDTree.findWay(target);
                var trueCoords = model.wayKDTree.inArea(temp, target);
                if (temp != null)
                {
                    if (isStart[0]){
                        startId = trueCoords;

                    } else {
                        endId = trueCoords;
                        view.runDijstra(startId, endId);
                    }
                    isStart[0] = !isStart[0];
                }
                if (temp != null)
                {
                    view.getMapDraw().gc.setLineWidth(5 * 1/Math.sqrt(view.getMapDraw().trans.determinant()));
                    view.getMapDraw().gc.beginPath();
                    var coords = Dijkstra.getCoordsFromNodeId(trueCoords);
                    view.getMapDraw().gc.lineTo(coords[0], coords[1]);

                    view.getMapDraw().gc.setStroke(Color.CHOCOLATE);
                    view.getMapDraw().gc.stroke();
    //                view.getMapDraw().gc.setLineWidth(5 * 1/Math.sqrt(view.getMapDraw().trans.determinant()));
    //                view.getMapDraw().gc.beginPath();
    //
    //                view.getMapDraw().gc.lineTo(temp.xVal, temp.yVal);
    //
    //                view.getMapDraw().gc.setStroke(Color.CHOCOLATE);
    //                view.getMapDraw().gc.stroke();


                }

            } catch (Exception ex) {
                isStart[0] = true;
            }
        } else {
            lastX = e.getX();
            lastY = e.getY();
        }
    });


    view.getCanvas().setOnMouseDragged(e -> {
        double dx = e.getX() - lastX;
        double dy = e.getY() - lastY;
        view.panMap(dx, dy);

        lastX = e.getX();
        lastY = e.getY();

        view.redrawMap();
    });
    view.getCanvas().setOnScroll(e -> {
        double factor = e.getDeltaY();
        view.zoomMap(e.getX(), e.getY(), Math.pow(1.01, factor));

        view.redrawMap();
    });

    view.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
        if (keyEvent.getText().equals("\t")){
            view.toggleDebug();
            view.redrawMap();
        } else if (keyEvent.getText().equals("a")) {
            view.increaseMapDebugZone();
        } else if (keyEvent.getText().equals("d")) {
            view.decreaseMapDebugZone();
        }
    });

    // Searchbar
    view.getSearchbar()
        .setOnKeyPressed(event -> {
            try {
                parser.getAddresses(view.getSearchbar().getText());
            } catch (Exception e) {

            }
    });

    view.getMenuBar()
            .getMenus()
            .get(1)// choose file
            .getItems()
            .forEach(menuItem -> {
            menuItem.setOnAction(event -> {
                try {
                    App.reloadModel(menuItem.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            });
        });

    view.getMenuBar()
            .getMenus()
            .get(0)// Dark mode toggle
            .getItems()
            .forEach(menuItem -> {
        menuItem.setOnAction(event -> {
            view.setDarkMode(menuItem.getText());
        });
    });

    view.getMenuBar()
            .getMenus()
            .get(2) //View mode toggle
            .getItems()
            .forEach(menuItem -> {
                menuItem.setOnAction(event -> {
                    view.setViewRoads(menuItem.getText());
                });
            });





    }

}
