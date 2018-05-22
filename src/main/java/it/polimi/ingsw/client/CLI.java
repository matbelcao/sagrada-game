package it.polimi.ingsw.client;

import it.polimi.ingsw.server.connection.Validator;

import java.io.*;

public class CLI implements ClientUI{
    private BufferedReader inKeyboard;
    private PrintWriter outCli;
    private Client client;

    public CLI(Client client) {
        this.client = client;
        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }


    private void clearCLI() {
        for (int i = 0; i < 50; i++) {
            outCli.printf("\n");
        }
    }

    public void loginProcedure() {
        String username;
        String password;
        //clearCLI();
        try {
            outCli.printf("\nUSERNAME: ");
            username = inKeyboard.readLine();
            outCli.printf("\nPASSWORD: ");
            password = inKeyboard.readLine();

            client.setPassword(password);
            client.setUsername(username);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getCommand(){
        try {
            return inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.println("\nSuccessfully logged in as " + client.getUsername());
        } else {
            outCli.println("\nCouldn't login correctly, please retry ...");
        }
    }

    public void updateConnection() {
        outCli.println(client.getClientConn().getGreetings());
    }

    public void updateLobby(int num_users){
        outCli.println("Lobby : " + num_users);
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
}


