package it.polimi.ingsw.common.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class QueuedInReader {
    private BufferedReader inReader;
    private final Object lockReader = new Object();
    private final Object lockQueue = new Object();
    private String temp;
    private ArrayList<String> queue = new ArrayList<>();

    public QueuedInReader(BufferedReader inReader) {
        this.inReader= inReader;
    }

    public void add() throws IOException {
        try {
            synchronized (lockReader) {
                while ((temp = inReader.readLine())==null) {
                    lockReader.wait(100);
                }

                lockReader.notifyAll();
                //debug
                System.out.println("\t\t\t\t\t"+temp);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //debug
            //System.out.println("ERR interrupt");
        }
        put();
    }

    private void put(){
        synchronized(lockQueue) {
            queue.add(temp);
            lockQueue.notifyAll();
        }
            temp=null;
    }

    public String readln(){
        return queue.get(0);
    }

    public String getln(){

        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            String line = queue.get(0);
            queue.remove(0);
            lockQueue.notifyAll();
            return line;
        }
    }

    public void pop(){
        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            queue.remove(0);
            lockQueue.notifyAll();
        }
    }

    public boolean isEmpty(){
        synchronized (lockQueue) {
            lockQueue.notifyAll();
            return queue.isEmpty();
        }
    }
    public void waitForLine() throws IOException {
        while(isEmpty()){
            add();
        }
    }
}
