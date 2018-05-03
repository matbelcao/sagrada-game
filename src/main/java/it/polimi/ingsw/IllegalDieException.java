package it.polimi.ingsw;

public class IllegalDieException extends Exception {
    /**
     * Constructs  anIllegalDieException with a string message
     * @param s
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
