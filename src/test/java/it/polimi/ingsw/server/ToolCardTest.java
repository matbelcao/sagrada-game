package it.polimi.ingsw.server;

import it.polimi.ingsw.common.enums.DieQuantity;
import it.polimi.ingsw.common.enums.ModifyDie;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.common.enums.Turn;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.ToolCard;
import it.polimi.ingsw.server.model.enums.IgnoredConstraint;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ToolCardTest {
    @Test
    void  testToolCardConstructor(){
        ToolCard tool1 = new ToolCard(1);
        assertEquals(Integer.parseInt("1"),tool1.getId());
        assertEquals("Pinza Sgrossatrice",tool1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"1.png",tool1.getImgSrc());
        assertEquals("Dopo aver scelto un dado, aumenta o dominuisci il valore del dado scelto di 1 (Non puoi cambiare un 6 in 1 o un 1 in 6)",tool1.getDescription());

        assertFalse(tool1.hasAlreadyUsed());
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

    @Test
    void testSwapDie(){
        Die die1=new Die("THREE","RED");
        Die die2=new Die("SIX","GREEN");
        ToolCard tool5 = new ToolCard(5);
        tool5.swapDie(die1,die2);
        assertEquals("RED",die2.getColor().toString());
        assertEquals("THREE",die2.getShade().toString());
        assertEquals("SIX",die1.getShade().toString());
    }
}
