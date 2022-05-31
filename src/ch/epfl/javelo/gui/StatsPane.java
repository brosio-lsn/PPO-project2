package ch.epfl.javelo.gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.function.Consumer;
/**
 * represents the pane containing the eco-stats
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */
public class StatsPane {

    private static final int UNIT_TO_THOUSAND = 1000;
    private static final double DIESEL_CO2_GRAMS_PER_LITER = 2640;
    private static final double GASOLINE_CO2_GRAMS_PER_LITER = 2392;
    private static final int HUNDRED_TO_UNIT_FACTOR = 100;
    private final RouteBean routeBean;
    private final Label conso;
    private final TextField consoF;
    private final Label fuel;
    private final ChoiceBox fuelChoiceBox;
    private final Button calculate;
    private final Text calValue;
    private final Consumer<String> consumer;
    private final GridPane grid;
    private final Label title;

    /**
     * the constructor of the class
     * @param routeBean the routeBean of the route
     * @param consumer  an error consumer to display error messages
     */
    public StatsPane(RouteBean routeBean, Consumer<String> consumer){
        this.routeBean=routeBean;
        this.consumer=consumer;
        title=new Label("Calculate how much CO2 your car would have produced for this route:");
        title.setTextFill(Color.RED);
        calculate = new Button("calculate");
        calValue= new Text();
        grid=new GridPane();
        grid.setPickOnBounds(false);
        conso = new Label(" fuel consumption(liter/100km)");
        consoF= new TextField();
        fuel = new Label(" fuel type");
        fuelChoiceBox= new ChoiceBox(FXCollections.observableArrayList("diesel", "gasoline"));
        grid.addRow(0, title);
        grid.addRow(1, conso, consoF);
        grid.addRow(2, fuel, fuelChoiceBox);
        grid.addRow(3, calculate, calValue);
        grid.backgroundProperty().set(new Background(new BackgroundFill(
                Color.LIGHTBLUE,
                new CornerRadii(2),
                new Insets(0)
        )));
        grid.setAlignment(Pos.CENTER);

        createListeners();
    }

    /**
     * returns the pane containing all the graphical elements
     * @return the pane containing all the graphical elements
     */
    public GridPane pane(){
        return grid;
    }

    /**
     * makes the pane dynamic
     */
    private void createListeners() {
        calculate.setOnAction(event -> {
            //calValue.textProperty().set(String.valueOf("lol"));
            String TxtConsoF = consoF.textProperty().get();
            Double inDouble;
            try {
                inDouble = Double.parseDouble(TxtConsoF);
            } catch (NumberFormatException e) {
                consumer.accept("Give a valid fuel consumption!!");
                return;
            }
            String selectedFuelType = (String) fuelChoiceBox.getSelectionModel().getSelectedItem();
            if (selectedFuelType == null) {
                consumer.accept("Enter the fuel type!!");
                return;
            }
            double factor = selectedFuelType.equals("diesel") ? DIESEL_CO2_GRAMS_PER_LITER : GASOLINE_CO2_GRAMS_PER_LITER;
            calValue.textProperty().set(String.format("%.1f kilogrammes of CO2 not produced :)",
                    inDouble * factor * routeBean.route().get().length() / (UNIT_TO_THOUSAND * UNIT_TO_THOUSAND* HUNDRED_TO_UNIT_FACTOR)));
        });

        routeBean.route().addListener((property, prev, newV) -> {
            calValue.textProperty().set(null);
        });
    }


}