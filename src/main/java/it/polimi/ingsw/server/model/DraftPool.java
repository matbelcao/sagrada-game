package it.polimi.ingsw.server.model;


import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the area where drafted dice are put
 */
public class DraftPool {
    private List<Die> drafted;
    private DiceBag diceBag;
    private RoundTrack roundTrack;

    /**
     * Constructor of the class
     */
    public DraftPool(){
        this.drafted = new ArrayList<>();
        diceBag=new DiceBag();
        roundTrack=new RoundTrack();
    }
    /**
     * Adds a die to the draft area
     * @param die the die to be added
     */
    public void addDie(Die die){drafted.add(die);}

    /**
     * Adds a group of dice to the draft area, during a game this can happen only after drafting
     * from the dice bag, when the draft area is empty
     * @param dice the dice to be added
     */
    public void addDice(List<Die> dice){
        assert(this.drafted.isEmpty());
        this.drafted.addAll(dice);
    }

    /**
     * This method returns the die selected through the index and removes it from the draft area
     * @param index the index of the selected die
     */
    public void removeDie(int index){
        assert index>=0 && index<this.drafted.size();
        this.drafted.remove(index);
        return;
    }

    public List<Die> getDraftedDice(){
        List<Die> diceList=new ArrayList<>();
        for(Die d:drafted){
            diceList.add(d);
        }
        return diceList;
    }

    public List<Integer> getDicePoolList(){
        List<Integer> positions=new ArrayList<>();
        for(int i=0;i<drafted.size();i++){
            positions.add(i);
        }
        return positions;
    }

    public List<Die> draftDice(int numPlayers) {
        drafted=new ArrayList<>();
        drafted = diceBag.draftDice((numPlayers*2)+1);
        return drafted;
    }

    public RoundTrack getRoundTrack(){
        return this.roundTrack;
    }

    public void clearDraftPool(int round){
        roundTrack.putDice(round, drafted);
        drafted=new ArrayList<>();
    }

    public Die putInBagAndExtract(Die die){
        return diceBag.substituteDie(die);
    }
    
}
