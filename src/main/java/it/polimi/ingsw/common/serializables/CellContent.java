package it.polimi.ingsw.common.serializables;

import it.polimi.ingsw.common.enums.DieColor;
import it.polimi.ingsw.common.enums.Shade;

public interface CellContent{
    boolean isDie();
    boolean hasColor();
    Shade getShade();
    DieColor getDieColor();

}
