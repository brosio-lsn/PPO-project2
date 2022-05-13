package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
     * Default constructor of the errorManager class.
     * It initialises the Vbox the text should be displayed upon, sets the right stylesheets and creates the transitions
     */
    public ErrorManager(){
        text=new Text();
        pane=new VBox(text);
        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);
        createSequentialTransition();
    }

    public Pane pane(){
        return pane;
    }

    /**
     * displays the given error on the screen
     * @param message error to be displayed
     */
    public void displayError(String message){
        text.textProperty().set(message);
        System.out.println(message);
        sequentialTransition.stop();
        java.awt.Toolkit.getDefaultToolkit().beep();
        sequentialTransition.play();
    }

    /**
     * creates the graphic effects of the error display.
     * It consists of two fading transitions. One fades the text in, the other fades the text out of the screen.
     * Between these two fading transitions is a pause time, which allows the user to read the error.
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
