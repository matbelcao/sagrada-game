package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.UserStatus;
import it.polimi.ingsw.common.serializables.*;
import it.polimi.ingsw.server.controller.Game;
import it.polimi.ingsw.server.controller.Validator;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the implementation of the SOCKET server-side connection methods
 */
public class SocketServer extends Thread implements ServerConn  {
    private Socket socket;
    private QueuedBufferedReader inSocket;
    private PrintWriter outSocket;
    private User user;
    private Timer pingTimer;
    private final Object pingLock;
    private boolean connectionOk;
    private final Object lockOutSocket;
    private boolean timerActive;

    /**
     * This is the constructor of the class, it starts a thread linked to an open socket
     * @param socket the socket already open used to communicate with the client
     */
    SocketServer(Socket socket, User user, QueuedBufferedReader inSocket, PrintWriter outSocket){
        this.inSocket=inSocket;
        this.outSocket=outSocket;
        this.lockOutSocket=new Object();
        this.user = user;
        this.socket = socket;
        this.pingLock = new Object();
        this.connectionOk=true;
        this.timerActive=false;
        start();
    }

    private void syncedSocketWrite(String message){
        synchronized (lockOutSocket){
            outSocket.println(message);
            outSocket.flush();
            lockOutSocket.notifyAll();
        }
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

                if(!inSocket.isEmpty() && connectionOk){
                    command=inSocket.getln();
                    if (Validator.isValid(command, parsedResult)) {
                        playing = execute(command,parsedResult);
                    }else{
                        syncedSocketWrite("INVALID message");
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
                    gameCommand(parsedResult.get(1));
                    break;
                case "GET":
                    getCommands(parsedResult);
                    break;
                case "GET_DICE_LIST":
                    if (!user.isMyTurn()) { throw new IllegalActionException(); }
                    sendDiceList();
                    break;
                case "SELECT":
                    if (!user.isMyTurn()) { throw new IllegalActionException(); }
                    selectDie(Integer.parseInt(parsedResult.get(1)));
                    break;
                case "CHOOSE":
                    choose(Integer.parseInt(parsedResult.get(1)));
                    break;
                case "GET_PLACEMENTS_LIST":
                    if (!user.isMyTurn()) { throw new IllegalActionException(); }
                    sendPlacementList();
                    break;
                case "TOOL":
                    toolCommand(parsedResult);
                    break;
                case "DISCARD":
                    if (!user.isMyTurn()) { throw new IllegalActionException(); }
                    user.getGame().discard();
                    break;
                case "BACK":
                    if (!user.isMyTurn()) { throw new IllegalActionException(); }
                    user.getGame().back(true);
                    break;
                case "QUIT":
                    user.quit();
                    return false;
                case "PONG":
                    pong();
                    break;
                default:
                    return true;
            }
        } catch (IllegalActionException e) {
            syncedSocketWrite("ILLEGAL ACTION!!");

        }
        return true;
    }

