package it.polimi.ingsw.client;

import it.polimi.ingsw.client.clientFSM.ClientFSMState;
import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;

import static it.polimi.ingsw.client.clientFSM.ClientFSMState.*;


public class ClientFSM {

    static final char QUIT = 'q';
    static final char END_TURN = 'e';
    static final char BACK = 'b';
    static final char DISCARD = 'd';

    private final Object lockState=new Object();
    private ClientFSMState state;
    private Client client;

    ClientFSM(Client client){
        state= ClientFSMState.CHOOSE_SCHEMA;
        this.client=client;
    }

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
                    if(!(state.equals(NOT_MY_TURN)||state.equals(CHOOSE_SCHEMA)||state.equals(GAME_ENDED))) {
                        client.getClientConn().endTurn();
                    }else{
                        client.getClientUI().showLastScreen();
                    }
                    break;
                case BACK:
                    if(!(state.equals(NOT_MY_TURN)||state.equals(CHOOSE_SCHEMA)||state.equals(GAME_ENDED)||state.equals(MAIN))){
                        client.getClientConn().back();
                    }else{
                        client.getClientUI().showLastScreen();
                    }
                    break;
                case DISCARD:
                    if (state.equals(CHOOSE_PLACEMENT)) {
                        client.getClientConn().discard();
                        client.getBoard().setLatestDiceList(client.getClientConn().getDiceList());
                    }else{
                        client.getClientUI().showLastScreen();
                    }
                    break;

                default:
                    client.getClientUI().showLastScreen();
                    return;
            }
            //state update

            state=state.nextState(false, option==BACK, option==END_TURN, option==DISCARD);
            lockState.notifyAll();
        }
        client.getBoard().notifyObservers();
    }

    void invalidInput() {
        client.getClientUI().showLastScreen();
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

            case NOT_MY_TURN:
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


            case TOOL_CAN_CONTINUE:
                break;
            case GAME_ENDED:
                manageNewGameChoice(index);
                break;
            default:
                client.getClientUI().showLastScreen();
                break;
        }
    }

    private void manageNewGameChoice(int index) {
        if(index==0){
            client.reset();
        }else{
            client.getClientUI().showLastScreen();
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
            client.getClientUI().showLastScreen();
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
                    client.getClientUI().showLastScreen();
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
                client.getClientUI().showLastScreen();
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
            client.getClientUI().showLastScreen();
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
            client.getClientUI().showLastScreen();
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

                if(client.getBoard().getLatestPlacementsList().isEmpty()){
                    if(isToolEnabled()) {
                        toolContinue();
                    }
                }
            } else {
                synchronized (lockState) {
                    state=CHOOSE_OPTION.nextState(false);
                }
                toolContinue();
            }
        }else {client.getClientUI().showLastScreen();}
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

        client.getUpdates();
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
                client.getUpdates();
            }
        }else{
            client.getClientUI().showLastScreen();
        }
    }

    public ClientFSMState getState(){
        return state;
    }

    void setNotMyTurn() {
        synchronized (lockState) {
            state = NOT_MY_TURN;

            lockState.notifyAll();
        }
        client.getBoard().stateChanged();
    }

    void setMyTurn(boolean isMyTurn) {
        synchronized (lockState) {
            assert (state.equals(NOT_MY_TURN));
            state = NOT_MY_TURN.nextState(isMyTurn);
            lockState.notifyAll();
        }
        client.getBoard().stateChanged();
    }

    void endGame() {
        synchronized (lockState) {
            state = GAME_ENDED;
            lockState.notifyAll();
        }
    }

    public void resetState() {
        if(client.getBoard().isInit()){
            synchronized (lockState) {
                state = CHOOSE_SCHEMA;
                lockState.notifyAll();
            }
        }else{
            synchronized (lockState) {
                state = NOT_MY_TURN;
                lockState.notifyAll();
            }
        }
    }
}
