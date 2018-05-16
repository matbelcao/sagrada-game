package it.polimi.ingsw.server.connection;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    public void notifyLobbyUpdate(int n);

    public void notifyGameStart(int n,int id);

    public void notifyStatusUpdate (String event,int id);
}
