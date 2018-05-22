package it.polimi.ingsw.client.connection;

public interface ClientConn {
    public boolean login(String username,String password);
    public void getPrivateObj();
    public void getPublicObj();
    public void getTools();
    public void getDraftPool();
    public void getRoundtrack();
    public void getPlayers();
    public void getFavorTokens(int playerId);
    public void getSchema(int playerId);
    public void draftSchema();
    public boolean ping();
    String getGreetings();
    //...
}
