package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;

import java.io.*;
import java.net.Socket;

public class SocketClient extends Thread implements ClientConn {
    Client client;
    Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    SocketClient(Client client,String address,int port){
        this.client=client;
        try {
            socket = new Socket(address, port);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("--> SOCKET STARTED");
    }

    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public void getPrivateObj() {

    }

    @Override
    public void getPublicObj() {

    }

    @Override
    public void getTools() {

    }

    @Override
    public void getDraftPool() {

    }

    @Override
    public void getRoundtrack() {

    }

    @Override
    public void getPlayers() {

    }

    @Override
    public void getFavorTokens(int playerId) {

    }

    @Override
    public void getSchema(int playerId) {

    }

    @Override
    public void draftSchema() {

    }

    @Override
    public boolean ping() {
        return false;
    }
}
