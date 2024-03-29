package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;

import static it.polimi.ingsw.client.controller.ClientFSMState.*;

/**
 * this class implements a simple fsm for the client aimed at optimizing  and simplyfying the experience
 * of interacting with the server during the game
 */

public class ClientFSM {

    public static final char QUIT = 'q';
    public static final char END_TURN = 'e';
    public static final char BACK = 'b';
    public static final char DISCARD = 'd';
    public static final char NEW_GAME = 'n';

    private final Object lockState=new Object();
    private ClientFSMState state;
    private Client client;

    ClientFSM(Client client){
        state = ClientFSMState.CHOOSE_SCHEMA;
        this.client=client;
    }

    /**
     * @return true if the fsm is in a state that actually means something, based on the status of the client
     */
    boolean isAlive(){
        return client.isLogged();
    }

    /**
     * this method manages a command that is not an index but some letter, those letters have a particular meaning and can
     * trigger different actions
     * @param option the command received from the ui (SINGLE_CHAR)
     */
    void evolve(char option){
        synchronized (lockState) {
            switch (option) {

                case QUIT:
                    client.quit();

                    break;
                case END_TURN:
                    if(!(state.equals(NOT_MY_TURN)||state.equals(CHOOSE_SCHEMA)||state.equals(SCHEMA_CHOSEN)||state.equals(GAME_ENDED))) {
                        endTurn();
                    }else{
                        invalidInput();
                    }
                    break;
                case BACK:
                    if(!(state.equals(NOT_MY_TURN)||state.equals(CHOOSE_SCHEMA)||state.equals(SCHEMA_CHOSEN)||state.equals(GAME_ENDED)||state.equals(MAIN))){
                        back();
                    }else{
                        invalidInput();
                    }
                    break;
                case DISCARD:
                    if (state.equals(CHOOSE_PLACEMENT)
                            && !client.getBoard().getLatestSelectedDie().getPlace().equals(Place.DICEBAG)) {
                        discard();
                    }else{
                        invalidInput();
                    }
                    break;
                case NEW_GAME:
                    if (state.equals(GAME_ENDED)) {

                        newGame();
                    }else{
                       invalidInput();
                    }
                    break;
                default:
                    invalidInput();
                    return;
            }

            lockState.notifyAll();
        }
        if(option != QUIT) {
            client.getBoard().notifyObservers();
        }
    }

    /**
     * prepares fsm and client for a new game
     */
    private void newGame() {
        state=GAME_ENDED.nextState(true);
        client.prepareForNewGame();
    }

    /**
     * sets new state and sends ent turn message
     */
    private void endTurn() {
        client.getClientConn().endTurn();
        state=state.nextState(false, false, true, false);
    }

    /**
     * sends back message and sets state accordingly
     */
    private void back() {
        client.getClientConn().back();
        state=state.nextState(false, true, false, false);
    }

    /**
     * sends discard message re-gets the dicelist and sets the proper state
     */
    private void discard() {
        client.getClientConn().discard();
        client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
        state=state.nextState(false, false, false, true);
    }

    /**
     * prints the latest valid screen

     */
    void invalidInput() {
        client.getClientUI().showLatestScreen();
    }

    /**
     * this manages the indexes received from the ui that have a different meaning and trigger different procedures
     * according to the state of the client
     * @param index index to be elaborated
     */
    void evolve(int index) {
        switch (state) {

            case CHOOSE_SCHEMA:
                chooseSchemaAction(index);
                break;

            case MAIN:
                mainChoiceAction(index);
                break;

            case CHOOSE_TOOL:
                chooseToolAction(index);
                break;

            case SELECT_DIE:
                selectDieAction(index);
                break;

            case CHOOSE_OPTION:
                chooseOptionAction(index);
                break;

            case CHOOSE_PLACEMENT:
                choosePlacementAction(index);
                break;

            default:
                invalidInput();
                break;
        }
    }


    /**
     * this allows the user to choose the schema to play with at the beginning of the match
     * @param index the index of the desired schema
     */
    private void chooseSchemaAction(int index) {
        if (client.getClientConn().choose(index)) {
            synchronized (lockState) {
                state=CHOOSE_SCHEMA.nextState(true);
                lockState.notifyAll();
            }
            client.getClientUI().showWaitingForGameStartScreen();
        } else {
            invalidInput();
        }
    }

    /**
     * this implements the choice of either placing a die or activating a tool
     * @param index the choice made by the player
     */
    private void mainChoiceAction(int index) {
        switch (index) {
            case 1:
                if(isPlacedDie()){
                   invalidInput();
                    return;
                }
                client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                synchronized (lockState) {
                    state=MAIN.nextState(false);
                    lockState.notifyAll();
                }
                break;
            case 0:

                synchronized (lockState) {
                    state=MAIN.nextState(true);
                    lockState.notifyAll();
                }
                client.getBoard().stateChanged();

                break;
            default:
                invalidInput();
        }
        client.getBoard().notifyObservers();
    }


