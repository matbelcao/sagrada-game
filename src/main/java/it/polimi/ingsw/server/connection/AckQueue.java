package it.polimi.ingsw.server.connection;

import java.util.ArrayList;

/**
 * this class implements a FIFO queue to store the ACKs expected from a client
 */
public class AckQueue {
    private final ArrayList<String> expected=new ArrayList<>();

    /**
     * adds according to a FIFO logic a ack to expect from the client
     * @param ack said ack
     */
    public void add(String ack){
        ArrayList<String> result=new ArrayList<>();
        if(!Validator.checkAckParams(ack,result)){ throw new IllegalArgumentException();}

        synchronized (expected){
            this.expected.add(ack);
            expected.notifyAll();
        }
    }

    /**
     * removes the first element added of those ones remaining only if it matches the ack passed as parameter
     * @param ack the ack to be removed
     * @return true iff the ack matched and was removed from the queue
     */
    public boolean remove(String ack){
        synchronized (expected){
            if(ack.equals(expected.get(0))){
                expected.remove(0);
                expected.notifyAll();
                return true;
            }
            expected.notifyAll();
        }

        return false;
    }

}
