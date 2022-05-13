package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
/**
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 *
 * handles the display of the error message
 */
public final class ErrorManager {

    public static final int LOW_OPACITY = 0;
    public static final double HIGH_OPACITY = 0.8;
    private final Pane pane;
    private final Text text;
    private SequentialTransition sequentialTransition;
    private static final Duration FIRST_DURATION=new Duration(200);
    private static final Duration SECOND_DURATION=new Duration(500);
    private static final Duration PAUSE_DURATION=new Duration(2000);

    /**
     * constructor of the class
     */
    public ErrorManager(){
        text=new Text();
        pane=new VBox(text);
        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);
        createSequentialTransition();
    }

    /**
     * returns the pane containing the error message
     * @return the pane containing the error message
     */
    public Pane pane(){
        return pane;
    }

    /**
     * displayes the given error message
     * @param message the error message to be displayed
     */
    public void displayError(String message){
        text.textProperty().set(message);
        sequentialTransition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        sequentialTransition.play();
    }

    /**
     * creates the necessary transitions for the erroe message display
     */
    private void createSequentialTransition(){
        FadeTransition firstFadeTransition = new FadeTransition(FIRST_DURATION, pane);
        firstFadeTransition.setFromValue(LOW_OPACITY);
        firstFadeTransition.setToValue(HIGH_OPACITY);

        PauseTransition pauseTransition = new PauseTransition(PAUSE_DURATION);

        FadeTransition secondFadeTransition = new FadeTransition(SECOND_DURATION, pane);
        secondFadeTransition.setFromValue(HIGH_OPACITY);
        secondFadeTransition.setToValue(LOW_OPACITY);

        sequentialTransition = new SequentialTransition(firstFadeTransition, pauseTransition, secondFadeTransition);

    }
}
