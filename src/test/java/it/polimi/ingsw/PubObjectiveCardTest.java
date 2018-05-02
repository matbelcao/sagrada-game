package it.polimi.ingsw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PubObjectiveCardTest {
    @Test
    void  testPubObjectiveCardConstructor(){
        PubObjectiveCard pub1 = new PubObjectiveCard(1,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("1"),pub1.getId());
        assertEquals("Colori diversi - Riga [6]",pub1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PubObjectiveCard"+File.separator+"1.png",pub1.getImgSrc());
        assertEquals("Righe senza colori ripetuti",pub1.getDescription());

        PubObjectiveCard pub2 = new PubObjectiveCard(10,"src"+File.separator+"xml"+File.separator+"PubObjectiveCard.xml");
        assertEquals(Integer.parseInt("10"),pub2.getId());
        assertEquals("Varietà di Colore [4]",pub2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PubObjectiveCard"+File.separator+"10.png",pub2.getImgSrc());
        assertEquals("Set di dadi di ogni colore ovunque",pub2.getDescription());
    }
}
