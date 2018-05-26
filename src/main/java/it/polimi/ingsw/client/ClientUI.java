package it.polimi.ingsw.client;

import java.io.File;

public interface ClientUI {
    static final String MESSAGES_FILE="src"+ File.separator+"xml"+File.separator+"client"+File.separator+"UIMessages.xml";

    public void loginProcedure();

    public void updateLogin(boolean logged);

    public void updateConnectionOk();

    public void updateLobby(int numUsers);

    public void updateGameStart(int numUsers, int playerId);

    public void updateConnectionClosed();

    public void updateConnectionBroken();

    public void printmsg(String msg);

    String getCommand();
}
