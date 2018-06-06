package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.immutables.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient implements ClientConn {
    private static final int NUM_DRAFTED_SCHEMAS=4;
    private static final int COMMA_PARAMS_START=2;
    private static final int LIST_START=1;
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
                        if (ClientParser.isStatus(inSocket.readln())) {
                            inSocket.pop();
                            switch (result.get(1)) {
                                case "check":
                                    pong();
                                    break;
                                case "reconnect":
                                    break;
                                case "disconnect":
                                    break;
                                case "quit":
                                    break;
                            }
                        } else if (ClientParser.isLobby(inSocket.readln())) {
                            inSocket.pop();
                            updateLobby(result.get(1));
                        } else if (ClientParser.isGame(inSocket.readln())) {
                            inSocket.pop();
                            updateMessages(result);
                        } else if (ClientParser.isPing(inSocket.readln())) {
                            pong();
                        } else if (ClientParser.isInvalid(inSocket.readln())) {
                            inSocket.pop();
                            System.out.println("INVALID message");
                        } else if (ClientParser.isIllegalAction(inSocket.readln())) {
                            inSocket.pop();
                            System.out.println("ILLEGAL ACTION!");
                        } else {
                            //debug
                            System.out.println("ERR: control error caused by:  " + inSocket.readln());
                        }
                    }
                } catch (Exception e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    client.setUserStatus(UserStatus.DISCONNECTED);
                }
            }
            System.out.println("QUITTED");
        }).start();
    }

    /**
     * The client invokes this method and then waits for a response from the server. This is typically the first communication
     * exchanged between client and server. The server will reply accordingly the authentication procedure
     * @param username the username of the user trying to login
     * @param password the password of the user
     * @return true iff the user has been logged into the server
     */
    @Override
    public boolean login(String username, char [] password) {
        ArrayList<String> parsedResult = new ArrayList<>();

        outSocket.println("LOGIN " + username + " " + Credentials.toString(password));
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
                for(i=COMMA_PARAMS_START; i<outcomes.size();i++){
                    param=outcomes.get(i).split(",");
                    LightPlayer player=client.getBoard().getPlayerByIndex(Integer.parseInt(param[0]));
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
                client.printDebug("Turn start: "+Integer.parseInt(outcomes.get(2))+" "+Integer.parseInt(outcomes.get(3)));
                client.updateGameTurnStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3))==0);
                break;
            case "turn_end":
                client.updateGameTurnEnd(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
        }
    }

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round).The draft option makes the server send the four schema cards the user
     * has to choose from.
     * @return the list of four schema cards immutable objects
     */
    @Override
    public List<LightSchemaCard> getSchemaDraft() {
        ArrayList<String> result= new ArrayList<>();
        List<LightSchemaCard> lightSchemaCards=new ArrayList<>();
        LightSchemaCard lightSchema;

        outSocket.println("GET schema draft");
        outSocket.flush();

        int i=0;
        while(i<NUM_DRAFTED_SCHEMAS){
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema")){
                lightSchema=LightSchemaCard.toLightSchema(inSocket.readln());
                lightSchemaCards.add(lightSchema);
                inSocket.pop();
                i++;
            }
        }
        return lightSchemaCards;
    }

    /**
     * This function can be invoked to request the updated schema card or the complete schema card (in case of reconnection
     * or if it’s the beginning of the first round) of a scecific user.
     * @param playerId the id of the player's desired schema card
     * @return one schema card immutable object
     */
    @Override
    public LightSchemaCard getSchema(int playerId) {
        ArrayList<String> result= new ArrayList<>();
        LightSchemaCard lightSchema=null;

        outSocket.println("GET schema "+playerId);
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema")));

        lightSchema=LightSchemaCard.toLightSchema(inSocket.readln());
        inSocket.pop();
        return lightSchema;
    }

    /**
     * This function can be invoked to request the private objective card parameters
     * @return one private objective card immutable object
     */
    @Override
    public LightPrivObj getPrivateObject() {
        ArrayList<String> result= new ArrayList<>();
        LightPrivObj lightObjCard=null;

        outSocket.println("GET priv");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("priv")));

        lightObjCard = LightPrivObj.toLightPrivObj(inSocket.readln());
        inSocket.pop();
        System.out.println("qui");
        return lightObjCard;
    }

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    @Override
    public List<LightCard> getPublicObjects() {
            ArrayList<String> result= new ArrayList<>();
            List<LightCard> pubObjCards=new ArrayList<>();
            LightCard lightObjCard;

            outSocket.println("GET pub");
            outSocket.flush();

            int i=0;
            while(i<NUM_CARDS){
                try {
                    inSocket.waitForLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("pub")){
                    lightObjCard=LightCard.toLightCard(inSocket.readln());
                    pubObjCards.add(lightObjCard);
                    inSocket.pop();
                    i++;
                }
            }
            return pubObjCards;
    }

    /**
     * This function can be invoked to request the three toolcards parameters
     * @return a list of three toolcards immutable objects
     */
    @Override
    public List<LightTool> getTools() {
        List<String> result= new ArrayList<>();
        List<LightTool> toolCards=new ArrayList<>();
        LightTool lightTool;

        outSocket.println("GET tool");
        outSocket.flush();

        int i=0;
        while(i<NUM_CARDS){
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("tool")){
                lightTool=LightTool.toLightTool(inSocket.readln());
                toolCards.add(lightTool);
                inSocket.pop();
                i++;
            }
        }
        return toolCards;
    }

    /**
     * This function can be invoked to request the dice in the draftpool
     * @return a list of immutable dice contained in the draftpool
     */
    @Override
    public List<LightDie> getDraftPool() {
        ArrayList<String> result= new ArrayList<>();
        List<LightDie> draftPool=new ArrayList<>();
        LightDie die;
        String args[];

        outSocket.println("GET draftpool");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("draftpool")));

        inSocket.pop();
        for(int i=COMMA_PARAMS_START;i<result.size();i++) {
            args = result.get(i).split(",");
            die = new LightDie(args[2], args[1]);
            draftPool.add(die);
        }
        return draftPool;
    }

    /**
     * This function can be invoked to request the dice in the roundtrack
     * @return a list of immutable dice contained in the roundtrack
     */
    @Override
    public List<List<LightDie>> getRoundtrack() {
        ArrayList<String> result= new ArrayList<>();
        List<List<LightDie>> roundTrack=new ArrayList<>();
        List<LightDie> container;
        LightDie die;
        int index=-1;
        String args[];

        outSocket.println("GET roundtrack");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("roundtrack")));

        inSocket.pop();
        roundTrack = new ArrayList<>();
        container = new ArrayList<>();
        for (int i = COMMA_PARAMS_START; i < result.size(); i++) {
            args = result.get(i).split(",");
            die = new LightDie(args[2], args[1]);
            if (index != Integer.parseInt(args[0])) {
                container = new ArrayList<>();
                index = Integer.parseInt(args[0]);
                roundTrack.add(index, container);
            }
            (roundTrack.get(index)).add(die);
        }
        return roundTrack;
    }

    /**
     * The client invokes this function to request the list of players of the match
     * @return a list of immutable players that are playing the match
     */
    @Override
    public List<LightPlayer> getPlayers() {
        ArrayList<String> result= new ArrayList<>();
        List<LightPlayer> playerList=new ArrayList<>();
        LightPlayer player;
        String args[];

        outSocket.println("GET players");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("players")));

        inSocket.pop();
        for(int i=COMMA_PARAMS_START;i<result.size();i++) {
            args = result.get(i).split(",");
            player = new LightPlayer(args[1], Integer.parseInt(args[0]));
            playerList.add(player);
        }
        return playerList;
    }

    /**
     * This function can be invoked to get the number of tokens remaining to the specified player.
     * @param playerId the id of the player (0 to 3)
     * @return the number of favor tokens of the specific player
     */
    @Override
    public int getFavorTokens(int playerId) {
        ArrayList<String> result= new ArrayList<>();
        int favor_tokens=0;

        outSocket.println("GET favor_tokens "+playerId);
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("favor_tokens")));

        inSocket.pop();
        favor_tokens=Integer.parseInt(result.get(1));
        return favor_tokens;
    }

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the schema card
     * @return and immutable and indexed list containing the dice
     */
    @Override
    public List<IndexedCellContent> getDiceList(){
        ArrayList<String> result= new ArrayList<>();
        List<IndexedCellContent> diceList=new ArrayList<>();
        IndexedCellContent indexedDie;
        String [] args;

        outSocket.println("GET_DICE_LIST");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isDiceList(inSocket.readln())));

        inSocket.pop();
        for(int i=LIST_START;i<result.size();i++) {
            args= result.get(i).split(",");
            indexedDie=new IndexedCellContent(Integer.parseInt(args[0]),args[1],args[2],args[3]);
            diceList.add(indexedDie);
        }

        return diceList;
    }

    /**
     * This function can be invoked to select one die of a previolsly GET_DICE_LIST command and obtain
     * a list of to options to manipulate it
     * @return and immutable and indexed list containing the dice
     */
    public List<Commands> select(int die_index){
        ArrayList<String> result= new ArrayList<>();
        List<Commands> options=new ArrayList<>();

        outSocket.println("SELECT "+die_index);
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isOptionList(inSocket.readln())));

        inSocket.pop();
        for(int i=LIST_START;i<result.size();i++) {
            options.add(Commands.valueOf(result.get(i)));
        }

        return options;
    }

    /**
     * This function can be invoked by the client to request the list of possible placements of a die (that is
     * temporarily selected by the user) in his schema card
     * @return an immutable and indexed list of possible placements
     */
    @Override
    public List<Integer> getPlacementsList(){
        ArrayList<String> result= new ArrayList<>();
        ArrayList<Integer> positions=new ArrayList<>();

        outSocket.println("GET_PLACEMENTS_LIST");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isPlacementList(inSocket.readln()) && result.get(1).equals("placements")));

        inSocket.pop();
        for(int i=LIST_START;i<result.size();i++) {
            positions.add(Integer.parseInt(result.get(i)));
        }

        return positions;
    }

    /**
     *  This function can be invoked to notify the server in order to make a possibly definitive choice. The server is
     *  still going to do his checks and will reply.
     * @param option_index the index of the object in the list previously sent by the server
     * @return true if the procedure is successful
     */
    @Override
    public boolean choose(int option_index){
        ArrayList<String> result=new ArrayList<>();

        outSocket.println("CHOOSE "+option_index);
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isChoice(inSocket.readln())));
        inSocket.pop();
        return result.get(1).equals("ok");
    }

    /**
     *  This function can be invoked to notify the server the intenction to select a tool car. The server is
     *  still going to do his checks and will reply.
     * @param tool_index the index of the toolcard the user wants to use
     * @return true iff the toolcard has been activated
     */
    @Override
    public boolean enableTool(int tool_index){
        ArrayList<String> result=new ArrayList<>();

        outSocket.println("TOOL enable "+tool_index);
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!(ClientParser.parse(inSocket.readln(),result) && ClientParser.isTool(inSocket.readln())));
        inSocket.pop();
        return result.get(1).equals("ok");
    }

    /**
     * This function is invoked by the client to know if the toolcard's execution flow is still active
     * @return true iff the toolcard is active
     */
    @Override
    public boolean toolCanContinue() {
        ArrayList<String> result = new ArrayList<>();

        outSocket.println("TOOL can_continue");
        outSocket.flush();

        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isTool(inSocket.readln()))) ;
        inSocket.pop();
        return result.get(1).equals("ok");
    }

    /**
     * This function can be invoked to notify the server in case the client wants to end his turn before the timer goes off.
     */
    @Override
    public void endTurn(){
        outSocket.println("GAME end_turn");
        outSocket.flush();
    }

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    @Override
    public void discard(){
        outSocket.println("DISCARD");
        outSocket.flush();
    }


    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    @Override
    public void exit(){
        outSocket.println("EXIT");
        outSocket.flush();
    }

    /**
     * This function can be invoked to notify the server of the closure of the communication and closes the socket.
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

    /**
     * This method provides the ping functionality for the client-side hearthBreath thread
     * @return false iff the connection has broken
     */
    @Override
    public boolean pong() {
        try{
            outSocket.println("PONG");
            outSocket.flush();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //ONLY FOR DEBUG PURPOSES
    public void sendDebugMessage(String message){
        outSocket.println(message);
        outSocket.flush();
    }
}
