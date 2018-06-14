package it.polimi.ingsw.client;

public enum ClientFSMState {

    CHOOSE_SCHEMA {//the game start message was just received and the client is choosing the schema among the drafted ones
        @Override
        public ClientFSMState nextState(boolean hasChosen, boolean back, boolean endTurn, boolean discard){


            if(hasChosen){
                return NOT_MY_TURN;
            }
            return CHOOSE_SCHEMA;
        }
        public ClientFSMState nextState(boolean hasChosen){
            return nextState(hasChosen,false,false,false);
        }
    },

    NOT_MY_TURN{//the match is being played, schemas were selected but it' is not this user's turn
        @Override
        public ClientFSMState nextState(boolean isMyTurn, boolean back, boolean endTurn, boolean discard){
            toolEnabled=false;
            placedDie=false;
            if(back){ return NOT_MY_TURN; }
            if(endTurn){ return NOT_MY_TURN; }
            if(isMyTurn){
                return MAIN;
            }
            return NOT_MY_TURN;
        }
        public ClientFSMState nextState(boolean isMyTurn){
            return nextState(isMyTurn,false,false,false);
        }
    },

    MAIN{ //it is the player's turn, he/she is choosing what to do (tool/placement)
        @Override
        public ClientFSMState nextState(boolean chooseTool, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(chooseTool){
                return CHOOSE_TOOL;
            }
            return SELECT_DIE;

        }
        public ClientFSMState nextState(boolean chooseTool){
            return nextState(chooseTool,false,false,false);
        }
    },

    CHOOSE_TOOL{
        @Override
        public ClientFSMState nextState(boolean enabledTool, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(!enabledTool){
                return MAIN;
            }
            toolEnabled=true;
            return SELECT_DIE;
        }

        public ClientFSMState nextState(boolean enabledTool){
            return nextState(enabledTool,false,false,false);
        }

    },
    SELECT_DIE {//the client decided what to do and sent a get_dice_list command, he is being presented with the dice he got from the server
        @Override
        public ClientFSMState nextState(boolean isListEmpty, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(isListEmpty && toolEnabled) {
                //tool is useless
                toolEnabled = false;

                return TOOL_CAN_CONTINUE;

            }
            return CHOOSE_OPTION;
        }
        public ClientFSMState nextState(boolean isListEmpty){
            return nextState(isListEmpty,false,false,false);
        }
    },
    CHOOSE_OPTION { // the user has selected a die and was sent a list of options
        @Override
        public ClientFSMState nextState(boolean isPlaceDie, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(isPlaceDie){
                return CHOOSE_PLACEMENT;
            }
            if(toolEnabled){
                return TOOL_CAN_CONTINUE;
            }
            return MAIN;
        }
        public ClientFSMState nextState(boolean isPlaceDie){
            return nextState(isPlaceDie,false,false,false);
        }
    },
    CHOOSE_PLACEMENT {// the client has received a list of placements
        @Override
        public ClientFSMState nextState(boolean placedDieFromOutside, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(discard){
                return SELECT_DIE;
            }

            if(placedDieFromOutside) {
                placedDie = true;

                if (toolEnabled) {
                    return TOOL_CAN_CONTINUE;
                }else{
                    return MAIN;
                }
            }

            //this should never happen
            return CHOOSE_PLACEMENT;
        }

        public ClientFSMState nextState(boolean placedDieFromOutside){
            return nextState(placedDieFromOutside,false,false,false);
        }
    },

    TOOL_CAN_CONTINUE{ //the client has requested and received the tool can continue response
        @Override
        public ClientFSMState nextState(boolean canContinue, boolean back, boolean endTurn, boolean discard){
            if(back){ return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(canContinue){
                return SELECT_DIE;
            }

            //this should never happen
            return MAIN;
        }
        public ClientFSMState nextState(boolean canContinue){
            return nextState(canContinue,false,false,false);
        }
    };

    private static boolean toolEnabled;
    private static boolean placedDie;

    public abstract ClientFSMState nextState(boolean stateSpecific, boolean back, boolean endTurn, boolean discard);
    public abstract ClientFSMState nextState(boolean stateSpecific);

    public static boolean isPlacedDie() {
        return placedDie;
    }
}
