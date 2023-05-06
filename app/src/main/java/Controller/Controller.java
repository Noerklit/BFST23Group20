package Controller;

import Model.Model;
import View.View;
import javafx.geometry.Point2D;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import App.App;
import javafx.scene.input.KeyEvent;
import Parser.AddressSearch;

public class Controller {
    private final double clickRadius = 8;
    private boolean leftClickRange = false;

    private double startX;
    private double startY;

    private double lastX;
    private double lastY;

    private boolean setStart = true;

    public Controller(Model model, View view) {

    view.getCanvas().setOnMousePressed(e -> {
        startX = lastX = e.getX();
        startY = lastY = e.getY();
        leftClickRange = false;
    });

    view.getCanvas().setOnMouseDragged(e -> {
        double dx = e.getX() - lastX;
        double dy = e.getY() - lastY;
        view.panMap(dx, dy);

        lastX = e.getX();
        lastY = e.getY();

        if (!withinRange(e.getX() - startX, e.getY() - startY, clickRadius)) {
            leftClickRange = true;
        }

        view.redrawMap();
    });
    view.getCanvas().setOnMouseClicked(e -> {
        double clickX = e.getX();
        double clickY = e.getY();

        if (leftClickRange) return;

        if (setStart) {
            view.setStartClick(new Point2D(clickX, clickY));
            view.hideShortestPath();
        } else {
            view.setEndClick(new Point2D(clickX, clickY));
            view.redrawMap();
            view.findShortestPath();
        }
        setStart = !setStart;

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
    view.getSearchbar().setOnMouseClicked(event -> {
        view.showSearchResults();
    });
    /*/
    view.getSearchbar().setOnMouseExited(event -> {
        view.hideSearchResults();
    });
    //*/
    view.getSearchbar()
        .setOnKeyTyped(event -> {
            String searchText = view.getSearchbar().getText().toLowerCase();
            if (true) {
                view.search(searchText);
                view.showSearchResults();
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

    private boolean withinRange(double deltaX, double deltaY, double range) {
        double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        return magnitude <= range;
    }
}
