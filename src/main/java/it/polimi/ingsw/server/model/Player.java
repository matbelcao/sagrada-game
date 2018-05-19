package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.util.NoSuchElementException;

public class Player {
    private final String username;
    private final Board board;
    private final PrivObjectiveCard privObjective;
    private final SchemaCard schema;
    private int favorTokens;
    private int finalPosition;
    private int score;
    private boolean skipsNextTurn;// true when the player has used a particular tool card on his first turn of the round



    private Die chosenDie;

    public Player(String username, Board board, SchemaCard schema, PrivObjectiveCard privObjective){
        this.username = username;
        this.board=board;
        this.schema=schema;
        this.privObjective=privObjective;
        this.score=0;
        this.finalPosition=0;
        this.favorTokens=this.schema.getFavorTokens();
        this.skipsNextTurn=false;
    }

    /**
     * Returns the favorTokens left to the player
     * @return the number of favorTokens
     */
    public int getFavorTokens() {
        return favorTokens;
    }

    /**
     * Checks if the player has to skip this turn or not
     * @return
     */
    public boolean isSkippingTurn() {
        return this.skipsNextTurn;
    }

    /**
     * This method sets the skipsNextTurn flag following the usage of the tool card number 8. Need to set this to false on every round beginning
     * @param skips true iff the player used the tool card number 9 in the first turn of the round
     */
    public void setSkipsNextTurn(boolean skips){
        this.skipsNextTurn=skips;
    }

    /**
     * This method checks if the player matches with a user on their usernames
     * @param user the user to be checked
     * @return true iff the two usernames are the same
     */
    public boolean matchesUser(User user){ return this.username.equals(user.getUsername()); }

    /**
     * Gets the schemaCard assigned to the player
     * @return the schema3
     */
    public SchemaCard getSchema(){
        return this.schema;
    }

    /**
     * Decrease the favorTokens if possible
     * @param tokens token to subtract
     */
    public void decreaseFavorTokens(int tokens) throws NegativeTokensException {
        if((this.favorTokens - tokens) < 0){throw new NegativeTokensException();}
        this.favorTokens -= tokens;


    }

    /**
     * Returns the score of the player
     * @return player's score
     */
    public int getScore(){
        return this.score;
    }

    /**
     * Gets the player's chosen die for the current turn (make sure to set it before getting it)
     * @return the die
     */
    public Die getChosenDie() {
        if(chosenDie==null){ throw new NoSuchElementException(); }
        return chosenDie;
    }

    /**
     * Sets the chosen die for the turn
     * @param chosenDie the chosen die
     */
    public void setChosenDie(Die chosenDie) { this.chosenDie = chosenDie; }

    /**
     * Flushes (sets to null) the chosen die of the turn
     */
    public void flushChosenDie() {
        setChosenDie(null);
    }

    /**
     * Calculates and sets the score of the player
     */
    public void calculateScore(){
        FullCellIterator diceIterator=(FullCellIterator)this.schema.iterator();
        if (this.score != 0) {
            this.score = 0;
        }
        for (int i = 0; i < 3; i++) {
            this.score += this.board.getPublicObjective(i).getCardScore(this.schema);
        }
        this.score += this.favorTokens;
        this.score += this.privObjective.getCardScore(this.schema);
        this.score -= (SchemaCard.NUM_ROWS * SchemaCard.NUM_COLS) - diceIterator.numOfDice();
    }

    /**
     * Returns the final position of the player
     * @return player's final position
     */
    public int getFinalPosition() {
        return finalPosition;
    }

    /**
     * Sets the final position of the player
     * @param finalPosition the player's final position
     */
    public void setFinalPosition(int finalPosition) { this.finalPosition=finalPosition; }





}