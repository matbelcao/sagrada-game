package it.polimi.ingsw.common.connection;

import java.io.IOException;

/**
 * Reads commands from an input source and adds them to a FIFO queue
 */
public interface QueuedReader {
    /**
     * clears the queue of messages
     */
    void clear();

    /**
     * this adds a message to the queue if any
     * @throws IOException
     */
    void add() throws IOException;

    /**
     * returns the first element and removes it from the queue
     * @return the first elem of the queue
     */
    String getln();

    /**
     * @return the first elem of the queue
     */
    String readln();

    /**
     * @return true if the queue is empty
     */
    boolean isEmpty();

    /**
     * removes the first element of the queue
     */
    void pop();

    /**
     * if empty tries to add a line
     * @throws IOException
     */
    void waitForLine() throws IOException;
}
