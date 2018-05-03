package it.polimi.ingsw;

/**
 * This exception signals that an illegal shade is trying to be set
 */
public class IllegalShadeException extends Exception {

    /**
     * Default constructor, calls the super default constructor
     */
    IllegalShadeException(){
        super();
    }

    /**
     * Calls the super constructor to create a new IllegalShadeException with a message
     * @param message the message
     */
    IllegalShadeException(String message){
        super(message);
    }
}
