package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.model.ServerFSM;
import it.polimi.ingsw.server.model.ToolCard;
import it.polimi.ingsw.server.model.enums.ServerState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFSMTest {

    @Test
    void testFsmFlow(){
        ServerFSM fsm=new ServerFSM();
        ServerState state;

        assertEquals(Place.DRAFTPOOL,fsm.getPlaceFrom());
        assertEquals(Place.SCHEMA,fsm.getPlaceTo());

        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.MAIN,state);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.PLACE_DIE);
        assertEquals(ServerState.GET_PLACEMENTS,state);
        state=fsm.nextState(Commands.PLACE_DIE);
        state=fsm.nextState(Commands.PLACE_DIE);
        assertEquals(ServerState.MAIN,state);

        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.PLACE_DIE);
        assertEquals(ServerState.GET_PLACEMENTS,state);

        state=fsm.nextState(Commands.PLACE_DIE);
        state=fsm.fsmDiscard();
        assertEquals(ServerState.GET_DICE_LIST,state);


    }

    @Test
    void testFsmFlowTool(){
        ServerFSM fsm=new ServerFSM();
        ServerState state;
        ToolCard tool =new ToolCard(3);

        assertEquals(Place.DRAFTPOOL,fsm.getPlaceFrom());
        assertEquals(Place.SCHEMA,fsm.getPlaceTo());
        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.MAIN,state);

        fsm.newToolUsage(tool);
        assertEquals(Place.SCHEMA,fsm.getPlaceFrom());
        assertEquals(Place.SCHEMA,fsm.getPlaceTo());
        assertTrue(fsm.isToolActive());

        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.CHOOSE_OPTION,state);
        state=fsm.nextState(Commands.REROLL);
        assertEquals(ServerState.TOOL_CAN_CONTINUE,state);
        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.GET_DICE_LIST,state);
        state=fsm.nextState(Commands.NONE);
        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.CHOOSE_OPTION,state);
        state=fsm.nextState(Commands.PLACE_DIE);
        state=fsm.nextState(Commands.PLACE_DIE);
        state=fsm.nextState(Commands.PLACE_DIE);
        assertEquals(ServerState.TOOL_CAN_CONTINUE,state);
        state=fsm.nextState(Commands.NONE);
        assertEquals(ServerState.GET_DICE_LIST,state);


        state=fsm.fsmExit();
        assertEquals(ServerState.MAIN,state);
        assertFalse(fsm.isToolActive());

        state=fsm.newToolUsage(tool);
        assertEquals(ServerState.GET_DICE_LIST,state);
        state=fsm.endTool();
        assertEquals(ServerState.TOOL_CAN_CONTINUE,state);

        state=fsm.newToolUsage(tool);
        assertEquals(ServerState.GET_DICE_LIST,state);
        state=fsm.newTurn(true);
        assertFalse(fsm.isToolActive());
        assertTrue(fsm.isFirstTurn());
        assertEquals(ServerState.MAIN,state);
    }
}
