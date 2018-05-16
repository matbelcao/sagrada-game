package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.UserStatus;

import java.io.*;
import java.net.Socket;

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
        this.user=user;
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
        boolean quit = false;
        while(!quit){
            try {
                command = inSocket.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            quit = execute(command);
        }
        try {
            //Broken connection / quitting managing
            UserStatus previousStatus=user.getStatus();
            socket.close();
            user.setStatus(UserStatus.DISCONNECTED);
            if(previousStatus==UserStatus.QUEUED){
                MasterServer.getMasterServer().cleanDisconnected(user,previousStatus);
            }else{
                //Game-class specific cases
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method provides the socket messages interpretation logic
     * @param command the socket's message recived
     * @return true if the connection has to be closed
     */
    private boolean execute(String command){
        try{
            outSocket.println("The command was " + command);
            outSocket.flush();
            String temp=command.replaceFirst(" ", ":");

            System.out.println(temp);
            String commandList[]= temp.split(":");

            if(commandList[0].equals("QUIT")){
                return true;
            }
            if(commandList[0].equals("GET")){
                //get(command[1]);
            }
            return false;
        }catch(NullPointerException e){
            return true;
        }

    }

    /**
     * Sends the "LOBBY n" update message to the user
     * @param n number of players in the lobby
     */
    public void lobbyUpdate(int n){
        outSocket.println("LOBBY "+n);
        outSocket.flush();
    }

    /**
     * Sends the "GAME start n id" message to the user
     * @param n the number of connected players
     * @param id the assigned id of the specific user
     */
    public void gameStart(int n,int id){
        outSocket.println("GAME start "+n+" "+id);
        outSocket.flush();
    }

    //Da rivedere!!!!!
    public void statusUpdate (String event){

    }

}
