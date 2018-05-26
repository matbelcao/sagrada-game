package it.polimi.ingsw.client;

public class GUI implements ClientUI {
    private Client client;
    private UIMessages uimsg;

    public GUI(Client client, UILanguage lang){
        this.uimsg=new UIMessages(lang);
        this.client = client;
        System.out.println("Created gui!!");
    }

    @Override
    public void loginProcedure() {

    }


    @Override
    public void updateLogin(boolean logged) {

    }

    @Override
    public void updateConnectionOk() {

    }

    @Override
    public void updateLobby(int numUsers) {

    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {

    }

    @Override
    public void updateStatusMessage(String statusChange, int playerid) {

    }


    @Override
    public void updateConnectionClosed() {

    }

    @Override
    public void updateConnectionBroken() {

    }

    @Override
    public void printmsg(String msg) {

    }

    @Override
    public String getCommand() {
        return null;
    }
}
