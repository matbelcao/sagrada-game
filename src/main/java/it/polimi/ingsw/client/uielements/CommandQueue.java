package it.polimi.ingsw.client.uielements;

import java.util.List;

public class CommandQueue {
    private List<String> queue;
    private final Object lockQueue=new Object();

    public void add(String command){
        synchronized (lockQueue){
            queue.add(command.trim());
            lockQueue.notifyAll();
        }
    }

    public String read(){
        synchronized (lockQueue){
            while(queue.isEmpty()){
                try {
                    lockQueue.wait();
                } catch (InterruptedException e) {
                }
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
