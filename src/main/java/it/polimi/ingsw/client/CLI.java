package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.QueuedInReader;

import java.io.*;

public class CLI implements ClientUI{
    private final CLIView view;
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
        this.view=new CLIView(lang);
    }

    public void loginProcedure() {
        String username;
        String password;
        try {

            outCli.printf("%n%nUSERNAME: ");
            inKeyboard.add();
            username = inKeyboard.getln();

            //username=inKeyboard.readLine();

            outCli.printf("%nPASSWORD: ");
            inKeyboard.add();
            password = inKeyboard.getln();
            //password=inKeyboard.readLine();

            client.setPassword(password);
            client.setUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            client.disconnect();
        }
    }


    public void updateLogin(boolean logged) {
        if (logged) {
            outCli.printf(String.format("%n%s", uimsg.getMessage("login-ok")), client.getUsername());
        } else {
            outCli.printf(String.format("%n%s", uimsg.getMessage("login-ko")));
        }
    }

    public void updateConnectionOk() { outCli.printf(String.format("%n%s", uimsg.getMessage("connection-ok"))); }

    public void updateLobby(int numUsers){
        outCli.printf(String.format("%n%s", uimsg.getMessage("lobby-update")),numUsers);
    }

    public void updateGameStart(int numUsers, int playerId){
        outCli.printf(String.format("%n%s", uimsg.getMessage("game-start")),numUsers,playerId);
        this.view.setMatchInfo(client.getPlayerId(),client.getBoard().getNumPlayers());
    }

    public void updateGameRoundStart(int numRound){
        outCli.printf(String.format("%n%s", uimsg.getMessage("round")),numRound);
    }

    public void updateGameTurnStart(int turnNumber,boolean isYourTurn){
        outCli.printf(String.format("%n%s", uimsg.getMessage("turn")),turnNumber);
        outCli.flush();
        if(isYourTurn){
            outCli.printf(String.format("%n%s%n", uimsg.getMessage("yourTurn")));
            outCli.flush();
        }
        //outCli.printf(view.printMainView());
    }


    @Override
    public void updateStatusMessage(String statusChange, int playerid) {

    }


    public void updateConnectionClosed()
    {
        outCli.println("Connection closed!");
    }

    public void updateConnectionBroken() { outCli.println("Connection broken!");
    }

    public void printmsg(String msg){
        outCli.println(msg);
    }

        @Override
    public String getCommand() {
        //String s="";
        if(inKeyboard.isEmpty()) {
            inKeyboard.add();
        }

        return inKeyboard.getln();
        /*try {
            s=inKeyboard.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //return s;
    }
}