    /**
     * this implements the choice of a tool to be enabled
     * @param index the index of said tool
     */
    private void chooseToolAction(int index) {
        if (index < LightBoard.NUM_TOOLS && index >= 0) {
            if (client.getClientConn().enableTool(index)) {
                synchronized (lockState) {
                    state=CHOOSE_TOOL.nextState(true);
                    lockState.notifyAll();
                }
                int myId=client.getBoard().getMyPlayerId();
                client.getBoard().updateFavorTokens(myId,client.getClientConn().getFavorTokens(myId));

                client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                if(client.getBoard().getLatestDiceList().isEmpty()){
                    synchronized (lockState) {
                        state=SELECT_DIE.nextState(true);
                        lockState.notifyAll();
                    }
                    toolContinue();
                }
            } else {
                synchronized (lockState) {
                    state=CHOOSE_TOOL.nextState(false);//back to MAIN
                    lockState.notifyAll();
                }
                client.getBoard().stateChanged();

            }
            client.getBoard().notifyObservers();
        } else {
            invalidInput();
        }
    }

    /**
     * this implements the actions needed after selecting a die (if the options received are one or less, this requires no
     * interaction with the user)
     * @param index the index of the selected die
     */
    private void selectDieAction(int index) {
        if (client.getBoard().getLatestDiceList().size() > index && index >= 0) {

            client.getBoard().setLatestSelectedDie( client.getBoard().getLatestDiceList().get(index));
            client.getBoard().setLatestOptionsList(client.getClientConn().select(index));

            if(client.getBoard().getLatestOptionsList().isEmpty()){
                synchronized (lockState) {
                    state=SELECT_DIE.nextState(true);
                    lockState.notifyAll();
                }
                client.getBoard().stateChanged();

            } else {
                singleOption();
            }
            client.getBoard().notifyObservers();
        } else {
            invalidInput();
        }
    }

    /**
     * this is called in case the options received sum to one and simply makes an automatic choice for that option
     */
    private void singleOption() {
        synchronized (lockState) {
            state=SELECT_DIE.nextState(false);

            chooseOptionAction(0);
        }
    }


    /**
     * this manages a choice by index of an option
     * @param index the index of the chosen option
     */
    private void chooseOptionAction(int index) {
        if(client.getClientConn().choose(index)) {

            if (client.getBoard().getLatestOptionsList().get(index).equals(Actions.PLACE_DIE)) {
                synchronized (lockState) {
                    state=CHOOSE_OPTION.nextState(true);
                    lockState.notifyAll();
                }

                client.getBoard().setLatestPlacementsList(client.getClientConn().getPlacementsList());

                if(client.getBoard().getLatestPlacementsList().isEmpty()
                    && client.getBoard().getLatestSelectedDie().getPlace().equals(Place.DICEBAG)
                    && isToolEnabled()) {

                    toolContinue();
                }
            } else {
                synchronized (lockState) {
                    state=CHOOSE_OPTION.nextState(false);
                }
                toolContinue();
            }
        }else {invalidInput();}
    }

    /**
     * this method manages the TOOL_CAN_CONTINUE state without requiring interaction with the user
     */
    private void toolContinue() {

        boolean canContinue = client.getClientConn().toolCanContinue();
        synchronized (lockState) {
            state=TOOL_CAN_CONTINUE.nextState(canContinue);
            lockState.notifyAll();
        }
        if(canContinue){
            client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
            if(client.getBoard().getLatestDiceList().isEmpty()) {
                synchronized (lockState) {
                    state = SELECT_DIE.nextState(true);
                    lockState.notifyAll();
                }
                client.getClientConn().back();
            }
        }

        client.getBoardUpdates();
    }



    /**
     * @return true iff the last dice list was from something other than the schema
     */
    private boolean isPlacedDieFromOutside(){
        return !client.getBoard().getLatestSelectedDie().getPlace().equals(Place.SCHEMA);
    }

    /**
     * this method implements the choice of a placement based on the passed index
     * @param index the chosen index
     */
    private void choosePlacementAction(int index) {

        if(client.getClientConn().choose(index)) {

            synchronized (lockState) {
                state=CHOOSE_PLACEMENT.nextState( isPlacedDieFromOutside());
                lockState.notifyAll();
            }
            if(state.equals(TOOL_CAN_CONTINUE)){
                toolContinue();
            }else {
                client.getBoardUpdates();
            }
        }else{
            invalidInput();
        }
    }

    /**
     * @return the state of the fsm
     */
    public ClientFSMState getState(){
        return state;
    }

    /**
     * sets the state to NOT_MY_TURN
     */
    void setNotMyTurn() {
        synchronized (lockState) {
            state = NOT_MY_TURN;

            lockState.notifyAll();
        }
        client.getBoard().stateChanged();
        client.getBoard().resetLatests();
    }

    /**
     * sets the state to MAIN or NOT_MY_TURN according to the param
     * @param isMyTurn true if it is so
     */
    void setMyTurn(boolean isMyTurn) {
        synchronized (lockState) {
            assert (state.equals(NOT_MY_TURN));
            state = NOT_MY_TURN.nextState(isMyTurn);
            lockState.notifyAll();
        }
        client.getBoard().stateChanged();
    }

    /**
     * sets the state to GAME_ENDED
     */
    void endGame() {
        synchronized (lockState) {
            state = GAME_ENDED;
            lockState.notifyAll();
        }
    }

    /**
     * sets the state to either CHOOSE_SCHEMA or NOT_MY_TURN according to the phase of the game
     * @param isInit true if the schemas still have to be chosen by the players
     */
    void resetState(boolean isInit) {
        if(isInit){
            synchronized (lockState) {
                state = CHOOSE_SCHEMA;
                lockState.notifyAll();
            }
        }else{
            setNotMyTurn();
        }
    }
}
