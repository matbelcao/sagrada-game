package it.polimi.ingsw.server.connection;

public interface CientConn {

    boolean login();
    void getPrivateObj();
    void getPublicObj();
    void getTools();
    void getDraftPool();
    void getRoundtrack();
    void getPlayers();
    void getFavorTokens(int playerId);
    void getSchema(int playerId);
    void draftSchema();
    //...

}
