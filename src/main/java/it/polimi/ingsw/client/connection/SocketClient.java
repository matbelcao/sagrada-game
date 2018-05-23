package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
            inSocket.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening(){
        new Thread(() -> {
            //client.updateGreeting();
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

    private void updateLobby(String outcome){
        client.getClientUI().updateLobby(Integer.parseInt(outcome));
    }

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
