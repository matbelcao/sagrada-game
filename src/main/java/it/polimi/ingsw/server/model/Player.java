package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

public class Player {
    private int favorTokens;
    private int finalPosition;
    private int score;
    private boolean skipsNextTurn;// true when the player has used a particular tool card on his first turn of the round
    private Board board;
    private PrivObjectiveCard privObjective;
    private SchemaCard schema;
    private Die chosenDie;

    public Player(Board board,SchemaCard schema, PrivObjectiveCard privObjective){
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