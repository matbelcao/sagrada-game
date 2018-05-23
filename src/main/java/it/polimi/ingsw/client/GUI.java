package it.polimi.ingsw.client;

public class GUI implements ClientUI {
    private Client client;

    public GUI(Client client){
        this.client = client;
        System.out.println("Created gui!!");
    }

    @Override
    public void loginProcedure() {

    }

    @Override
    public String getCommand() {
        return null;
    }

    @Override
    public void updateLogin(boolean logged) {

    }

    @Override
    public void updateConnection() {

    }

    @Override
    public void updateLobby(int num_users) {

    }

    @Override
    public void updateGameStart(int numUsers, int playerId) {

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
}
