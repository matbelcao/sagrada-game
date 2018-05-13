package it.polimi.ingsw;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *This class runs as a thread launched by the MasterServer and opens a ServerSocket that keeps listening
 * for incoming requests of socket connections. Once it opens a socket, it promptly launches a SocketConn passing it the
 * newly created socket
 */
public class SocketListener extends Thread {
    private int port;

    SocketListener(int port){
        this.port = port;
    }

    /**
     * The thread starts an infinite loop listening for new connections via socket
     */
    @Override
    public void run(){
        ServerSocket serverSocket=null;
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("\nServer waiting for socket connection on port " +  serverSocket.getLocalPort());
            // server infinite loop
            while(true)
            {
                Socket socket = serverSocket.accept();
                login(socket);
                new SocketConn(socket);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
            try
            {
                serverSocket.close();
            }
            catch(Exception ex)
            {}
        }
    }

    /**
     * The functions runs an infinite loop until it receives a correct username/password or new user's data
     * @param socket the client's socket
     */
    private void login(Socket socket){
        BufferedReader inSocket=null;
        PrintWriter outSocket=null;
        String comand = "";
        MasterServer master=MasterServer.getMasterServer();

        try {
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            try {
                comand = inSocket.readLine();
                System.out.println(comand);
                String params[]= comand.split(" ");
                if (params.length==3 && params[0].equals("LOGIN") ){
                    if (master.isIn(params[1]) && params[2].equals(master.getUser(params[1]).getPassword())){
                        outSocket.println("LOGIN ok");
                        return;
                    }
                    if (!master.isIn(params[1])){
                        User user = new User(params[1],params[2]);
                        master.addUser(user);
                        outSocket.println("LOGIN ok");
                        return;
                    }
                    outSocket.println("LOGIN ko");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
