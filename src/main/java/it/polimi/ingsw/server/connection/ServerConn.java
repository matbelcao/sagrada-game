package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    void notifyLobbyUpdate(int n);

    void notifyGameStart(int n,int id);

    void notifyStatusUpdate (String event,int id);

    void notifyGameEnd(List<Player> players);

    void notifyRoundEvent(String event,int roundNumber);

    void notifyTurnEvent(String event,int playerId,int turnNumber);

    void notifySchema(SchemaCard schemaCard);

    void notifyToolCard(ToolCard toolCard);

    void notifyPublicObjective(PubObjectiveCard pubObjectiveCard);

    void notifyPrivateObjective(PrivObjectiveCard privObjectiveCard);

    void notifyPlayers(List<Player> players);

    void notifyDraftPool(List<Die> draftedDice);

    void notifyRoundTrack(ArrayList<ArrayList<Die>> trackList);

    void notifyFavorTokens(int favorTokens);

    void notifySchemaDiceList(SchemaCard schema);

    void notifyRoundTrackDiceList(ArrayList<ArrayList<Die>> trackList);

    void notifyDraftPoolDiceList(List<Die> draftedDice);

    boolean ping();



}
