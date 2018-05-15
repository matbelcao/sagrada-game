package it.polimi.ingsw;
import it.polimi.ingsw.toolaction.ToolAction;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the Cards named "Tools" and their score calculating algorithms
 */
public class ToolCard extends Card{
    private boolean used;
    private ToolAction toolAction;
    static final int NUM_TOOL_CARDS=12;

    /**
     * Constructs the card setting its id, name, description and use calculating algorithm
     * @param id the id of the card
     * @param xmlSrc the address to the xml file containing necessary information to initialize the cards
     */
    public ToolCard(int id, String xmlSrc){
        super();
        super.xmlReader(id,xmlSrc,"ToolCard");
        this.used=false;

        String className = "ToolAction" + id;
        String fullPathOfTheClass = "it.polimi.ingsw.toolaction." + className;
        Class cls = null;
        try {
            cls = Class.forName(fullPathOfTheClass);
            assert cls != null;
            this.toolAction = (ToolAction) cls.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks whether the player can or can not use the tool card, based on the cost in favor tokens
     * @param player the player that wants to use the tool card
     * @return true iff the
     */
    public boolean canBeUsedBy(Player player){
        int cost;
        if (used) cost = 2;
        else cost = 1;
        return player.getFavorTokens() >= cost;
    }

    /**
     * This method calls the card-specific method for using the Tool
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    public boolean useTool(Player player) throws NegativeTokensException {
        if(!used){
            used =true;
            player.decreaseFavorTokens(1);
        }else{
            player.decreaseFavorTokens(2);
        }

        if (this.getId()==8){ player.setSkipsNextTurn(true); }
        return toolAction.useToolCard(player);
    }

}
