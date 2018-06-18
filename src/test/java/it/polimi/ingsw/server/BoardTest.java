package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Commands;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalActionException;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import it.polimi.ingsw.server.model.exceptions.IllegalShadeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private static ArrayList<User> users1,users2;
    private static User u3,u6;
    private static boolean additionalSchemas=false;

    @BeforeAll
    static void setUp(){
        User u1 = new User("Mario", "pass1".toCharArray());
        User u2 = new User("Luigi", "pass2".toCharArray());
        u3 = new User("Giovanni", "pass3".toCharArray());
        User u4 = new User("Giacomo", "pass4".toCharArray());
        User u5 = new User("Aldo", "pass5".toCharArray());
        u6 = new User("Paolo", "pass6".toCharArray());

        users1=new ArrayList<>();
        users2=new ArrayList<>();

        users1.add(u1);
        users1.add(u2);
        users1.add(u3);
        users1.add(u4);

        users2.add(u5);
        users2.add(u6);
    }

    @Test
    void testBoardConstructor(){
        Board board1=new Board(users1, additionalSchemas);
        Board board2=new Board(users2, additionalSchemas);


        assertEquals(2, board1.getPlayer(u3).getGameId());

        assertEquals(1, board2.getPlayer(u6).getGameId());
        assertFalse(board2.getPlayer(u6).matchesUser(u3));
        assertTrue(board2.getPlayer(u6).matchesUser(u6));

        assertThrows(NoSuchElementException.class,() -> board2.getPlayerById(2));

        assertNotEquals(board1.getToolCard(0),board1.getToolCard(1));
        assertNotEquals(board1.getToolCard(1),board1.getToolCard(2));
        assertNotEquals(board1.getToolCard(0),board1.getToolCard(2));

        assertNotEquals(board1.getPublicObjective(0),board1.getPublicObjective(1));
        assertNotEquals(board1.getPublicObjective(1),board1.getPublicObjective(2));
        assertNotEquals(board1.getPublicObjective(0),board1.getPublicObjective(2));
    }

    @Test
    void testDraftSchemas(){
        Board board1=new Board(users1, additionalSchemas);
        assertEquals(16,board1.draftSchemas().length);

        Board board3=new Board(users2,true);
        assertEquals(8,board3.draftSchemas().length);
    }

    @Test
    void testInternalPlacement(){
        Board board=new Board(users1, additionalSchemas);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");

        Player player0 = board.getPlayer(users1.get(0));
        player0.setSchema(schema);


        ServerFSM fsm = board.getFSM();
        fsm.newTurn(0,true);
        board.exit();
        fsm.setPlaceFrom(Place.SCHEMA);

        try {
            schema.putDie(2,die1);
            schema.putDie(6,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        board.getDraftPool().draftDice(4);
        assertEquals(9, board.getDraftPool().getDraftedDice().size());

        List<IndexedCellContent> schemaList1=board.getDiceList();
        assertEquals(die1.getShade(),schemaList1.get(0).getContent().getShade());
        assertEquals(die2.getColor(),schemaList1.get(1).getContent().getColor());

        assertEquals(Commands.PLACE_DIE,board.selectDie(0).get(0));

        assertFalse(board.chooseOption(1));
        assertTrue(board.chooseOption(0));

        List<Integer> list =new ArrayList<>();
        list.add(8);
        list.add(11);
        list.add(12);
        List<Integer> schemaPlacements = board.getPlacements();
        assertEquals(list,schemaPlacements);

        board.discard();
        assertFalse(board.choosePlacement(3));
        assertFalse(board.choosePlacement(1));
        assertEquals(null, schema.getCell(11).getDie());

        assertEquals(Commands.PLACE_DIE,board.selectDie(0).get(0));
        assertTrue(board.chooseOption(0));
        assertTrue(board.choosePlacement(1));
        assertEquals(die1, schema.getCell(11).getDie());
        assertEquals("RED\\FOUR", schema.getCell(11).getDie().toString());
    }

    @Test
    void testChooseSchema(){
        Board board=new Board(users1, additionalSchemas);
        board.draftSchemas();

        Player player0 = board.getPlayer(users1.get(0));
        assertTrue(board.chooseSchemaCard(users1.get(0),1));
        assertFalse(board.chooseSchemaCard(users1.get(0),1));

        Player player1 = board.getPlayer(users1.get(1));
        SchemaCard schema=new SchemaCard(1,false);
        player1.setSchema(schema);
        assertEquals(schema,board.getUserSchemaCard(player1.getGameId()));
    }

    @Test
    void indexedListTest(){
        Board board=new Board(users1, additionalSchemas);

        ServerFSM fsm = board.getFSM();
        fsm.newTurn(0,true);
        board.exit();
        fsm.setPlaceFrom(Place.DRAFTPOOL);
        board.getDraftPool().draftDice(4);
        assertEquals(9, board.getDraftPool().getDraftedDice().size());
        assertEquals(9,board.getDiceList().size());
        assertEquals(Place.DRAFTPOOL,board.getDiceList().get(0).getPlace());

        fsm.newTurn(1,true);
        board.exit();
        fsm.setPlaceFrom(Place.ROUNDTRACK);
        assertEquals(0, board.getDraftPool().getRoundTrack().getTrackList().size());
        assertEquals(0,board.getDiceList().size());
        board.getDraftPool().clearDraftPool(0);
        assertEquals(9,board.getDiceList().size());
        assertEquals(Place.ROUNDTRACK,board.getDiceList().get(0).getPlace());

    }
}
