package it.polimi.ingsw.server.connection;

/**
 * This class is an interfaces that declares the common methods between SOCKET/RMI server-side connections
 */
public interface ServerConn {

    public void lobbyUpdate(int n);

    public void gameStart(int n,int id);
}
