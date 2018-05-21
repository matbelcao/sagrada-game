package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.CLI;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SocketClient extends Thread implements ClientConn {
    CLI cli;
    Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    public SocketClient(CLI cli, String address, int port){
        this.cli=cli;
        try {
            socket = new Socket(address, port);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("--> SOCKET STARTED");
    }

    private String readSocket(){
        String command = null;
        try {
            command = inSocket.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return command;
    }

   @Override
    public boolean waitGreeting() {
        try {
            while(!inSocket.readLine().equals("Connection established!")){
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean login(String username, String password) {
        outSocket.println("LOGIN "+username+" "+password);
        outSocket.flush();

        ArrayList<String> parsedResult = new ArrayList<>();
        if(Parser.checkLoginParams(readSocket(),parsedResult)){
            if(parsedResult.get(1).equals("ok")){
                cli.updateLogin(true);
                return true;
            }
        }
        cli.updateLogin(false);
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
        try{
            outSocket.print((char)0);
            outSocket.flush();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException x) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }
}
