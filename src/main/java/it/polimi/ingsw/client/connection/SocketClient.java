package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.immutables.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient implements ClientConn {
    private Socket socket;
    private QueuedInReader inSocket;
    private PrintWriter outSocket;
    private Client client;

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
        inSocket = new QueuedInReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            inSocket.add();

        inSocket.pop();
        client.getClientUI().updateConnectionOk();
    }

    /**
     * This method generates a new thread that listens to the incoming messages of the socket and notifies their
     * reception to the update method
     */
    public void startListening(){

        new Thread(() -> {
            ArrayList<String> result= new ArrayList<>();
            while(!socket.isClosed()) {

                try {

                    inSocket.add();

                    if(ClientParser.parse(inSocket.readln(),result)) {

                        if (ClientParser.isStatus(inSocket.readln())) {
                            inSocket.pop();
                            if (result.get(1).equals("check")) {
                                this.pong();
                            }

                        }else if (ClientParser.isLobby(inSocket.readln())) {
                            updateLobby(result.get(1));
                            inSocket.pop();

                        }else if(ClientParser.isGame(inSocket.readln())) {
                            updateGame(result);
                            inSocket.pop();

                        }else{
                            System.out.println("ERR: control error caused by:  "+inSocket.readln());
                            inSocket.pop();
                        }
                    }
                } catch ( NullPointerException e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();

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

        inSocket.add();


        if (ClientParser.isLogin(inSocket.readln())) {
            ClientParser.parse(inSocket.readln(),parsedResult);
            inSocket.pop();
            if (parsedResult.get(1).equals("ok")) {
                startListening();
                client.getClientUI().updateLogin(true);
                return true;
            }
        }

        client.getClientUI().updateLogin(false);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LightCard getPrivateObj() {


        return null;
    }

    @Override
    public LightCard getPublicObj() {

        return null;
    }

    @Override
    public LightTool getTools() {

        return null;
    }

    @Override
    public List<IndexedCellContent> getDraftPool() {

        return null;
    }

    @Override
    public List<IndexedCellContent> getRoundtrack() {

        return null;
    }

    @Override
    public List<LightPlayer> getPlayers() {

        return null;
    }

    @Override
    public int getFavorTokens(int playerId) {

        return 0;
    }

    @Override
    public LightSchemaCard getSchema(int playerId) {

        return null;
    }

    @Override
    public ArrayList<LightSchemaCard> draftSchema() {

        return null;
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
    public boolean pong() {
        System.out.println("ping_buono!!  "+inSocket.readln());
        try{
            outSocket.println("ACK status");
            outSocket.flush();

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
