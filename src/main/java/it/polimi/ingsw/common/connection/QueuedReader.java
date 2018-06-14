package it.polimi.ingsw.common.connection;

import java.io.IOException;

public interface QueuedReader {
    void clear();
    void add() throws IOException;
    String getln();
    String readln();
    boolean isEmpty();
    void pop();
    void waitForLine() throws IOException;
}
