package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;
import it.polimi.ingsw.server.Validator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class is the implementation of the SOCKET server-side connection methods
 */
public class SocketConn extends Thread implements ServerConn  {
    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private User user;

    /**
     * This is the constructor of the class, it starts a thread linked to an open socket
     * @param socket the socket already open used to communicate with the client
     */
    SocketConn(Socket socket, User user){
        this.user = user;
        this.socket = socket;
        try {
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }

    /**
     * This method runs a loop that manages the socket commands until the connection is closed
     */
    @Override
    public void run(){
        String command = "";
        boolean playing = true;
        while(playing){
            try {
                command = inSocket.readLine();
                playing = execute(command);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                playing=true;
                user.disconnect();
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
        ArrayList<String> params = new ArrayList<>();

        if (Validator.isValid(command, params)) {

            switch (params.get(0)) {
                case "QUIT":
                    user.quit();
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * Sends the "LOBBY n" update message to the user
     * @param n number of players in the lobby
     */
    public void notifyLobbyUpdate(int n){
        outSocket.println("LOBBY "+n);
        outSocket.flush();
    }

    /**
     * Sends the "GAME start n id" message to the user
     * @param n the number of connected players
     * @param id the assigned id of the specific user
     */
    public void notifyGameStart(int n,int id){
        outSocket.println("GAME start "+n+" "+id);
        outSocket.flush();
    }

    //Da rivedere!!!!!
    public void notifyStatusUpdate (String event,int id){
        outSocket.println("STATUS "+event+" "+id);
        outSocket.flush();
    }

    @Override
    public boolean ping() {
        return false;
    }

}
