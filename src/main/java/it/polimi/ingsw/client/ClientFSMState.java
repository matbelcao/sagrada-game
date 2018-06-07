package it.polimi.ingsw.client;

public enum ClientFSMState {

    NOT_MY_TURN{
        public ClientFSMState nextState(boolean isMyTurn, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return NOT_MY_TURN; }
            if(endTurn){ return NOT_MY_TURN; }
            if(isMyTurn){
                return MAIN;
            }
            return NOT_MY_TURN;
        }
    },

    MAIN{
        public ClientFSMState nextState(boolean enabledTool, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(enabledTool){
                toolEnabled=true;
            }
            return GET_DICE_LIST;

        }
    },
    GET_DICE_LIST{
        public ClientFSMState nextState(boolean isListEmpty, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(isListEmpty){
                if(toolEnabled){
                    //tool is useless
                    toolEnabled=false;
                }
                return MAIN;
            }
            return SELECT_DIE;
        }
    },
    SELECT_DIE{
        public ClientFSMState nextState(boolean isPlaceDie, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(isPlaceDie){
                return LIST_PLACEMENTS;
            }
            if(toolEnabled){
                return TOOL_CAN_CONTINUE;
            }
            return MAIN;
        }
    },
    LIST_PLACEMENTS{
        public ClientFSMState nextState(boolean placedDie, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(discard){
                return GET_DICE_LIST;
            }
            if(toolEnabled){
                return TOOL_CAN_CONTINUE;
            }

            if(placedDie){
                return MAIN;
            }
            //this should never happen
            return LIST_PLACEMENTS;
        }
    },

    TOOL_CAN_CONTINUE{
        public ClientFSMState nextState(boolean canContinue, boolean exit, boolean endTurn, boolean discard){
            if(exit){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(canContinue){
                return GET_DICE_LIST;
            }

            //this should never happen
            return MAIN;
        }
    };


    private static boolean toolEnabled;
}
