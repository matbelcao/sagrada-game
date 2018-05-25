package it.polimi.ingsw.client;

import it.polimi.ingsw.common.connection.QueuedInReader;

import java.io.*;

public class CLI implements ClientUI{
    private QueuedInReader inKeyboard;
    private PrintWriter outCli;
    private Client client;

    public CLI(Client client) {
        this.client = client;
        inKeyboard = new QueuedInReader(new BufferedReader(new InputStreamReader(System.in)));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    public void loginProcedure() {
        String username;
        String password;


            outCli.printf("%nUSERNAME: ");
            inKeyboard.add();
            username = inKeyboard.getln();

            outCli.printf("%nPASSWORD: ");
            inKeyboard.add();
            password = inKeyboard.getln();

            client.setPassword(password);
            client.setUsername(username);

    }


    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.println("%nSuccessfully logged in as " + client.getUsername());
        } else {
            outCli.println("%nCouldn't login correctly, please retry ...");
        }
    }

    public void updateConnectionOk() { outCli.println("\nConnection established correctly!"); }

    public void updateLobby(int numUsers){
        outCli.println("Lobby : " + numUsers);
    }

    public void updateGameStart(int numUsers, int playerId){
        outCli.println("Starting Match : " + numUsers + " " + playerId);
    }

    public void updateConnectionClosed()
    {
        outCli.println("Connection closed!");
    }

    public void updateConnectionBroken() {outCli.println("Connection broken!");
    }

    public void printmsg(String msg){
        outCli.println(msg);
    }

    @Override
    public String getCommand() {

            if(inKeyboard.isEmpty()){inKeyboard.add();}

        return inKeyboard.getln();
    }
}


