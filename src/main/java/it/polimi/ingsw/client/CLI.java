package it.polimi.ingsw.client;

import it.polimi.ingsw.common.connection.QueuedInReader;

import java.io.*;

public class CLI implements ClientUI{
    private QueuedInReader inKeyboard;
    //private BufferedReader inKeyboard;
    private PrintWriter outCli;
    private Client client;
    private UIMessages uimsg;


    public CLI(Client client,UILanguage lang) {

        this.uimsg=new UIMessages(lang);
        this.client = client;
        inKeyboard = new QueuedInReader(new BufferedReader(new InputStreamReader(System.in)));
        //inKeyboard=new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
    }

    public void loginProcedure() {
        String username;
        String password;
        //try {

            outCli.printf("%nUSERNAME: ");
            inKeyboard.add();
            username = inKeyboard.getln();

            //username=inKeyboard.readLine();

            outCli.printf("%nPASSWORD: ");
            inKeyboard.add();
            password = inKeyboard.getln();
            //password=inKeyboard.readLine();

            client.setPassword(password);
            client.setUsername(username);
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.printf(uimsg.getMessage("login-ok"), client.getUsername());
        } else {
            outCli.println(uimsg.getMessage("login-ko"));
        }
    }

    public void updateConnectionOk() { outCli.printf("%nConnection established correctly!%n"); }

    public void updateLobby(int numUsers){
        outCli.printf(uimsg.getMessage("lobby-update"),numUsers);
    }

    public void updateGameStart(int numUsers, int playerId){
        outCli.printf(uimsg.getMessage("game-start"),numUsers,playerId);
    }

    public void updateConnectionClosed()
    {
        outCli.println("Connection closed!");
    }

    public void updateConnectionBroken() {outCli.println("Connection broken!");
    }

    public void printmsg(String msg){
        outCli.println(msg);
    }

    @Override
    public String getCommand() {
        //String s="";
        if(inKeyboard.isEmpty()){inKeyboard.add();}
        return inKeyboard.getln();
        /*try {
            s=inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //return s;
    }
}


