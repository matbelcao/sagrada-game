package it.polimi.ingsw.client;

public interface ClientConn {
    void printToServer(String message);
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