    private void gameCommand(String command) throws IllegalActionException {
        System.out.println("new_match");
        if(command.equals("new_match")){
            user.newMatch();
            return;
        }
        if(!user.isMyTurn()){ throw new IllegalActionException(); }
        user.getGame().startFlow();
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
            case "game_status":
                sendGameStatus();
                break;
        }
    }

    /**
     * The server notified to all players in the lobby after a successful login of a player that isn't reconnecting to a
     * match he was previously playing. The message is sent again to all said players whenever there is a change in the
     * number of the users in the lobby.
     * @param n number of the players waiting in the lobby to begin a new match
     */
    @Override
    public void notifyLobbyUpdate(int n){
        syncedSocketWrite("LOBBY "+n);

    }

    /**
     * Notifies that the game to which the user is playing is ready to begin
     * @param n the number of players that are participating to the new match
     * @param id the assigned number of the user receiving this notification
     */
    @Override
    public void notifyGameStart(int n,int id){
        syncedSocketWrite("GAME start "+n+" "+id);
    }

    /**
     * The server notifies the end of a match and sends to each client a list of fields that represent the ranking of
     * the match's players.
     * @param ranking the List containing the ranking
     */
    @Override
    public void notifyGameEnd(List<RankingEntry> ranking){
        StringBuilder builder= new StringBuilder("GAME end");
        for(RankingEntry e : ranking){
            builder.append(" "+e.getPlayerId()+","+e.getPoints()+","+e.getFinalPosition());
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     * This message is sent whenever a round is about to begin or has just ended.
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param roundNumber the number of the round (0 to 9)
     */
    @Override
    public void notifyRoundEvent(GameEvent gameEvent, int roundNumber){
        syncedSocketWrite("GAME "+ gameEvent.toString().toLowerCase()+" "+roundNumber);

    }

    /**
     * Notifies the beginning/ending of a turn
     * @param gameEvent the gameEvent that has occurred (start/end)
     * @param playerId the player's identifier (0 to 3)
     * @param turnNumber the number of the turn within the single round (0 to 1)
     */
    @Override
    public void notifyTurnEvent(GameEvent gameEvent, int playerId, int turnNumber){
        syncedSocketWrite("GAME "+ gameEvent.toString().toLowerCase()+" "+playerId+" "+turnNumber);

    }

    /**
     * Notifies to all connected users that the status of a certain player has been changed
     * @param gameEvent the new status of the player (reconnect|disconnect|quit)
     * @param id the id of the interested player
     */
    @Override
    public void notifyStatusUpdate (GameEvent gameEvent, int id, String userName){
        syncedSocketWrite("STATUS "+ gameEvent.toString().toLowerCase()+" "+id+" "+userName);

    }

    /**
     * Notifies that some parameter in the board has changed. Triggers the update request of the receiving client
     */
    @Override
    public void notifyBoardChanged(){
        syncedSocketWrite("GAME "+GameEvent.BOARD_CHANGED.toString().toLowerCase());

    }

    /**
     * Sends the client a text description of the four drafted schema card passed as a parameter
     */
    private void draftSchemaCards() throws IllegalActionException {
        Game game= user.getGame();
        List<LightSchemaCard> lightSchemas=game.getDraftedSchemaCards(user);

        for(LightSchemaCard s: lightSchemas){
            StringBuilder builder= new StringBuilder("SEND schema "+s.getName().replaceAll(" ","_")+" "+s.getFavorTokens());
            for (int index=0; index < SchemaCard.NUM_ROWS*SchemaCard.NUM_COLS ; index++) {
                if (s.hasConstraintAt(index)) {
                    builder.append(" C,"+index+"," + s.getConstraintAt(index).toString());
                }
                if (s.hasDieAt(index)) {
                    builder.append(" D,"+index+"," + s.getDieAt(index).getColor().toString() + "," + s.getDieAt(index).getShade().toString());
                }
            }
            syncedSocketWrite(builder.toString());
        }
    }

    /**
     * Sends the client a text description of the specific user schema card passed as a parameter
     * @param playerId the Id of the requested player's schema card
     */
    private void sendUserSchemaCard(int playerId) throws IllegalActionException {
        LightSchemaCard lightSchema = user.getGame().getUserSchemaCard(playerId);
        StringBuilder builder=new StringBuilder();
        builder.append("SEND schema "+lightSchema.getName().replaceAll(" ","_")+" "+lightSchema.getFavorTokens());
        for (int index=0; index < SchemaCard.NUM_ROWS*SchemaCard.NUM_COLS ; index++) {
            if (lightSchema.hasDieAt(index)) {
                builder.append(" D," + index + "," + lightSchema.getDieAt(index).getColor().toString() + "," + lightSchema.getDieAt(index).getShade().toString());
            }else if (lightSchema.hasConstraintAt(index)) {
                builder.append(" C," + index + "," + lightSchema.getConstraintAt(index).toString());
            }
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     * Sends the client a text containing his amount of favor tokens
     */
    public void sendFavorTokens(int playerId) {
        syncedSocketWrite("SEND favor_tokens "+user.getGame().getFavorTokens(playerId));

    }


    /**
     * Sends the client a text description of the private objective card
     */
    private void sendPrivateObjectiveCard(){
        LightPrivObj privObjectiveCard=user.getGame().getPrivCard(user);

        syncedSocketWrite("SEND priv "+privObjectiveCard.getId()+" "+privObjectiveCard.getName().replaceAll(" ", "_")
                +" "+privObjectiveCard.getDescription().replaceAll(" ", "_")+" "+privObjectiveCard.getColor().toString());

    }

    /**
     * Sends the client a text description of the public objective card
     */
    private void sendPublicObjectiveCards(){
        List<LightCard> pubObjectiveCards= user.getGame().getPubCards();

        for(LightCard p:pubObjectiveCards){
            syncedSocketWrite("SEND pub "+p.getId()+" "+p.getName().replaceAll(" ", "_")+" "+p.getDescription().replaceAll(" ", "_"));

        }
    }

    /**
     * Sends the client a text description of the tool card passed as a parameter
     */
    private void sendToolCards(){
        List<LightTool> toolCards= user.getGame().getToolCards();

        for (LightTool t:toolCards){
            syncedSocketWrite("SEND tool "+t.getId()+" "+t.getName().replaceAll(" ", "_")+" "+t.getDescription().replaceAll(" ", "_") +" "+t.isUsed());
        }
    }

    /**
     * Sends the client a textual list of the dice in the DraftPool
     */
    private void sendDraftPoolDice() throws IllegalActionException {
        List<LightDie> draftPool= user.getGame().getDraftedDice();

        StringBuilder builder=new StringBuilder("SEND draftpool");
        for (int i=0;i<draftPool.size();i++){
            builder.append(" "+i+","+draftPool.get(i).getColor().toString()+","+draftPool.get(i).getShade().toString());
        }
        syncedSocketWrite(builder.toString());

    }

    /**
     * Sends the client a textual list of the dice in the RoundTrack (can be placed multiple die at the same index)
     */
    private void sendRoundTrackDice() throws IllegalActionException {
        List<List<LightDie>> trackList = user.getGame().getRoundTrackDice();
        List<LightDie> dieList;

        StringBuilder builder=new StringBuilder("SEND roundtrack");
        for(int i=0;i<trackList.size();i++){
            dieList= trackList.get(i);
            for(LightDie d:dieList){
                builder.append(" "+i+","+d.getColor().toString()+","+d.getShade().toString());
            }
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     *
     * Sends the client a text description of the users that are currently playing in the match
     */
    private void sendPlayers(){
        List<LightPlayer> players= user.getGame().getPlayers();

        StringBuilder builder=new StringBuilder("SEND players");
        for (LightPlayer p:players){
            builder.append(" "+p.getPlayerId()+","+p.getUsername());
        }
        syncedSocketWrite(builder.toString());
    }

    private void sendGameStatus(){
        LightGameStatus gameStatus=user.getGame().getGameStatus();
        StringBuilder builder=new StringBuilder("SEND game_status "+gameStatus.isInit()+" "+gameStatus.getNumPlayers()+" "
                +gameStatus.getNumRound()+" "+ gameStatus.getIsFirstTurn()+" "+gameStatus.getNowPlaying());

        syncedSocketWrite(builder.toString());
    }

    /**
     * Sends to the client a text list of the dice contained in the selected area (with an unique INDEX)
     */
    private void sendDiceList() throws IllegalActionException {
        List<IndexedCellContent> dice=user.getGame().getDiceList();
        StringBuilder builder= new StringBuilder();
        if(dice.size()>0){
            builder.append("LIST_DICE "+dice.get(0).getPlace().toString().toLowerCase());
            for(int index=0;index<dice.size();index++){
                builder.append(" "+dice.get(index).getPosition()+","+dice.get(index).getContent().getShade().toString()
                        +"," +dice.get(index).getContent().getColor().toString());
            }
        }else{
            builder.append("LIST_DICE");
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     * Sends to the client a text list of possible placements in his schema card (with an unique INDEX)
     */
    private void sendPlacementList() throws IllegalActionException {
        List<Integer> placements=user.getGame().getPlacements();

        StringBuilder builder=new StringBuilder("LIST_PLACEMENTS");
        for(int i=0;i<placements.size();i++){
            builder.append(" "+placements.get(i));
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     * Allows the user to select the die of the previous list received, then sends the relative list of allowed positions
     * @param dieIndex the index of the die previously received by the client
     */
    private void selectDie(int dieIndex) throws IllegalActionException {
        List<Actions> options = user.getGame().selectDie(dieIndex);

        StringBuilder builder=new StringBuilder("LIST_OPTIONS");
        for(int i=0;i<options.size();i++){
            builder.append(" "+i+ ","+options.get(i).toString());
        }
        syncedSocketWrite(builder.toString());
    }

    /**
     * Allows the user to put the die contained in the previous list received, then sends an answer about the action
     * @param optionIndex the index of the option received
     */
    private void choose(int optionIndex) throws IllegalActionException {
        if(user.getGame()==null){throw new IllegalActionException();}

        Boolean result=user.getGame().choose(user,optionIndex);
        if(result){
            syncedSocketWrite("CHOICE ok");
        }else{
            syncedSocketWrite("CHOICE ko");
        }

    }

    /**
     * Notifies the server that the user wants to enable a ToolCard and sends the result to the client
     * @param toolIndex the index of the tool to enable
     * @throws IllegalActionException
     */
    private void toolEnable(int toolIndex) throws IllegalActionException {
        Boolean used;
        used=user.getGame().activeTool(toolIndex);
        if(used){
            syncedSocketWrite("TOOL ok");
        }else{
            syncedSocketWrite("TOOL ko");
        }

    }

    /**
     * Recalls to the controller if the execution of the toolcard is terminated and sends the response to the client
     * @throws IllegalActionException
     */
    private void toolCanContinue() throws IllegalActionException {
        Boolean isActive=null;
        isActive=user.getGame().toolStatus();
        if(isActive){
            syncedSocketWrite("TOOL ok");
        }else{
            syncedSocketWrite("TOOL ko");
        }

    }


    /**
     * Timeot connection
     */
    private class connectionTimeout extends TimerTask {
        @Override
        public void run(){
            synchronized (pingLock) {
                if(user.getStatus()!=UserStatus.DISCONNECTED){
                    connectionOk = false;
                    pingLock.notifyAll();
                    user.disconnect();
                    System.out.println("CONNECTION TIMEOUT!");
                }
            }
        }
    }

    private void pong(){
        synchronized (pingLock) {
            pingTimer.cancel();
            pingTimer=null;
            pingLock.notifyAll();
        }
    }

    /**
     * Tests if the client is still connected
     */
    @Override
    public void ping() {
        new Thread(() -> {
            while(connectionOk) {
                synchronized (pingLock) {
                    if (pingTimer==null && connectionOk) {
                        syncedSocketWrite("PING");

                        System.out.println("PING SOCKET");

                        pingTimer = new Timer();
                        pingTimer.schedule(new connectionTimeout(), 5000);
                    }
                    pingLock.notifyAll();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
