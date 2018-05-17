package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.controller.Game;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    void notifyLobbyUpdate(int n);

    void notifyGameStart(int n,int id);

    void notifyStatusUpdate (String event,int id);




}
