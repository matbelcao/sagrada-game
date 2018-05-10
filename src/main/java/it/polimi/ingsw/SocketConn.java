package it.polimi.ingsw;

import java.io.*;
import java.net.Socket;

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

    @Override
    public void run(){
        String comand = "";
        while(true){
            try {
                comand = inSocket.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            execute(comand);
    }
    }

    void execute(String comand){
        outSocket.println("The comand was "+comand);
        //add methods to parse the string
    }
}
