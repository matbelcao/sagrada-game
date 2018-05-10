package it.polimi.ingsw;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener extends Thread {
    private int port;
    SocketListener(int port){
        this.port = port;
    }

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
}
