package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.server.model.Cell;
import it.polimi.ingsw.server.model.SchemaCard;

import java.io.*;
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
                inSocket.add();

                if(Validator.checkAckParams(inSocket.readln(),result)&& result.get(1).equals("status")){
                    inSocket.pop();
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

        if (Validator.isValid(command, parsedResult)) {
            switch (parsedResult.get(0)) {
                case "QUIT":
                    user.quit();
                    return false;
                case "CHOOSE":
                    if("schema".equals(parsedResult.get(1))){
                        user.getGame().chooseSchemaCard(user,Integer.parseInt(parsedResult.get(2)));
                    }
                    return true;
                default:
                    return true;
            }
        }
        return true;
    }

    /**
     * Sends the "LOBBY n" update message to the user
     * @param n number of players in the lobby
     */
    @Override
    public void notifyLobbyUpdate(int n){
        outSocket.println("LOBBY "+n);
        outSocket.flush();
    }

    /**
     * Sends the "GAME start n id" message to the user
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
     * Send the user a text description of the schema card passed as a parameter
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

    @Override
    public boolean ping() {
        List<String> result= new ArrayList<>();
        try{
            outSocket.println("STATUS check");
            outSocket.flush();
            System.out.println(inSocket.isEmpty());
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
