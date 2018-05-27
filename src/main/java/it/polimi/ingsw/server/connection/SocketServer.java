package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.immutables.LightCard;
import it.polimi.ingsw.common.immutables.LightPlayer;
import it.polimi.ingsw.common.immutables.LightTool;
import it.polimi.ingsw.server.model.*;

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
        Game game= user.getGame();

        if(Validator.isValid(command, parsedResult)) {
            if(Validator.checkQuitParams(command,parsedResult)) {
                user.quit();
                return false;
            }
            if(Validator.checkChooseParams(command,parsedResult)){
                if("schema".equals(parsedResult.get(1))){
                    game.chooseSchemaCard(user,Integer.parseInt(parsedResult.get(2)));
                }
            }
            if(Validator.checkGetParams(command,parsedResult)){
                switch (parsedResult.get(1)){
                    case "schema":
                        if(parsedResult.get(2).equals("draft")){
                           game.sendSchemaCards(user);
                        }else{
                           game.sendUserSchemaCard(user,Integer.parseInt(parsedResult.get(2)));
                        }
                        break;
                    case "favor_tokens":
                        game.sendFavorTokens(user);
                        break;
                    case "priv":
                        game.sendPrivCard(user);
                        break;
                    case "pub":
                        game.sendPubCards(user);
                        break;
                    case "tool":
                        game.sendToolCards(user);
                        break;
                    case "draftpool":
                        //to implement
                        break;
                    case "roundtrack":
                        //to implement
                        break;
                    case "players":
                        game.sendPlayers(user);
                        break;
                }
                return true;
            }
            if(Validator.checkAckParams(command,parsedResult)){
                switch(parsedResult.get(1)){
                    case "status":
                        return true;
                }
            }
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
     * Sends the client a text description of the schema card passed as a parameter
     * @param schemaCard the schema card to send
     */
    @Override
    public void notifySchema(SchemaCard schemaCard){
        Cell cell;

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

    public void notifyDraftPool(DraftPool pool){
        //
    }

    /**
     * Sends the client a text description of the tool card passed as a parameter
     * @param toolCard the tool card to send
     */
    @Override
    public void notifyToolCard(ToolCard toolCard){
            outSocket.println("SEND tool "+toolCard.getId()+" "+toolCard.getName().replaceAll(" ", "_")+" "+toolCard.getDescription().replaceAll(" ", "_"));
            outSocket.flush();
    }

    /**
     * Sends the client a text description of the public objective card passed as a parameter
     * @param pubObjectiveCard the public objective card to send
     */
    @Override
    public void notifyPublicObjective(PubObjectiveCard pubObjectiveCard){
        outSocket.println("SEND pub "+pubObjectiveCard.getId()+" "+pubObjectiveCard.getName().replaceAll(" ", "_")+" "+pubObjectiveCard.getDescription().replaceAll(" ", "_"));
        outSocket.flush();
    }

    /**
     * Sends the client a text description of the private objective card passed as a parameter
     * @param privObjectiveCard the private objective card to send
     */
    @Override
    public void notifyPrivateObjective(PrivObjectiveCard privObjectiveCard){
        outSocket.println("SEND priv "+privObjectiveCard.getId()+" "+privObjectiveCard.getName().replaceAll(" ", "_")+" "+privObjectiveCard.getDescription().replaceAll(" ", "_"));
        outSocket.flush();
    }

    /**
     * Sends the client a text description of the users that are currently playing in the match
     * @param players the player's list to send
     */
    @Override
    public void notifyPlayers(List<Player> players) {
        outSocket.print("SEND players");
        for (Player p:players){
            outSocket.print(" "+p.getGameId()+","+p.getUsername());
        }
        outSocket.println("");
        outSocket.flush();
    }

    /**
     * Sends the client a text containing the number of favor tokens passed as parameter
     * @param favorTokens the user's actual favor tokens
     */
    @Override
    public void notifyFavorTokens(int favorTokens) {
        //Da aggiungere al protocollo!!!!!
        outSocket.println("SEND favor_tokens "+favorTokens);
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
