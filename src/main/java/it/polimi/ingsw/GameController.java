package it.polimi.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class GameController implements Iterable {
    private Board board;
    static String xmlSource = "src"+ File.separator+"xml"+File.separator; //append class name + ".xml" to obtain complete path

    @NotNull
    @Override
    public Iterator iterator() {
        return new RoundIterator(board.getPlayers());
    }

    @Override
    public void forEach(Consumer action) {

    }
}



class RoundIterator implements Iterator<Player> {
    private final Integer numPlayers;
    private Integer i;
    private ArrayList<Player> players;
    private Player next;


    RoundIterator(List<Player> players){
        this.players=(ArrayList<Player>) players;
        this.numPlayers=players.size();
        this.next=null;
        this.i=0;
    }

    @Override
    public boolean hasNext() {
        if(i<2*numPlayers){
            if(i<numPlayers){
                next=players.get(i);
            }else{
                next=players.get(numPlayers-1 - i%numPlayers);
            }
            return true;
        }
        return false;
    }

    @Override
    public Player next() {
        if(this.hasNext()){ return next;}
        throw new NoSuchElementException();
    }
}