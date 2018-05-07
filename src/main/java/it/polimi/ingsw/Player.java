package it.polimi.ingsw;

public class Player {
    private String username;
    private String password;
    private ServerConn serverConn;
    private PrivObjectiveCard privObjective;
    private SchemaCard schema;
    private int favorTokens;
    private int finalPosition;
    private int score;
    private Boolean skipsNextTurn;// true when the player has used
    private Boolean isConnected;
    //to be continued

    public Player(String username, String password){
        this.username=username;
        this.password=password;
    }

    /**
     * Assign a SchemaCard to the player
     * @param id Schemacard's id
     */
    public void chooseSchemaCard(Integer id){
        schema = new SchemaCard(id,GameController.xmlSource+"SchemaCard.xml");
        favorTokens=schema.getFavorTokens();
        isConnected=true;
    }

    /**
     * Allows the user to perform the login
     * @param username username
     * @param password  password
     * @return true if the credentials matches
     */
    public Boolean login(String username,String password){
        if(this.username==username && this.password==password){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Assign a SchemaCard to the player
     * @param id Schemacard's id
     * @param schemaCardFilename xml file path
     */
    public void chooseSchemaCard(Integer id,String schemaCardFilename){ //for extra schemacards
        schema = new SchemaCard(id,GameController.xmlSource+schemaCardFilename);
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

    public void calculateScore(){
        score+=privObjective.getCardScore(schema);
    }
}
