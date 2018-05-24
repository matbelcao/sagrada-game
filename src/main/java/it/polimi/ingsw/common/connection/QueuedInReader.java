package it.polimi.ingsw.common.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class QueuedInReader {
    private BufferedReader inSocket;
    private String temp;
    private final ArrayList<String> queue=new ArrayList<>();

    public QueuedInReader(BufferedReader inSocket) {
        this.inSocket = inSocket;
    }

    public void add() throws IOException {
        while(!inSocket.ready()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        temp=inSocket.readLine();
        put();
    }

    private void put(){
        synchronized(queue) {
            queue.add(temp);
        }
            temp=null;
    }

    public String readln(){
        return queue.get(0);
    }

    public String getln(){

        synchronized(queue) {
            assert(!queue.isEmpty());
            String line = queue.get(0);
            queue.remove(0);
            return line;
        }
    }

    public void pop(){
        synchronized(queue) {
            assert(!queue.isEmpty());
            queue.remove(0);

        }
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
