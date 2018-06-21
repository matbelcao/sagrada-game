package it.polimi.ingsw.client.view.clientUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.LightBoardEvents;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIView;
import it.polimi.ingsw.client.view.clientUI.uielements.CLIViewUtils;
import it.polimi.ingsw.client.view.clientUI.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientUI.uielements.enums.UIMsg;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.serializables.LightPrivObj;
import it.polimi.ingsw.common.serializables.LightSchemaCard;

import java.io.BufferedReader;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static it.polimi.ingsw.client.clientFSM.ClientFSMState.*;
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
    private final Object lockCli;

    /**
     * this constructs the object
     * @param client the client
     * @param lang the set language
     */
    public CLI(Client client,UILanguage lang) {
        this.lockCli=new Object();
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

    private void printToScreen(String stuff){
        synchronized (lockCli){
            console.printf(stuff);
            lockCli.notifyAll();
        }
    }

    /**
     * this cleans the screen and resets the cursor at the top of the page
     */
    private void resetScreen(){
        printToScreen(CLIViewUtils.resetScreenPosition());
    }

    /**
     * this method builds and shows the login screen
     */
    @Override
    public void showLoginScreen() {
        String username;
        char [] password;

            try {
                printToScreen(view.printLoginUsername());
                username = console.readLine().trim();

                printToScreen(view.printLoginPassword());
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
            printToScreen(String.format(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOGIN_OK)), client.getUsername()));
            view.setClientInfo(client.getConnMode(),client.getUsername());

        } else {
            printToScreen(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOGIN_KO)));
            showLoginScreen();
        }

    }

    /**
     * this method is used to show the last screen that was printed to the console
     */
    @Override
    public void showLastScreen() {
        printToScreen(view.printLatestScreen());
    }

    /**
     * this notifies the successful connection towards the server
     */
    @Override
    public synchronized void updateConnectionOk() {
        resetScreen();
        view.setLatestScreen(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.CONNECTION_OK)));
        printToScreen(view.printLatestScreen());
    }

    /**
     * this is the message that notifies the clients in the lobby that another player has logged in
     * @param numUsers the number of connected players at the moment
     */
    @Override
    public synchronized void updateLobby(int numUsers){
        resetScreen();
        view .setLatestScreen(String.format(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOBBY_UPDATE)),numUsers));
        printToScreen(view.printLatestScreen());
    }

    /**
     * this method notifies the start of the game
     * @param numUsers the number of participants
     * @param playerId the id of the user
     */
    @Override
    public synchronized void updateGameStart(int numUsers, int playerId){

        resetScreen();
        view.setLatestScreen(String.format(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.GAME_START)),numUsers,playerId));
        printToScreen(view.printLatestScreen());
        view.setMatchInfo(playerId,numUsers);

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
        printToScreen(view.printSchemaChoiceView());
    }

    /**
     * this method updates the view to the latest changes in the lightboard and/or state of the client
     * @param board the board
     */
    private synchronized void updateBoard(LightBoard board) {

        if (board == null) {
            throw new IllegalArgumentException();
        }
        List<Integer> changes=board.getChanges();
        for(Integer change : changes) {
            switch (change) {
                case LightBoardEvents.PrivObj:
                    view.updatePrivObj(board.getPrivObj());
                    break;
                case LightBoardEvents.Tools:
                    view.updateTools(board.getTools());
                    break;
                case  LightBoardEvents.PubObjs:
                    view.updateObjectives(board.getPubObjs(), board.getPrivObj());
                    break;
                case LightBoardEvents.DraftPool:
                    view.updateDraftPool(board.getDraftPool());
                    break;
                case LightBoardEvents.RoundTrack:
                    view.updateRoundTrack(board.getRoundTrack());
                    break;
                case LightBoardEvents.Status:
                    view.setMatchInfo(board.getMyPlayerId(),board.getNumPlayers());
                case LightBoardEvents.Schema:
                    for (int i = 0; i < board.getNumPlayers(); i++) {
                        view.updateSchema(board.getPlayerById(i));
                    }
                    break;
                case LightBoardEvents.NowPlaying:
                    view.updateRoundTurn(board.getRoundNumber(), board.getIsFirstTurn(), board.getNowPlaying());
                    break;
                default:
                    break;
            }
        }
        switch (client.getTurnState()) {
            case CHOOSE_SCHEMA:
                break;

            case NOT_MY_TURN:
                view.updateMenuNotMyTurn(board.getPlayerById(board.getNowPlaying()).getUsername());
                printToScreen(view.printMainView(NOT_MY_TURN));
                break;
            case MAIN:
                view.updateMenuMain();
                printToScreen(view.printMainView(MAIN));
                break;
            case SELECT_DIE:
                if (board.getLatestDiceList().get(0).getPlace().equals(Place.ROUNDTRACK) &&
                        board.getLatestOptionsList().get(0).equals(Actions.SWAP)) {
                    board.getLatestDiceList().add(0, board.getLatestSelectedDie());
                }
                view.updateMenuDiceList(board.getLatestDiceList());
                printToScreen(view.printMainView(SELECT_DIE));
                break;
            case CHOOSE_OPTION:
                break;
            case CHOOSE_TOOL:
                view.updateMenuListTools(board.getTools());
                printToScreen(view.printMainView(CHOOSE_TOOL));
                break;
            case CHOOSE_PLACEMENT:
                view.updateMenuListPlacements(board.getLatestPlacementsList(), board.getLatestSelectedDie().getContent());
                printToScreen(view.printMainView(CHOOSE_PLACEMENT));
                break;
            case TOOL_CAN_CONTINUE:
                break;
            case GAME_ENDED:
                view.updateGameRanking(board.sortFinalPositions());
                printToScreen(view.printGameEndScreen());
                break;
        }

        board.clearChanges();


    }


    /**
     * this notifies the client that wanted to quit that his connection has been closed and he has successfully quit
     */
    @Override
    public void updateConnectionClosed() {
        printToScreen(uimsg.getMessage(UIMsg.CLOSED_CONNECTION));
    }

    /**
     * this notifies an error in the connection towards the server
     */
    @Override
    public void updateConnectionBroken() {
        printToScreen(uimsg.getMessage(UIMsg.BROKEN_CONNECTION));

    }


    @Override
    public void showWaitingForGameStartScreen() {
        resetScreen();

        String msg=String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.WAIT_FOR_GAME_START));
        view.setLatestScreen(msg);

        printToScreen(msg);


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