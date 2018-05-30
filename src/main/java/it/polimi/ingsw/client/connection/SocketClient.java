package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.immutables.*;
import it.polimi.ingsw.server.model.Board;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient implements ClientConn {
    private static final int NUM_DRAFTED_SCHEMAS=4;
    private static final int COMMA_PARAMS_START=2;
    private static final int NUM_CARDS=3;

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

                        //STATUS

                        if (ClientParser.isStatus(inSocket.readln())) {
                            inSocket.pop();
                            manageStatusMsg(result);

                            //LOBBY

                        } else if (ClientParser.isLobby(inSocket.readln())) {
                            updateLobby(result.get(1));
                            inSocket.pop();

                            //GAME

                        } else if (ClientParser.isGame(inSocket.readln())) {
                            updateMessages(result);
                            inSocket.pop();

                        } else if (ClientParser.isList(inSocket.readln())) {
                            switch (result.get(1)) {
                                case "schema":
                                    client.printDebug(inSocket.readln());
                                    break;
                                case "roundtrack":
                                    client.printDebug(inSocket.readln());
                                    break;
                                case "draftpool":
                                    client.printDebug(inSocket.readln());
                                    break;
                                case "placements":
                                    client.printDebug(inSocket.readln());
                                    break;
                                case "tool_details":
                                    break;
                            }
                            inSocket.pop();
                        } else if (ClientParser.isChoice(inSocket.readln())) {
                            client.printDebug(inSocket.readln());
                            inSocket.pop();
                        }
                        else if(ClientParser.isInvalid(inSocket.readln())){
                            inSocket.pop();
                            client.printDebug("INVALID message");
                        }else {
                            client.printDebug("ERR: control error caused by:  " + inSocket.readln());
                            inSocket.pop();
                        }
                    }
                } catch (Exception e) {
                    this.quit();
                }
            }
        }).start();
    }



    private void manageStatusMsg(List<String> parsed){

        //STATUS check
        if (parsed.get(1).equals("check")) {
            this.pong();
        }

        //STATUS reconnect
        if (parsed.get(1).equals("reconnect")) {

        }

        //STATUS disconnect
        if (parsed.get(1).equals("disconnect")) {
            this.pong();
        }

        //STATUS quit
        if (parsed.get(1).equals("quit")) {
            this.pong();
        }
    }


    //ONLY FOR DEBUG PURPOSES
    public void sendDebugMessage(String message){
        outSocket.println(message);
        outSocket.flush();
    }


    /**
     * This method receives the server's message and calls the proper updateXxxx() method (providing the parsed command)
     * @param rawCommand the server's message
     */

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
            inSocket.add();
        } catch (Exception e) {
            this.quit();
        }

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
            socket=null;
            inSocket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LightCard getPrivateObject() {
        ArrayList<String> result= new ArrayList<>();
        LightPrivObj lightObjCard=null;

        outSocket.println("GET priv");
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("priv")) {
            lightObjCard = LightPrivObj.toLightPrivObj(inSocket.readln());
            inSocket.pop();
        }
        return lightObjCard;
    }

    @Override
    public List<LightCard> getPublicObjects() {
            ArrayList<String> result= new ArrayList<>();
            List<LightCard> pubObjCards=new ArrayList<>();
            LightCard lightObjCard;

            outSocket.println("GET pub");
            outSocket.flush();

            int i=0;
            while(i<NUM_CARDS){
                if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("pub")){
                    lightObjCard=LightCard.toLightCard(inSocket.readln());
                    pubObjCards.add(lightObjCard);
                    inSocket.pop();
                    i++;
                }
            }
            return pubObjCards;
    }

    @Override
    public List<LightCard> getTools() {
        ArrayList<String> result= new ArrayList<>();
        List<LightCard> toolCards=new ArrayList<>();
        LightTool lightTool;

        outSocket.println("GET tool");
        outSocket.flush();

        int i=0;
        while(i<NUM_CARDS){
            if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("tool")){
                lightTool=LightTool.toLightTool(inSocket.readln());
                toolCards.add(lightTool);
                inSocket.pop();
                i++;
            }
        }
        return toolCards;
    }

    @Override
    public List<CellContent> getDraftPool() {
        ArrayList<String> result= new ArrayList<>();
        List<CellContent> draftPool=new ArrayList<>();
        CellContent lightCell;
        String args[];

        outSocket.println("GET draftpool");
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("draftpool")){
            inSocket.pop();
            for(int i=COMMA_PARAMS_START;i<result.size();i++) {
                args= result.get(i).split(",");
                lightCell=new LightDie(args[2],args[1]);
                draftPool.add(lightCell);
            }
        }
        return draftPool;
    }

    @Override
    public List<List<CellContent>> getRoundtrack() {
        ArrayList<String> result= new ArrayList<>();
        List<List<CellContent>> roundTrack=new ArrayList<>();
        List<CellContent> container=new ArrayList<>();
        CellContent lightCell;
        int index=-1;
        String args[];

        outSocket.println("GET roundtrack");
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("roundtrack")) {
            inSocket.pop();
            roundTrack = new ArrayList<>();
            container = new ArrayList<>();
            for (int i = COMMA_PARAMS_START; i < result.size(); i++) {
                args = result.get(i).split(",");
                lightCell = new LightDie(args[2], args[1]);
                if (index != Integer.parseInt(args[0])) {
                    container = new ArrayList<>();
                    index = Integer.parseInt(args[0]);
                    roundTrack.add(index,container);
                }
                (roundTrack.get(index)).add(lightCell);
            }
        }
        return roundTrack;
    }


    @Override
    public List<LightPlayer> getPlayers() {
        ArrayList<String> result= new ArrayList<>();
        List<LightPlayer> playerList=new ArrayList<>();
        LightPlayer player;
        String args[];

        outSocket.println("GET players");
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("players")){
            inSocket.pop();
            for(int i=COMMA_PARAMS_START;i<result.size();i++) {
                args= result.get(i).split(",");
                player=new LightPlayer(args[1],Integer.parseInt(args[0]));
                playerList.add(player);
            }
        }
        return playerList;
    }

    @Override
    public int getFavorTokens(int playerId) {
        ArrayList<String> result= new ArrayList<>();
        int favor_tokens=0;

        outSocket.println("GET favor_tokens "+playerId);
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("favor_tokens")){
            favor_tokens=Integer.parseInt(result.get(1));
            inSocket.pop();
        }
        return favor_tokens;
    }

    @Override
    public LightSchemaCard getSchema(int playerId) {
        ArrayList<String> result= new ArrayList<>();
        LightSchemaCard lightSchema=null;

        outSocket.println("GET schema "+playerId);
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema")){
            lightSchema=LightSchemaCard.toLightSchema(inSocket.readln());
            inSocket.pop();
        }
        return lightSchema;
    }

    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        ArrayList<String> result= new ArrayList<>();
        List<LightSchemaCard> lightSchemaCards=new ArrayList<>();
        LightSchemaCard lightSchema;

        outSocket.println("GET schema draft");
        outSocket.flush();

        int i=0;
        while(i<NUM_DRAFTED_SCHEMAS){
            if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema")){
                lightSchema=LightSchemaCard.toLightSchema(inSocket.readln());
                lightSchemaCards.add(lightSchema);
                inSocket.pop();
                i++;
            }
        }
        return lightSchemaCards;
    }

    public void getDiceList(){

    }

    @Override
    public List<Integer> selectDie(int index){
        ArrayList<String> result= new ArrayList<>();
        ArrayList<Integer> positions=new ArrayList<>();
        String [] args;

        outSocket.println("SELECT die "+index);
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isList(inSocket.readln()) && result.get(1).equals("placements")){
            inSocket.pop();
            for(int i=COMMA_PARAMS_START;i<result.size();i++) {
                args= result.get(i).split(",");
                positions.add(Integer.parseInt(args[1]));
            }
        }
        return positions;
    }

    @Override
    public boolean selectTool(LightTool lightTool, int index){
        ArrayList<String> result= new ArrayList<>();

        outSocket.println("SELECT tool "+index);
        outSocket.flush();

        while(ClientParser.parse(inSocket.readln(),result) && ClientParser.isList(inSocket.readln()) && result.get(1).equals("tool_details")){
            inSocket.pop();
            if(Integer.parseInt(result.get(2))==index){
                lightTool.setUsed(Boolean.parseBoolean(result.get(4)));
                return result.get(5).equals("ok");
            }
        }
        return false;
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
    private void updateMessages(List<String> outcomes){
        String param[];
        int i;
        switch(outcomes.get(1)){
            case "start":
                client.updateGameStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "end":
                List<LightPlayer> playerData = client.getPlayers();
                for(i=COMMA_PARAMS_START; i<outcomes.size();i++){
                    param=outcomes.get(i).split(",");
                    LightPlayer player=playerData.get(Integer.parseInt(param[0]));
                    if (player.getPlayerId()==Integer.parseInt(param[0])){
                        player.setPoints(Integer.parseInt(param[1]));
                        player.setFinalPosition(Integer.parseInt(param[2]));
                    }

                }
                client.updateGameEnd();
                break;
            case "round_start":
                client.updateGameRoundStart(Integer.parseInt(outcomes.get(2)));
                break;
            case "round_end":
                client.updateGameRoundEnd(Integer.parseInt(outcomes.get(2)));
                break;
            case "turn_start":
                client.updateGameTurnStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "turn_end":
                client.updateGameTurnEnd(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
        }
    }

    /**
     * This method provides the ping functionality for the client-side hearthBreath thread
     * @return false iff the connection has broken
     */
    @Override
    public boolean pong() {
        //Debug
        //System.out.println("ping_buono!!");
        try{
            outSocket.println("ACK status");
            outSocket.flush();

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
