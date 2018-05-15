package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;

/**
 * This class is the implementation of the SOCKET server-side connection methods
 */
public class SocketConn extends Thread implements ServerConn  {
    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;

    /**
     * This is the constructor of the class, it starts a thread linked to an open socket
     * @param socket the socket already open used to communicate with the client
     */
    SocketConn(Socket socket){
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
        String comand = "";
        boolean quit = false;
        while(!quit){
            try {
                comand = inSocket.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            quit = execute(comand);
        }
    }

    /**
     * This method provides the socket messages interpretation logic
     * @param command the socket's message recived
     * @return true if the connection has to be closed
     */
    private boolean execute(String command){
        outSocket.println("The comand was "+command);
        outSocket.flush();
        String temp=command.replaceFirst(" ", ":");

        System.out.println(temp);

        String commandList[]= temp.split(":");

        if(commandList[0].equals("quit")){
            try {
                socket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(commandList[0].equals("GET")){
            //get(command[1]);
        }
        return false;

    }
}
