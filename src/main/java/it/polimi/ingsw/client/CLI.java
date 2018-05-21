package it.polimi.ingsw.client;

import it.polimi.ingsw.client.connection.ClientConn;
import it.polimi.ingsw.client.connection.SocketClient;

import java.io.*;

public class CLI {
    ClientConn clientConn;
    boolean logged;
    private BufferedReader inKeyboard;
    private PrintWriter outCli;

    public CLI(ConnectionMode connectionMode,String address,int port){
        this.logged=false;
        if (connectionMode==ConnectionMode.SOCKET){
            clientConn = new SocketClient(this,address,port);
            ((SocketClient) clientConn).start();
        }
        inKeyboard = new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    public void run(){
        clientConn.waitGreeting();
        outCli.println("Server Connected!");
        login();
        while(1==1){

        }
    }


    private void login()
    {
        String username;
        String password;
        try {
            while(!logged)
            {
                outCli.println("USERNAME: ");
                username=inKeyboard.readLine();
                outCli.println("PASSWORD:");
                password = inKeyboard.readLine();

                clientConn.login(username,password);

                if(logged)
                    outCli.println("Login effettuato correttamente");
                else
                    outCli.println("Login fallito");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void updateLogin(boolean logged){
        this.logged=logged;
    }

    public void updateConnection(){
        outCli.println("Connection established!");
        outCli.flush();
    }

}
