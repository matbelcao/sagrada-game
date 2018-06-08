package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * This class is the implementation of the SOCKET server-side connection methods
 */
public class SocketServer extends Thread implements ServerConn  {
    private Socket socket;
    private QueuedInReader inSocket;
    private PrintWriter outSocket;
    private User user;
    private Timer pingTimer;
    private final Object pingLock;
    private boolean connectionOk;

    /**
     * This is the constructor of the class, it starts a thread linked to an open socket
     * @param socket the socket already open used to communicate with the client
     */
    SocketServer(Socket socket, User user,QueuedInReader inSocket,PrintWriter outSocket){
        this.inSocket=inSocket;
        this.outSocket=outSocket;
        this.user = user;
        this.socket = socket;
        this.pingLock = new Object();
        this.connectionOk=true;
        start();
        //pingThread();
    }

    /**
     * This method runs a loop that manages the socket commands until the connection is closed
     */
    @Override
    public void run(){
        String command = "";
        ArrayList<String> parsedResult = new ArrayList<>();
        boolean playing = true;
        while(playing && connectionOk){
            try {
                try {
                    inSocket.add();
                } catch (Exception e) {
                    user.disconnect();
                    return;
                }

                if(!inSocket.isEmpty()){
                    command=inSocket.getln();
                    if (Validator.isValid(command, parsedResult)) {
                        playing = execute(command,parsedResult);
                    }else{
                        outSocket.println("INVALID message");
                        outSocket.flush();
                    }
                }
            } catch (IllegalArgumentException e) {
                user.disconnect();
                playing=false;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method provides the socket messages interpretation logic
     * @param command the socket's message received
     * @return true if the connection has to be closed
     */
    private boolean execute(String command,ArrayList<String> parsedResult) {
        try {
        switch (parsedResult.get(0)) {
            case "GAME":
                gameCommand(parsedResult);
                break;
            case "GET":
                getCommands(parsedResult);
                break;
            case "SELECT_DIE":
                sendDiceList();
                break;
            case "SELECT":
                selectDie(Integer.parseInt(parsedResult.get(1)));
                break;
            case "CHOOSE":
                choose(Integer.parseInt(parsedResult.get(1)));
                break;
            case "TOOL":
                toolCommand(parsedResult);
                break;
            case "DISCARD":
                user.getGame().discard();
                break;
            case "EXIT":
                //user.getGame().exit();
                break;
            case "QUIT":
                user.quit();
                return false;
            case "PONG":
                break;
            default:
                return false;
        }
        } catch (IllegalActionException e) {
            outSocket.println("ILLEGAL ACTION!!");
            outSocket.flush();
        }
        return true;
    }

    private void gameCommand(ArrayList<String> parsedResult) throws IllegalActionException {
        if (parsedResult.get(1).equals("end_turn")) {
            if(!user.isMyTurn()){
                throw new IllegalActionException();
            }
            user.getGame().startFlow();
        }
    }

    private void toolCommand(ArrayList<String> parsedResult) throws IllegalActionException {
        if(!user.isMyTurn()){throw new IllegalActionException();}
        if ("enable".equals(parsedResult.get(1))) {
            toolEnable(Integer.parseInt(parsedResult.get(2)));
        }else if("can_continue".equals(parsedResult.get(1))){
           toolCanContinue();
        }
    }

    private void getCommands(ArrayList<String> parsedResult) throws IllegalActionException {
        switch (parsedResult.get(1)) {
            case "schema":
                if (parsedResult.get(2).equals("draft")) {
                    draftSchemaCards();
                } else {
                    sendUserSchemaCard(Integer.parseInt(parsedResult.get(2)));
                }
                break;
            case "favor_tokens":
                sendFavorTokens(Integer.parseInt(parsedResult.get(2)));
                break;
            case "priv":
                sendPrivateObjectiveCard();
                break;
            case "pub":
                sendPublicObjectiveCards();
                break;
            case "tool":
                sendToolCards();
                break;
            case "draftpool":
                sendDraftPoolDice();
                break;
            case "roundtrack":
                sendRoundTrackDice();
                break;
            case "players":
                sendPlayers();
                break;
        }
    }

    /**
     * Sends the lobby update message to the user
     * @param n the number of players in the lobby
     */
    @Override
    public void notifyLobbyUpdate(int n){
        outSocket.println("LOBBY "+n);
        outSocket.flush();
    }

    /**
     * Sends the match starting message to the user
     * @param n the number of connected players
     * @param id the assigned id of the specific user
     */
    @Override
    public void notifyGameStart(int n,int id){
        outSocket.println("GAME start "+n+" "+id);
        outSocket.flush();
    }

    /**
     * Sends the match ending message and the relative ranking  to the user
     * @param players the player's list containing the data
     */
    @Override
    public void notifyGameEnd(List<Player> players){
        outSocket.print("GAME end");
        for(Player p : players){
            outSocket.print(" "+p.getGameId()+","+p.getScore()+","+p.getFinalPosition());
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the round starting/ending message to the user
     * @param event the round's event string: "start" or "end"
     * @param roundNumber the round's number
     */
    @Override
    public void notifyRoundEvent(String event,int roundNumber){
        outSocket.println("GAME round_"+event+" "+roundNumber);
        outSocket.flush();
    }

    /**
     * Sends the turn starting/ending message to the user
     * @param event the turns's event string: "start" or "end"
     * @param playerId the involved player's ID
     * @param turnNumber the turn number
     */
    @Override
    public void notifyTurnEvent(String event,int playerId,int turnNumber){
        outSocket.println("GAME turn_"+event+" "+playerId+" "+turnNumber);
        outSocket.flush();
    }

    /**
     * Notifies the client of a user's status change
     * @param event the event happened
     * @param id the id of the interested user
     */
    @Override
    public void notifyStatusUpdate (String event,int id){
        outSocket.println("STATUS "+event+" "+id);
        outSocket.flush();
    }

    /**
     * Sets the schema card during the match if it's possible
     * @param schemaId the schema's id
     */
    private void chooseSchema(int schemaId){
        Game game= user.getGame();
        boolean result;
        result=game.chooseSchemaCard(user,schemaId);
        if(result){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    /**
     * Sends the client a text description of the four drafted schema card passed as a parameter
     */
    private void draftSchemaCards() throws IllegalActionException {
        Cell cell;
        Game game= user.getGame();
        ArrayList<SchemaCard> schemas=(ArrayList<SchemaCard>) game.getDraftedSchemaCards(user);

        for(SchemaCard s: schemas){
            outSocket.print("SEND schema "+s.getName().replaceAll(" ","_")+" "+s.getFavorTokens());
            for (int index=0; index < SchemaCard.NUM_ROWS*SchemaCard.NUM_COLS ; index++) {
                cell = s.getCell(index);
                if (cell.hasConstraint()) {
                    outSocket.print(" C,"+index+"," + cell.getConstraint().toString());
                }
                if (cell.hasDie()) {
                    outSocket.print(" D,"+index+"," + cell.getDie().getColor().toString() + "," + cell.getDie().getShade().toString());
                }
            }
            outSocket.println("");
            outSocket.flush();
        }
    }

    /**
     * Sends the client a text description of the specific user schema card passed as a parameter
     * @param playerId the Id of the requested player's schema card
     */
    private void sendUserSchemaCard(int playerId) throws IllegalActionException {
        Cell cell;
        SchemaCard schemaCard = user.getGame().getUserSchemaCard(playerId,false);

        outSocket.print("SEND schema "+schemaCard.getName());
        for (int index=0; index < SchemaCard.NUM_ROWS*SchemaCard.NUM_COLS ; index++) {
            cell = schemaCard.getCell(index);
            if (cell.hasDie()) {
                outSocket.print(" D," + index + "," + cell.getDie().getColor().toString() + "," + cell.getDie().getShade().toString());
            }else if (cell.hasConstraint()) {
                outSocket.print(" C," + index + "," + cell.getConstraint().toString());
            }
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the client a text containing his amount of favor tokens
     */
    public void sendFavorTokens(int playerId) {
        outSocket.println("SEND favor_tokens "+user.getGame().getFavorTokens(playerId));
        outSocket.flush();
    }


    /**
     * Sends the client a text description of the private objective card
     */
    private void sendPrivateObjectiveCard(){
        PrivObjectiveCard privObjectiveCard=user.getGame().getPrivCard(user);

        outSocket.println("SEND priv "+privObjectiveCard.getId()+" "+privObjectiveCard.getName().replaceAll(" ", "_")
                +" "+privObjectiveCard.getDescription().replaceAll(" ", "_")+" "+privObjectiveCard.getColor().toString());
        outSocket.flush();
    }

    /**
     * Sends the client a text description of the public objective card
     */
    private void sendPublicObjectiveCards(){
        ArrayList<PubObjectiveCard> pubObjectiveCards= (ArrayList<PubObjectiveCard>) user.getGame().getPubCards();

        for(PubObjectiveCard p:pubObjectiveCards){
            outSocket.println("SEND pub "+p.getId()+" "+p.getName().replaceAll(" ", "_")+" "+p.getDescription().replaceAll(" ", "_"));
            outSocket.flush();
        }
    }

    /**
     * Sends the client a text description of the tool card passed as a parameter
     */
    private void sendToolCards(){
        ArrayList<ToolCard> toolCards= (ArrayList<ToolCard>) user.getGame().getToolCards();

        for (ToolCard t:toolCards){
            outSocket.println("SEND tool "+t.getId()+" "+t.getName().replaceAll(" ", "_")+" "+t.getDescription().replaceAll(" ", "_") +" "+t.isAlreadyUsed());
            outSocket.flush();
        }
    }

    /**
     * Sends the client a textual list of the dice in the DraftPool
     */
    private void sendDraftPoolDice() throws IllegalActionException {
        Die die;
        ArrayList<Die> dice= (ArrayList<Die>) user.getGame().getDraftedDice(false);

        outSocket.print("SEND draftpool");
        for (int i=0;i<dice.size();i++){
            die=dice.get(i);
            outSocket.print(" "+i+","+die.getColor().toString()+","+die.getShade().toString());
        }
        outSocket.println("");
        outSocket.flush();

    }

    /**
     * Sends the client a textual list of the dice in the RoundTrack (can be placed multiple die at the same index)
     */
    private void sendRoundTrackDice() throws IllegalActionException {
        List<List<Die>> trackList = user.getGame().getRoundTrackDice(false);
        ArrayList<Die> dieList;

        outSocket.print("SEND roundtrack");
        for(int i=0;i<trackList.size();i++){
            dieList= (ArrayList<Die>) trackList.get(i);
            for(Die d:dieList){
                outSocket.print(" "+i+","+d.getColor().toString()+","+d.getShade().toString());
            }
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     *
     * Sends the client a text description of the users that are currently playing in the match
     */
    private void sendPlayers(){
        ArrayList<Player> players= (ArrayList<Player>) user.getGame().getPlayers();

        outSocket.print("SEND players");
        for (Player p:players){
            outSocket.print(" "+p.getGameId()+","+p.getUsername());
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends to the client a text list of the dice contained in the selected area (with an unique INDEX)
     */
    private void sendDiceList() throws IllegalActionException {
        List<IndexedCellContent> dice=new ArrayList<>();

        //dice= (ArrayList<Die>) user.getGame().getDiceList(false);

        if(dice.size()>0){
            outSocket.print("LIST_DICE "+dice.get(0).getPlace().toString().toLowerCase());
            for(int index=0;index<dice.size();index++){
                outSocket.print(" "+index+","+dice.get(index).getPosition()+","+dice.get(index).getContent().getShade().toString()
                        +"," +dice.get(index).getContent().getColor().toString());
                index++;
            }
        }else{
            throw new IllegalActionException();
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends to the client a text list of possible placements in his schema card (with an unique INDEX)
     */
    private void sendPlacementList(){
        List<Integer> placements=new ArrayList<>();

        //placements=user.getGame().getPlacements(user);
        outSocket.print("CHOOSE_PLACEMENT");
        for(Integer position:placements){
            outSocket.print(" "+position);
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Allows the user to select the die of the previous list received, then sends the relative list of allowed positions
     * @param index the index of the die previously received by the client
     */
    private void selectDie(int index) throws IllegalActionException {
        List<Commands> options =new ArrayList<>();

        //options = user.getGame().selectDie(int index);

        outSocket.print("LIST_OPTIONS");
        for(int i=0;i<options.size();i++){
            outSocket.print(" "+i+ ","+options.get(i).toString());
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Allows the user to put the die contained in the previous list received, then sends an answer about the action
     * @param index the index of the die previously selected
     */
    private void choose(int index) throws IllegalActionException {
        Boolean result=null;

        //result=user.getGame().choose(user,index);
        if(result){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    private void toolEnable(int index) throws IllegalActionException {
        Boolean used;
        used=user.getGame().chooseTool(user,index);
        if(used){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    private void toolCanContinue(){
        Boolean isActive=null;
        //isActive=user.getGame().toolStatus(user);
        if(isActive){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }



    /*public void pingThread(){
        new Thread(() -> {
            while(connectionOk) {
                outSocket.println("STATUS check");
                outSocket.flush();

                pingTimer = new Timer();
                pingTimer.schedule(new connectionTimeout(), 2000);
                try {
                    Thread.sleep(2100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            user.disconnect();
        }).start();
    }*/

    /**
     * Timeot connection
     */
    /*private class connectionTimeout extends TimerTask {
        @Override
        public void run(){
            synchronized (pingLock) {
                connectionOk = false;
                pingLock.notifyAll();
            }
        }
    }*/

    private void pong(){
        synchronized (pingLock) {
            pingTimer.cancel();
            pingLock.notifyAll();
        }
    }

    @Override
    public boolean ping() {
        List<String> result= new ArrayList<>();
        try{
            //outSocket.println("PING");
            //outSocket.flush();
            //debug
            //System.out.println(inSocket.isEmpty());
            while(inSocket.isEmpty()){
                Thread.sleep(50);
            }
            if(Validator.isValid(inSocket.readln(),result) ){
                if(Validator.checkPong(inSocket.readln())){
                    inSocket.pop();
                }
                return true;
            }
        } catch (Exception e) {
            try {
                socket.close();
            } catch (IOException x) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
