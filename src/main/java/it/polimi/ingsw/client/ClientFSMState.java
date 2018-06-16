package it.polimi.ingsw.client;

import java.util.TooManyListenersException;

/**
 * this class contains the information needed to know which state the FSM of the client is in, and retrieve the next one
 * based on a series of conditions and the current state itself
 */
public enum ClientFSMState {
    /**
     * this is the state in which the player selects his schema for the match
     */
    CHOOSE_SCHEMA {//the game start message was just received and the client is choosing the schema among the drafted ones

        /**
         * this method is called when the user has chosen a certain schema or has tried to
         * @param hasChosen this tells if the choice of the schema was performed correctly
         * @param back this has no effect here
         * @param endTurn this has no effect here
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean hasChosen, boolean back, boolean endTurn, boolean discard){

            if(hasChosen){
                return NOT_MY_TURN;
            }
            return CHOOSE_SCHEMA;
        }
        /**
         * this method is called when the user has chosen a certain schema or has tried to
         * @param hasChosen this tells if the choice of the schema was performed correctly
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean hasChosen){
            return nextState(hasChosen,false,false,false);
        }
    },

    /**
     * this is the state the players are in when it is another player's turn
     */
    NOT_MY_TURN{//the match is being played, schemas were selected but it' is not this user's turn
        /**
         * this method is called whenever a new turn starts, the isMyTurn parameter is the only one that actually has an effect here
         * @param isMyTurn this tells if the turn about to begin is going to be played by this user
         * @param back this has no effect here
         * @param endTurn this has no effect here
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean isMyTurn, boolean back, boolean endTurn, boolean discard){
            toolEnabled=false;
            placedDie=false;

            if(isMyTurn){
                return MAIN;
            }
            return NOT_MY_TURN;
        }

        /**
         * this method is called whenever a new turn starts, the isMyTurn parameter is the only one that actually has an effect here
         * @param isMyTurn this tells if the turn about to begin is going to be played by this user
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean isMyTurn){
            return nextState(isMyTurn,false,false,false);
        }
    },

    /**
     * this is the root of every turn, when a player starts his turn he will be in this state where he can either choose to
     * use a tool or place a die
     */
    MAIN{ //it is the player's turn, he/she is choosing what to do (tool/placement)
        /**
         * this method is called whenever the client makes a valid choice between tool and die placement
         * @param chooseTool this is true when the choice was to use a tool
         * @param back if true brings the client back to this same state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean chooseTool, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;
                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(chooseTool){
                return CHOOSE_TOOL;
            }
            return SELECT_DIE;

        }

        /**
         * this method is called whenever the client makes a valid choice between tool and die placement
         * @param chooseTool this is true when the choice was to use a tool
         * @return the next state
         */
        @Override
        public  ClientFSMState nextState(boolean chooseTool){
            return nextState(chooseTool,false,false,false);
        }
    },

    /**
     * this is the state in which the user can try to enable a tool
     */
    CHOOSE_TOOL{
        /**
         * this is called when the user has made a choice for the tool he wants to enable
         * @param enabledTool this is true when the chosen tool was correctly enabled
         * @param back if true brings the client back to the MAIN state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean enabledTool, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;
                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(!enabledTool){
                return MAIN;
            }
            toolEnabled=true;
            return SELECT_DIE;
        }

        /**
         * this is called when the user has made a choice for the tool he wants to enable
         * @param enabledTool this is true when the chosen tool was correctly enabled
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean enabledTool){
            return nextState(enabledTool,false,false,false);
        }

    },
    /**
     * this is the state in which the client is enabled to choose a die among the ones he received following the last
     * GET_DICE_LIST he did
     */
    SELECT_DIE {
        /**
         * this is called when the user has made a choice for the die he wants to select
         * @param isListEmpty this is true if the list of options for the selected die is empty (it should never happen)
         * @param back if true brings the client back to the MAIN state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean isListEmpty, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;

                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }
            if(isListEmpty){
                return TOOL_CAN_CONTINUE;
            }
            return CHOOSE_OPTION;
        }
        /**
         * this is called when the user has made a choice for the die he wants to select
         * @param isListEmpty this is true if the list of options for the selected die is empty (it should never happen)
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean isListEmpty){
            return nextState(isListEmpty,false,false,false);
        }
    },
    /**
     * this is the state where the user is able to choose between the options he received for the previously selected die
     */
    CHOOSE_OPTION { // the user has selected a die and was sent a list of options
        /**
         * this is called when the user has made a choice for the option
         * @param isPlaceDie this is true if the chosen option was a PLACE_DIE
         * @param back if true brings the client back to the MAIN state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard this has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean isPlaceDie, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;
                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(isPlaceDie){
                return CHOOSE_PLACEMENT;
            }
            if(toolEnabled){
                return TOOL_CAN_CONTINUE;
            }
            return MAIN;
        }
        /**
         * this is called when the user has made a choice for the option
         * @param isPlaceDie this is true if the chosen option was a PLACE_DIE
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean isPlaceDie){
            return nextState(isPlaceDie,false,false,false);
        }
    },


    /**
     * in this state the user is able to choose a placement in the schema for the die he previously selected
     */
    CHOOSE_PLACEMENT {// the client has received a list of placements

        /**
         * this is called when the user has made a choice for the placement of the selected die in the schema
         * @param placedDieFromOutside this is true if the die that is being placed comes from outside the schema
         * @param back if true brings the client back to the MAIN state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard this allows the client to select another die to place
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean placedDieFromOutside, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;
                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(discard){
                return SELECT_DIE;
            }

            if (toolEnabled) {
                if(placedDieFromOutside) {
                    placedDie = true;
                }

                return TOOL_CAN_CONTINUE;
            }

            if(placedDieFromOutside) {
                placedDie = true;

                return MAIN;
            }


            return MAIN;
        }

        /**
         * this is called when the user has made a choice for the placement of the selected die in the schema
         * @param placedDieFromOutside this is true if the die that is being placed comes from outside the schema
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean placedDieFromOutside){
            return nextState(placedDieFromOutside,false,false,false);
        }
    },

    /**
     * in this state the client asks the server if it needs to/can  make more actions to finish the use of the tool
     */
    TOOL_CAN_CONTINUE{
        /**
         * this is called when the user needs to know if the tool requires more actions to be made
         * @param canContinue this is true if the tool requires more selections and choices from the user
         * @param back if true brings the client back to the MAIN state
         * @param endTurn if true ends the turn and brings the user back to the NOT_MY_TURN state
         * @param discard has no effect here
         * @return the next state
         */
        @Override
        public synchronized ClientFSMState nextState(boolean canContinue, boolean back, boolean endTurn, boolean discard){
            if(back){
                toolEnabled=false;
                return MAIN; }
            if(endTurn){ return NOT_MY_TURN; }

            if(canContinue){
                return SELECT_DIE;
            }
            toolEnabled=false;

            //this should never happen
            return MAIN;
        }

        /**
         * this is called when the user needs to know if the tool requires more actoins to be made
         * @param canContinue this is true if the tool requires more selections and choices from the user
         * @return the next state
         */
        @Override
        public ClientFSMState nextState(boolean canContinue){
            return nextState(canContinue,false,false,false);
        }
    };

    /**
     * true iff a tool is enabled
     */
    private static boolean toolEnabled;

    /**
     * true iff a die was added to the schema
     */
    private static boolean placedDie;

    public abstract ClientFSMState nextState(boolean stateSpecific, boolean back, boolean endTurn, boolean discard);
    public abstract ClientFSMState nextState(boolean stateSpecific);

    public static synchronized boolean isPlacedDie() {
        return placedDie;
    }
}
