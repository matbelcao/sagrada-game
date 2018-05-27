package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightTool;
import it.polimi.ingsw.server.model.PrivObjectiveCard;
import it.polimi.ingsw.server.model.PubObjectiveCard;
import it.polimi.ingsw.server.model.SchemaCard;

import java.util.ArrayList;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    void notifyLobbyUpdate(int n);

    void notifyGameStart(int n,int id);

    void notifyStatusUpdate (String event,int id);

    void notifySchema(SchemaCard schemaCard);

    void notifyToolCard(LightTool toolCard);

    void notifyPublicObjective(LightCard pubObjectiveCard);

    void notifyPrivateObjective(LightCard privObjectiveCard);

    void notifyPlayers(ArrayList<LightPlayer> players);

    void notifyFavorTokens(int favorTokens);

    boolean ping();



}
