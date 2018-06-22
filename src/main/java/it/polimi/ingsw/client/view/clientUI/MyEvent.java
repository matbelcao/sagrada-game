package it.polimi.ingsw.client.view.clientUI;

import javafx.event.Event;
import javafx.event.EventType;

public class MyEvent extends Event {
    int cellIndex;
    public static EventType<MyEvent> MOUSE_EXITED_BACK_PANE = new EventType<>("MOUSE_EXITED_BACK_PANE");
    public static EventType<MyEvent> MOUSE_ENTERED_MULTIPLE_DICE_CELL = new EventType<>("MOUSE_ENTERED_MULTIPLE_DICE_CELL");

    public MyEvent(EventType<? extends Event> eventType, int cellIndex) {
        super(eventType);
        this.cellIndex = cellIndex;
    }
    public MyEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    int getCellIndex(){
        return cellIndex;
    }
}