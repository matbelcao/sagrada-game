package it.polimi.ingsw.common.immutables;

import it.polimi.ingsw.common.enums.Color;
import it.polimi.ingsw.common.enums.Shade;

public interface CellContent{
    boolean isDie();
    boolean hasColor();
    Shade getShade();
    Color getColor();

}
