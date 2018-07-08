package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.connection.QueuedReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueuedCmdReader implements QueuedReader,CmdWriter {
    private final Object lockQueue=new Object();
    private final List<String> queue= new ArrayList<>();
    private final Object lockTemp=new Object();
    private String temp;

    @Override
    public void write(String cmd){
        synchronized (lockTemp) {
            while (temp!=null){
                try {
                    lockTemp.wait();
                } catch (InterruptedException e) {
                    Logger.getGlobal().log(Level.INFO,e.getMessage());
                    System.exit(2);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }

            temp = cmd;
            lockTemp.notifyAll();
        }
        System.out.println("+++++++++++++++++++++++++command was" + cmd); // TODO: 08/07/2018
    }

    @Override
    public void write(char cmd) {
        write(cmd + "");
    }

    @Override
    public void write(int cmd) {
        write(cmd + "");
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
        synchronized (lockTemp){
            while(temp==null){
                try {
                    lockTemp.wait();
                } catch (InterruptedException e) {
                    Logger.getGlobal().log(Level.INFO,e.getMessage());
                    System.exit(2);
                    // Restore interrupted state...
                    Thread.currentThread().interrupt();
                }
            }
            synchronized (lockQueue){
                queue.add(temp);
                lockQueue.notifyAll();
            }

            temp=null;
            lockTemp.notifyAll();
        }
    }

    @Override
    public String getln() {
        String cmd;
        synchronized (lockQueue) {
            cmd = queue.get(0);
            queue.remove(0);
        }
            return cmd;

    }

    @Override
    public String readln() {
        return queue.get(0);
    }

    @Override
    public boolean isEmpty() {
        synchronized (lockQueue) {
            return queue.isEmpty();
        }
    }

    @Override
    public void pop() {
        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            queue.remove(0);
        }
    }

    @Override
    public void waitForLine() throws IOException {
        if (isEmpty()){
            add();
        }
    }
}
