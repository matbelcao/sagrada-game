package it.polimi.ingsw.server;

import it.polimi.ingsw.server.connection.MasterServer;
import it.polimi.ingsw.server.model.ToolCard;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ToolCardTest {
    @Test
    void  testToolCardConstructor(){
        ToolCard tool1 = new ToolCard(1,MasterServer.XML_SOURCE+"ToolCard.xml");
        assertEquals(Integer.parseInt("1"),tool1.getId());
        assertEquals("Pinza Sgrossatrice",tool1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"1.png",tool1.getImgSrc());
        assertEquals("Dopo aver scelto un dado, aumenta o dominuisci il valore del dado scelto di 1 (Non puoi cambiare un 6 in 1 o un 1 in 6)",tool1.getDescription());

        ToolCard tool2 = new ToolCard(5,MasterServer.XML_SOURCE+"ToolCard.xml");
        assertEquals(Integer.parseInt("5"),tool2.getId());
        assertEquals("Taglierina circolare",tool2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"5.png",tool2.getImgSrc());
        assertEquals("Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round",tool2.getDescription());

        ToolCard tool3 = new ToolCard(12,MasterServer.XML_SOURCE+"ToolCard.xml");
        assertEquals(Integer.parseInt("12"),tool3.getId());
        assertEquals("Taglierina Manuale",tool3.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"ToolCard"+File.separator+"12.png",tool3.getImgSrc());
        assertEquals("Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round (Devi rispettare tutte le restrizioni di piazzamento)",tool3.getDescription());
    }
}
