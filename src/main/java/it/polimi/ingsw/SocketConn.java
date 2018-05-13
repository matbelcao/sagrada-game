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


    private boolean execute(String comand){
        outSocket.println("The comand was "+comand);
        String temp=comand.replaceFirst(" ", ":");

        System.out.println(temp);

        String command[]= temp.split(":");

        if(command[0].equals("STATUS")){
            if (command[1].equals("quit") && command.length==2){
                try {
                    socket.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(command[0].equals("GET")){
            //get(command[1]);
        }
        return false;

    }
}
