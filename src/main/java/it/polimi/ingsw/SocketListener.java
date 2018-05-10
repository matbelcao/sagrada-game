package it.polimi.ingsw;

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
