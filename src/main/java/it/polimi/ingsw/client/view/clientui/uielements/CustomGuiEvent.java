package it.polimi.ingsw.client.view.clientui.uielements;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * This class represent events meant to be fired only in particular occasion in the game by nodes. Those events are specifically handled
 * by the parents of the nodes that fire those events so that they can switch the view.
 * During the game the view changes only when the client updates it, except when such events are fired.
 */
public class CustomGuiEvent extends Event {
    private int eventObjectIndex;
    public static final EventType<CustomGuiEvent> MOUSE_EXITED_BACK_PANE = new EventType<>("MOUSE_EXITED_BACK_PANE");
    public static final EventType<CustomGuiEvent> MOUSE_ENTERED_MULTIPLE_DICE_CELL = new EventType<>("MOUSE_ENTERED_MULTIPLE_DICE_CELL");
    public static final EventType<CustomGuiEvent> SELECTED_PLAYER = new EventType<>("SELECTED_PLAYER");

    /**
     * Constructor of the class
     * @param eventType he event type of the custom gui class
     * @param eventObjectIndex the index of the node firing the event in its container
     */
    CustomGuiEvent(EventType<? extends Event> eventType, int eventObjectIndex) {
        super(eventType);
        this.eventObjectIndex = eventObjectIndex;
    }

    /**
     * Constructor of the class
     * @param eventType the event type of the custom gui class
     */
    CustomGuiEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public int getEventObjectIndex(){
        return eventObjectIndex;
    }
}