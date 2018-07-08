package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.LightBoard;
import it.polimi.ingsw.common.serializables.LightPlayer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class LightboardTest {
    @Test
    void testSortRanking(){
        List <LightPlayer> players= new ArrayList<>();
        players.add(new LightPlayer("djsshd",0));
        players.add(new LightPlayer("djssdsdsdsd",1));
        players.add(new LightPlayer("bubu",2));

        players.get(0).setFinalPosition(3);
        players.get(2).setFinalPosition(2);
        players.get(1).setFinalPosition(1);

        LightBoard board=new LightBoard(3);
        board.addPlayer(players.get(0));
        board.addPlayer(players.get(1));
        board.addPlayer(players.get(2));

        for(LightPlayer player: board.sortFinalPositions()){
            System.out.println(player.getUsername()+"  "+player.getFinalPosition());
        }
    }
}
