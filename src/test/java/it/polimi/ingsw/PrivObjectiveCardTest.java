package it.polimi.ingsw;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PrivObjectiveCardTest {
    @Test
    void  testPrivObjectiveCardConstructor(){
        PrivObjectiveCard priv1 = new PrivObjectiveCard(1,"src"+File.separator+"xml"+File.separator+"PrivObjectiveCard.xml");
        assertEquals(Integer.parseInt("1"),priv1.getId());
        assertEquals("Sfumature Rosse",priv1.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"1.png",priv1.getImgSrc());
        assertEquals("Somma dei valori su tutti i dadi rossi",priv1.getDescription());

        PrivObjectiveCard pub2 = new PrivObjectiveCard(5,"src"+File.separator+"xml"+File.separator+"PrivObjectiveCard.xml");
        assertEquals(Integer.parseInt("5"),pub2.getId());
        assertEquals("Sfumature Viola",pub2.getName());
        assertEquals("src"+File.separator+"img"+File.separator+"PrivObjectiveCard"+File.separator+"5.png",pub2.getImgSrc());
        assertEquals("Somma dei valori su tutti i dadi viola",pub2.getDescription());
    }
}