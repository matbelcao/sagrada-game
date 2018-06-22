package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.server.model.exceptions.NegativeTokensException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.util.List;
import java.util.NoSuchElementException;

public class  Player {
    private final String username;
    private final int gameId;
    private final Board board;
    private final PrivObjectiveCard privObjective;
    private SchemaCard schema;
    private int favorTokens;
    private int finalPosition;
    private int score;
    private boolean skipsNextTurn;// true when the player has used a particular tool card on his first turn of the round
    private boolean quitted;


    public Player(String username,int gameId, Board board, PrivObjectiveCard privObjective){
        this.username = username;
        this.gameId=gameId;
        this.board=board;
        this.privObjective=privObjective;
        this.score=0;
        this.finalPosition=0;
        this.schema=null;
        this.favorTokens=0;
        this.skipsNextTurn=false;
        this.quitted=false;
    }

    /**
     * Returns the associated username (the same in the relative User class)
     * @return the player's username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Return the player's id of the match
     * @return the id of the match
     */
    public int getGameId(){
        return this.gameId;
    }

    /**
     * Returns the favorTokens left to the player
     * @return the number of favorTokens
     */
    public int getFavorTokens() {
        return favorTokens;
    }

    /**
     * Returns the private objective card of the player
     * @return the card
     */
    public PrivObjectiveCard getPrivObjective(){return this.privObjective;}

    /**
     * Checks if the player has to skip this turn or not, then reset the variable to false
     * @return
     */
    public boolean isSkippingTurn() {
        Boolean skip=this.skipsNextTurn;
        this.skipsNextTurn=false;
        return skip;
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
     * This method assigns a schemaCard to the player
     * @param schema the Schema Card to assign
     * @return true iff the schema will be set
     */
     public boolean setSchema(SchemaCard schema){
        this.schema=schema;
        this.favorTokens=this.schema.getFavorTokens();
        return true;
     }

     public void replaceSchema(SchemaCard schema){
         this.schema=schema;
     }

    /**
     * Gets the schemaCard assigned to the player
     * @return the schema
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
        if(schema!=null) {
            List<Die> schemaDie = schema.getSchemaDiceList(Color.NONE);

            if (this.score != 0) {
                this.score = 0;
            }
            for (int i = 0; i < 3; i++) {
                this.score += this.board.getPublicObjective(i).getCardScore(this.schema);
            }
            this.score += this.favorTokens;
            this.score += this.privObjective.getCardScore(this.schema);
            this.score -= (SchemaCard.NUM_ROWS * SchemaCard.NUM_COLS) - schemaDie.size();

            if (this.score < 0) {
                this.score = 0;
            }
        }
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

    public void quitMatch(){
        quitted=true;
    }

    public boolean hasQuitted(){
        return quitted;
    }
}