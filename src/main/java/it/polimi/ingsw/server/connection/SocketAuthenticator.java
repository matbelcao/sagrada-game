package it.polimi.ingsw.server.connection;
import it.polimi.ingsw.ConnectionMode;
import it.polimi.ingsw.server.User;
import it.polimi.ingsw.server.Validator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

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
        Boolean logged = false;
        MasterServer master=MasterServer.getMasterServer();
        ArrayList<String> params = new ArrayList<>();
        try {
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert outSocket != null;
        outSocket.println("connection established!");
        outSocket.flush();
        try {
            while (!logged) {
                command = inSocket.readLine();
                if (Validator.checkLoginParams(command, params)) {
                    if (master.login(params.get(1), params.get(2))) {
                        outSocket.println("LOGIN ok");
                        logged = true;
                        //Setting Socket specific parameters
                        User user = master.getUser(params.get(1));
                        user.setConnectionMode(ConnectionMode.SOCKET);
                        user.setServerConn(new SocketConn(socket, user));
                        master.updateConnected(user);
                        master.printMessage("LOGGED "+params.get(1)+" "+params.get(2));
                    } else {
                        outSocket.println("LOGIN ko");
                        logged = false;
                    }
                    outSocket.flush();
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            try {

                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
