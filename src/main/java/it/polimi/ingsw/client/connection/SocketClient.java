package it.polimi.ingsw.client.connection;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.connection.SocketServer;

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

        inSocket.pop();
        connectionOk=true;
        client.getClientUI().updateConnectionOk();
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
                                client.updatePlayerStatus(Integer.parseInt(result.get(2)), Event.valueOf(result.get(1).toUpperCase()));

                            } else if (ClientParser.isLobby(inSocket.readln())) {
                                inSocket.pop();
                                updateLobby(result.get(1));
                            } else if (ClientParser.isGame(inSocket.readln())) {
                                inSocket.pop();

                                (new Thread(() -> updateMessages( result))).start();

                            } else if (ClientParser.isPing(inSocket.readln())) {
                                inSocket.pop();
                                pong();
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
                    }
                    client.setUserStatus(UserStatus.DISCONNECTED);
                }
            }
            System.out.println("QUITTED(1)");
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
            case "start":
                client.updateGameStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "end":
                List<RankingEntry> ranking= new ArrayList<>();
                for(i=COMMA_PARAMS_START; i<outcomes.size();i++){
                    param=outcomes.get(i).split(",");
                    ranking.add(new RankingEntry(Integer.parseInt(param[0]),Integer.parseInt(param[1]),Integer.parseInt(param[2])));
                }
                client.updateGameEnd(ranking);
                System.out.println("<--<----<----<-----<---<---GAME END (CLASSIFICA)---->----->--->--->---->--->-->");
                connectionOk = false;
                break;
            case "round_start":
                client.updateGameRoundStart(Integer.parseInt(outcomes.get(2)));
                break;
            case "round_end":
                client.updateGameRoundEnd(Integer.parseInt(outcomes.get(2)));
                break;
            case "turn_start":
                client.updateGameTurnStart(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3))==0);
                break;
            case "turn_end":
                client.updateGameTurnEnd(Integer.parseInt(outcomes.get(2)),Integer.parseInt(outcomes.get(3)));
                break;
            case "board_changed":
                client.getUpdates();
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
            synchronized (lockin) {
                try {
                    inSocket.waitForLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema"))) {
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

        outSocket.println("GET schema "+playerId);
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("schema"))) {
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

        outSocket.println("GET priv");
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("priv"))) {
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
    public List<LightCard> getPublicObjects() {
            ArrayList<String> result= new ArrayList<>();
            List<LightCard> pubObjCards=new ArrayList<>();
            LightCard lightObjCard;

            outSocket.println("GET pub");
            outSocket.flush();

            int i=0;
            while(i<NUM_CARDS){
                synchronized (lockin) {
                    try {
                        inSocket.waitForLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("pub"))) {
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

        outSocket.println("GET tool");
        outSocket.flush();

        int i=0;
        while(i<NUM_CARDS){

            synchronized (lockin) {
                try {
                    inSocket.waitForLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("tool"))) {
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
        String args[];

        outSocket.println("GET draftpool");
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("draftpool"))) {
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

        outSocket.println("GET roundtrack");
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("roundtrack"))) {
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
        LightPlayer player;
        String args[];

        outSocket.println("GET players");
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("players"))) {
                waitForTheRightOne();
            }

            inSocket.pop();
            lockin.notifyAll();
        }
        for(int i=COMMA_PARAMS_START;i<result.size();i++) {
            args = result.get(i).split(",");
            playerList.add( new LightPlayer(args[1], Integer.parseInt(args[0])));
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
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isSend(inSocket.readln()) && result.get(1).equals("favor_tokens"))) {
                waitForTheRightOne();
            }
            inSocket.pop();
            lockin.notifyAll();
        }
        favor_tokens=Integer.parseInt(result.get(2));
        return favor_tokens;
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

        outSocket.println("GET_DICE_LIST");
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

        outSocket.println("SELECT "+dieIndex);
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

        outSocket.println("GET_PLACEMENTS_LIST");
        outSocket.flush();
        synchronized (lockin) {
            try {
                inSocket.waitForLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!(ClientParser.parse(inSocket.readln(), result) && ClientParser.isPlacementList(inSocket.readln()) && result.get(0).equals("LIST_PLACEMENTS"))) {
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

        outSocket.println("CHOOSE "+optionIndex);
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
        return result.get(1).equals("ok");
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

        outSocket.println("TOOL enable "+toolIndex);
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
    public void back(){
        outSocket.println("BACK");
        outSocket.flush();
    }

    /**
     * This function can be invoked to notify the server of the closure of the communication and closes the socket.
     */
    @Override
    public void quit(){
        outSocket.println("QUIT");
        outSocket.flush();
        connectionOk = false;
        if(!socket.isClosed()){
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("already closed");
            }
        }
        System.out.println("QUITTED(2)");
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
            synchronized (pingLock) {
                pingTimer.cancel();
                pingTimer = new Timer();
                pingTimer.schedule(new connectionTimeout(), 5000);
                pingLock.notifyAll();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Timeot connection
     */
    private class connectionTimeout extends TimerTask {
        @Override
        public void run(){
            synchronized (pingLock) {
                System.out.println("CONNECTION TIMEOUT!");
                connectionOk = false;
                pingLock.notifyAll();
            }
        }
    }
}
