package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.view.clientUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Timer;
import java.util.TimerTask;
/**
 * This class represents a listener of the size of the stage.
 */
public class SizeListener implements ChangeListener<Number> {
    private static final int DELAY_TIME = 250; // delay that has to pass in order to consider an operation done
    private Timer timer;
    private final Object lockTimer;
    private GUI gui;
    private boolean enabled = false; //resizing can be disabled this way in the scenes that are not meat to be resizable

    /**
     * Conatructor of the class
     * @param gui a reference to the gui to be used to draw on it
     */
    public SizeListener(GUI gui){
        this.timer = new Timer();
        this.lockTimer = new Object();
        this.gui = gui;
    }
    //when the sizes of the stage change a new timertask starts, if it reaches zero it triggers a redraw of the scene
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        synchronized (lockTimer) {
                if (enabled) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer(); //to be shure to delete all the accumulated timer tasks
                    TimerTask task; // task to execute after defined delay
                    task = new TimerTask() {// create new task that calls resize operation
                        @Override
                        public void run() {
                            if (enabled) {
                                gui.drawMainGameScene();
                            }
                        }
                    };
                    // schedule new task
                    timer.schedule(task, DELAY_TIME);
                }
            }
    }

    /**
     * this method deletes the timer and gets called everytime the size listener gets disabled or the stage gets resized
     */
    public void purgeTimer(){
        synchronized (lockTimer) {
          if(timer!=null){
             timer.cancel();
          }
        }
    }

    /**
     * This method enables the listener
     */
    public void enable() {
        this.enabled = true;
    }
    /**
     * This method disables the listener
     */
    public void disable() {
        purgeTimer();
        this.enabled = false;
    }
}
