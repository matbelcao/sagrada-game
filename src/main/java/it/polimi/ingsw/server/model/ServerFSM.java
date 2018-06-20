package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Actions;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.model.enums.ServerState;

/**
 * This class is responsible for managing the correct execution of the message flow that the server receives from the
 * user, and accordingly establishes the progress of the turn's state.
 */
public class ServerFSM {
    private boolean toolActive;
    private int numDiePlaced;
    private boolean isFirstTurn;
    private int userPlayingId;

    private Place placeFrom;
    private Place placeTo;
    private ServerState curState;

    /**
     * The class constructor. Resets all variables and sets the Game's status to INIT
     */
    public ServerFSM(){
        curState=ServerState.INIT;
        toolActive=false;
        numDiePlaced=0;
        placeFrom=Place.DRAFTPOOL;
        placeTo=Place.SCHEMA;
    }

    /**
     * Allows to cancel the selection of a die, and then to select another one without exiting the ToolCard usage
     * @return the new turn's state
     */
    public synchronized ServerState fsmDiscard(){
        if (curState.equals(ServerState.CHOOSE_PLACEMENT)){
            curState=ServerState.GET_DICE_LIST;
        }
        return curState;
    }

    /**
     * Allows to exit the ToolCard execution procedure, interrupting the regular execution flow
     * @return the new turn's state
     */
    public synchronized ServerState fsmExit(){
        if(!curState.equals(ServerState.INIT)){
            curState=ServerState.MAIN;
            toolActive=false;
            placeFrom=Place.DRAFTPOOL;
            placeTo=Place.SCHEMA;
        }
        return curState;
    }

    /**
     * Resets all state variables for the starting of a new turn.
     * @param isFirstTurn true if is the 1/2 player's turn
     * @return the new turn's state
     */
    public synchronized ServerState newTurn(int userPlayingId, boolean isFirstTurn){
        curState=ServerState.MAIN;
        toolActive=false;
        numDiePlaced=0;
        this.isFirstTurn=isFirstTurn;
        this.userPlayingId=userPlayingId;
        placeFrom=Place.DRAFTPOOL;
        placeTo=Place.SCHEMA;
        return curState;
    }

    /**
     * Sets the variables for the starting of a new ToolCard usage, if it's possible.
     * @param toolCard the toolcard selected by the player
     * @return the new turn's state
     */
    public synchronized ServerState newToolUsage(ToolCard toolCard){
        curState=ServerState.GET_DICE_LIST;
        toolActive=true;
        placeFrom=toolCard.getPlaceFrom();
        placeTo=toolCard.getPlaceTo();
        return curState;
    }

    /**
     * Sets the variables for the ending of a new ToolCard usage, if it was previously activated
     * @return the new turn's state
     */
    public synchronized ServerState endTool(){
        while(!curState.equals(ServerState.TOOL_CAN_CONTINUE)){
            curState=nextState(Actions.NONE);
        }
        return curState;
    }

    public synchronized ServerState endGame(){
        curState=ServerState.GAME_ENDED;
        return curState;
    }

    /**
     * Computes the next state, determining it using the class flags and the previous state of the FSM
     * @param command the action that the player wants to perform
     * @return the new turn's state
     */
    public synchronized ServerState nextState(Actions command){
        switch (curState){
            case INIT:
                curState=ServerState.MAIN;
                break;
            case MAIN:
                if(isToolActive() || !isDiePlaced()){
                    curState=ServerState.GET_DICE_LIST;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            case GET_DICE_LIST:
                curState=ServerState.SELECT;
                break;
            case SELECT:
                curState=ServerState.CHOOSE_OPTION;
                break;
            case CHOOSE_OPTION:
                if (command.equals(Actions.PLACE_DIE)){
                    curState=ServerState.GET_PLACEMENTS;
                }else if(isToolActive()){
                    curState=ServerState.TOOL_CAN_CONTINUE;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            case GET_PLACEMENTS:
                curState=ServerState.CHOOSE_PLACEMENT ;
                break;
            case CHOOSE_PLACEMENT:
                if(isToolActive()){
                    curState=ServerState.TOOL_CAN_CONTINUE;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            case TOOL_CAN_CONTINUE:
                if(isToolActive()){
                    curState=ServerState.GET_DICE_LIST;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            default:
                curState=ServerState.MAIN;
        }
        return curState;
    }


    /**
     * Returns if the ToolCard is enabled
     * @return true if the tool is enabled
     */
    public boolean isToolActive(){
        return toolActive;
    }

    /**
     * Returns if any dice have already been placed in the current turn
     * @return true if at least one die has been placed
     */
    public boolean isDiePlaced(){
        return numDiePlaced >= 1;
    }

    /**
     * Increase the number of dice placed on the turn
     */
    public void placeDie(){
        numDiePlaced++;
    }

    /**
     * Returns the number of dice placed on the turn
     * @return the number of dice
     */
    public int getNumDiePlaced(){
        return numDiePlaced;
    }

    /**
     * Returns true if it's the player's first turn
     * @return true if it's the player's first turn
     */
    public boolean isFirstTurn(){
        return isFirstTurn;
    }

    /**
     * Returns the position from which the dice selected by the player come from
     * @return the from position
     */
    public Place getPlaceFrom(){
        return placeFrom;
    }

    /**
     * Returns the position from which the dice must be placed
     * @return the destination position
     */
    public Place getPlaceTo(){
        return placeTo;
    }

    /**
     * Forces the position where the dice selected by the player come from (only for special cases of ToolCards)
     * @param place the new forced position
     */
    public void setPlaceFrom(Place place){
        placeFrom=place;
    }

    public synchronized ServerState getCurState(){
        return curState;
    }

    public int getUserPlayingId() {
        return userPlayingId;
    }
}
