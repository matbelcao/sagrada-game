package it.polimi.ingsw.server.connection;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.ConnectionMode;
import it.polimi.ingsw.server.controller.MasterServer;
import it.polimi.ingsw.server.controller.Validator;
import it.polimi.ingsw.server.model.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 *This class runs as a thread launched by the MasterServer and opens a ServerSocket that keeps listening
 * for incoming requests of socket connections. Once it opens a socket, it promptly launches a SocketServer passing it the
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
        QueuedBufferedReader inSocket=null;
        PrintWriter outSocket=null;
        String command = null;
        Boolean logged = false;
        MasterServer master=MasterServer.getMasterServer();
        ArrayList<String> params = new ArrayList<>();
        try {
            inSocket = new QueuedBufferedReader(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        assert outSocket != null;
        outSocket.println("Connection established!");
        outSocket.flush();
        try {
            while (!logged) {

                inSocket.add();
                command = inSocket.readln();
                inSocket.pop();
                if (Validator.checkLoginParams(command, params)) {
                    if (master.login(params.get(1), params.get(2).toCharArray())) {
                        outSocket.println("LOGIN ok");
                        outSocket.flush();
                        logged = true;
                        //Setting Socket specific parameters
                        User user = master.getUser(params.get(1));
                        user.setConnectionMode(ConnectionMode.SOCKET);
                        user.setServerConn(new SocketServer(socket, user,inSocket,outSocket));
                        master.updateConnected(user);
                    } else {
                        outSocket.println("LOGIN ko");
                        outSocket.flush();
                        logged = false;
                    }
                }else{
                    outSocket.println("LOGIN ko");
                }
            }
        } catch ( Exception e) {
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
