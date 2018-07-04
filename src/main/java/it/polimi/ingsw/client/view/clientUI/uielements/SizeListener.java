package it.polimi.ingsw.client.view.clientUI.uielements;

import it.polimi.ingsw.client.view.clientUI.GUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Timer;
import java.util.TimerTask;

public class SizeListener implements ChangeListener<Number> {
    private static final int DELAY_TIME = 250;
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
             {
                if(timer!=null){
                    timer.cancel();
                   System.out.print("CANCELLATO TIMER");
                }
                timer = new Timer();
                TimerTask task = null; // task to execute after defined delay
                final long delayTime = DELAY_TIME; // delay that has to pass in order to consider an operation done
                    if (task != null) { // there was already a task scheduled from the previous operation ...
                        task.cancel(); // cancel it, we have a new size to consider
                    }
                    task = new TimerTask() {// create new task that calls resize operation
                        @Override
                        public void run() {
                            System.out.println("resize to stage");

                            System.out.println("resiziiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiing");
                            if(enabled) {
                                gui.drawMainGameScene();
                            }
                        }
                    };
                    // schedule new task
                    timer.schedule(task, delayTime);
                }
            }
    }

    public void purgeTimer(){
        synchronized (lockTimer) {
            {
                if(timer!=null){
                    timer.cancel();
                    //System.out.print("CANCELLATO TIMER");
                }
            }
        }
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
