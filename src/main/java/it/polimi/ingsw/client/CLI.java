package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.CLIViewUtils;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.*;

import java.io.Console;
import java.util.List;
import java.util.Observable;

public class CLI implements ClientUI {
    private final CLIView view;
    private Console console;

    private Client client;
    private UIMessages uimsg;

    public CLI(Client client,UILanguage lang) {

        this.console=System.console();

        if (console == null) {
            System.err.println("ERR: couldn't retrieve any console!");
            System.exit(1);
        }

        this.uimsg=new UIMessages(lang);
        this.client = client;
        this.view=new CLIView(lang);
        resetScreen();
    }






    private void resetScreen(){
        console.printf(CLIViewUtils.resetScreenPosition());
    }

    @Override
    public void showLoginScreen() {
        String username;
        char [] password;

        try {
            console.printf(view.showLoginUsername());
            username=console.readLine().trim();

            console.printf(view.showLoginPassword());
            password= Credentials.hash(username,console.readPassword());

            client.setPassword(password);
            client.setUsername(username);
        } catch (Exception e) {
            client.disconnect();
        }
    }


    @Override
    public void updateLogin(boolean logged) {
        resetScreen();
        if (logged) {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ok")), client.getUsername());

        } else {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ko")));
        }

    }

    public void showPlacementsList(List<Integer> placements,LightDie die){
        view.updateMenuListPlacements(placements,Place.SCHEMA,die);
    }

    @Override
    public void updateConnectionOk() {
        resetScreen();
        console.printf(String.format("%n%s", uimsg.getMessage("connection-ok")));

    }

    @Override
    public void updateLobby(int numUsers){

        view.setClientInfo(client.getConnMode(),client.getUsername());

        console.printf(String.format("%s%n", uimsg.getMessage("lobby-update")),numUsers);

    }

    @Override
    public void updateGameStart(int numUsers, int playerId){
        resetScreen();
        console.printf(String.format("%s%n", uimsg.getMessage("game-start")),numUsers,playerId);
        this.view.setMatchInfo(client.getPlayerId(),client.getBoard().getNumPlayers());
    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        view.updateDraftedSchemas(draftedSchemas);
        view.updatePrivObj(privObj);
        console.printf(view.printSchemaChoiceView());
    }

    @Override
    public void updateBoard(LightBoard board) {
        if(board==null){ throw new IllegalArgumentException();}
        view.updateTools(board.getTools());
        view.updatePrivObj(board.getPrivObj());
        view.updateObjectives(board.getPubObjs(),board.getPrivObj());
        view.updateMenuListDefault();

        for(int i=0;i<board.getNumPlayers();i++){
            view.updateSchema(board.getPlayerByIndex(i));
        }
        console.printf(view.printMainView(client.getTurnState()));
    }

    @Override
    public void updateDraftPool(List<LightDie> draftpool) {
        view.updateDraftPool(draftpool);
        console.printf(view.printMainView(client.getTurnState()));
    }

    @Override
    public void updateSchema(LightPlayer player) {
        view.updateSchema(player);
        console.printf(view.printMainView(client.getTurnState()));
    }

    @Override
    public void updateRoundTrack(List<List<LightDie>> roundtrack) {
        view.updateRoundTrack(roundtrack);
        console.printf(view.printMainView(client.getTurnState()));
    }

    @Override
    public void showRoundtrackDiceList(List<IndexedCellContent> roundtrack) {
        view.updateMenuDiceList(roundtrack,Place.ROUNDTRACK);
    }

    @Override
    public void showDraftPoolDiceList(List<IndexedCellContent> draftpool) {
        view.updateMenuDiceList(draftpool,Place.DRAFTPOOL);
    }

    @Override
    public void showSchemaDiceList(List<IndexedCellContent> schema) {
        view.updateMenuDiceList(schema,Place.SCHEMA);
    }

    @Override
    public void showTurnInitScreen() {

    }

    @Override
    public void showNotYourTurnScreen() {

    }



    @Override
    public void updateToolUsage(List<LightTool> tools) {

    }

    @Override
    public void showPlacementsList(List<Integer> placements, Place to, LightDie die) {
        view.updateMenuListPlacements(placements,to,die);
    }

    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

    }


    @Override
    public void updateConnectionClosed()
    {
        console.printf("Connection closed!%n");
    }

    @Override
    public void updateConnectionBroken() { console.printf("Connection broken!%n");
    }

    @Override
    public void printmsg(String msg){
        console.printf(msg);
    }

    @Override
    public String getCommand() {
        return console.readLine();
    }

    @Override
    public void showOptions(List<Commands> optionsList) {

    }


    @Override
    public void update(Observable o, Object arg) {
        LightBoard board= (LightBoard) o;
        view.updateTools(board.getTools());
        view.updatePrivObj(board.getPrivObj());
        view.updateObjectives(board.getPubObjs(),board.getPrivObj());
        view.updateNewRound(board.getRoundNumber());

        for(int i=0;i<board.getNumPlayers();i++){
            view.updateSchema(board.getPlayerByIndex(i));
        }
        console.printf(view.printMainView(client.getTurnState()));

        if(board.getNowPlaying()==board.getMyPlayerId()){
            showTurnInitScreen();
        } else{
            showNotYourTurnScreen();
        }

    }
}


