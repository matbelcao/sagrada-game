package it.polimi.ingsw.client.uielements;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private List<String> queue;
    private final Object lockQueue=new Object();
    public CommandQueue(){
        queue=new ArrayList<>();
    }

    public void add(String command){
        synchronized (lockQueue){
            queue.add(command.trim());
            lockQueue.notifyAll();
        }
    }

    public boolean isEmpty(){
        synchronized (lockQueue){
            if(queue.isEmpty()){
                notifyAll();
                return true;
            }
            notifyAll();
            return false;

        }
    }

    public String read(){
        synchronized (lockQueue){
            while(queue.isEmpty()){
                try {
                    lockQueue.wait();
                } catch (InterruptedException e) {}
            }
            String line=queue.get(0);
            lockQueue.notifyAll();
            return line;
        }
    }

    public void pop(){
        synchronized (lockQueue) {
            while (queue.isEmpty()) {
                try {
                    lockQueue.wait();
                } catch (InterruptedException e) {
                }
            }
            queue.remove(0);
            lockQueue.notifyAll();
        }
    }

    public String  get(){
        String line=read();
        pop();
        return line;
    }



}
