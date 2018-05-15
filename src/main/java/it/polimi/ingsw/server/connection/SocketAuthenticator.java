package it.polimi.ingsw.server.connection;
import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.server.User;

import java.io.*;
import java.net.Socket;

/**
 *This class runs as a thread launched by the MasterServer and opens a ServerSocket that keeps listening
 * for incoming requests of socket connections. Once it opens a socket, it promptly launches a SocketConn passing it the
 * newly created socket
 */
public class SocketAuthenticator extends Thread {
    Socket socket;

    public SocketAuthenticator(Socket socket){
        this.socket=socket;
    }

    /**
     * The thread starts an infinite loop until it receives a valid credentials or new user's data
     */
    @Override
    public void run(){
        BufferedReader inSocket=null;
        PrintWriter outSocket=null;
        String command = "";
        Boolean connected = false;
        MasterServer master=MasterServer.getMasterServer();

        try {
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outSocket.println("connection established!");
            outSocket.flush();
            while (!connected){
                command = inSocket.readLine();
                System.out.println(command);
                String params[]= command.split(" ");
                if (params.length==3 && params[0].equals("LOGIN") ){

                    if (master.login(params[1],params[2])){
                        outSocket.println("LOGIN ok");
                        connected=true;

                        //Setting Socket specific parameters
                        User user = master.getUser(params[1]);
                        user.setConnectionMode(ConnectionMode.SOCKET);
                        user.setServerConn(new SocketConn(socket,user));
                    }else{
                        outSocket.println("LOGIN ko");
                        connected=false;
                    }
                    outSocket.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
