package it.polimi.ingsw;
import java.io.*;
import java.net.Socket;

/**
 *This class runs as a thread launched by the MasterServer and opens a ServerSocket that keeps listening
 * for incoming requests of socket connections. Once it opens a socket, it promptly launches a SocketConn passing it the
 * newly created socket
 */
public class SocketAcceptor extends Thread {
    Socket socket;

    public SocketAcceptor(Socket socket){
        this.socket=socket;
    }

    /**
     * The thread starts an infinite loop upon it receives a valid credentials or new user's data
     */
    @Override
    public void run(){
        BufferedReader inSocket=null;
        PrintWriter outSocket=null;
        String comand = "";
        Boolean connected = false;
        MasterServer master=MasterServer.getMasterServer();

        try {
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (connected==false){
                comand = inSocket.readLine();
                System.out.println(comand);
                String params[]= comand.split(" ");
                if (params.length==3 && params[0].equals("LOGIN") ){
                    connected=master.login(params[1],params[2]);
                    if (connected){
                        outSocket.println("LOGIN ok");

                        //Setting Socket specific parameters
                        User user = master.getUser(params[1]);
                        user.setConnectionMode(ConnectionMode.SOCKET);
                        user.setServerConn(new SocketConn(socket));
                    }else{
                        outSocket.println("LOGIN ko");
                    }
                    outSocket.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
