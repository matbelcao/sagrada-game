package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;
import it.polimi.ingsw.common.enums.GameStatus;
import it.polimi.ingsw.common.enums.Place;
import it.polimi.ingsw.server.connection.User;
import it.polimi.ingsw.server.model.Die;
import it.polimi.ingsw.server.model.SchemaCard;
import it.polimi.ingsw.server.model.iterators.FullCellIterator;

import java.util.ArrayList;
import java.util.List;

public class IndexedCellContent {
    private Place place;
    private int position;
    private CellContent content;


    public IndexedCellContent(int position, Face face, Color color){
        this.content=new LightDie(face,color);
        this.position=position;
    }

    public IndexedCellContent(int position, Die die){
        this.content=new LightDie(die.getShade(),die.getColor());
        this.position=position;
    }

    public IndexedCellContent(int position, String face, String color){
        this.content=new LightDie(face,color);
        this.position=position;
    }

    public IndexedCellContent(int position, String constraint){
        this.content=new LightConstraint(constraint);
        this.position=position;
    }
    public IndexedCellContent(int position, Face shade){
        this.content=new LightConstraint(shade);
        this.position=position;
    }
    public IndexedCellContent(int position, Color color){
        this.content=new LightConstraint(color);
        this.position=position;
    }

    public int getPosition(){
        return position;
    }

    public Place getPlace() {
        return place;
    }

    public CellContent getContent() {
        return content;
    }

    /**
     * builds a list of indexed cellcontents that represents the roundtrack
     * @param roundTrack the actual roundtrack
     * @return the light version of the roundtrack
     */
    public static List<IndexedCellContent> toLightRoundTrack(List<List<Die>> roundTrack){
        ArrayList<IndexedCellContent> result= new ArrayList<>();
        Die die;
        for(int round=0; round<roundTrack.size();round++){
            for(int i=0; i<roundTrack.get(round).size();i++) {
                die=roundTrack.get(round).get(i);
                result.add(new IndexedCellContent(round, die.getShade(), die.getColor()));
            }
        }
        return result;
    }

    /**
     * builds a lis of LightDie from a draftpool
     * @param draftpool the draftpool to be copied
     * @return the light version of the draftpool
     */
    public static List<LightDie> toLightDraftPool(List<Die> draftpool){
        ArrayList<LightDie> result= new ArrayList<>();
        for(int index=0; index<draftpool.size();index++){
            result.add(LightDie.toLightDie(draftpool.get(index)));
        }
        return result;
    }

    public static List<IndexedCellContent> toSchemaDiceList(SchemaCard schema){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        FullCellIterator diceIterator=(FullCellIterator)schema.iterator();

        while(diceIterator.hasNext()) {
            die = diceIterator.next().getDie();
            indexedCell = new IndexedCellContent(diceIterator.getIndex(), die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }

    public static List<IndexedCellContent> toDraftedDiceList(List<Die> draftedDice){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;
        Die die;

        for (int index=0;index<draftedDice.size();index++){
            die=draftedDice.get(index);
            indexedCell=new IndexedCellContent(index,die);
            indexedList.add(indexedCell);
        }
        return indexedList;
    }

    public static List<IndexedCellContent> toRoundTrackDiceList(List<List<Die>> dieTrack){
        List<IndexedCellContent> indexedList=new ArrayList<>();
        IndexedCellContent indexedCell;

        for(int index=0;index<dieTrack.size();index++){
            List<Die> dieList= dieTrack.get(index);
            for(Die d:dieList){
                indexedCell=new IndexedCellContent(index,d);
                indexedList.add(indexedCell);
            }
        }
        return indexedList;
    }

}
