package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.view.clientUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Timer;
import java.util.TimerTask;

public class SizeListener implements ChangeListener<Number> {
    private static final int DELAY_TIME = 250; // delay that has to pass in order to consider an operation done
    private Timer timer;
    private final Object lockTimer;
    private GUI gui;
    private boolean enabled = false;

    public SizeListener(GUI gui){
        this.timer = new Timer();
        this.lockTimer = new Object();
        this.gui = gui;
    }
    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        synchronized (lockTimer) {
                if (enabled) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
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

    public void purgeTimer(){
        synchronized (lockTimer) {
          if(timer!=null){
             timer.cancel();
          }
        }
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        purgeTimer();
        this.enabled = false;
    }
}
