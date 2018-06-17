package it.polimi.ingsw.client;

import it.polimi.ingsw.client.uielements.CLIView;
import it.polimi.ingsw.client.uielements.CLIViewUtils;
import it.polimi.ingsw.client.uielements.UILanguage;
import it.polimi.ingsw.client.uielements.UIMessages;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.LightPrivObj;
import it.polimi.ingsw.common.immutables.LightSchemaCard;

import java.io.BufferedReader;
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
                username = console.readLine().trim();

                console.printf(view.showLoginPassword());
                password = Credentials.hash(username, console.readPassword());
                synchronized (client.getLockCredentials()) {
                    client.setPassword(password);
                    client.setUsername(username);
                    client.getLockCredentials().notifyAll();
                }
            } catch (Exception e) {
                client.disconnect();
            }

    }


    @Override
    public void updateLogin(boolean logged) {
        resetScreen();
        if (logged) {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ok")), client.getUsername());
            view.setClientInfo(client.getConnMode(),client.getUsername());

        } else {
            console.printf(String.format("%s%n", uimsg.getMessage("login-ko")));
            showLoginScreen();
        }

    }

    @Override
    public void showLastScreen() {
        console.printf(view.printLastScreen());
    }

    @Override
    public void updateConnectionOk() {
        resetScreen();
        console.printf(String.format("%n%s", uimsg.getMessage("connection-ok")));

    }

    @Override
    public void updateLobby(int numUsers){

        console.printf(String.format("%s%n", uimsg.getMessage("lobby-update")),numUsers);

    }

    @Override
    public void updateGameStart(int numUsers, int playerId){

        resetScreen();
        console.printf(String.format("%s%n", uimsg.getMessage("game-start")),numUsers,playerId);
        this.view.setMatchInfo(playerId,client.getBoard().getNumPlayers());

    }

    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        view.updateDraftedSchemas(draftedSchemas);
        view.updatePrivObj(privObj);
        console.printf(view.printSchemaChoiceView());
    }

   private void updateBoard(LightBoard board) {
        if(board==null){ throw new IllegalArgumentException();}
        view.updateTools(board.getTools());
        view.updatePrivObj(board.getPrivObj());
        view.updateObjectives(board.getPubObjs(),board.getPrivObj());


        for(int i=0;i<board.getNumPlayers();i++){
            view.updateSchema(board.getPlayerById(i));
        }
        if(board.getNowPlaying()!=-1){ view.updateRoundTurn(board.getRoundNumber(),board.getIsFirstTurn(),board.getNowPlaying() );}
        view.updateRoundTrack(board.getRoundTrack());
        view.updateDraftPool(board.getDraftPool());

        switch (client.getTurnState()){
            case CHOOSE_SCHEMA:
                break;
            case NOT_MY_TURN:
                view.updateMenuNotMyTurn(board.getPlayerById(board.getNowPlaying()).getUsername());
                break;
            case MAIN:
                view.updateMenuMain();
                break;
            case SELECT_DIE:
                if(board.getLatestDiceList().get(0).getPlace().equals(Place.ROUNDTRACK) &&
                        board.getLatestOptionsList().get(0).equals(Commands.SWAP)){
                    board.getLatestDiceList().add(0,board.getLatestSelectedDie());
                }
                view.updateMenuDiceList(board.getLatestDiceList());
                break;
            case CHOOSE_OPTION:
                if(board.getLatestOptionsList().size()>1){
                    view.updateMenuListOptions(board.getLatestOptionsList());
                }
                break;
            case CHOOSE_TOOL:
                view.updateMenuListTools(board.getTools());
                break;
            case CHOOSE_PLACEMENT:
                view.updateMenuListPlacements(board.getLatestPlacementsList(),board.getLatestSelectedDie().getContent());
                break;
            case TOOL_CAN_CONTINUE:
                break;
        }

        synchronized (client.getLockUI()) {
            console.printf(view.printMainView(client.getTurnState()));
            client.getLockUI().notifyAll();
        }
    }


    @Override
    public void updateStatusMessage(String statusChange, int playerId) {

    }


    @Override
    public void updateConnectionClosed()
    {
        synchronized (client.getLockUI()) {
            console.printf("Connection closed!%n");
            client.getLockUI().notifyAll();
        }
    }

    @Override
    public void updateConnectionBroken() {
        synchronized (client.getLockUI()) {
            console.printf("Connection broken!%n");
            client.getLockUI().notifyAll();
        }

    }

    @Override
    public void showOptions(List<Commands> optionsList) {

    }

    @Override
    public void showWaitingForGameStartScreen() {
        resetScreen();

        String msg=String.format("%s%n", uimsg.getMessage("waiting-game-start"));
        view.setLastScreen(msg);
        synchronized (client.getLockUI()) {
            console.printf(msg);
            client.getLockUI().notifyAll();
        }

    }

    @Override
    public void showMainScreen(ClientFSMState turnState) {
        synchronized (client.getLockUI()) {
            console.printf(view.printMainView(turnState));
            client.getLockUI().notifyAll();
        }

    }

    @Override
    public QueuedBufferedReader getCommandQueue() {
        return new QueuedBufferedReader(new BufferedReader(System.console().reader()));
    }


    @Override
    public void update(Observable o, Object arg) {
        updateBoard((LightBoard)o);
    }
}