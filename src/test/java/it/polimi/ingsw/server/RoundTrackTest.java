package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.RoundTrack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundTrackTest {
    @Test
    void testRoundTrackConstructor(){
        RoundTrack roundTrack=new RoundTrack();

        ArrayList<Die> dieList=new ArrayList<>();
        dieList.add(new Die("THREE","GREEN"));
        dieList.add(new Die("TWO","RED"));

        roundTrack.putDice(0,dieList);

        //assertEquals(dieList,roundTrack.getTrack().get(0));
        assertEquals(dieList.get(0).toString(),roundTrack.getTrack().get(0).get(0).toString());
    }
}
