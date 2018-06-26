package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
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

/**
 * This class is the implementation of the SOCKET client-side connection methods
 */
public class SocketClient implements ClientConn {
    private static final int NUM_DRAFTED_SCHEMAS=4;
    private static final int COMMA_PARAMS_START=2;
    private static final int LIST_START=1;
    private static final int NUM_CARDS=3;

    private Socket socket;
    private QueuedBufferedReader inSocket;
    private PrintWriter outSocket;
    private Client client;
    private final Object lockin=new Object();
    private Timer pingTimer;
    private final Object pingLock=new Object();
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

    private void syncedSocketWrite(String message){
        synchronized (lockOutSocket){
            outSocket.println(message);
            outSocket.flush();
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
                                client.addUpdateTask(new Thread(()->
                                        client.updatePlayerStatus(
                                                Integer.parseInt(result.get(2)),
                                                GameEvent.valueOf(result.get(1).toUpperCase()),
                                                result.get(3))
                                ));

                            } else if (ClientParser.isLobby(inSocket.readln())) {
                                inSocket.pop();
                                updateLobby(result.get(1));
                            } else if (ClientParser.isGame(inSocket.readln())) {
                                inSocket.pop();

                                updateMessages( result);

                            } else if (ClientParser.isPing(inSocket.readln())) {
                                inSocket.pop();
                                socketPong();
                            } else if (ClientParser.isInvalid(inSocket.readln())) {
                                inSocket.pop();
                                System.out.println("INVALID message");
                            } else if (ClientParser.isIllegalAction(inSocket.readln())) {
                                inSocket.pop();
                                System.out.println("ILLEGAL ACTION!");
                            }
                        }
                        lockin.notifyAll();
                    }
                } catch (IOException e) {
                    if(!socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException e1) {
                            System.err.println("ERR: error while closing the socket");
                        }
                        System.out.println("QUITTED(1)");
                    }
                    synchronized (pingLock){
                        if(timerActive){
                            pingTimer.cancel();
                            timerActive=false;
                            pingTimer.notifyAll();
                        }
                    }
                    client.disconnect();
                }
            }
            System.out.println("EXIT LISTENING THREAD");
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
                try {
                    inSocket.waitForLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    private void waitForTheRightOne() {
        lockin.notifyAll();
        try {
            lockin.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            inSocket.waitForLine();
        }catch (IOException e){
            e.printStackTrace();
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
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                    try {
                        inSocket.waitForLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                try {
                    inSocket.waitForLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
        System.out.println("QUI");
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

        syncedSocketWrite(SocketString.GET_DRAFTPOOL);

        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        String args[];

        syncedSocketWrite(SocketString.GET_ROUNDTRACK);

        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();

        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        outSocket.flush();
    }

    /**
     * This message is sent to the server when the client that received a list of possible placement for a die chooses
     * not to place that die
     */
    @Override
    public void discard(){
        syncedSocketWrite(SocketString.DISCARD);
        outSocket.flush();
    }


    /**
     * This message is sent to the server when the client wants to stop using a toolcard before it ends
     */
    @Override
    public void back(){
        syncedSocketWrite(SocketString.BACK);
        outSocket.flush();
    }

    /**
     * This function can be invoked to notify the server of the closure of the communication and closes the socket.
     */
    @Override
    public void quit(){
        syncedSocketWrite(SocketString.QUIT);
        outSocket.flush();
        connectionOk = false;
        if(!socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
        synchronized (pingLock){
            if(timerActive){
                pingTimer.cancel();
                timerActive=false;
                pingTimer.notifyAll();
            }
        }
    }

    /**
     * This function is invoked in case the client wants to start a new match when the previously is just ended
     */
    @Override
    public void newMatch() {
        syncedSocketWrite(SocketString.GAME_NEW_MATCH);
        outSocket.flush();
    }

    /**
     * Disabled for socket
     */
    @Override
    public void pong() {
        return;
    }

    private void socketPong(){
        try{
            syncedSocketWrite(SocketString.PONG);
            outSocket.flush();
            //System.out.println("PONG");
            synchronized (pingLock) {
                if(connectionOk) {
                    pingTimer.cancel();
                    pingTimer = new Timer();
                    pingTimer.schedule(new connectionTimeout(), 5000);
                    timerActive = true;
                    pingLock.notifyAll();
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Timeot connection
     */
    private class connectionTimeout extends TimerTask {
        @Override
        public void run(){
            synchronized (pingLock) {
                if(connectionOk) {
                    System.out.println("CONNECTION TIMEOUT!");
                    connectionOk = false;
                    timerActive = false;
                    client.disconnect();
                    pingLock.notifyAll();
                }
            }
        }
    }
}
