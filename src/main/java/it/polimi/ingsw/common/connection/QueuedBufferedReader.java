package it.polimi.ingsw.common.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueuedBufferedReader implements QueuedReader {
    private final BufferedReader inReader;
    private final Object lockReader = new Object();
    private final Object lockQueue = new Object();
    private String temp;
    private List<String> queue = new ArrayList<>();

    public QueuedBufferedReader(BufferedReader inReader) {
        this.inReader= inReader;
    }



    @Override
    public void clear() {
        synchronized (lockQueue){
            queue.clear();
            lockQueue.notifyAll();
        }


    }

    @Override
    public void add() throws IOException {
        try {
            synchronized (lockReader) {
                while ((temp = inReader.readLine())==null) {
                    lockReader.wait(100);
                }

                //debug
                if(!temp.equals("PING"))
                    System.out.println("\t\t\t\t\t"+temp);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //debug
            System.err.println("ERR interrupt");
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

    @Override
    public String readln(){
        return queue.get(0);
    }

    @Override
    public String getln(){
        synchronized(lockQueue) {
            String line = queue.get(0);
            queue.remove(0);
            lockQueue.notifyAll();
            return line;
        }
    }

    @Override
    public void pop(){
        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            queue.remove(0);
            lockQueue.notifyAll();
        }
    }

    @Override
    public boolean isEmpty(){
        synchronized (lockQueue) {
            lockQueue.notifyAll();
            return queue.isEmpty();
        }
    }
    @Override
    public void waitForLine() throws IOException {
        if (isEmpty()){
            add();
        }
    }
}
