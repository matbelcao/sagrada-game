package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient extends Thread implements ClientConn {
    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    Client client;

    /**
     * Thi is the class constructor, it instantiates the new socket and the input/output buffers for the communications
     * @param client the Client class reference
     * @param address the server's IP address
     * @param port the server's network port
     * @throws IOException iff there are problems on contacting the server
     */
    public SocketClient(Client client,String address, int port) throws IOException {
        this.client=client;
        socket = new Socket(address, port);
        inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        inSocket.readLine();
        client.getClientUI().updateConnectionOk();
    }

    /**
     * This method generates a new thread that listens to the incoming messages of the socket and notifies their
     * reception to the update method
     */
    public void startListening(){
        new Thread(() -> {
            while(socket!=null) {
                synchronized (inSocket) {
                    try {
                        if (!socket.isClosed() && inSocket.ready()) {
                            update(inSocket.readLine());
                        }
                    } catch (IOException | NullPointerException e) {
                        socket = null;
                    }
                }
            }
        }).start();
    }

    /**
     * This method receives the server's message and calls the proper updateXxxx() method (providing the parsed command)
     * @param rawCommand the server's message
     */
    private void update(String rawCommand){
        ArrayList<String> parsedResult = new ArrayList<>();

        if (ClientParser.parse(rawCommand,parsedResult)) {
            switch (parsedResult.get(0)) {

                case "LOBBY":
                    updateLobby(parsedResult.get(1));
                    break;
                case "GAME":
                    updateGame(parsedResult);
                    break;
                /*case "SEND":
                    getSend(parsedResult);
                    break;
                case "LIST":
                    getList(parsedResult);
                    break;
                case "DISCARD":
                    getDiscard(parsedResult);
                    break;
                case "CHOICE":
                    getChoice(parsedResult);
                    break;*/
            }
        }
    }

    /**
     * This methods provides the client-side login functionality to a socket connection
     * @param username the username of the user trying to login
     * @param password the password of the user
     * @return true iff the user has been logged into the server
     */
    @Override
    public boolean login(String username, String password) {
        ArrayList<String> parsedResult = new ArrayList<>();

        outSocket.println("LOGIN " + username + " " + password);
        outSocket.flush();
        try {
            if (ClientParser.parse(inSocket.readLine(),parsedResult) && parsedResult.get(0).equals("LOGIN")) {
                if(parsedResult.get(1).equals("ok")){
                    startListening();
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method notifies the server of the closure of the communication and closes the socket.
     */
    @Override
    public void quit(){
        outSocket.println("QUIT");
        outSocket.flush();
        try {
            socket.close();
            socket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer getPrivateObj() {
        Integer i= 3;
        synchronized (inSocket) {
            outSocket.println("ciao");
            outSocket.flush();

            try {
                i = Integer.parseInt(inSocket.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return i;
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

    /**
     * This method notifies to the view that the number of player in the lobby has changed
     * @param lobbySize the new number of players
     */
    private void updateLobby(String lobbySize){
        client.getClientUI().updateLobby(Integer.parseInt(lobbySize));
    }

    /**
     * This method notifies to the client that there has been a change in the status of the match
     * @param outcomes the server's parsed message
     */
    private void updateGame(List<String> outcomes){
        int i;
        switch(outcomes.get(1)){
            case "start":
                client.updateGameStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "end":
                ArrayList<String> playerData = new ArrayList<>();
                for(i=2;i<outcomes.size();i++){
                    playerData.add(outcomes.get(i));
                }
                //client.updateGameEnd(playerData);
                break;
            case "round_start":
                //client.updateGameRoundStart(Integer.parseInt(outcomes.get(2)));
                break;
            case "round_end":
                //client.updateGameRoundEnd(Integer.parseInt(outcomes.get(2)));
                break;
            case "turn_start":
                //client.updateGameTurnStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "turn_end":
                //client.updateGameTurnEnd(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
        }
    }

    /**
     * This method provides the ping functionality for the client-side hearthBreath thread
     * @return false iff the connection has broken
     */
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
}
