package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.model.enums.ServerState;

public class ServerFSM {
    private boolean toolActive;
    private boolean diePlaced;
    private boolean isFirstTurn;

    Place placeFrom,placeTo;
    private ServerState curState;


    public ServerFSM(){
        curState=ServerState.INIT;
        toolActive=false;
        diePlaced=false;
        placeFrom=Place.DRAFTPOOL;
        placeTo=Place.SCHEMA;
    }

    /*public ServerState getCurState(){
        return curState;
    }*/

    public ServerState discard(){
        if (curState.equals(ServerState.CHOOSE_PLACEMENT)){
            curState=ServerState.GET_DICE_LIST;
        }
        return curState;
    }

    public ServerState exit(){
        if(!curState.equals(ServerState.INIT)){
            curState=ServerState.MAIN;
            toolActive=false;
            placeFrom=Place.DRAFTPOOL;
            placeTo=Place.SCHEMA;
        }
        return curState;
    }

    public ServerState newTurn(boolean isFirstTurn){
        curState=ServerState.MAIN;
        toolActive=false;
        diePlaced=false;
        this.isFirstTurn=isFirstTurn;
        placeFrom=Place.DRAFTPOOL;
        placeTo=Place.SCHEMA;
        return curState;
    }

    public ServerState nextState(Commands command){
        switch (curState){
            case INIT:
                curState=ServerState.MAIN;
                break;
            case MAIN:
                if(toolActive || !diePlaced){
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
                if (command.equals(Commands.PLACE_DIE)){
                    curState=ServerState.GET_PLACEMENTS;
                }else if(toolActive){
                    curState=ServerState.TOOL_CAN_CONTINUE;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            case GET_PLACEMENTS:
                curState=ServerState.CHOOSE_PLACEMENT ;
                break;
            case CHOOSE_PLACEMENT:
                if(toolActive){
                    curState=ServerState.TOOL_CAN_CONTINUE;
                }else{
                    curState=ServerState.MAIN;
                }
                break;
            case TOOL_CAN_CONTINUE:
                if(toolActive){
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

    public void setToolActive(boolean status){
        toolActive=status;
    }

    public boolean isToolActive(){
        return toolActive;
    }

    public void setDiePlaced(boolean status){
        if(!diePlaced){
            toolActive=status;
        }
    }

    public boolean isFirstTurn(){
        return isFirstTurn;
    }

    public void setPlaceTo(Place place){
        placeTo=place;
    }

    public Place getPlaceTo(){
        return placeTo;
    }

    public void setPlaceFrom(Place place){
        placeFrom=place;
    }

    public Place getPlaceFrom(){
        return placeFrom;
    }
}
