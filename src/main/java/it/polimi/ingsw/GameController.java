package it.polimi.ingsw;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * This class represents the controller of the game. It manages the rounds and the operations that the players make on the board
 */
public class GameController implements Iterable{
    public static final int NUM_ROUND=10;
    private Board board;
    static String xmlSource = "src"+ File.separator+"xml"+File.separator; //append class name + ".xml" to obtain complete path
    private ArrayList<Player> players;

    /**
     * Constructs the class and sets the players list
     * @param players the players of the match
     */
    public GameController(List<Player>players){
        this.players= (ArrayList<Player>) players;
    }

    /**
     * Instantiates a new board for the match
     */
    public void createBoard(){
        this.board= new Board(this.players,draftPubObjectives(),draftToolCards());
    }

    /**
     * Selects random ToolCards to be used in the match
     * @return an array containing the tools
     */
    private ToolCard[] draftToolCards() {
        ToolCard[] toolCards= new ToolCard[Board.NUM_TOOLS];
        Random randomGen = new Random();
        for(int i =0; i<Board.NUM_TOOLS;i++){
            toolCards[i]=new ToolCard(randomGen.nextInt(ToolCard.NUM_TOOL_CARDS) + 1,xmlSource+"ToolCard.xml");
        }
        return toolCards;
    }

    /**
     * Selects random Public Objective Cards
     * @return an array containing the public objectives for the match
     */
    private PubObjectiveCard[] draftPubObjectives() {
        PubObjectiveCard[] pubObjectiveCards= new PubObjectiveCard[Board.NUM_OBJECTIVES];
        Random randomGen = new Random();
        for(int i =0; i<Board.NUM_OBJECTIVES;i++){
            pubObjectiveCards[i]=new PubObjectiveCard(randomGen.nextInt(PubObjectiveCard.NUM_PUB_OBJ) + 1,xmlSource+"PubObjectiveCard.xml");
        }
        return pubObjectiveCards;
    }

    /**
     * This method creates an iterator that implements the round's turns management system
     * @return an iterator on the players of this game
     */
    @NotNull
    @Override
    public Iterator iterator() {
        return new RoundIterator(board.getPlayers());
    }

}

/**
 * This class implements an iterator on the players following Sagrada's rules for round management
 */
class RoundIterator implements Iterator<Player> {
    private final Integer numPlayers;
    private int i;
    private ArrayList<Player> players;
    private Player next;
    private int round;

    /**
     * Constructs the iterator initializing it as necessary
     * @param players the List of players that are playing the match
     */
    RoundIterator(List<Player> players){
        this.players=(ArrayList<Player>) players;
        this.numPlayers=players.size();
        this.next=null;
        this.i=0;
        this.round=0;
    }

    /**
     * Resets variables to begin a new round
     */
    public void nextRound() throws NoSuchMethodException {
        if(hasNextRound()) {
            this.next = null;
            this.i = 0;
            this.round++;
        }else{
            throw new NoSuchElementException("This is the last Round, there are no more rounds to go!");
        }
    }

    /**
     * This method checks whether the round is over
     * @return true iff the round is not over
     */
    @Override
    public boolean hasNext() {
        if(i<2*numPlayers){
            if(i<numPlayers){
                next=players.get((round + i)%numPlayers);
            }else{
                next=players.get((numPlayers - 1 + round - i%numPlayers)%numPlayers);
            }
            return true;
        }
        return false;
    }

    /**
     * This method checks whether there are more rounds to go or not
     * @return true iff this is not the last round
     */
    public boolean hasNextRound() {
        return this.round < Board.NUM_ROUNDS - 1;
    }

    /**
     * Checks whether the players are playing the first or the second turn of the round
     * @return true iff there's at least one player that
     */
    public boolean isFirstTurn(){ return i<=numPlayers;}

    /**
     * Gets the next player in the round
     * @return the next player
     */
    @Override
    public Player next() throws NoSuchElementException {
        if(this.hasNext()){ i++; return next;}
        throw new NoSuchElementException();
    }
}