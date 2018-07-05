package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.RoundTrack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test class checks the roundtrack methods
 */
class RoundTrackTest {

    /**
     * Tries to put some dice in the roundtrack and then tries to retrieve the list of dice
     */
    @Test
    void testRoundTrack(){
        RoundTrack roundTrack=new RoundTrack();

        ArrayList<Die> dieList1=new ArrayList<>();
        dieList1.add(new Die("THREE","GREEN"));
        dieList1.add(new Die("TWO","RED"));
        roundTrack.putDice(0,dieList1);

        ArrayList<Die> dieList2=new ArrayList<>();
        dieList2.add(new Die("FOUR","YELLOW"));
        dieList2.add(new Die("ONE","RED"));
        roundTrack.putDice(1,dieList2);

        assertEquals(dieList1.get(0).toString(),roundTrack.getTrack().get(0).get(0).toString());
        assertEquals(dieList2.get(0).toString(),roundTrack.getTrack().get(1).get(0).toString());

        List<Die> trackList= roundTrack.getTrackList();
        assertEquals(trackList.size(),4);
        assertEquals(dieList1.get(0).toString(),trackList.get(0).toString());
        assertEquals(dieList2.get(0).toString(),trackList.get(2).toString());

        roundTrack.removeDie(2);
        List<Die> trackList2= roundTrack.getTrackList();
        assertEquals(trackList2.size(),3);
        assertEquals(dieList1.get(0).toString(),trackList2.get(0).toString());
        assertEquals(dieList2.get(0).toString(),trackList2.get(2).toString());
        assertEquals(trackList2.get(2).getShade().toInt(),1);
    }
}
