package it.polimi.ingsw.client.view.clientUI.uielements;

import javafx.event.Event;
import javafx.event.EventType;

public class MyEvent extends Event {
    private int eventObjectIndex;
    public static final EventType<MyEvent> MOUSE_EXITED_BACK_PANE = new EventType<>("MOUSE_EXITED_BACK_PANE");
    public static final EventType<MyEvent> MOUSE_ENTERED_MULTIPLE_DICE_CELL = new EventType<>("MOUSE_ENTERED_MULTIPLE_DICE_CELL");
    public static final EventType<MyEvent> SELECTED_PLAYER = new EventType<>("SELECTED_PLAYER");

    public MyEvent(EventType<? extends Event> eventType, int eventObjectIndex) {
        super(eventType);
        this.eventObjectIndex = eventObjectIndex;
    }
    public MyEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    public int getEventObjectIndex(){
        return eventObjectIndex;
    }
}