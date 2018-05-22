package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Face;

public interface CellContent{
    boolean isDie();
    boolean hasColor();
    Face getShade();
    Color getColor();

}
