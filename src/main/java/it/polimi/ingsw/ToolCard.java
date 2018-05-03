package it.polimi.ingsw;
import it.polimi.ingsw.toolaction.ToolAction;

import java.lang.reflect.InvocationTargetException;

/**
 * This class implements the Cards named "Tools" and their score calculating algorithms
 */
public class ToolCard extends Card{
    private boolean used;
    private ToolAction toolAction;

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
     * Return if the toolcard has been used during the game
     * @return true if it has been used, false if not
     */
    public boolean hasBeenUsed(){
        return this.used;
    }

    /**
     * This method calls the card-specific method for using the Tool
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    public boolean useTool(Player player){
        return toolAction.useToolCard(player);
    }

}
