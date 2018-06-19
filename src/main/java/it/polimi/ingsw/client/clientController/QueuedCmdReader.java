package it.polimi.ingsw.client.clientController;

import it.polimi.ingsw.common.connection.QueuedReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    e.printStackTrace();
                    System.exit(2);
                }
            }
            System.out.println("+++++++++++++++++++++++++command was" + cmd);
            temp = cmd;
            lockTemp.notifyAll();
        }
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
                    e.printStackTrace();
                    System.exit(2);
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
        synchronized (lockQueue) {
            String cmd = queue.get(0);
            queue.remove(0);
            lockQueue.notifyAll();

            return cmd;
        }
    }

    @Override
    public String readln() {
        return queue.get(0);
    }

    @Override
    public boolean isEmpty() {
        synchronized (lockQueue) {
            lockQueue.notifyAll();
            return queue.isEmpty();
        }
    }

    @Override
    public void pop() {
        synchronized(lockQueue) {
            assert(!queue.isEmpty());
            queue.remove(0);
            lockQueue.notifyAll();
        }
    }

    @Override
    public void waitForLine() throws IOException {
        if (isEmpty()){
            add();
        }
    }
}
