package it.polimi.ingsw.client;

import it.polimi.ingsw.client.controller.clientFSM.ClientFSMState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class checks the client-side FSM execution
 */
class ClientFSMStateTest {

    /**
     * this tests a normal loop from choosing the schema to placing a die from the draftpool to the schema, without tools
     */
    @Test
    void testNextStateDiePlacement(){
        ClientFSMState state= ClientFSMState.CHOOSE_SCHEMA;

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.SCHEMA_CHOSEN,state);

        state= ClientFSMState.NOT_MY_TURN;

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.NOT_MY_TURN,state);

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.MAIN,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.SELECT_DIE,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.CHOOSE_OPTION,state);

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.CHOOSE_PLACEMENT,state);

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.MAIN,state);

    }

    /**
     * this tests a double loop within the usage of a tool without any die placement
     */
    @Test
    void testNextStateToolDiePlacement(){
        ClientFSMState state= ClientFSMState.MAIN;



        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.CHOOSE_TOOL,state);

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.SELECT_DIE,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.CHOOSE_OPTION,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.TOOL_CAN_CONTINUE,state);

        state=state.nextState(true,false,false,false);
        assertEquals(ClientFSMState.SELECT_DIE,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.CHOOSE_OPTION,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.TOOL_CAN_CONTINUE,state);

        state=state.nextState(false,false,false,false);
        assertEquals(ClientFSMState.MAIN,state);
    }


}