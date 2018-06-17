package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.Color;
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
        Board board2=new Board(users2, additionalSchemas);

        assertEquals(16,board1.draftSchemas().length);
        assertEquals(8,board2.draftSchemas().length);
    }

    @Test
    void testIndexedDiceList(){
        Board board1=new Board(users1, additionalSchemas);
        SchemaCard schema= new SchemaCard(1);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");

        Player player0 = board1.getPlayer(users1.get(0));
        player0.setSchema(schema);

        try {
            schema.putDie(2,die1);
            schema.putDie(6,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        List<IndexedCellContent> schemaList1=board1.indexedDiceList(player0.getGameId(),Place.SCHEMA,Color.NONE);
        assertEquals(die1.getShade(),schemaList1.get(0).getContent().getShade());
        assertEquals(die2.getColor(),schemaList1.get(1).getContent().getColor());

        List<IndexedCellContent> schemaList2=board1.indexedDiceList(player0.getGameId(),Place.SCHEMA,Color.YELLOW);
        assertNotEquals(die1.getShade(),schemaList2.get(0).getContent().getShade());
        assertEquals(die2.getColor(),schemaList2.get(0).getContent().getColor());
        assertEquals(Place.SCHEMA,schemaList2.get(0).getPlace());

        List<Integer> list =new ArrayList<>();
        list.add(8);
        list.add(11);
        list.add(12);
        List<Integer> schemaPlacements = board1.listSchemaPlacements(player0.getGameId(),new Die("TWO","RED"),IgnoredConstraint.NONE);
        assertEquals(list,schemaPlacements);

        board1.getDraftPool().draftDice(users1.size());
        assertEquals(9,board1.indexedDiceList(player0.getGameId(),Place.DRAFTPOOL,Color.NONE).size());
        assertEquals(0,board1.indexedDiceList(player0.getGameId(),Place.ROUNDTRACK,Color.NONE).size());

        board1.getDraftPool().clearDraftPool(0);
        assertEquals(0,board1.indexedDiceList(player0.getGameId(),Place.DRAFTPOOL,Color.NONE).size());
        assertEquals(9,board1.indexedDiceList(player0.getGameId(),Place.ROUNDTRACK,Color.NONE).size());
    }

    @Test
    void testGameMethods(){
        Board board1=new Board(users1, additionalSchemas);
        SchemaCard schema= new SchemaCard(1);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");

        Player player0 = board1.getPlayer(users1.get(0));
        player0.setSchema(schema);

        try {
            schema.putDie(2,die1);
            schema.putDie(6,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        Die dieTemp = board1.selectDie(player0.getGameId(),Place.SCHEMA,0,Color.YELLOW);
        assertEquals(die2,dieTemp);
        assertEquals(2,board1.getDiePosition(player0.getGameId(),Place.SCHEMA,die1));

        board1.getDraftPool().draftDice(users1.size());
        Die dieDraftPool = board1.selectDie(player0.getGameId(),Place.DRAFTPOOL,1,Color.NONE);
        assertEquals(1, board1.getDiePosition(player0.getGameId(),Place.DRAFTPOOL,dieDraftPool));
        board1.getDraftPool().clearDraftPool(0);
        assertEquals(-1, board1.getDiePosition(player0.getGameId(),Place.DRAFTPOOL,dieDraftPool));
        board1.getDraftPool().draftDice(users1.size());
        Die dieRoundTrack = board1.selectDie(player0.getGameId(),Place.ROUNDTRACK,1,Color.NONE);
        assertEquals(1, board1.getDiePosition(player0.getGameId(),Place.ROUNDTRACK,dieRoundTrack));
        assertEquals(dieDraftPool,dieRoundTrack);

        List<Integer> oldIndexes=new ArrayList<>();
        oldIndexes.add(2);
        oldIndexes.add(6);
        board1.removeOldDice(player0.getGameId(),Place.SCHEMA,oldIndexes);
        assertEquals(0,schema.getSchemaDiceList(Color.NONE).size());

        board1.removeOldDice(player0.getGameId(),Place.DRAFTPOOL,oldIndexes);
        board1.removeOldDice(player0.getGameId(),Place.ROUNDTRACK,oldIndexes);
        assertEquals(7,board1.getDraftPool().getDraftedDice().size());
        assertEquals(7,board1.getDraftPool().getRoundTrack().getTrackList().size());
    }
}
