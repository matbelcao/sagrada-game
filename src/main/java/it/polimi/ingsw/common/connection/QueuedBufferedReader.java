package it.polimi.ingsw.common.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class QueuedBufferedReader implements QueuedReader {
    private final BufferedReader inReader;
    private final Object lockReader = new Object();
    private final Object lockQueue = new Object();
    private String temp;
    private List<String> queue = new ArrayList<>();

    /**
     * Instantiates a new Queued buffered reader.
     *
     * @param inReader the in reader
     */
    public QueuedBufferedReader(BufferedReader inReader) {
        this.inReader= inReader;
    }

    /**
     * clears the queue of messages
     */
    @Override
    public void clear() {
        synchronized (lockQueue){
            queue.clear();
            lockQueue.notifyAll();
        }


    }

    /**
     * this adds a message to the queue if any
     * @throws IOException
     */
    @Override
    public void add() throws IOException {
        try {
            synchronized (lockReader) {
                while ((temp = inReader.readLine())==null) {
                    lockReader.wait(100);
                }


            }
        } catch (InterruptedException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());

        }
        put();
    }


    /**
     * puts the read message to the queue
     */
    private void put(){
        synchronized(lockQueue) {
            queue.add(temp);
            lockQueue.notifyAll();
        }
        temp=null;
    }

    /**
     * @return the first element of the queue
     */
    @Override
    public String readln(){
        return queue.get(0);
    }

    /**
     * returns the first element and removes it from the queue
     * @return the first elem of the queue
     */
    @Override
    public String getln(){
        synchronized(lockQueue) {
            String line = queue.get(0);
            queue.remove(0);
            lockQueue.notifyAll();
            return line;
        }
    }

    /**
     * removes the first element of the queue
     */
    @Override
    public void pop(){
        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            queue.remove(0);
            lockQueue.notifyAll();
        }
    }

    /**
     * @return true if the queue is empty
     */
    @Override
    public boolean isEmpty(){
        synchronized (lockQueue) {
            lockQueue.notifyAll();
            return queue.isEmpty();
        }
    }

    /**
     * if empty tries to add a line
     * @throws IOException
     */
    @Override
    public void waitForLine() throws IOException {
        if (isEmpty()){
            add();
        }
    }
}
