package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.controller.ClientFSMState;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.connection.SocketString;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.serializables.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient implements ClientConn {
    private static final int NUM_DRAFTED_SCHEMAS=4;
    private static final int COMMA_PARAMS_START=2;
    private static final int LIST_START=1;
    private static final int NUM_CARDS=3;
    private static final int PONG_TIME=6000;
    private static final String INVALID_MESSAGE = "INVALID message";
    private static final String ERR_ERROR_WHILE_CLOSING_THE_SOCKET = "ERR: error while closing the socket";

    private Socket socket;
    private QueuedBufferedReader inSocket;
    private PrintWriter outSocket;
    private Client client;
    private final Object lockin=new Object();
    private Timer pingTimer;
    private final Object lockPing =new Object();
    private boolean connectionOk;
    private boolean timerActive;
    private final Object lockOutSocket;

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
        inSocket = new QueuedBufferedReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
        outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
        inSocket.add();
        lockOutSocket= new Object();
        inSocket.pop();
        connectionOk=true;
        this.timerActive=false;
        client.getClientUI().updateConnectionOk();
    }

    /**
     * Adds a string into the Socket buffer, the flushes it
     * @param message the message to add in the buffer
     */
    private void syncedSocketWrite(String message){
        synchronized (lockOutSocket){

            try {
                outSocket.println(message);
                outSocket.flush();
            }catch (Exception e){
                client.disconnect();
            }
            lockOutSocket.notifyAll();
        }
    }

    /**
     * This method generates a new thread that listens to the incoming messages of the socket and notifies their
     * reception to the update method
     */
    public void startListening(){

        new Thread(() -> {

            while(!socket.isClosed() && connectionOk) {
                ArrayList<String> result= new ArrayList<>();
                try {

                    synchronized (lockin) {
                        inSocket.waitForLine();

                        if (ClientParser.parse(inSocket.readln(), result) && connectionOk) {
                            if (ClientParser.isStatus(inSocket.readln())) {
                                inSocket.pop();
                                updatePlayerStatus(result);

                            } else if (ClientParser.isLobby(inSocket.readln())) {
                                inSocket.pop();
                                updateLobby(result.get(1));

                            } else if (ClientParser.isGame(inSocket.readln())) {
                                inSocket.pop();
                                updateMessages( result);

                            } else if (ClientParser.isPing(inSocket.readln())) {
                                inSocket.pop();
                                pong();

                            } else if (ClientParser.isInvalid(inSocket.readln())) {
                                inSocket.pop();
                                Logger.getGlobal().log(Level.INFO,INVALID_MESSAGE);

                            } else if (ClientParser.isIllegalAction(inSocket.readln())) {
                                inSocket.pop();
                                manageIllegalAction();
                            }
                        }
                        lockin.notifyAll();
                    }
                } catch (IOException e) {
                    manageSocketError();
                }
            }
        }).start();
    }

    /**
     * manages the illegal action exception
     */
    private void manageIllegalAction() {
        if(client.isPlayingTurns()&&!client.getFsmState().equals(ClientFSMState.NOT_MY_TURN)){
            endTurn();
        }
    }

    /**
     * manages an error in the connection via socket
     */
    private void manageSocketError() {
        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e1) {
                Logger.getGlobal().log(Level.INFO, ERR_ERROR_WHILE_CLOSING_THE_SOCKET);
            }
        }
        synchronized (lockPing){
            if(timerActive && pingTimer!=null){
                pingTimer.cancel();
                timerActive=false;
                lockPing.notifyAll();
            }
        }

        client.disconnect();
    }

    /**
     * updates the client with the changes in status of a player
     * @param result the message
     */
    private void updatePlayerStatus(ArrayList<String> result) {
        client.addUpdateTask(new Thread(()->
                client.updatePlayerStatus(
                        Integer.parseInt(result.get(2)),
                        GameEvent.valueOf(result.get(1).toUpperCase()),
                        result.get(3))
        ));
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

        syncedSocketWrite(SocketString.LOGIN+" "+ username + " " + Credentials.toString(password));


        try {
            inSocket.add();
        } catch (Exception e) {
            this.quit();
        }

        if (ClientParser.isLogin(inSocket.readln())) {
            ClientParser.parse(inSocket.readln(),parsedResult);
            inSocket.pop();
            if (parsedResult.get(1).equals(SocketString.OK)) {
                startListening();
                return true;
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
        String [] param;
        int i;
        switch(outcomes.get(1)){
            case SocketString.START:
                client.addUpdateTask(new Thread(()->
                    client.updateGameStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)))
                ));
                break;
            case SocketString.END:
                List<RankingEntry> ranking= new ArrayList<>();
                for(i=COMMA_PARAMS_START; i<outcomes.size();i++){
                    param=outcomes.get(i).split(",");
                    ranking.add(new RankingEntry(Integer.parseInt(param[0]),Integer.parseInt(param[1]),Integer.parseInt(param[2])));
                }
                client.addUpdateTask(new Thread(()->
                    client.updateGameEnd(ranking)
                ));

                break;
            case SocketString.ROUND_START:
                client.addUpdateTask(new Thread(()->
                    client.updateGameRoundStart(Integer.parseInt(outcomes.get(2)))
                ));
                break;
            case SocketString.ROUND_END:
                client.addUpdateTask(new Thread(()->
                client.updateGameRoundEnd(Integer.parseInt(outcomes.get(2)))
                ));
                break;
            case SocketString.TURN_START:
                client.addUpdateTask(new Thread(()->
                    client.updateGameTurnStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3))==0)
                ));
                break;
            case SocketString.TURN_END:
                client.addUpdateTask(new Thread(()->
                    client.updateGameTurnEnd(Integer.parseInt(outcomes.get(2)))
                ));
                break;
            case SocketString.BOARD_CHANGED:
                client.addUpdateTask(new Thread(()->
                    client.getBoardUpdates()
                ));
                break;
            default:
        }
    }

    /**
     * this waits for the next message coming in via socket
     */
    private void waitForLine() {
        try {
            inSocket.waitForLine();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
            System.exit(1);
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

        syncedSocketWrite(SocketString.GET_SCHEMA+SocketString.DRAFTED);


        int i=0;
        while(i<NUM_DRAFTED_SCHEMAS){
            synchronized (lockin) {
                waitForLine();
                while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.SCHEMA))) {
                    waitForTheRightOne();
                }

                lightSchema = LightSchemaCard.toLightSchema(inSocket.readln());
                lightSchemaCards.add(lightSchema);
                inSocket.pop();
                lockin.notifyAll();
                i++;
            }

        }
        return lightSchemaCards;
    }

    /**
     * this waits for a new message that could possibly be the expected one
     */
    private void waitForTheRightOne() {
        synchronized (lockin) {
            lockin.notifyAll();
            try {
                lockin.wait();

                inSocket.waitForLine();
            } catch (IOException |InterruptedException e) {
                Logger.getGlobal().log(Level.INFO,e.getMessage());
                System.exit(1);
            }
        }

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

        syncedSocketWrite(SocketString.GET_SCHEMA+playerId);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.SCHEMA))) {
                waitForTheRightOne();
            }

            lightSchema = LightSchemaCard.toLightSchema(inSocket.readln());
            inSocket.pop();
            lockin.notifyAll();
        }
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

        syncedSocketWrite(SocketString.GET_PRIVATE);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.PRIVATE))) {
                waitForTheRightOne();
            }

            lightObjCard = LightPrivObj.toLightPrivObj(inSocket.readln());
            inSocket.pop();
            lockin.notifyAll();
        }
        return lightObjCard;
    }

    /**
     * This function can be invoked to request the three public objective cards parameters
     * @return a list of three public objective cards immutable objects
     */
    @Override
    public List<LightCard> getPublicObjectives() {
            ArrayList<String> result= new ArrayList<>();
            List<LightCard> pubObjCards=new ArrayList<>();
            LightCard lightObjCard;

            syncedSocketWrite(SocketString.GET_PUBLIC);


            int i=0;
            while(i<NUM_CARDS){
                synchronized (lockin) {
                    waitForLine();
                    while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.PUBLIC))) {
                        waitForTheRightOne();
                    }

                    lightObjCard = LightCard.toLightCard(inSocket.readln());
                    pubObjCards.add(lightObjCard);
                    inSocket.pop();
                    lockin.notifyAll();
                }
                i++;

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

        syncedSocketWrite(SocketString.GET_TOOLCARD);

        int i=0;
        while(i<NUM_CARDS){

            synchronized (lockin) {
                waitForLine();

                while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.TOOLCARD))) {
                    waitForTheRightOne();
                }
                lightTool = LightTool.toLightTool(inSocket.readln());
                toolCards.add(lightTool);
                inSocket.pop();
                lockin.notifyAll();
            }
            i++;
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
        String [] args;

        syncedSocketWrite(SocketString.GET_DRAFTPOOL);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.DRAFTPOOL))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
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
        List<List<LightDie>> roundTrack;
        List<LightDie> container;
        LightDie die;
        int index=-1;
        String [] args;

        syncedSocketWrite(SocketString.GET_ROUNDTRACK);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.ROUNDTRACK))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
        roundTrack = new ArrayList<>();
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
        String [] args;

        syncedSocketWrite(SocketString.GET_PLAYERS);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.PLAYERS))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
        for(int i=COMMA_PARAMS_START;i<result.size();i++) {
            args = result.get(i).split(",");
            LightPlayer lightPlayer=new LightPlayer(args[1], Integer.parseInt(args[0]));
            lightPlayer.setStatus(LightPlayerStatus.valueOf(args[2]));
            playerList.add( lightPlayer);
        }
        return playerList;
    }

    /**
     * The client invokest this function to retireve the necessary information to
     * guarantee the correct reconnection during the game.
     * @return the match status
     */
    @Override
    public LightGameStatus getGameStatus(){
        LightGameStatus gameStatus=null;
        ArrayList<String> result= new ArrayList<>();

        syncedSocketWrite(SocketString.GET_GAME_STATUS);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.GAME_STATUS))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
        for(int i=COMMA_PARAMS_START;i<result.size();i++) {
            gameStatus=new LightGameStatus(Boolean.parseBoolean(result.get(2)),Integer.parseInt(result.get(3)),Integer.parseInt(result.get(4)),
                    Boolean.parseBoolean(result.get(5)),Integer.parseInt(result.get(6)));
        }
        return gameStatus;
    }

    /**
     * This function can be invoked to get the number of tokens remaining to the specified player.
     * @param playerId the id of the player (0 to 3)
     * @return the number of favor tokens of the specific player
     */
    @Override
    public int getFavorTokens(int playerId) {
        ArrayList<String> result= new ArrayList<>();
        int favorTokens=0;

        syncedSocketWrite(SocketString.GET_TOKENS+playerId);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals(SocketString.TOKENS))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        favorTokens=Integer.parseInt(result.get(2));
        return favorTokens;
    }

    /**
     * This function can be invoked to obtain an immutable and indexed list containing the information about the dice placed
     * in the schema card
     * @return and immutable and indexed list containing the dice
     */
    @Override
    public List<IndexedCellContent> getDiceList() {
        ArrayList<String> result = new ArrayList<>();
        List<IndexedCellContent> diceList = new ArrayList<>();
        IndexedCellContent indexedDie;
        String[] args;

        syncedSocketWrite(SocketString.GET_DICE_LIST);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isDiceList(inSocket.readln()))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }

        for(int i=LIST_START+1;i<result.size();i++) {
            args= result.get(i).split(",");
            indexedDie=new IndexedCellContent(Integer.parseInt(args[0]),result.get(1),args[1],args[2]);
            diceList.add(indexedDie);
        }

        return diceList;
    }



    /**
     * This function can be invoked to select one die of a previously GET_DICE_LIST command and obtain
     * a list of to options to manipulate it
     * @param dieIndex the index of the die to select
     * @return and immutable and indexed list containing the dice
     */
    @Override
    public List<Actions> select(int dieIndex){
        ArrayList<String> result= new ArrayList<>();
        List<Actions> options=new ArrayList<>();

        syncedSocketWrite(SocketString.SELECT+" "+dieIndex);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isOptionList(inSocket.readln()))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
        for(int i=LIST_START;i<result.size();i++) {
            String [] opt=result.get(i).split(",");
            options.add(Actions.valueOf(opt[1]));
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

        syncedSocketWrite(SocketString.GET_PLACEMENTS_LIST);

        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isPlacementList(inSocket.readln()) && result.get(0).equals(SocketString.LIST_PLACEMENTS))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        for(int i=1;i<result.size();i++) {
            positions.add(Integer.parseInt(result.get(i)));
        }

        return positions;
    }

    /**
     *  This function can be invoked to notify the server in order to make a possibly definitive choice. The server is
     *  still going to do his checks and will reply.
     * @param optionIndex the index of the object in the list previously sent by the server
     * @return true if the procedure is successful
     */
    @Override
    public boolean choose(int optionIndex){
        ArrayList<String> result=new ArrayList<>();

        syncedSocketWrite(SocketString.CHOOSE+" "+optionIndex);


        synchronized (lockin) {
            waitForLine();

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isChoice(inSocket.readln()))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        return result.get(1).equals(SocketString.OK);
    }

    /**
     *  This function can be invoked to notify the server the intenction to select a tool car. The server is
     *  still going to do his checks and will reply.
     * @param toolIndex the index of the toolcard the user wants to use
     * @return true iff the toolcard has been activated
     */
    @Override
    public boolean enableTool(int toolIndex){
        ArrayList<String> result=new ArrayList<>();

        syncedSocketWrite(SocketString.TOOL_ENABLE+toolIndex);

        synchronized (lockin) {
            waitForLine();
            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isTool(inSocket.readln()))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        return result.get(1).equals(SocketString.OK);
    }

    /**
     * This function is invoked by the client to know if the toolcard's execution flow is still active
     * @return true iff the toolcard is active
     */
    @Override
    public boolean toolCanContinue() {
        ArrayList<String> result = new ArrayList<>();

        syncedSocketWrite(SocketString.TOOL_CONTINUE);

        synchronized (lockin) {
            waitForLine();
            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isTool(inSocket.readln()))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        return result.get(1).equals(SocketString.OK);
    }

    /**
     * This function can be invoked to notify the server in case the client wants to end his turn before the timer goes off.
     */
    @Override
    public void endTurn(){
        syncedSocketWrite(SocketString.GAME_END_TURN);

    }

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    @Override
    public void discard(){
        syncedSocketWrite(SocketString.DISCARD);

    }


    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    @Override
    public void back(){
        syncedSocketWrite(SocketString.BACK);

    }

    /**
     * This function can be invoked to notify the server of the closure of the communication and closes the socket.
     */
    @Override
    public void quit(){
        syncedSocketWrite(SocketString.QUIT);

        if(!socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                Logger.getGlobal().log(Level.INFO,e.getMessage());
            }
        }
        synchronized (lockPing){
            connectionOk=false;
            if(timerActive){
                pingTimer.cancel();
                timerActive=false;
            }
            lockPing.notifyAll();
        }
    }

    /**
     * This function is invoked in case the client wants to start a new match when the previously is just ended
     */
    @Override
    public void newMatch() {
        syncedSocketWrite(SocketString.GAME_NEW_MATCH);

    }

    /**
     * This method provides the ping functionality for checking if the connection is still active
     */
    @Override
    public void pong() {
        try{
            syncedSocketWrite(SocketString.PONG);

            synchronized (lockPing) {
                if(connectionOk) {
                    if(pingTimer!=null){
                        pingTimer.cancel();
                    }
                    pingTimer = new Timer();
                    pingTimer.schedule(new ConnectionTimeout(), PONG_TIME);
                    timerActive = true;
                    lockPing.notifyAll();
                }
            }
        } catch (Exception e) {
            Logger.getGlobal().log(Level.INFO,e.getMessage());
            return;
        }
    }

    /**
     * If triggered, it means that the connection has broken
     */
    private class ConnectionTimeout extends TimerTask {
        @Override
        public void run(){
            synchronized (lockPing) {
                if(connectionOk) {
                    connectionOk = false;
                    timerActive = false;
                    client.disconnect();
                    lockPing.notifyAll();
                }
            }
        }
    }
}
