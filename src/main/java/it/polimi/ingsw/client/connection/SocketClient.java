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

    public SocketClient(String address, int port){
        try {
            socket = new Socket(address, port);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        //String command = "";
        boolean playing = true;
        while(playing){
            try {
                try {
                    String command = inSocket.readLine();
                    System.out.println(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*command = inSocket.readLine();
                playing = execute(command);*/
            } catch ( IllegalArgumentException ignored) {
            }finally {
                playing=false;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



   /* private boolean execute(String command) {

        String[] parsed=command.split(" ");
        switch (parsed[0]) {
            case "Connection established!":
                cli.updateConnection();

                return true;
            case "LOGIN":
                if("ok".equals(parsed[1])){
                        cli.updateLogin(true);
                }else if("ko".equals(parsedResult.get(1))){
                        cli.updateLogin(false);
                }
                return true;
                break;
            default:
                    return true;
        }
        return false;
    }

*/

    private String readBuffer(){
        try {
            return inSocket.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean login(String username, String password) {
        String command="";
        outSocket.println("LOGIN " + username + " " + password);
        outSocket.flush();
        command = readBuffer();

        return ClientParser.isLoginOk(command);

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
