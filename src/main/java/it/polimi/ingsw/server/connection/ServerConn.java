package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.model.PrivObjectiveCard;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.ToolCard;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    /**
     * Sends the lobby update message to the user
     * @param n the number of players in the lobby
     */
    void notifyLobbyUpdate(int n);

    /**
     * Sends the match starting message to the user
     * @param n the number of connected players
     * @param id the assigned id of the specific user
     */
    void notifyGameStart(int n,int id);

    /**
     * Notifies the client of a user's status change
     * @param event the event happened
     * @param id the id of the interested user
     */
    void notifyStatusUpdate (String event,int id);

    /**
     * Sends the user a text description of the schema card passed as a parameter
     * @param schemaCard the schema card to send
     */
    void notifySchema(SchemaCard schemaCard);

    /**
     * Sends the user a text description of the tool card passed as a parameter
     * @param toolCard the tool card to send
     */
    void notifyToolCard(ToolCard toolCard);

    /**
     * Sends the user a text description of the public objective card passed as a parameter
     * @param pubObjectiveCard the public objective card to send
     */
    void notifyPublicObjective(PubObjectiveCard pubObjectiveCard);

    /**
     * Sends the user a text description of the private objective card passed as a parameter
     * @param privObjectiveCard the private objective card to send
     */
    void notifyPrivateObjective(PrivObjectiveCard privObjectiveCard);

    boolean ping();

}
