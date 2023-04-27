package Controller;

import Model.Model;
import View.View;
import javafx.geometry.Point2D;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import App.App;
import javafx.scene.input.KeyEvent;
import Parser.AddressParser;

public class Controller {
    private String search;
    private double lastX;
    private double lastY;
    AddressParser parser;

    public Controller(Model model, View view) {
    
    parser = new AddressParser(); 

    view.getCanvas().setOnMousePressed(e -> {
        lastX = e.getX();
        lastY = e.getY();
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
                System.out.println("Now searching for: " + view.getSearchbar().getText());
                parser.getAddresses(view.getSearchbar().getText());
            } catch (Exception e) {
                System.out.println(e);
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
