package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.QueuedInReader;
import it.polimi.ingsw.common.immutables.LightDie;
import it.polimi.ingsw.common.immutables.LightSchemaCard;
import it.polimi.ingsw.common.immutables.LightTool;

import java.io.*;
import java.util.List;
import java.util.Map;

public class CLI implements ClientUI{
    private final CLIView view;
    private QueuedInReader inKeyboard;
    //private BufferedReader inKeyboard;
    private PrintWriter outCli;
    private Client client;
    private UIMessages uimsg;

    public CLI(Client client,UILanguage lang) throws InstantiationException {

        this.uimsg=new UIMessages(lang);
        this.client = client;
        inKeyboard = new QueuedInReader(new BufferedReader(new InputStreamReader(System.in)));
        //inKeyboard=new BufferedReader(new InputStreamReader(System.in));
        outCli = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
        this.view=new CLIView(lang);
    }

    public void showLoginScreen() {
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
            outCli.printf(String.format("%s%n", uimsg.getMessage("login-ok")), client.getUsername());
        } else {
            outCli.printf(String.format("%s%n", uimsg.getMessage("login-ko")));
        }
    }

    @Override
    public void showLobby() {

    }

    public void updateConnectionOk() { outCli.printf(String.format("%n%s", uimsg.getMessage("connection-ok"))); }

    public void updateLobby(int numUsers){
        outCli.printf(String.format("%s%n", uimsg.getMessage("lobby-update")),numUsers);
    }

    public void updateGameStart(int numUsers, int playerId){
        outCli.printf(String.format("%s%n", uimsg.getMessage("game-start")),numUsers,playerId);
        this.view.setMatchInfo(client.getPlayerId(),client.getBoard().getNumPlayers());
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas) {
        
    }

    @Override
    public void updateBoard(LightBoard board) {

    }

    @Override
    public void updateDraftPool(Map<Integer, LightDie> draftpool) {

    }

    @Override
    public void updateSchema(LightSchemaCard schema, int playerId) {

    }

    @Override
    public void updateRoundTrack(List<List<LightDie>> roundtrack) {

    }

    @Override
    public void showRoundTrackWithCoordinates(List<List<LightDie>> roundtrack) {

    }

    
    public void updateRoundStart(int numRound,List<List<LightDie>> roundtrack){
        outCli.printf(String.format("%s%n", uimsg.getMessage("round")),numRound);
    }

    @Override
    public void updateTurnStart(int playerId, boolean isFirstTurn, Map<Integer,LightDie> draftpool) {

    }

    @Override
    public void updateToolUsage(List<LightTool> tools) {

    }

    // TODO: 31/05/2018
    /*public void updateTurnStart(int playerId, boolean isFirstTurn){
        outCli.printf(String.format("%s%n", uimsg.getMessage("turn")),playerId);
        outCli.flush();
        if(this.client.getPlayerId()==playerId){
            outCli.printf(String.format("%s%n", uimsg.getMessage("yourTurn")));
            outCli.flush();
        }
        //outCli.printf(view.printMainView());
    }

*/
    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

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


