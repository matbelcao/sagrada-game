package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieQuantity;
import it.polimi.ingsw.common.enums.ModifyDie;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.Turn;
import it.polimi.ingsw.server.model.ToolCard;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolCardTest {
    @Test
    void  testToolCardConstructor(){
        ToolCard tool1 = new ToolCard(1);
        assertEquals(Integer.parseInt("1"),tool1.getId());
        assertEquals("Pinza Sgrossatrice",tool1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"1.png",tool1.getImgSrc());
        assertEquals("Dopo aver scelto un dado, aumenta o dominuisci il valore del dado scelto di 1 (Non puoi cambiare un 6 in 1 o un 1 in 6)",tool1.getDescription());

        assertFalse(tool1.isAlreadyUsed());
        assertEquals(Place.DRAFTPOOL,tool1.getFrom());
        assertEquals(Place.DRAFTPOOL,tool1.getTo());
        assertEquals(DieQuantity.ONE,tool1.getQuantity().get(0));
        assertEquals(ModifyDie.INCREASE,tool1.getModify().get(0));
        assertEquals(ModifyDie.DECREASE,tool1.getModify().get(1));
        assertEquals(IgnoredConstraint.NONE,tool1.getIgnoredConstraint());
        assertEquals(Turn.NONE,tool1.getTurn());



        ToolCard tool2 = new ToolCard(5);
        assertEquals(Integer.parseInt("5"),tool2.getId());
        assertEquals("Taglierina circolare",tool2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"5.png",tool2.getImgSrc());
        assertEquals("Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round",tool2.getDescription());

        ToolCard tool3 = new ToolCard(12);
        assertEquals(Integer.parseInt("12"),tool3.getId());
        assertEquals("Taglierina Manuale",tool3.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"12.png",tool3.getImgSrc());
        assertEquals("Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round (Devi rispettare tutte le restrizioni di piazzamento)",tool3.getDescription());
    }
/*
    @Test
    void testSwapDie(){
        Die die1=new Die("THREE","RED");
        Die die2=new Die("SIX","GREEN");
        ToolCard tool5 = new ToolCard(5);
        tool5.initStage();
        assertTrue(tool5.stageFrom());
        assertTrue(tool5.selectDie1(die1));
        assertTrue(tool5.stageTo());
        assertTrue(tool5.swapDie(die2));
        assertEquals("RED",die2.getColor().toString());
        assertEquals("THREE",die2.getShade().toString());
        assertEquals("SIX",die1.getShade().toString());
    }
    */

   /* @Test
    void testStageFlowExecution(){
        ToolCard tool1 = new ToolCard(1);
        tool1.initStage();
        assertTrue(tool1.stageFrom());
        assertTrue(tool1.canModify1());
        assertFalse(tool1.stageSelect1());
        assertFalse(tool1.stageTo());
        assertFalse(tool1.stageModify2());
        assertFalse(tool1.canSelect2());

        ToolCard tool2 = new ToolCard(2);
        tool2.initStage();
        assertTrue(tool2.stageFrom());
        assertFalse(tool2.canModify1());
        assertTrue(tool2.stageSelect1());
        assertFalse(tool2.stageTo());
        assertFalse(tool2.stageModify2());
        assertFalse(tool2.canSelect2());

        ToolCard tool3 = new ToolCard(3);
        tool3.initStage();
        assertTrue(tool3.stageFrom());
        assertFalse(tool3.canModify1());
        assertTrue(tool3.stageSelect1());
        assertFalse(tool3.stageTo());
        assertFalse(tool3.stageModify2());
        assertFalse(tool3.canSelect2());

        ToolCard tool4 = new ToolCard(4);
        tool4.initStage();
        assertTrue(tool4.stageFrom());
        assertFalse(tool4.canModify1());
        assertTrue(tool4.stageSelect1());
        assertTrue(tool4.stageTo());
        assertFalse(tool4.stageModify2());
        assertTrue(tool4.canSelect2());

        ToolCard tool5 = new ToolCard(5);
        tool5.initStage();
        assertTrue(tool5.stageFrom());
        assertTrue(tool5.canModify1());
        assertFalse(tool5.stageSelect1());
        assertTrue(tool5.stageTo());
        assertTrue(tool5.stageModify2());
        assertFalse(tool5.canSelect2());

        ToolCard tool6 = new ToolCard(6);
        tool6.initStage();
        assertTrue(tool6.stageFrom());
        assertTrue(tool6.canModify1());
        assertFalse(tool6.stageSelect1());
        assertFalse(tool6.stageTo());
        assertFalse(tool6.stageModify2());
        assertFalse(tool6.canSelect2());

        ToolCard tool7 = new ToolCard(7);
        tool7.initStage();
        assertTrue(tool7.stageFrom());
        assertFalse(tool7.canModify1());
        assertFalse(tool7.stageSelect1());
        assertFalse(tool7.stageTo());
        assertFalse(tool7.stageModify2());
        assertFalse(tool7.canSelect2());

        ToolCard tool8 = new ToolCard(8);
        tool8.initStage();
        assertTrue(tool8.stageFrom());
        assertFalse(tool8.canModify1());
        assertTrue(tool8.stageSelect1());
        assertFalse(tool8.stageTo());
        assertFalse(tool8.stageModify2());
        assertFalse(tool8.canSelect2());

        ToolCard tool9 = new ToolCard(9);
        tool9.initStage();
        assertTrue(tool9.stageFrom());
        assertFalse(tool9.canModify1());
        assertTrue(tool9.stageSelect1());
        assertFalse(tool9.stageTo());
        assertFalse(tool9.stageModify2());
        assertFalse(tool9.canSelect2());

        ToolCard tool10 = new ToolCard(10);
        tool10.initStage();
        assertTrue(tool10.stageFrom());
        assertTrue(tool10.canModify1());
        assertFalse(tool10.stageSelect1());
        assertFalse(tool10.stageTo());
        assertFalse(tool10.stageModify2());
        assertFalse(tool10.canSelect2());

        ToolCard tool11 = new ToolCard(11);
        tool11.initStage();
        assertTrue(tool11.stageFrom());
        assertTrue(tool11.canModify1());
        assertFalse(tool11.stageSelect1());
        assertTrue(tool11.stageTo());
        assertTrue(tool11.stageModify2());
        assertFalse(tool11.canSelect2());

        /*ToolCard tool12 = new ToolCard(12);
        tool12.initStage();
        assertTrue(tool12.stageFrom());
        assertFalse(tool12.canModify1());
        assertTrue(tool12.stageSelect1());
        assertFalse(tool12.stageTo());
        assertFalse(tool12.stageModify2());
        assertFalse(tool12.canSelect2());*/
    //}
   /*
    @Test
    public void testWrongFlowExecution(){
        ToolCard tool5 = new ToolCard(5);
        tool5.initStage();
        assertTrue(tool5.stageFrom());
        assertFalse(tool5.stageFrom());
        assertFalse(tool5.stageModify2());
        assertTrue(tool5.canModify1());
        assertFalse(tool5.stageSelect1());
        assertFalse(tool5.stageModify2());
        assertTrue(tool5.stageTo());
        assertTrue(tool5.stageModify2());
        assertFalse(tool5.canSelect2());
        assertFalse(tool5.stageModify2());
    }
    */
}
