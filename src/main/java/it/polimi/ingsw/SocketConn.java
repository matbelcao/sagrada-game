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
        String temp=comand.replaceFirst(" ", ":");

        System.out.println(temp);

        String command[]= temp.split(":");

        if(command[0].equals("LOGIN")){
            String param[]=command[1].split(" ");
            //login(param[0],param[1]);
        }
        if(command[0].equals("SELECT")){
            //select(command[1]);
        }
        if(command[0].equals("GET")){
            //get(command[1]);
        }

    }
}
