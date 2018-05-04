package it.polimi.ingsw.toolaction;
import it.polimi.ingsw.Player;

/**
 * This class implements the tool named "Eglomise Brush" of ToolCard
 */
public class ToolAction2 implements ToolAction{

    /**
     * Allows the Player given by ToolCard to use the tool during the game
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    public boolean useToolCard(Player player){
        return false;
    }
}