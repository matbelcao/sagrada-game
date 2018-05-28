package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the implementation of the SOCKET server-side connection methods
 */
public class SocketServer extends Thread implements ServerConn  {
    private Socket socket;
    private QueuedInReader inSocket;
    private PrintWriter outSocket;
    private AckQueue ack;
    private User user;

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

        start();
    }

    /**
     * This method runs a loop that manages the socket commands until the connection is closed
     */
    @Override
    public void run(){
        String command = "";
        ArrayList<String> result= new ArrayList<>();
        boolean playing = true;
        while(playing){
            try {
                try {
                    inSocket.add();
                } catch (Exception e) {
                    user.disconnect();
                }

                if(!inSocket.isEmpty()){
                    playing=execute(inSocket.getln());
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
    private boolean execute(String command) {
        ArrayList<String> parsedResult = new ArrayList<>();

        if(Validator.isValid(command, parsedResult)) {
            if(Validator.checkQuitParams(command,parsedResult)) {
                user.quit();
                return false;
            }
            if(Validator.checkChooseParams(command,parsedResult)){
                switch(parsedResult.get(1)){
                    case "schema":
                        chooseSchema(Integer.parseInt(parsedResult.get(2)));
                        break;
                    case "die_placement":
                        putDie(Integer.parseInt(parsedResult.get(2)));
                        break;
                    case"tool":
                        break;
                }
                return true;
            }
            if(Validator.checkGetParams(command,parsedResult)){
                switch (parsedResult.get(1)){
                    case "schema":
                        if(parsedResult.get(2).equals("draft")){
                           draftSchemaCards();
                        }else{
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
                return true;
            }
            if(Validator.checkGetDiceListParams(command,parsedResult)){
                switch(parsedResult.get(1)){
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
                return true;
            }
            if(Validator.checkSelectParams(command,parsedResult)){
                switch(parsedResult.get(1)){
                    case "die":
                        selectDie(Integer.parseInt(parsedResult.get(2)));
                        break;
                    case "modified_die":
                        //game.select
                        break;
                    case "tool":
                        //to implement
                        break;
                }
                return true;
            }
            if(Validator.checkDiscardParams(command,parsedResult)){
                discardAction();
                return true;
            }
            if(Validator.checkAckParams(command,parsedResult)){
                switch(parsedResult.get(1)){
                    case "status":
                        return true;
                }
            }
        }else{
            outSocket.println("INVALID message");
            outSocket.flush();
        }
        return true;
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
    private void draftSchemaCards(){
        Cell cell;
        Game game= user.getGame();
        ArrayList<SchemaCard> schemas=(ArrayList<SchemaCard>) game.getSchemaCards(user);

        for(SchemaCard s: schemas){
            outSocket.print("SEND schema");
            for (int row=0; row < SchemaCard.NUM_ROWS ; row++) {
                for (int column=0; column < SchemaCard.NUM_COLS ;column++){
                    cell=s.getCell(row,column);
                    if(cell.hasConstraint()) {
                        outSocket.print(" C,"+row+","+column+","+cell.getConstraint().toString());
                    }
                    if(cell.hasDie()){
                        outSocket.print(" D,"+row+","+column+","+cell.getDie().getColor().toString()+","+cell.getDie().getShade().toString());
                    }
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
    private void sendUserSchemaCard(int playerId){
        Cell cell;
        SchemaCard schemaCard = user.getGame().getUserSchemaCard(playerId,false);

        outSocket.print("SEND schema");
        for (int row=0; row < SchemaCard.NUM_ROWS ; row++) {
            for (int column=0; column < SchemaCard.NUM_COLS ;column++){
                cell=schemaCard.getCell(row,column);
                if(cell.hasConstraint()) {
                    outSocket.print(" C,"+row+","+column+","+cell.getConstraint().toString());
                }
                if(cell.hasDie()){
                    outSocket.print(" D,"+row+","+column+","+cell.getDie().getColor().toString()+","+cell.getDie().getShade().toString());
                }
            }
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the client a text containing the number of favor tokens passed as parameter
     * @param favorTokens the user's actual favor tokens
     */
    public void sendFavorTokens() {
        //Da aggiungere al protocollo!!!!!
        outSocket.println("SEND favor_tokens "+user.getGame().getFavorTokens(user));
        outSocket.flush();
    }


    /**
     * Sends the client a text description of the private objective card passed as a parameter
     * @param privObjectiveCard the private objective card to send
     */
    private void sendPrivateObjectiveCard(){
        PrivObjectiveCard privObjectiveCard=user.getGame().getPrivCard(user);

        outSocket.println("SEND priv "+privObjectiveCard.getId()+" "+privObjectiveCard.getName().replaceAll(" ", "_")+" "+privObjectiveCard.getDescription().replaceAll(" ", "_"));
        outSocket.flush();
    }

    /**
     * Sends the client a text description of the public objective card passed as a parameter
     * @param pubObjectiveCard the public objective card to send
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
     * @param toolCard the tool card to send
     */
    private void sendToolCards(){
        ArrayList<ToolCard> toolCards= (ArrayList<ToolCard>) user.getGame().getToolCards();

        for (ToolCard t:toolCards){
            outSocket.println("SEND tool "+t.getId()+" "+t.getName().replaceAll(" ", "_")+" "+t.getDescription().replaceAll(" ", "_"));
            outSocket.flush();
        }
    }

    /**
     * Sends the client a textual list of the dice in the DraftPool
     * @param draftedDice the DraftPool's dice list
     */
    private void sendDraftPoolDice(){
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
     * Sends the client a textual list of the dice in the RoundTrack (can be multiple die at the same index)
     * @param trackList the RoundTrack's dice list (index,die)
     */
    private void sendRoundTrackDice(){
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

    /**{
     *
     * Sends the client a text description of the users that are currently playing in the match
     * @param players the player's list to send
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
     * @param schema the schema card to get the Dice
     */
    private void sendSchemaDiceList() {
        int index=0;
        Die die;
        SchemaCard schema=user.getGame().getUserSchemaCard(user,false);
        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        outSocket.print("LIST schema");
        while(diceIterator.hasNext()){
            die=diceIterator.next().getDie();
            outSocket.print(" "+index+","+diceIterator.getRow()+","+diceIterator.getColumn()+","+die.getColor().toString()
                    +","+die.getShade().toString());
            index++;
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the client a text list of the dice contained in the RoundTrack (with an unique INDEX)
     * @param trackList the RoundTrack's dice list (index,die)
     */
    private void sendRoundTrackDiceList() {
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
     * @param draftedDice the DraftPool's dice list
     */
    private void sendDraftPoolDiceList() {
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
     * @param index
     */
    private void selectDie(int index){
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
     * @param index
     */
    private void putDie(int index){
        Boolean placed;
        placed=user.getGame().putDie(user,index);
        if(placed){
            outSocket.println("CHOICE ok");
        }else{
            outSocket.println("CHOICE ko");
        }
        outSocket.flush();
    }

    /**
     * Allows the Game model to discard a multiple-message command (for complex actions like putDie(), ToolCard usages)
     */
    private void discardAction(){
        user.getGame().discard();
        outSocket.println("DISCARD ack");
        outSocket.flush();
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
