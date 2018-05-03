package it.polimi.ingsw;

import java.util.ArrayList;

public class ScoreCalculator10 implements ScoreCalculator{

    @Override
    public Integer calculateScore(SchemaCard schema) {
        Integer points;
        Integer[] count;
        FullCellIterator fullCell = (FullCellIterator) schema.iterator();
        Cell next;

        count = new Integer[Color.values().length];

        while(fullCell.hasNext()){
            next=fullCell.next();
            count[next.getDie().getColorOrdinal()] += 1;
        }
        points=count[0];
        for(Integer i : count){
           if(points>i){ points=i;}
        }
        return points;
    }

}
