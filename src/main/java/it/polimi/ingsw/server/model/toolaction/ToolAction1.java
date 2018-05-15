package it.polimi.ingsw.server.model.toolaction;
import it.polimi.ingsw.server.model.Player;

/**
 * This class implements the tool named "Grozing Pliers" of ToolCard
 */
public class ToolAction1 implements ToolAction{

    /**
     * Allows the Player given by ToolCard to use the tool during the game
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    public boolean useToolCard(Player player){
        return false;
    }
}
