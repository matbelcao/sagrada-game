package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIView;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIViewUtils;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
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

import static it.polimi.ingsw.common.enums.ErrMsg.ERR;
import static it.polimi.ingsw.common.enums.ErrMsg.ERROR_RETRIEVING_CONSOLE;

/**
 * This is the class that implements the UI for the client as a command line interface
 */
public class CLI implements ClientUI {
    private static final String STRING_NEWLINE="%s%n";
    private final CLIView view;
    private Console console;

    private Client client;
    private UIMessages uimsg;

    /**
     * this constructs the object
     * @param client the client
     * @param lang the set language
     */
    public CLI(Client client,UILanguage lang) {

        this.console=System.console();

        if (console == null) {
            System.err.println(ERR.toString()+ERROR_RETRIEVING_CONSOLE);
            System.exit(1);
        }

        this.uimsg=new UIMessages(lang);
        this.client = client;
        this.view=new CLIView(lang);

        resetScreen();
    }


    /**
     * this cleans the screen and resets the cursor at the top of the page
     */
    private void resetScreen(){
        console.printf(CLIViewUtils.resetScreenPosition());
    }

    /**
     * this method builds and shows the login screen
     */
    @Override
    public void showLoginScreen() {
        String username;
        char [] password;

            try {
                console.printf(view.showLoginUsername());
                username = console.readLine().trim();

                console.printf(view.showLoginPassword());
                password = Credentials.hash(username, console.readPassword());

                client.setPassword(password);
                client.setUsername(username);

            } catch (Exception e) {
                client.disconnect();
            }

    }


    /**
     * this updates the login screen according to the outcome of the latest attempt to login
     * @param logged the outcome of the login (true iff it went fine)
     */
    @Override
    public void updateLogin(boolean logged) {
        resetScreen();
        if (logged) {
            console.printf(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOGIN_OK)), client.getUsername());
            view.setClientInfo(client.getConnMode(),client.getUsername());

        } else {
            console.printf(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOGIN_KO)));
            showLoginScreen();
        }

    }

    /**
     * this method is used to show the last screen that was printed to the console
     */
    @Override
    public void showLastScreen() {
        console.printf(view.printLatestScreen());
    }

    /**
     * this notifies the successful connection towards the server
     */
    @Override
    public void updateConnectionOk() {
        resetScreen();
        console.printf(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.CONNECTION_OK)));

    }

    /**
     * this is the message that notifies the clients in the lobby that another player has logged in
     * @param numUsers the number of connected players at the moment
     */
    @Override
    public void updateLobby(int numUsers){
        resetScreen();
        console.printf(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOBBY_UPDATE)),numUsers);

    }

    /**
     * this method notifies the start of the game
     * @param numUsers the number of participants
     * @param playerId the id of the user
     */
    @Override
    public void updateGameStart(int numUsers, int playerId){

        resetScreen();
        console.printf(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.GAME_START)),numUsers,playerId);
        this.view.setMatchInfo(playerId,numUsers);

    }

    /**
     * this method is called right after the message that signals the start of a game and shows to the user elements
     * of the board and the drafted schemas to be able to make a choice of the schema based on them
     * @param draftedSchemas the schemas that have been drafted for this player
     * @param privObj the private objective of the player
     */
    @Override
    public void showDraftedSchemas(List<LightSchemaCard> draftedSchemas, LightPrivObj privObj) {
        view.updateDraftedSchemas(draftedSchemas);
        view.updatePrivObj(privObj);
        console.printf(view.printSchemaChoiceView());
    }

    /**
     * this method updates the view to the latest changes in the lightboard and/or state of the client
     * @param board the board
     */
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


    /**
     * this notifies the client that wanted to quit that his connection has been closed and he has successfully quit
     */
    @Override
    public void updateConnectionClosed()
    {
        synchronized (client.getLockUI()) {
            console.printf(uimsg.getMessage(UIMsg.CLOSED_CONNECTION));
            client.getLockUI().notifyAll();
        }
    }

    /**
     * this notifies an error in the connection towards the server
     */
    @Override
    public void updateConnectionBroken() {
        synchronized (client.getLockUI()) {
            console.printf(uimsg.getMessage(UIMsg.BROKEN_CONNECTION));
            client.getLockUI().notifyAll();
        }

    }


    @Override
    public void showWaitingForGameStartScreen() {
        resetScreen();

        String msg=String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.WAIT_FOR_GAME_START));
        view.setLatestScreen(msg);
        synchronized (client.getLockUI()) {
            console.printf(msg);
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