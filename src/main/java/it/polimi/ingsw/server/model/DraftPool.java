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
     * This method returns the die selected through the index and removes it from the draft area
     * @param index the index of the selected die
     */
    public void removeDie(int index){
        assert index>=0 && index<this.drafted.size();
        this.drafted.remove(index);
    }

    /**
     * Return the dice contained in the DraftPool
     * @return a list of die
     */
    public List<Die> getDraftedDice(){
        List<Die> diceList=new ArrayList<>();
        for(Die d:drafted){
            diceList.add(d);
        }
        return diceList;
    }

    /**
     * Extracts (Num_Players*2)+1) dice from the DiceBag, then puts them into the DraftPool
     * @param numPlayers the numpre of players of the match
     * @return the list of dice extracted
     */
    public List<Die> draftDice(int numPlayers) {
        drafted=new ArrayList<>();
        drafted = diceBag.draftDice((numPlayers*2)+1);
        return drafted;
    }

    /**
     * Returns the RoundTrack's reference
     * @return the RoundTrack's reference
     */
    public RoundTrack getRoundTrack(){
        return this.roundTrack;
    }

    /**
     * Remove the remaining dice from the DraftPool, then puts them into the RoundTrack
     * @param round the number of the ended round
     */
    public void clearDraftPool(int round){
        roundTrack.putDice(round, drafted);
        drafted=new ArrayList<>();
    }

    /**
     * Puts one one die in the DiceBag and extracts a new one (for the toolcard #11)
     * @param die the die to put in the bag
     * @return the new die extracted
     */
    public Die putInBagAndExtract(Die die){
        return diceBag.substituteDie(die);
    }
    
}
