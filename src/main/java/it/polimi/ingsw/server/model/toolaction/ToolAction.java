package it.polimi.ingsw.server.model.toolaction;
import it.polimi.ingsw.server.model.Player;

/**
 * This is the interface for the strategy pattern used to aim the Player using different ToolAction card
 */
public interface ToolAction{

    /**
     * Allows the Player given by ToolCard to use the tool during the game
     * @param player the player that wants to use the card
     * @return if the card has been used successfully
     */
    boolean useToolCard(Player player);
}
