package it.polimi.ingsw;

public class Player {
    private String username;
    private String password;
    private int favorTokens;
    private int finalPosition;
    private int score;
    private Boolean skipsNextTurn;// true when the player has used a particular tool card on his first turn of the round
    private Boolean isConnected;

    private ServerConn serverConn;
    private Board board;
    private PrivObjectiveCard privObjective;
    private SchemaCard schema;

    public Player(String username, String password){
        this.username=username;
        this.password=password;
    }

    /**
     * Returns the player name
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Allows the user to perform the login
     * @param username username
     * @param password  password
     * @return true if the credentials matches
     */
    public Boolean login(String username,String password){
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Assign a SchemaCard to the player
     * @param id Schemacard's id
     */
    public void chooseSchemaCard(Integer id){
        schema = new SchemaCard(id,Game.xmlSource+"SchemaCard.xml");
        favorTokens=schema.getFavorTokens();
        isConnected=true;
    }

    /**
     * Assign a SchemaCard to the player
     * @param id SchemaCard's id
     * @param fileName xml file path
     */
    public void chooseSchemaCard(Integer id,String fileName){ //for extra schemacards
        schema = new SchemaCard(id,Game.xmlSource+fileName);
    }

    /**
     * Returns the favorTokens left to the player
     * @return the number of favorTokens
     */
    public int getFavorTokens() {
        return favorTokens;
    }

    /**
     * Decrease the favorTokens if possible
     * @param tokens token to subtract
     * @return true if the operation was successful
     */
    public boolean decreaseFavorTokens(int tokens){
        if(tokens>0 && this.favorTokens >= tokens){
            this.favorTokens -= tokens;
            return true;
        }
        return false;
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


    /**
     * Allows tho change the player's connection status
     * @param isConnected connection status
     */
    public void setConnected(Boolean isConnected){
        this.isConnected=isConnected;
    }

    /**
     * Returns if the player is connected on the server
     * @return player's connection status
     */
    public Boolean getConnected (){
        return this.isConnected;
    }

}