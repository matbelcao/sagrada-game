package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.exceptions.GameStartedException;
import it.polimi.ingsw.server.connection.Validator;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketClient extends Thread implements ClientConn {
    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    Client client;

    public SocketClient(Client client,String address, int port){
        this.client=client;
        try {
            socket = new Socket(address, port);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening(){
        new Thread(() -> {
            while(socket!=null) {
                try {
                    if(inSocket.ready()){
                        update(inSocket.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update(String rawCommand){
        ArrayList<String> parsedResult = new ArrayList<>();

        ClientParser.parse(rawCommand,parsedResult);
    }


    /*@Override
    public void notifyLogin(String username, String password) {
        outSocket.println("LOGIN " + username + " " + password);
        outSocket.flush();
    }


    public String getGreetings(){
        return readBuffer();
    }

    @Override
    public int getLobby() throws GameStartedException {
        ArrayList<String> parsedResult = new ArrayList<>();
        String command="";
        command = readBuffer();

        if(ClientParser.isLoobbyMessage(command,parsedResult)){
            return Integer.parseInt(parsedResult.get(1));
        }
        return 0;
    }*/

    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public int getLobby() throws GameStartedException {
        return 0;
    }

    public void quit(){
        outSocket.println("QUIT");
        outSocket.flush();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try{
            outSocket.print((char)0);
            outSocket.flush();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public String getGreetings() {
        return null;
    }
}
