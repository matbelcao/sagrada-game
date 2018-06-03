package it.polimi.ingsw.server.model.exceptions;

public class IllegalDieException extends Exception {
    /**
     * Constructs  anIllegalDieException with a string message
     * @param s the message that possibly explains why that is
     */
    public IllegalDieException(String s) {
        super(s);
    }

    /**
     * Constructs  an IllegalDieException
     */
    public IllegalDieException(){
        super();
    }
}
