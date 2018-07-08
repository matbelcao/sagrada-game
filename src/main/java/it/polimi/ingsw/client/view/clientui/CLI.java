package it.polimi.ingsw.client.view.clientui;

import it.polimi.ingsw.client.controller.Client;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.client.view.LightBoardEvents;
import it.polimi.ingsw.client.view.clientui.uielements.CLIObjects;
import it.polimi.ingsw.client.view.clientui.uielements.CLIUtils;
import it.polimi.ingsw.client.view.clientui.uielements.UIMessages;
import it.polimi.ingsw.client.view.clientui.uielements.enums.UILanguage;
import it.polimi.ingsw.client.view.clientui.uielements.enums.UIMsg;
import it.polimi.ingsw.common.connection.Credentials;
import it.polimi.ingsw.common.connection.QueuedBufferedReader;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;

import java.io.BufferedReader;
import java.io.Console;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.client.controller.ClientFSMState.*;

/**
 * This is the class that implements the UI for the client as a command line interface
 */
public class CLI implements ClientUI {
    private static final String STRING_NEWLINE="%s%n";
    private static final String ERR_RETRIEVING_CONSOLE="ERR: error while retrieving console";
    private final CLIObjects cliObjs;
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
            Logger.getGlobal().log(Level.INFO,ERR_RETRIEVING_CONSOLE);
            System.exit(1);
        }

        this.uimsg=new UIMessages(lang);
        this.client = client;
        this.cliObjs =new CLIObjects(lang);

        resetScreen();
    }

    /**
     * this is a method that writes to the cli in a synchronized way
     * @param stuff what has to be printed
     */
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
        printToScreen(CLIUtils.resetScreenPosition());
    }

    /**
     * this method builds and shows the login screen
     */
    @Override
    public void showLoginScreen() {
        String username;
        char [] password;

            try {
                printToScreen(cliObjs.printLoginUsername());
                username = console.readLine().trim();

                printToScreen(cliObjs.printLoginPassword());
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
            cliObjs.setClientInfo(client.getConnMode(),client.getUsername());

        } else {
            printToScreen(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOGIN_KO)));
            showLoginScreen();
        }

    }

    /**
     * this method is used to show the last screen that was printed to the console
     */
    @Override
    public void showLatestScreen() {
        printToScreen(cliObjs.printLatestScreen());
    }

    /**
     * this notifies the successful connection towards the server
     */
    @Override
    public synchronized void updateConnectionOk() {
        resetScreen();
        cliObjs.setLatestScreen(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.CONNECTION_OK)));
        printToScreen(cliObjs.printLatestScreen());
    }

    /**
     * this is the message that notifies the clients in the lobby that another player has logged in
     * @param numUsers the number of connected players at the moment
     */
    @Override
    public synchronized void updateLobby(int numUsers){
        resetScreen();
        cliObjs.setLatestScreen(String.format(String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.LOBBY_UPDATE)),numUsers));
        printToScreen(cliObjs.printLatestScreen());
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

        updateCLIObjects(board, changes);

        switch (client.getFsmState()) {

            case CHOOSE_SCHEMA:
                cliObjs.updateDraftedSchemas(board.getDraftedSchemas());
                cliObjs.updatePrivObj(board.getPrivObj());
                printToScreen(cliObjs.printSchemaChoiceView());
                break;

            case NOT_MY_TURN:
                cliObjs.updateMenuNotMyTurn(board.getPlayerById(board.getNowPlaying()).getUsername());
                printToScreen(cliObjs.printMainView(NOT_MY_TURN));
                break;
            case MAIN:
                cliObjs.updateMenuMain();
                printToScreen(cliObjs.printMainView(MAIN));
                break;
            case SELECT_DIE:
                if (!board.getLatestDiceList().isEmpty()
                        && board.getLatestDiceList().get(0).getPlace().equals(Place.ROUNDTRACK)
                        && !board.getLatestOptionsList().isEmpty()
                        && board.getLatestOptionsList().get(0).equals(Actions.SWAP)) {
                    board.getLatestDiceList().add(0, board.getLatestSelectedDie());
                }
                cliObjs.updateMenuDiceList(board.getLatestDiceList());
                printToScreen(cliObjs.printMainView(SELECT_DIE));
                break;
            case CHOOSE_OPTION:
                break;
            case CHOOSE_TOOL:
                cliObjs.updateMenuListTools(board.getTools());
                printToScreen(cliObjs.printMainView(CHOOSE_TOOL));
                break;
            case CHOOSE_PLACEMENT:
                cliObjs.updateMenuListPlacements(board.getLatestPlacementsList(), board.getLatestSelectedDie().getContent());
                printToScreen(cliObjs.printMainView(CHOOSE_PLACEMENT,board.getLatestSelectedDie().getPlace()));
                break;
            case TOOL_CAN_CONTINUE:
                break;
            case GAME_ENDED:
                cliObjs.updateGameRanking(board);
                printToScreen(cliObjs.printGameEndScreen());
                break;
            default:
                break;
        }
    }

    /**
     * this method updates the cliObjects according to the changes detected by the lightboard
     * @param board the board
     * @param changes the list of changes
     */
    private void updateCLIObjects(LightBoard board, List<Integer> changes) {
        if(changes.contains(LightBoardEvents.MY_PLAYER_ID)){
            cliObjs.setMatchInfo(board.getMyPlayerId(),board.getNumPlayers());
        }

        if(changes.contains(LightBoardEvents.PRIV_OBJ))
            cliObjs.updatePrivObj(board.getPrivObj());

        if(changes.contains(LightBoardEvents.TOOLS))
            cliObjs.updateTools(board.getTools());

        if(changes.contains(LightBoardEvents.PUB_OBJ))
            cliObjs.updateObjectives(board.getPubObjs(), board.getPrivObj());

        if(needSchemasUpdate(changes)) {
            cliObjs.setMatchInfo(board.getMyPlayerId(), board.getNumPlayers());
            if (client.isPlayingTurns()) {
                for (int i = 0; i < board.getNumPlayers(); i++) {
                    cliObjs.updateSchema(board.getPlayerById(i));
                }
            }
        }

        if(changes.contains(LightBoardEvents.DRAFT_POOL))
            cliObjs.updateDraftPool(board.getDraftPool());

        if(changes.contains(LightBoardEvents.ROUND_NUMBER))
            cliObjs.updateRoundNumber(board.getRoundNumber());

        if(changes.contains(LightBoardEvents.IS_FIRST_TURN))
            cliObjs.updateIsFirstTurn(board.getIsFirstTurn());

        if(changes.contains(LightBoardEvents.ROUND_TRACK))
            cliObjs.updateRoundTrack(board.getRoundTrack());

        if(changes.contains(LightBoardEvents.NOW_PLAYING))
            cliObjs.updateNowPlaying(board.getNowPlaying());
    }

    /**
     * @param changes the list of changes in the lightboard
     * @return true if the windows need to be updated
     */
    private boolean needSchemasUpdate(List<Integer> changes) {
        return changes.contains(LightBoardEvents.STATUS)
                || changes.contains(LightBoardEvents.STATE_CHANGED)
                || changes.contains(LightBoardEvents.FAVOR_TOKENS)
                || changes.contains(LightBoardEvents.SCHEMA);
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
        System.exit(1);
    }


    /**
     * this tells the user to wait for the new game
     */
    @Override
    public void showWaitingForGameStartScreen() {
        resetScreen();

        String msg=String.format(STRING_NEWLINE, uimsg.getMessage(UIMsg.WAIT_FOR_GAME_START));
        cliObjs.setLatestScreen(msg);

        printToScreen(msg);


    }

    /**
     * @return the command queue of the ui
     */
    @Override
    public QueuedBufferedReader getCommandQueue() {
        return new QueuedBufferedReader(new BufferedReader(System.console().reader()));
    }


    @Override
    public void update(Observable o, Object arg) {
        updateBoard((LightBoard)o);
    }

}