package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

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
    private QueuedInReader inSocket;
    private PrintWriter outSocket;
    private AckQueue ack;
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
        this.ack=new AckQueue();
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
            if (Validator.checkQuitParams(command, parsedResult)) {
                user.quit();
                return false;
            }else if (Validator.checkGameParams(command, parsedResult)) {
                gameCommand(parsedResult);
            }else if (Validator.checkChooseParams(command, parsedResult)) {
                chooseCommand(parsedResult);
            }else if (Validator.checkGetParams(command, parsedResult)) {
                getCommands(parsedResult);
            }else if (Validator.checkGetDiceListParams(command, parsedResult) ) {
                listCommand(parsedResult);
            }else if (Validator.checkSelectParams(command, parsedResult) ) {
                selectCommand(parsedResult);
            }else if (Validator.checkDiscardParams(command, parsedResult) ) {
                discardAction();
            }else if (Validator.checkAckParams(command, parsedResult)) {
                switch (parsedResult.get(1)) {
                    case "status":
                        pong();
                        break;
                }
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

    private void listCommand(ArrayList<String> parsedResult) throws IllegalActionException {
        if(!user.isMyTurn()){throw new IllegalActionException();}
        switch (parsedResult.get(1)) {
            case "schema":
                sendSchemaDiceList();
                break;
            case "roundtrack":
                sendRoundTrackDiceList();
                break;
            case "draftpool":
                sendDraftPoolDiceList();
                break;
        }
    }

    private void selectCommand(ArrayList<String> parsedResult) throws IllegalActionException {
        if(!user.isMyTurn()){throw new IllegalActionException();}
        switch (parsedResult.get(1)) {
            case "die":
                selectDie(Integer.parseInt(parsedResult.get(2)));
                break;
            case "modified_die":
                //game.select
                break;
        }
    }

    private void chooseCommand(ArrayList<String> parsedResult) throws IllegalActionException {
        if(parsedResult.get(1).equals("schema")){
            chooseSchema(Integer.parseInt(parsedResult.get(2)));
            return;
        }
        if(!user.isMyTurn()){throw new IllegalActionException();}
        if ("die_placement".equals(parsedResult.get(1))) {
            putDie(Integer.parseInt(parsedResult.get(2)));

        } else if ("tool".equals(parsedResult.get(1))) {
            useTool(Integer.parseInt(parsedResult.get(2)));
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
                sendFavorTokens();
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
            outSocket.print("SEND schema "+s.getName().replaceAll(" ","_"));
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
    public void sendFavorTokens() {
        outSocket.println("SEND favor_tokens "+user.getGame().getFavorTokens(user));
        outSocket.flush();
    }


    /**
     * Sends the client a text description of the private objective card
     */
    private void sendPrivateObjectiveCard(){
        PrivObjectiveCard privObjectiveCard=user.getGame().getPrivCard(user);

        outSocket.println("SEND priv "+privObjectiveCard.getId()+" "+privObjectiveCard.getName().replaceAll(" ", "_")
                +" "+privObjectiveCard.getDescription().replaceAll(" ", "_")+","+privObjectiveCard.getColor().toString());
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
            outSocket.println("SEND tool "+t.getId()+" "+t.getName().replaceAll(" ", "_")+" "+t.getDescription().replaceAll(" ", "_") +","+t.hasAlreadyUsed());
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
     * Sends the client a text list of the dice contained in the schema card parameter (with an unique INDEX)
     */
    private void sendSchemaDiceList() throws IllegalActionException {
        int index=0;
        Die die;
        SchemaCard schema=user.getGame().getUserSchemaCard(user,false);
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        outSocket.print("LIST schema");
        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();
            outSocket.print(" "+index+","+diceIterator.getIndex()+","+die.getColor().toString()
                    +","+die.getShade().toString());
            index++;
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the client a text list of the dice contained in the RoundTrack (with an unique INDEX)
     */
    private void sendRoundTrackDiceList() throws IllegalActionException {
        int index=0;
        int numberInRound;
        List<List<Die>> trackList = user.getGame().getRoundTrackDice(false);
        ArrayList<Die> dieList;

        outSocket.print("LIST roundtrack");
        for(int round=0;round<trackList.size();round++){
            numberInRound=0;
            dieList= (ArrayList<Die>) trackList.get(round);
            for(Die d:dieList){
                outSocket.print(" "+index+","+round+","+numberInRound+","+d.getColor().toString()+","+d.getShade().toString());
                numberInRound++;
                index++;
            }
        }
        outSocket.println("");
        outSocket.flush();
    }


    /**
     * Sends the client a text list of the dice contained in the DraftPool (with an unique INDEX)
     */
    private void sendDraftPoolDiceList() throws IllegalActionException {
        Die die;
        ArrayList<Die> draftedDice= (ArrayList<Die>) user.getGame().getDraftedDice(false);

        outSocket.print("LIST draftpool");
        for (int i=0;i<draftedDice.size();i++){
            die=draftedDice.get(i);
            outSocket.print(" "+i+","+die.getColor().toString()+","+die.getShade().toString());
        }
        outSocket.println("");
        outSocket.flush();

    }

    /**
     * Allows the user to select the die of the previous list received, then sends the relative list of allowed positions
     * @param index the index of the die previously received by the client
     */
    private void selectDie(int index) throws IllegalActionException {
        int placementIndex=0;
        Die die;
        die = user.getGame().selectDie(user,index);
        if(die!=null){
                ArrayList<Integer> placements= (ArrayList<Integer>) user.getGame().getUserSchemaCard(user,true).listPossiblePlacements(die);
                outSocket.print("LIST placements");
                for (Integer p:placements){
                    outSocket.print(" "+placementIndex+","+p);
                    placementIndex++;
                }
                outSocket.println("");
                outSocket.flush();
        }
    }

    /**
     * Allows the user to put the die contained in the previous list received, then sends an answer about the action
     * @param index the index of the die previously selected
     */
    private void putDie(int index) throws IllegalActionException {
        Boolean placed;
        placed=user.getGame().putDie(user,index);
        if(placed){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    private void useTool(int index) throws IllegalActionException {
        Boolean used;
        used=user.getGame().chooseTool(user,index);
        if(used){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    /**
     * Allows the Game model to discard a multiple-message command (for complex actions like putDie(), ToolCard usages)
     */
    private void discardAction() throws IllegalActionException {
        user.getGame().discard();
        outSocket.println("DISCARD ack");
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
            outSocket.println("STATUS check");
            outSocket.flush();
            //debug
            //System.out.println(inSocket.isEmpty());
            while(inSocket.isEmpty()){
                Thread.sleep(50);
            }
            if(Validator.isValid(inSocket.readln(),result) ){
                if(Validator.checkAckParams(inSocket.readln(),result)&& result.get(1).equals("status")){
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
