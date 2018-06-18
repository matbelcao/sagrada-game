package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.*;
import it.polimi.ingsw.common.immutables.IndexedCellContent;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import it.polimi.ingsw.server.model.exceptions.IllegalDieException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.tools.Tool;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolCardTest {
    private static Board board;
    private static PrivObjectiveCard priv;

    @BeforeAll
    static void setUp(){
        List<User> users=new ArrayList<>();
        users.add(new User("User1","Pass1".toCharArray()));
        users.add(new User("User2","Pass2".toCharArray()));
        board=new Board(users,false);
        priv =new PrivObjectiveCard(1);
    }

    @Test
    void  testToolCardConstructor(){
        ToolCard tool1 = new ToolCard(1);
        assertEquals(Integer.parseInt("1"),tool1.getId());
        assertEquals("Pinza Sgrossatrice",tool1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"1.png",tool1.getImgSrc());
        assertEquals("Dopo aver scelto un dado, aumenta o dominuisci il valore del dado scelto di 1 (Non puoi cambiare un 6 in 1 o un 1 in 6)",tool1.getDescription());

        assertFalse(tool1.isAlreadyUsed());
        assertEquals(Place.DRAFTPOOL,tool1.getPlaceFrom());
        assertEquals(Place.DRAFTPOOL,tool1.getPlaceTo());
        assertEquals(Commands.INCREASE_DECREASE,tool1.getActions().get(0));
        assertEquals(IgnoredConstraint.NONE,tool1.getIgnoredConstraint());



        ToolCard tool2 = new ToolCard(5);
        assertEquals(Integer.parseInt("5"),tool2.getId());
        assertEquals("Taglierina circolare",tool2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"5.png",tool2.getImgSrc());
        assertEquals("Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round",tool2.getDescription());
        assertFalse(tool2.isSetColorFromRountrackCard());
        assertFalse(tool2.isInternalSchemaPlacement());
        assertTrue(tool2.isExternalPlacement());

        ToolCard tool3 = new ToolCard(12);
        assertEquals(Integer.parseInt("12"),tool3.getId());
        assertEquals("Taglierina Manuale",tool3.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"12.png",tool3.getImgSrc());
        assertEquals("Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round (Devi rispettare tutte le restrizioni di piazzamento)",tool3.getDescription());

    }

    @Test
    void testInternalPlacement(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");
        player.setSchema(schema);

        ToolCard tool= new ToolCard(2);

        //No dice placed in the schema, internal placements non possible.
        assertFalse(tool.enableToolCard(player,0,Turn.FIRST_TURN,0,schema));

        try {
            schema.putDie(2,die1);
            schema.putDie(6,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        int tokens=player.getFavorTokens();
        //No dice placed in the schema, internal placements non possible.
        assertTrue(tool.enableToolCard(player,0,Turn.FIRST_TURN,1,schema));
        assertEquals(tokens,player.getFavorTokens()+1);

        List<IndexedCellContent> internalSchemaDiceList=tool.internalIndexedSchemaDiceList();
        assertEquals(2,internalSchemaDiceList.size());
        assertEquals(die1.getShade().toString(),internalSchemaDiceList.get(0).getContent().getShade().toString());
        assertEquals(die2.getColor().toString(),internalSchemaDiceList.get(1).getContent().getColor().toString());

        tool.internalSelectDie(0);
        assertEquals(Commands.PLACE_DIE,tool.getActions().get(0));

        List<Integer> placements=tool.internalListPlacements();

        int newIndex=placements.get(0);
        assertTrue(tool.internalDiePlacement(0));
        assertEquals(die1.toString(),tool.getNewSchema().getCell(newIndex).getDie().toString());
        assertFalse(tool.getNewSchema().getCell(2).hasDie());
        assertEquals(1,tool.getOldIndexes().size());

        assertFalse(tool.toolCanContinue(player));
        assertEquals(schema.getSchemaDiceList(Color.NONE).toString(),tool.getNewSchema().getSchemaDiceList(Color.NONE).toString());
        assertTrue(tool.isAlreadyUsed());
    }

    @Test
    void testToolExitAndDiscard(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");
        player.setSchema(schema);

        ToolCard tool= new ToolCard(2);

        try {
            schema.putDie(2,die1);
            schema.putDie(6,die2);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        int tokens=player.getFavorTokens();
        assertTrue(tool.enableToolCard(player,0,Turn.FIRST_TURN,1,schema));
        assertEquals(tokens,player.getFavorTokens()+1);

        List<IndexedCellContent> internalSchemaDiceList=tool.internalIndexedSchemaDiceList();
        tool.internalSelectDie(0);

        tool.toolExit(player);
        assertTrue(tool.enableToolCard(player,0,Turn.FIRST_TURN,1,schema));
        assertEquals(tokens,player.getFavorTokens()+3);
        //The buffer of dice is empty
        assertThrows(IndexOutOfBoundsException.class,() -> tool.internalDiePlacement(0));

        tool.internalSelectDie(0);
        List<Integer> placements=tool.internalListPlacements();
        tool.toolDiscard();
        //The buffer of dice is empty
        assertThrows(IndexOutOfBoundsException.class,() -> tool.internalDiePlacement(0));
        tool.internalSelectDie(1);
        List<Integer> placements2=tool.internalListPlacements();
        assertNotEquals(placements,placements2);
        tool.internalDiePlacement(0);

        tool.toolExit(player);
        assertEquals(1,player.getFavorTokens());
        assertFalse(tool.enableToolCard(player,0,Turn.FIRST_TURN,1,schema));
    }

    @Test
    void testShadeIncreaseDecrease(){
        ToolCard tool= new ToolCard(1);
        Die die1=new Die("ONE","GREEN");
        Die die2=new Die("THREE","GREEN");
        Die die3=new Die("SIX","GREEN");
        List<IndexedCellContent> diceList;

        assertEquals(Commands.INCREASE_DECREASE,tool.getActions().get(0));

        diceList=tool.shadeIncreaseDecrease(die1);
        assertEquals(1,diceList.size());
        assertEquals("TWO",diceList.get(0).getContent().getShade().toString());
        assertEquals("GREEN",diceList.get(0).getContent().getColor().toString());

        diceList=tool.shadeIncreaseDecrease(die2);
        assertEquals(2,diceList.size());
        assertEquals("FOUR",diceList.get(0).getContent().getShade().toString());
        assertEquals("GREEN",diceList.get(0).getContent().getColor().toString());
        assertEquals("TWO",diceList.get(1).getContent().getShade().toString());
        assertEquals("GREEN",diceList.get(1).getContent().getColor().toString());

        diceList=tool.shadeIncreaseDecrease(die3);
        assertEquals(1,diceList.size());
        assertEquals("FIVE",diceList.get(0).getContent().getShade().toString());
        assertEquals("GREEN",diceList.get(0).getContent().getColor().toString());
    }

    @Test
    void testSwapDie(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FOUR","RED");
        Die die2=new Die("ONE","YELLOW");
        player.setSchema(schema);
        ToolCard tool= new ToolCard(5);

        int tokens=player.getFavorTokens();
        //not possible on round 0...because rountrack is empty
        assertFalse(tool.enableToolCard(player,0,Turn.FIRST_TURN,1,schema));
        assertTrue(tool.enableToolCard(player,1,Turn.FIRST_TURN,1,schema));
        assertEquals(tokens,player.getFavorTokens()+1);

        assertEquals(Commands.SWAP,tool.getActions().get(0));
        tool.selectDie(die1);
        tool.swapDie();
        assertEquals(0,tool.getOldIndexes().size());
        assertTrue(tool.toolCanContinue(player));

        assertEquals(Commands.SWAP,tool.getActions().get(0));
        tool.selectDie(die2);
        tool.swapDie();
        assertEquals(0,tool.getOldIndexes().size());
        assertFalse(tool.toolCanContinue(player));

        assertEquals("YELLOW\\ONE",die1.toString());
        assertEquals("RED\\FOUR",die2.toString());
    }

    @Test
    void testRerollOneDie(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FOUR","RED");
        player.setSchema(schema);
        ToolCard tool= new ToolCard(6);

        assertTrue(tool.enableToolCard(player,1,Turn.FIRST_TURN,1,schema));
        assertEquals(Commands.REROLL,tool.getActions().get(0));
        assertFalse(tool.isExternalPlacement());
        assertFalse(tool.isRerollAllDiceCard());

        tool.selectDie(die1);
        List<IndexedCellContent> newDie=tool.rerollDie();
        assertEquals(1,newDie.size());
        assertEquals("RED",die1.getColor().toString());

        assertFalse(tool.toolCanContinue(player));

    }

    @Test
    void testRerollAllDice(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        List<Die> dieList=new ArrayList<>();
        dieList.add(new Die("FOUR","RED"));
        dieList.add(new Die("ONE","GREEN"));
        dieList.add(new Die("THREE","PURPLE"));
        player.setSchema(schema);
        ToolCard tool= new ToolCard(7);


        //Not possible to enable on first,turn....only on second and without having placed any die
        assertFalse(tool.enableToolCard(player,0,Turn.FIRST_TURN,0,schema));
        assertFalse(tool.enableToolCard(player,0,Turn.SECOND_TURN,1,schema));
        assertTrue(tool.enableToolCard(player,0,Turn.SECOND_TURN,0,schema));

        assertEquals(Commands.REROLL,tool.getActions().get(0));
        assertFalse(tool.isExternalPlacement());
        assertTrue(tool.isRerollAllDiceCard());

        tool.rerollAll(dieList);
        assertEquals(3,dieList.size());
        assertEquals("RED",dieList.get(0).getColor().toString());
        assertEquals("PURPLE",dieList.get(2).getColor().toString());
    }

    @Test
    void testFlipDie(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("ONE","RED");
        player.setSchema(schema);
        ToolCard tool= new ToolCard(10);

        assertTrue(tool.enableToolCard(player,1,Turn.FIRST_TURN,1,schema));
        assertEquals(Commands.FLIP,tool.getActions().get(0));
        assertFalse(tool.isExternalPlacement());
        assertFalse(tool.isRerollAllDiceCard());

        tool.selectDie(die1);
        List<IndexedCellContent> newDie=tool.flipDie();
        assertEquals(1,newDie.size());
        assertEquals("RED",die1.getColor().toString());
        assertEquals("SIX",die1.getShade().toString());

        assertFalse(tool.toolCanContinue(player));
    }

    @Test
    void testChooseShade(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FIVE","PURPLE");
        player.setSchema(schema);
        ToolCard tool= new ToolCard(11);

        //Not possible to enable...one die yet placed
        assertFalse(tool.enableToolCard(player,1,Turn.FIRST_TURN,1,schema));
        assertTrue(tool.enableToolCard(player,1,Turn.FIRST_TURN,0,schema));
        assertEquals(Commands.SET_SHADE,tool.getActions().get(0));
        assertTrue(tool.isExternalPlacement());
        assertFalse(tool.isInternalSchemaPlacement());

        tool.selectDie(die1);
        List<IndexedCellContent> diceList=tool.chooseShade();
        assertEquals(6,diceList.size());
        for(int i=0;i<diceList.size();i++){
            assertEquals("PURPLE",diceList.get(i).getContent().getColor().toString());
            assertEquals(i+1,diceList.get(i).getContent().getShade().toInt());
        }
        assertTrue(tool.toolCanContinue(player));
        assertEquals(Commands.PLACE_DIE,tool.getActions().get(0));
    }

    @Test
    void testSetColor(){
        Player player = new Player("Player1",0,board,priv);
        SchemaCard schema= new SchemaCard(1,false);
        Die die1=new Die("FIVE","PURPLE");
        player.setSchema(schema);
        ToolCard tool= new ToolCard(12);

        //Not possible to enable...insufficient dice in the schema
        assertFalse(tool.enableToolCard(player,2,Turn.SECOND_TURN,1,schema));

        try {
            schema.putDie(2,die1);
        } catch (IllegalDieException e) {
            e.printStackTrace();
        }

        assertTrue(tool.enableToolCard(player,2,Turn.SECOND_TURN,1,schema));

        assertTrue(tool.isSetColorFromRountrackCard());
        assertFalse(tool.isRerollAllDiceCard());
        assertTrue(tool.isInternalSchemaPlacement());
        assertFalse(tool.isExternalPlacement());


        assertEquals(Commands.SET_COLOR,tool.getActions().get(0));
        assertEquals(Color.NONE,tool.getColorConstraint());
        tool.selectDie(new Die("ONE","BLUE"));
        tool.setColor();
        assertEquals(Color.BLUE,tool.getColorConstraint());
    }
}
