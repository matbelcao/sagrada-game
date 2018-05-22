package it.polimi.ingsw.client;

public interface ClientUI {

    public void loginProcedure();

    public String getCommand();

    public void updateLogin(boolean logged);

    public void updateConnection();

    public void updateLobby(int num_users);

    public void updateGameStart(int numUsers, int playerId);

    public void updateConnectionClosed();
}
